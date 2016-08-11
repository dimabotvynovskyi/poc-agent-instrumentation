package testing.instrument.transformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class MyClassTransformer implements ClassFileTransformer {

	public byte[] transform(ClassLoader loader,
			String className,
			Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {

		System.out.println("-- load class: " + className);

		try {
			if ("my/simple/app/SomeClass".equals(className)) {
				return instrumentSomeClass(classBeingRedefined, classfileBuffer);
			} else if ("org/springframework/context/support/AbstractApplicationContext".equals(className)) {
				return instrumentAbstractApplicationContext(classBeingRedefined, classfileBuffer);
			}
		}
		catch (Exception e) {
			System.out.println(">>>>> Error " + e.getMessage());
			e.printStackTrace();
		}

		return classfileBuffer;
	}

	private byte[] instrumentSomeClass(Class<?> someClass, byte[] classfileBuffer)
			throws Exception {
		System.out.println("-- Transformer: Start instrumenting SomeClass");

		ClassPool pool = ClassPool.getDefault();
		System.out.println("-- Transformer: looking for class");
		CtClass cc = pool.get("my.simple.app.SomeClass");
		System.out.println("-- Transformer: looking for method in class: " + cc);
		CtMethod method = cc.getMethod("getName", "()Ljava/lang/String;");
		System.out.println("-- Transformer: inserting method body in: " + method);
		method.insertBefore("System.out.println(\"SomeClass injected part of method body\");");

		System.out.println("-- Transformer: Finish instrumenting SomeClass");
		return cc == null ? classfileBuffer : cc.toBytecode();
	}

	private byte[] instrumentAbstractApplicationContext(Class<?> abstractApplicationContext, byte[] classfileBuffer) throws Exception {
		System.out.println("-- Transformer: Start instrumenting AbstractApplicationContext");

		ClassPool pool = ClassPool.getDefault();

		CtClass abstractAppContextCtClass = pool.get("org.springframework.context.support.AbstractApplicationContext");
		CtMethod prepareBeanFactoryCtMethod = abstractAppContextCtClass
				.getMethod("prepareBeanFactory", "(Lorg/springframework/beans/factory/config/ConfigurableListableBeanFactory;)V");

		prepareBeanFactoryCtMethod.insertAfter("{\n"
				+ "\t\t\tString AGENT_BEAN_NAME = \"agentBeanDefinition\";\n"
				+ "\n"
				+ "\t\t\tboolean hasAagentBeanDefinition = beanFactory.containsBeanDefinition(AGENT_BEAN_NAME);\n"
				+ "\t\t\tif (!hasAagentBeanDefinition && beanFactory instanceof org.springframework.beans.factory.support"
				+ ".BeanDefinitionRegistry) {\n"
				+ "\t\t\t\torg.springframework.beans.factory.support.BeanDefinitionRegistry registry = (org.springframework.beans.factory"
				+ ".support.BeanDefinitionRegistry) beanFactory;\n"
				+ "\n"
				+ "\t\t\t\torg.springframework.beans.factory.support.GenericBeanDefinition beanDefinition =\n"
				+ "\t\t\t\t\t\tnew org.springframework.beans.factory.support.GenericBeanDefinition();\n"
				+ "\t\t\t\tbeanDefinition.setBeanClass(testing.spring.AgentBean.class);\n"
				+ "\t\t\t\tbeanDefinition.setLazyInit(false);\n"
				+ "\t\t\t\tbeanDefinition.setAbstract(false);\n"
				+ "\t\t\t\tbeanDefinition.setScope(org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON);\n"
				+ "\n"
				+ "\t\t\t\tregistry.registerBeanDefinition(AGENT_BEAN_NAME, beanDefinition);\n"
				+ "\t\t\t}\n"
				+ "\t\t}");

		System.out.println("-- Transformer: Finish instrumenting AbstractApplicationContext");
		return abstractAppContextCtClass.toBytecode();
	}

	//TODO find a way how we can reuse this code to inject with javassist and cover with unit tests
	private void methodTemplateToInject(ConfigurableListableBeanFactory beanFactory) {
		{
			String AGENT_BEAN_NAME = "agentBeanDefinition";

			boolean hasAgentBeanDefinition = beanFactory.containsBeanDefinition(AGENT_BEAN_NAME);
			if (!hasAgentBeanDefinition && beanFactory instanceof org.springframework.beans.factory.support.BeanDefinitionRegistry) {
				org.springframework.beans.factory.support.BeanDefinitionRegistry registry =
						(org.springframework.beans.factory.support.BeanDefinitionRegistry) beanFactory;

				org.springframework.beans.factory.support.GenericBeanDefinition beanDefinition =
						new org.springframework.beans.factory.support.GenericBeanDefinition();
				beanDefinition.setBeanClass(testing.spring.AgentBean.class);
				beanDefinition.setLazyInit(false);
				beanDefinition.setAbstract(false);
				beanDefinition.setScope(org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON);

				registry.registerBeanDefinition(AGENT_BEAN_NAME, beanDefinition);
			}
		}
	}
}

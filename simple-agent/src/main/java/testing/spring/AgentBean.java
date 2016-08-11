package testing.spring;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;

@Lazy(false)
public class AgentBean implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@PostConstruct
	public void init() {
		System.out.println("Spring Agent Bean: init Agent Bean ");
		System.out.println("Spring Agent Bean: get external app bean " + applicationContext.getBean("appBean"));
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}

package my.simple.app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import my.simple.app.spring.context.RootApplicationContext;

public class MainApp {

	/**
	 * Run app with -javaagent:simple-agent/target/my-simple-agent-jar-with-dependencies.jar
	 */
	public static void main(String[] args) {
		System.out.println("App: Simple app started");

		System.out.println("App: SomeClass init");
		SomeClass s = new SomeClass();
		System.out.println(s.getName());

		System.out.println("App: SomeAnotherClass init");
		SomeAnotherClass ss = new SomeAnotherClass();
		System.out.println(ss.getName());

		System.out.println("App: init spring context");
		ApplicationContext ctx =
				new AnnotationConfigApplicationContext(RootApplicationContext.class);

		System.out.println("App: Simple app finished");
	}

}

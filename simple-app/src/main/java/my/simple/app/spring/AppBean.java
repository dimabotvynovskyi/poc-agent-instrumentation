package my.simple.app.spring;

import javax.annotation.PostConstruct;

public class AppBean {

	@PostConstruct
	public void init() {
		System.out.println("Spring Bean: init AppBean");
	}
}

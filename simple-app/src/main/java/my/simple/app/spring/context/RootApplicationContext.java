package my.simple.app.spring.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import my.simple.app.spring.AppBean;

@Configuration
public class RootApplicationContext {
	@Bean
	public AppBean appBean() {
		return new AppBean();
	}
}

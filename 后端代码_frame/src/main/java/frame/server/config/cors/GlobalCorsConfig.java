package frame.server.config.cors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置跨域
 * 
 * @author Administrator
 *
 */
@Configuration
public class GlobalCorsConfig {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("*")
						.allowCredentials(false)
						.allowedMethods("GET", "POST", "PUT", "DELETE")
						.allowedHeaders("*")
						.exposedHeaders("Header1", "Header2")
						.maxAge(3600);
			}
		};
	}

}

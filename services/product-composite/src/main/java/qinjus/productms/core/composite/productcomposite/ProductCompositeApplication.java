package qinjus.productms.core.composite.productcomposite;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

import qinjus.productms.core.composite.productcomposite.controller.ProductCompositeController;

@ComponentScan("qinjus.productms")
@SpringBootApplication
public class ProductCompositeApplication {
	private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeApplication.class);
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ProductCompositeApplication.class, args);
		boolean exist = context.containsBean("productCompositeController");
		LOG.info("exist:" + exist);

		Object c1 = context.getBean("productCompositeController");

		ProductCompositeController c2 = (ProductCompositeController)context.getBean(ProductCompositeController.class);
	}

	@Bean
	RestTemplate restTemplate() {
	  return new RestTemplate();
	}
}

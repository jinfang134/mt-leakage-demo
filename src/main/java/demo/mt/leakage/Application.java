package demo.mt.leakage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@SpringBootApplication
@ComponentScan
public class Application {

    private Logger log= LoggerFactory.getLogger(Application.class);

    public void addInterceptors (InterceptorRegistry registry) {
        registry.addInterceptor(new MyCounterInterceptor());
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

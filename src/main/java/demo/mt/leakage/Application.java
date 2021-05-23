package demo.mt.leakage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@SpringBootApplication(scanBasePackages = {"org.jeecg.modules.jmreport","demo.mt.leakage"})
//@ComponentScan
public class Application {

    @Autowired
    TokenInterceptor tokenInterceptor;

    @Autowired
    JwtUtils jwtUtils;

    private Logger log = LoggerFactory.getLogger(Application.class);

//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new MyCounterInterceptor());
//        registry.addInterceptor(tokenInterceptor);
//    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

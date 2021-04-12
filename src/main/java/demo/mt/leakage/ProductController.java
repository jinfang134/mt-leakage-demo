package demo.mt.leakage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@RestController
public class ProductController {
    private Logger log = LoggerFactory.getLogger(ProductController.class);
    Executor executor = Executors.newFixedThreadPool(8);

    private final static String HEADER_TANANT="TENANT";


    @GetMapping("/request")
    public String testRequestAttribute(HttpServletRequest request){
        String tenant=request.getHeader(HEADER_TANANT);
        return tenant;
    }

    @GetMapping("/test")
    public void longTimeJob(HttpServletRequest request){
        RequestAttributes requestAttributes= RequestContextHolder.getRequestAttributes();

        executor.execute(()-> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            for(int i=0;i<10000;i++){
                HttpServletRequest request1 =((ServletRequestAttributes) requestAttributes).getRequest();
                String uri=request1.getRequestURI();
                String header = request1.getHeader(HEADER_TANANT);
                if (uri!=null) {
                    log.info("{},uri:{},header:{}",i, uri, header) ;
                }
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }



}

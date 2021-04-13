package demo.mt.leakage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@RestController
public class TestController {
    private Logger log = LoggerFactory.getLogger(TestController.class);
    Executor executor = Executors.newFixedThreadPool(8);

    private final static String HEADER_TANANT = "TENANT";


    @GetMapping("/request")
    public String testRequestAttribute(HttpServletRequest request) {
        String tenant = request.getHeader(HEADER_TANANT);
        return tenant;
    }

    @GetMapping("/test")
    public List longTimeJob(HttpServletRequest request, @RequestHeader(HEADER_TANANT) String tenant) throws InterruptedException {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CopyOnWriteArrayList list = new CopyOnWriteArrayList();

        executor.execute(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            for (int i = 0; i < 100; i++) {
                HttpServletRequest request1 = ((ServletRequestAttributes) requestAttributes).getRequest();
                String uri = request1.getRequestURI();
                String tenantdiff = request1.getHeader(HEADER_TANANT);
                if (uri != null) {
                    log.info("{},uri:{},tenant:{}",i, uri, tenant) ;
                }
                if (tenantdiff != null && !tenantdiff.equals(tenant)) {
                    log.info("{},uri:{},tenant:{}", i, uri, tenant);
                    list.add(tenantdiff);
                }
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            countDownLatch.countDown();
        });
        countDownLatch.await();
        return list;
    }


}

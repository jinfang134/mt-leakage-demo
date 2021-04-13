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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class RequestHeaderWrapper {
    private final Map<String, String> headers;

    RequestHeaderWrapper(Map<String, String> map) {
        this.headers = map;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public static RequestHeaderWrapper wrap(ServletRequestAttributes attributes) {
        HttpServletRequest request = attributes.getRequest();
        Map<String, String> map = new HashMap<>();
        map.put("TENANT", request.getHeader("TENANT"));
        return new RequestHeaderWrapper(map);
    }
}

class RequestHeaderWrapperContext {
    private RequestHeaderWrapperContext() {
    }

    private final static ThreadLocal<RequestHeaderWrapper> CONTEXT = new ThreadLocal<RequestHeaderWrapper>();

    public static RequestHeaderWrapper get() {
        return CONTEXT.get();
    }

    public static void set(RequestHeaderWrapper requestHeaderWrapper) {
        CONTEXT.set(requestHeaderWrapper);
    }

    public static void clear() {
        CONTEXT.remove();
    }
}


@RestController
public class TestController {
    private Logger log = LoggerFactory.getLogger(TestController.class);
    Executor executor = Executors.newFixedThreadPool(8);
    CopyOnWriteArrayList list = new CopyOnWriteArrayList();

    private final static String HEADER_TANANT = "TENANT";


    @GetMapping("/request")
    public String testRequestAttribute(HttpServletRequest request) {
        String tenant = request.getHeader(HEADER_TANANT);
        return tenant;
    }

    @GetMapping("/test")
    public void longTimeJob(HttpServletRequest request, @RequestHeader(HEADER_TANANT) String tenant) throws InterruptedException {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        executor.execute(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            for (int i = 0; i < 1000; i++) {
                HttpServletRequest request1 = ((ServletRequestAttributes) requestAttributes).getRequest();
                String uri = request1.getRequestURI();
                String tenantdiff = request1.getHeader(HEADER_TANANT);
                if (tenantdiff != null && !tenantdiff.equals(tenant)) {
                    log.info("{},uri:{},tenant:{}", i, uri, tenantdiff);
                    list.add(tenantdiff);
                }
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @GetMapping("/fix")
    public void fixLeakage(@RequestHeader(HEADER_TANANT) String tenant) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        executor.execute(() -> {
            RequestHeaderWrapperContext.set(RequestHeaderWrapper.wrap((ServletRequestAttributes) requestAttributes));
            for (int i = 0; i < 10000; i++) {
                String tenantdiff = RequestHeaderWrapperContext.get().getHeaders().get(HEADER_TANANT);
                if (tenantdiff != null && !tenantdiff.equals(tenant)) {
                    log.info("{},tenant:{}", i, tenantdiff);
                    list.add(tenantdiff);
                }
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}

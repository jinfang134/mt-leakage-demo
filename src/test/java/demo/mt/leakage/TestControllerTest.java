package demo.mt.leakage;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
//@WebMvcTest(TestController.class)
@AutoConfigureMockMvc
public class TestControllerTest {

    private final static Logger log = LoggerFactory.getLogger(TestControllerTest.class);

    @Autowired
    MockMvc mockMvc;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    ExecutorService executor = Executors.newFixedThreadPool(8);

    public HttpEntity<String> getHttpHeader(String tenant){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("TENANT",tenant);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        return entity;
    }

    public ResponseEntity<String> doGet(String url, String tenant){
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,getHttpHeader(tenant),String.class);
        return response;
    }

    @Test
    public void greetingShouldReturnDefaultMessage() throws Exception {
        String url="http://localhost:" + port + "/request";
        assertThat(doGet(url,"jill").getBody()).contains("jill");
    }


    @Test
    public void shouldFindLeakage() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        executor.submit(() -> {
            try {
                this.mockMvc.perform(
                        get("/test")
                                .header("TENANT", "jill"))
                        .andDo(print()).andExpect(status().isOk())
                        .andExpect(content().string(containsString("jill")));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }

        });

        executor.submit(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    this.mockMvc.perform(
                            get("/request")
                                    .header("TENANT", "tom"))
                            .andDo(print()).andExpect(status().isOk())
                            .andExpect(content().string(containsString("tom")));
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            countDownLatch.countDown();

        });
        countDownLatch.await();
        log.info(" test end !!!!");
    }

}
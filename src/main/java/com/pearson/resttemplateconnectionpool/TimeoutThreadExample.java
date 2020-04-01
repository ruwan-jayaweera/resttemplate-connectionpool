package com.pearson.resttemplateconnectionpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;

public class TimeoutThreadExample {
    private static final Logger log = LoggerFactory.getLogger(TimeoutThreadExample.class);

    private RestTemplate restTemplate = null;

    private ExecutorService executor = null;

    public TimeoutThreadExample(int poolSize, int readTime, int connectionTime) {
        log.info("ExecutorService pool size: "+poolSize);
        executor = Executors.newFixedThreadPool(poolSize);

        log.info("HttpComponentsClientHttpRequestFactory readTimeout: "+readTime);
        log.info("HttpComponentsClientHttpRequestFactory connectionTimeout: "+connectionTime);
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setReadTimeout(readTime);
        requestFactory.setConnectTimeout(connectionTime);
        restTemplate = new RestTemplate(requestFactory);
    }

    public String getData() {
        Future<String> future = executor.submit(new Task(restTemplate));
        String response = null;

        try {
            response = future.get(5000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return response;
    }
}

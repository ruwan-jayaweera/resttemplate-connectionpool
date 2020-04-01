package com.pearson.resttemplateconnectionpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Callable;

class Task implements Callable<String> {

    private static final Logger log = LoggerFactory.getLogger(Task.class);

    private RestTemplate restTemplate;

    public Task(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String call() throws Exception {

        String url = "https://gturnquist-quoters.cfapps.io/api/random";
        String response = restTemplate.getForObject(url, String.class);

        log.info("Respose: "+response);

        return response;
    }
}

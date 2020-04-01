package com.pearson.resttemplateconnectionpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(value = "/")
public class ConsumeController {

    private static final Logger log = LoggerFactory.getLogger(ConsumeController.class);

    @GetMapping("/startRestTemplate")
    public void consumeQuoteRest(@RequestParam(required = false) final String count,
                                 @RequestParam(required = false) final String poolSize,
                                 @RequestParam(required = false) final String readTimeout,
                                 @RequestParam(required = false) final String connectionTimeout) {

        Param param = new Param(count, poolSize, readTimeout, connectionTimeout).invoke();
        int threadPoolSize = param.getThreadPoolSize();
        int noOfRequests = param.getNoOfRequests();
        int readTime = param.getReadTime();
        int connectionTime = param.getConnectionTime();

        TimeoutThreadExample bc = new TimeoutThreadExample(threadPoolSize, readTime, connectionTime);

        long start = System.currentTimeMillis();
        for (int i = 1; i <= noOfRequests; i++) {
            bc.getData();
        }
        long end = System.currentTimeMillis();
        log.info(">>>>>>>>>>>>>> Total time taken to raise " + noOfRequests + " requests: " + (end - start));

    }

    @GetMapping("/startWebClient")
    public void consumeQuoteWeb(@RequestParam(required = false) final String count,
                                @RequestParam(required = false) final String poolSize,
                                @RequestParam(required = false) final String readTimeout,
                                @RequestParam(required = false) final String connectionTimeout) {

        Param param = new Param(count, poolSize, readTimeout, connectionTimeout).invoke();
        int threadPoolSize = param.getThreadPoolSize();
        int noOfRequests = param.getNoOfRequests();
        int readTime = param.getReadTime();
        int connectionTime = param.getConnectionTime();



        long start = System.currentTimeMillis();
        for (int i = 1; i <= noOfRequests; i++) {
            Flux<Quote> quoteFlux = WebClient.create()
                    .get()
                    .uri("https://gturnquist-quoters.cfapps.io/api/random")
                    .retrieve()
                    .bodyToFlux(Quote.class);

            quoteFlux.subscribe(quote -> log.info(quote.toString()));
        }
        long end = System.currentTimeMillis();
        log.info(">>>>>>>>>>>>>> Total time taken to raise " + noOfRequests + " requests: " + (end - start));

    }

    private class Param {
        private String count;
        private String poolSize;
        private String readTimeout;
        private String connectionTimeout;
        private int threadPoolSize;
        private int noOfRequests;
        private int readTime;
        private int connectionTime;

        public Param(String count, String poolSize, String readTimeout, String connectionTimeout) {
            this.count = count;
            this.poolSize = poolSize;
            this.readTimeout = readTimeout;
            this.connectionTimeout = connectionTimeout;
        }

        public int getThreadPoolSize() {
            return threadPoolSize;
        }

        public int getNoOfRequests() {
            return noOfRequests;
        }

        public int getReadTime() {
            return readTime;
        }

        public int getConnectionTime() {
            return connectionTime;
        }

        public Param invoke() {
            threadPoolSize = 0;
            if (poolSize == null) {
                threadPoolSize = 10;
            } else {
                threadPoolSize = Integer.parseInt(poolSize);
            }

            noOfRequests = 0;
            if (count == null) {
                noOfRequests = 10;
            } else {
                noOfRequests = Integer.parseInt(count);
            }

            readTime = 0;
            if (readTimeout == null) {
                readTime = 1000;
            } else {
                readTime = Integer.parseInt(readTimeout);
            }

            connectionTime = 0;
            if (connectionTimeout == null) {
                connectionTime = 1000;
            } else {
                connectionTime = Integer.parseInt(connectionTimeout);
            }
            return this;
        }
    }
}
package com.learn.LoadBalancerMicroService1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/*
@RibbonClient annotation is used to customize the Ribbon settings or if we are not using any service discovery then its used to specify the list of servers.
The name we define in the name field of the @RibbonClient annotation is used as a prefix of the ribbon configurations in "application.properties" and as "logical identifier" in the URL we pass to the RestTemplate. The configuration field is used to specify a configuration class that holds all our customizations as @Bean.

 */

@RestController
@RibbonClient(name = "microservice1")
public class MicroService1Controller {

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/getmicroservice2")
    public String getMicroService2Instance() {
        String url = "http://microservice1/microservice2/port";
        String port = "Current instance port::::::: " + restTemplate.getForObject(url, String.class);

        return port;
    }
}

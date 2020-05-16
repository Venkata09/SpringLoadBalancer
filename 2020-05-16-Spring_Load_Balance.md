---
layout: single
title: Load balancing in Spring Boot Microservices using Netflix’s Ribbon
date:   2020-05-16 23:31:02 +0200
categories: [spring]
tags: [Programming, JAVA, spring]
excerpt: Load balancing Spring Boot Microservices using Netflix’s Ribbon.
---



One of the most prominent reasons of evolution from **monolithic ==> microservices architecture** is horizontal scaling. It is required in modern day applications to improve user experience in the case of higher traffic for a particular service. We create multiple instances of the service in order to handle the large traffic of requests. But if the requests are not distributed among the instances effectively, then horizontal scaling is of no use.



Load balancing refers to efficiently distributing the incoming network traffic across a group of backend servers (multiple instances of the service).


Types of load-balancing

Load balancing can be of two types:

    Server-side Load Balancing

    Client-side Load Balancing

### 1. Server-side Load Balancing

In Server-side load balancing, the instances of the service are deployed on multiple servers and then a load balancer is put in front of them. It is generally a hardware load balancer. All the incoming requests traffic firstly comes to this load balancer acting as a middle component. It then decides to which server a particular request must be directed to based on some algorithm.

![Server-Side Load Balancing](../images/SpringLoadBalancer/ServerSideLoadBalancer.JPG)


Let's discuss about the disadvantages of Server-side load balancing



1) Server side load balancer acts as a single point of failure as if it fails, all the instances of the microservice becomes inaccessible as only load balancer has the list of servers.

1) Since each microservice will have a separate load balancer, the overall complexity of the system increases and it becomes hard to manage.

1) The network latency increases as the number of hops for the request increases from one to two with the load balancer, one to the load balancer and then another from load balancer to the microservice.


### 2. Client-side Load Balancing

The instances of the service are deployed on multiple servers. Load balancer's logic is part of the client itself, it holds the list of servers and decides to which server a particular request must be directed to based on some algorithm. These client side load balancers are also known as software load balancers.

![](../images/SpringLoadBalancer/ClientSideLoadBlancer.JPG)

Disadvantages of Client-side load balancing

1) The load balancer's logic is mixed up with the microservice code.

### What is Netflix's Ribbon

Netflix's Ribbon is an Inter Process Communication (remote procedure calls) library with built in client side(software) load balancer and is a part of Netflix Open Source Software (Netflix OSS).

Features of Ribbon:

1) Load balancing: It provides client side load balancing functionality.

1) Fault tolerance: It can be used to determine whether the servers are up or not and can also detect those servers that are down and hence, ignore them for sending the further requests.

1) Configurable load balancing rules: By default ribbon uses RoundRobinRule for distributing requests among servers. In addition to it, it also provides AvailabilityFilteringRule and WeightedResponseTimeRule. We can also define our custom rules as per our needs.

1) It supports multiple protocols like HTTP, TCP, UDP etc.


### Use Ribbon in Spring Microservices

We will create two microservices: Microservice-1 and Microservice-2. The Microservice2 will be having its two instances running and Microservice1 will call Microservice2.

Ribbon logic will be included in Microservice1 in order to load balance the requests among the two instances of Microservice2. We will be using spring tool suite for this example:

![](../images/SpringLoadBalancer/ClientSideLoadBlancerUsingRibbon.JPG)

Create spring-boot web application. 

```java

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/microservice2")
public class MicroService2Controller {

    @Value("${server.port}")
    private String port;

    @GetMapping("/port")
    public String getPort() {
        return port;
    }
}


```

Start the spring boot application on two different ports: **8083 & 8082**. 

-------------------
 
Let's create microservice-1 and add ribbon dependency. 

```xml


<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
</dependency>


```


```java


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



```


```java

@Configuration
public class MicroService1Config {


    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

}


```


```properties

server.port=8085
microservice1.ribbon.eureka.enabled = false
microservice1.ribbon.listOfServers = localhost:8083, localhost:8082


```


Start the above microservice in port **8085**. 


Now that all the services are up and running. Let's invoke the service-1 which is running on **8085**

http://localhost:8085/getmicroservice2

output:

Current instance port:::::::  8083
Current instance port:::::::  8082
Current instance port:::::::  8083
Current instance port:::::::  8082
Current instance port:::::::  8083
Current instance port:::::::  8082
Current instance port:::::::  8083
Current instance port:::::::  8082
 
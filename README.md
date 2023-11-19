# This is an example application to show howto use Spring Boot, Angular and Mongodb with the reactive Webflux features of Spring.

[![CodeQL](https://github.com/Angular2Guy/AngularAndSpring/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/Angular2Guy/AngularAndSpring/actions/workflows/codeql-analysis.yml)

Author: Sven Loesekann

Technologies: Angular, Angular-Cli, Angular-Material, Typescript, Spring Boot, Spring Webflux, Spring Security,  MongoDB, Maven, Docker, ArchUnit, Kafka, Kafka-Streams, Spring Actuator with Prometheus interface

## Articles
* [Spring Boot 3 update experience](https://angular2guy.wordpress.com/2022/11/15/spring-boot-3-update-experience/)
* [Reactive Kafka with Streaming in Spring Boot Part 1](https://angular2guy.wordpress.com/2022/05/23/reactive-kafka-with-streaming-in-spring-boot-part-1/)
* [Reactive Kafka with Streaming in Spring Boot Part 2](https://angular2guy.wordpress.com/2022/06/09/reactive-kafka-with-streaming-in-spring-boot-part-2/)
* [Reactive Kafka with Streaming in Spring Boot Part 3](https://angular2guy.wordpress.com/2022/06/10/reactive-kafka-with-streaming-in-spring-boot-part-3/)
* [Performance improvement for Getter/Setter access with LambdaMetafactory](https://angular2guy.wordpress.com/2022/05/12/angularandspring-uses-lambdametafactory-for-getter-setter-access/)
* [Spring Boot/MongoDb Performance Analysis and Improvements](https://angular2guy.wordpress.com/2022/02/15/spring-boot-mongodb-performance-analysis-and-improvements/)
* [Ngx-Simple-Charts multiline and legend support howto](https://angular2guy.wordpress.com/2021/10/02/ngx-simple-charts-multiline-and-legend-support-howto/)
* [Deep Links With Angular Routing and i18n in Prod Mode](https://angular2guy.wordpress.com/2021/07/31/deep-links-with-angular-routing-and-i18n-in-prod-mode/)
* [Developing and Using Angular Libraries](https://angular2guy.wordpress.com/2021/07/31/developing-and-using-angular-libraries/)
* [How to Modularize an Angular Application](https://angular2guy.wordpress.com/2022/04/16/how-to-modularize-an-angular-application/)
* [Deployment Setup for Spring Boot Apps With MongoDB and Kubernetes](https://dzone.com/articles/a-developmentdeployment-setup-for-an-angular-sprin)
* [Using Angular and Reactive Spring With JWT Tokens](https://dzone.com/articles/angular-and-reactive-spring-with-jwt-tokens)
* [Angular and Spring Webflux](https://dzone.com/articles/angular-and-spring-webflux)

## What is the goal?

The goal is to be reactive from top to bottom. To do that the project uses Angular in the frontend and Spring Boot with Reactive Web as server. Mongodb is the database connected with the reactive MongoDB driver. That enables a reactive chain from the browser to the DB. The security is done with Jwt Tokens and the logged out tokens are invalidated. The project uses an in memory MongoDB to be just cloned build and ready to run. It serves as an example for clean architecture. The architecture is checked with ArchUnit in a test. The health and performance of the application can be monitored with Spring Actuator with Prometheus interface. With the 'kafka' and 'prod' profiles the Kafka support can be used for Jwt token revokation(Minikube setups(development/system) available). 

## What is it?

The application runs a scheduled task reads the exchange rates of cryptocurrencies and stores them in the Mongodb. The UI uses the rest service to read the rates and displays them on a table. The table updates itself regularly and shows out of date data in blue. A detail page shows the data of the currency and a chart of the rates of the current day, 7 days, 30 days, 90 days. 
If the user logs in the user can see the relevant part of the orderbooks for an order. The orderbooks route is implemented as a lazy loading feature module. The route guard checks for the Jwt token and the logout invalidates the Jwt token. 

## C4 Architecture Diagrams
The project has a [System Context Diagram](structurizr/diagrams/structurizr-1-SystemContext.svg), a [Container Diagram](structurizr/diagrams/structurizr-1-Containers.svg) and a [Component Diagram](structurizr/diagrams/structurizr-1-Components.svg). The Diagrams have been created with Structurizr. The file runStructurizr.sh contains the commands to use Structurizr and the directory structurizr contains the dsl file.

## Data Import and Preparation

The application has two scheduled jobs. The first is the ScheduledTask class. It reads the rates of the crypto currencies once a minute with different initial delays. That job provides one mongodb collection per exchange. The collections can have different documents with currency pairs like Usd to BitCoin or Eur to Ether or one document with all currency pairs, depends on what the exchanges provide. These collections provide the data for the current day chart and the current quote. To display the 7 day, 30 day, 90 day charts, hourly or daily quotes are required. Once a day the PrepareData class runs jobs to calculate the hourly and daily quotes. The jobs run between 0 and 4 o’clock. If no values are available the for the timeframe(hour, day) a value of zero is shown. For the 7 day chart the hourly data is used and for the 30 and 90 day charts the daily data is used. The Schedulers class provides a an elastic bounded scheduler with enough threads for each client(connection issues) of the ScheduledTask class for the quote imports. The aggregation jobs are run asynchronous(as @Async method) on application startup(@EventListener(ApplicationReadyEvent.class)) and the scheduled runs (@Scheduled(cron=...)) to do the calculation outside of the reactor event loop. The aggregation jobs are started only once(@SchedulerLock) in intervals with @Scheduled to separate them and to reduce the database load. 

## Minikube setup

The application can now be run in a Minikube cluster with a Helm chart. The setup has a persistent volume to store the files of mongodb. A setup of mongodb with the volume and a setup for the application. It can be found in the minikube directory as a Helm chart. It uses the resource limit support of Jdk 16+ to limit memory. Kubernetes limits the cpu use and uses the startupprobes and livenessprobes that Spring Actuator provides. A Helm chart for the Kafka development setup in Minikube can be found in the directory 'minikube/kafka'. A Helm chart for the deployment of Kafka/Zookeeper/AngularAndSpring/MongoDb system setup can be found in the directory 'minikube/angularandspringwithkafka'. Further documentation can be found in the [Blog](https://angular2guy.wordpress.com) articles. 

## Monitoring
The Spring Actuator interface with Prometheus interface can be used as it is described in this article: 

[Monitoring Spring Boot with Prometheus and Grafana](https://ordina-jworks.github.io/monitoring/2020/11/16/monitoring-spring-prometheus-grafana.html)

To test the setup the application has to be started and the Docker Images for Prometheus and Grafana have to be started and configured. The scripts 'runGraphana.sh' and 'runPrometheus.sh' can be used as a starting point.
The Spring Actuator configuration shows primarily the http performance and the Gc pauses. More metrics can be enabled in the application.properties file.

## Jvm Memory management

The memory state and other values of the Jvm can be watched with the jstat tool that is included in the jdk. To watch the memory of a running Jvm this command can be used:

jstat -gcutil -h 10 insert_process_id 1000

## Setup

MongoDB 4.4.x or newer.

Eclipse IDE for Enterprise Java and Web Developers newest version.

Java 21 or newer

Maven 3.9.5 or newer

Nodejs 18.13.x or newer

Npm 8.19.x or newer

Angular Cli 17 or newer.

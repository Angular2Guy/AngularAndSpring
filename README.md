# This is an example application to show howto use Spring Boot, Angular and Mongodb with the Webflux features of Spring.

<!-- ![Build Status](https://travis-ci.org/Angular2Guy/AngularAndSpring.svg?branch=master) -->

Author: Sven Loesekann

Technologies: Angular, Angular-Cli, Angular-Material, Typescript, Spring Boot, Spring Webflux, MongoDB, Maven, Docker, ArchUnit

## Articles
* [Deep Links With Angular Routing and i18n in Prod Mode](https://angular2guy.wordpress.com/2021/07/31/deep-links-with-angular-routing-and-i18n-in-prod-mode/)
* [Developing and Using Angular Libraries](https://dzone.com/articles/developing-and-using-angular-libraries)
* [How to Modularize an Angular Application](https://dzone.com/articles/howto-modularize-an-angular-application-by-example)
* [Deployment Setup for Spring Boot Apps With MongoDB and Kubernetes](https://dzone.com/articles/a-developmentdeployment-setup-for-an-angular-sprin)
* [Using Angular and Reactive Spring With JWT Tokens](https://dzone.com/articles/angular-and-reactive-spring-with-jwt-tokens)
* [Angular and Spring Webflux](https://dzone.com/articles/angular-and-spring-webflux)

## What is the goal?

The goal is to be reactive from top to bottom. To do that the project uses Angular in the frontend and Spring Boot with Reactive Web as server. Mongodb is the database connected with the reactive MongoDB driver. That enables a reactive chain from the browser to the DB. The project uses an in memory MongoDB to be just cloned build and ready to run. It serves as an example for clean architecture. The architecture is checked with ArchUnit in a test.

## What is it?

The application runs a scheduled task reads the exchange rates of cryptocurrencies and stores them in the Mongodb. The UI uses the rest service to read the rates and displays them on a table. The table updates itself regularly. A detail page shows the data of the currency and a chart of the rates of the current day, 7 days, 30 days, 90 days. 
If the user logs in the user can see the relevant part of the orderbooks for an order. The orderbooks route is implemented as a lazy loading feature module.

## Data Import and Preparation

The application has two scheduled jobs. The first is the ScheduledTask class. It reads the rates of the crypto currencies once a minute with different initial delays. That job provides one mongodb collection per exchange. The collections can have different documents with currency pairs like Usd to BitCoin or Eur to Ether or one document with all currency pairs, depends on what the exchanges provide. These collections provide the data for the current day chart and the current quote. To display the 7 day, 30 day, 90 day charts, hourly or daily quotes are required. Once a day the PrepareData class runs jobs to calculate the hourly and daily quotes. The jobs run between 0 and 2 o’clock. If no values are available the for the timeframe(hour, day) a value of zero is shown. For the 7 day chart the hourly data is used and for the 30 and 90 day charts the daily data is used. The SchedulingConfig class provides a config that provides the scheduler with 5 threads to enable the running of ScheduledTask class for the imports and the PrepareData class for aggregation concurrently. 

## Minikube setup

The application can now be run in a Minikube cluster. The setup has a persistent volume to store the files of mongodb. A setup of mongodb with the volume and a setup for the application. It can be found in the minikube directory. Further documentation can be found in the wiki.

## Setup

MongoDB 3.4.x or newer.

Eclipse 2021-03 JEE or newer.

Install Eclipse Plugin 'Eclipse Wild Web Developer' of the Eclipse Marketplace.(free)

Install Eclipse Plugin 'Java 16 Support for Eclipse 2021-03' of the Eclipse Marktplace.(free)

Java 16 or newer

Nodejs 14.15.x or newer

Npm 6.14.x or newer

Angular Cli 12 or newer.

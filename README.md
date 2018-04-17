# This is an example application to show howto use Spring Boot, Angular and Mongodb with the Webflux features of Spring.

![Build Status](https://travis-ci.org/Angular2Guy/AngularAndSpring.svg?branch=master)

Author: Sven Loesekann

Technologies: Angular, Angular-Cli, Angular-Material, Typescript, Spring Boot, Spring Webflux, MongoDB, Maven, Docker

## What is the goal?

The goal is to be reactive from top to bottom. To do that the project uses Angular in the frontend and Spring Boot with Reactive Web as server. Mongodb is the database connected with the reactive MongoDB driver. That enables a reactive chain from the browser to the DB. 

## What is it?

The application runs a scheduled task reads the exchange rates of cryptocurrencies and stores them in the Mongodb. The UI uses the rest service to read the rates and displays them on a table. The table updates itself regularly. A detail page shows the data of the currency and a chart of the rates of the current day, 7 days, 30 days, 90 days. 
If the user logs in the user can see the relevant part of the orderbooks for an order.

## Data Import and Preparation

The application has two scheduled jobs. The first is the ScheduledTask class. It reads the rates of the crypto currencies once a minute with different initial delays. That job provides one mongodb collection per exchange. The collections can have different documents with currency pairs like Usd to BitCoin or Eur to Ether or one document with all currency pairs, depends on what the exchanges provide. These collections provide the data for the current day chart and the current quote. To display the 7 day, 30 day, 90 day charts, hourly or daily quotes are required. Once a day the PrepareData class runs jobs to calculate the hourly and daily quotes. The jobs run between 0 and 2 o’clock. If no values are available the for the timeframe(hour, day) a value of zero is shown. For the 7 day chart the hourly data is used and for the 30 and 90 day charts the daily data is used. The SchedulingConfig class provides a config that provides the scheduler with 5 threads to enable the running of ScheduledTask class for the imports and the PrepareData class for aggregation concurrently. 

## Setup

MongoDB 3.4.x or newer.

Eclipse Oxygen JEE or newer.

Plugin Typescript.Java 1.4.0 or newer.

Maven 3.3.3 or newer.

Nodejs 6.9.x or newer

Npm 3.10.x or newer

Angular Cli 1.4.0 or newer.

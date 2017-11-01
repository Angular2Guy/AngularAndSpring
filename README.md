# This is an example application to show howto use Spring Boot, Angular and Mongodb with the reactive features of Spring.

Author: Sven Loesekann

Technologies: Angular, Angular-Cli, Angular-Material, Typescript, Spring Boot, MongoDB, Maven, Docker

## What is the goal?

The goal is to be reactive from top to bottom. To do that the project uses Angular in the frontend and Spring Boot with Reactive Web as server. Mongodb is the database connected with the reactive MongoDB driver. That enables a reactive chain from the browser to the DB. 

## What is it?

The application runs a scheduled task reads the exchange rates of cryptocurrencies and stores them in the Mongodb. The UI uses the rest service to read the rates and displays them on a table. The table updates itself regularly. A detail page shows the data of the currency and a chart of the rates of the current day. 
If the user logs in the user can see the relevant part of the orderbooks for an order.

## Setup

Eclipse Oxygen JEE or newer.

Plugin Typescript.Java 1.4.0 or newer.

Maven 3.3.3 or newer.

Nodejs 6.9.x or newer

Npm 3.10.x or newer

Angular Cli 1.4.0 or newer.


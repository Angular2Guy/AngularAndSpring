workspace "AngularAndSpring" "This is a project to show crypto currency values and statistics. It imports the quotes from the exchanges and enables requesting the current order book." {

    model {
        user = person "User"
        angularAndSpringSystem = softwareSystem "AngularAndSpring System" "System of multiple applications" {
        	kafka = container "Kafka Event System(Optional)" "Kafka provides the events between multiple deployed AngularAndSpring applications."
        	angularAndSpring = container "AngularAndSpring" "Multiple instances possible. Angular Frontend and Spring Boot Backend integrated." {
        	   angularFrontend = component "Angular Frontend" "The SPA shows the quotes / charts / statistics / order books" tag "Browser"
        	   backendCron = component "Scheduler" "Start the scheduled jobs." tag "Scheduler"
        	   backendQuoteClients = component "Rest Clients" "The rest clients request the quotes from the exchanges."
        	   backendPrepareDataJob = component "Prepare Data Job" "Aggrigates the quotes to hour / day values."
        	   backendJwtTokenFilters = component "Jwt Token Filters" "Provide the security based on Jwt Tokens."
        	   backendExchangeControllers = component "Exchange Controllers" "Provide the rest interface for the quote / order book requests"
        	   backendUserController = component "User Controller" "Provides the rest interface for Login/Signin/Logout of the users."
        	   backendStatisticsController = component "Statistics Controller" "Provides the interface for the statistics requests."
        	   backendKafkaConsumer = component "Kafka Consumer" "Consume the Kafka events." tag "Consumer"
        	   backendKafkaProducer = component "Kafka Producer" "Produce the Kafka events."
        	   backendRepository = component "Repository" "MongoDb repository to read / write data"
        	   backendEventMapper = component "Event Mapper" "Map the Kafka Events to Entities / Dtos."
        	   backendExchangeServices = component "Exchange Services" "Services implementing the exchange logic."
        	   backendUserService = component "User Service" "Service implementing the user / Kafka logic. "
        	   backendStatisticsService = component "Statistics Service" "Service implementing the statistics logic."        	   
        	}
        	database = container "MongoDb" "MongoDb stores all the data of the system." tag "Database"
        }
        cryptoCurrencyExchanges = softwareSystem "Crypto Currency Exchanges" "Multiple Exchanges"

		# relationships people / software systems
        user -> angularAndSpringSystem "views quotes / charts / statistics / order book"
        angularAndSpringSystem -> cryptoCurrencyExchanges "import quotes / request order book"
        
        # relationships containers
        user -> angularAndSpring "views quotes / charts / statistics / order book"
        angularAndSpring -> cryptoCurrencyExchanges 
        angularAndSpring -> kafka
        kafka -> angularAndSpring
        angularAndSpring -> database     
        
        # relationships components
        angularFrontend -> backendExchangeControllers        
        angularFrontend -> backendUserController
        angularFrontend -> backendStatisticsController
        backendCron -> backendPrepareDataJob
        backendCron -> backendQuoteClients
        backendPrepareDataJob -> backendExchangeServices
        backendQuoteClients -> backendExchangeServices
        backendExchangeControllers -> backendJwtTokenFilters
        backendUserController -> backendJwtTokenFilters
        backendStatisticsController -> backendJwtTokenFilters
        backendExchangeControllers -> backendExchangeServices
        backendUserController -> backendUserService
        backendStatisticsController -> backendStatisticsService
        backendKafkaProducer -> backendEventMapper
        backendKafkaConsumer -> backendEventMapper
        backendKafkaConsumer -> backendUserService 
        backendUserService -> backendKafkaProducer
        backendExchangeServices -> backendRepository
        backendUserService -> backendRepository
        backendStatisticsService -> backendRepository
    }

    views {
        systemContext angularAndSpringSystem "SystemContext" {
            include *
            autoLayout
        }
        
        container angularAndSpringSystem "Containers" {
        	include *
            autoLayout lr
        }
        
        component angularAndSpring "Components" {
        	include *
            autoLayout
        }               
        
    	styles {
        	element "Person" {            
            	shape Person
        	}        	
        	element "Database" {
                shape Cylinder                
            }
            element "Browser" {
                shape WebBrowser
            }
            element "Scheduler" {
            	shape Circle
            }
            element "Consumer" {
            	shape Pipe
            }
    	}
    }

}
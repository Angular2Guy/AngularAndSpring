workspace "AngularAndSpring" "This is a project to show crypto currency values and statistics. It imports the quotes from the exchanges and enables requesting the current order book." {

    model {
        user = person "User"
        angularAndSpringSystem = softwareSystem "AngularAndSpring System" "System of multiple applications" {
        	kafka = container "Kafka Event System(Optional)" "Kafka provides the events between multiple deployed AngularAndSpring applications."
        	angularAndSpring = container "AngularAndSpring" "Multiple instances possible. Angular Frontend and Spring Boot Backend integrated."
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
        
    	styles {
        	element "Person" {            
            	shape Person
        	}
        	
        	element "Database" {
                shape Cylinder                
            }
    	}
    }

}
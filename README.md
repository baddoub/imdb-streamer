# Introduction 

This is an attempt to resolve the G42 test interview.

### Explanation of some technical choices : 

On start, we feed the data base with csv data using [alpakka](https://doc.akka.io/docs/alpakka/current/), which based on Akka Streams. 

The database is a basic in-memory relational database on postgresSql mode. 

As you may notice, I have tried to implement the hexagonal architecture in this project, with different modules and good separation of concerns.
  
For unit testing, I have used the random generator [RandomDataGenerator] (https://github.com/DanielaSfregola/random-data-generator) in order to generate testing data.

For API documentation, I used tapir which is a library for describing HTTP endpoints in a type-safe way.

### Prerequisites
#### For back-end : 
* Install the building tool sbt. 
`brew install sbt`

#### For front-end :
* Install npm 
`brew install npm`
* Install angular cli
`npm install -g @angular/cli`
### Launch
#### For back-end :
`sbt infra/run`
#### For front-end :
`cd web`  
`ng serve`

Open your browser on http://localhost:4200/. 

#### back-end Tests
`sbt infra/test`

#### Enhancement :
* Create an angular component for the US map.
* add tests for the front-end part.
* Fix the todos ^^

### Conclusion

* Playing with D3.js was fun and painful in the same time. If I had to retake the test, I will choose an easier js library ^^.

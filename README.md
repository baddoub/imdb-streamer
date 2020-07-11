# imdb-streamer

This is an answer for the test recrutement of the imdb stream problem.

# Introduction 

### Explanation of some technical choices : 
On start, we feed database with csv data using [alpakka](https://doc.akka.io/docs/alpakka/current/). (First use of this library and i really like it).

The database is a basic in-memory relational database on postgresSql mode. I have used Doobie (typelevel) as a jdbc framework for fun since i don't have the chance to use it at work. 

As you will observe, I have tried to implement the hexagonal architecture in this project. A must-have for all web development projects.
  
For unit testing, I have used the random generator [RandomDataGenerator] (https://github.com/DanielaSfregola/random-data-generator) in order to generate testing data.

### Launch
  
Since the csv files were included on the ressources folder, for now we can not launch the program with sbt command.

Nevertheless, you can launch the program within your preferred IDE by triggering the method main on the ServerLauncher class.

### Disclaimer
For some reasons, the name.basics.csv does not store all people existing on other files. So I did a little hack to avoid database integrity problems.
As a consequence, principals with unknown person_id are not stored on database and will not be loaded on principalsForMovieName method.
#### Enhancement : 
* enhance error handling on orphan ids
* add http server to expose some routes (play, akka-http ...)
* add Tapir to generate openApi specs for new route
* add property based tests with scalacheck




### Conclusion

This test was so much fun. Hope to hear some feedbacks on this code base.

# imdb-streamer

This is an answer for the test recrutement of the imdb stream problem.

# Introduction 

### Explanation of some technical choices : 
On start, we feed database with csv data using [alpakka](https://doc.akka.io/docs/alpakka/current/). (First use of this library and i really like it).

The database is a basic in-memory relational database on postgresSql mode. I have used Doobie as a jdbc framework for 

fun since i don't have the chance to use it at work. 

As you will observe, i have tried to implement the hexagonal architecture in this project. A must have for all web developement projects.
  

### Launch
  
Since my csv files are stored on resources folder, for now we can not launch the program with sbt command.

Nevertheless, you can launch the program within your prefered IDE by triggring the method main on the ServerLauncher class.


#### Enhancement : 
* enhance error handling on orphan ids
* add http server to expose some routes (play, akka-http ...)
* add Tapir to generate openApi specs for new route
* add property based tests with scalacheck




### Conclusion

This test was so much fun. Hope to hear some feedbacks on this code base.

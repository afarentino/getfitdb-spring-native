# GetFitDB-Spring-Native

GetFitDB-Spring-Native adds database support to the
core libraries found in the GetFit-Spring-Native project.

### Technologies Used
* Spring Native
* Spring Boot
* Spring Web
* SQLite
* Gradle 7.x
* JUnit 5
* Java OpenJDK 17
* GraalVM CE with Native Image Support

### Application Background

Given a CSVFile the records found in it will be imported into a
database.  Native database tooling can be used to view the 
tables.  A Tiny web application is provided to generate a page 
showing the contents of the database.

### Usage

#### Import a CSV ####
```getfitdb --inFile fileName.csv```


### Building
Download the source code of this project and follow the
documentation over
in the [HELP.md](HELP.md) file.

### License
* [MIT License](LICENSE.txt)
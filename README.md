# GetFitDB-Spring-Native

GetFitDB-Spring-Native adds database support to the
core libraries found in the [GetFit-Spring-Native](https://github.com/afarentino/getfit-spring-native) 
project.

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

Given a CSV File as input the application will create a new table
called records and insert the rows found.  If the records tables
exists the new rows only found in the csv file are added.

If no --inFile argument is specified, the application starts
in server mode firing up an embedded tomcated web container to
display a page containing the contents of the underling database.

Native SQLite database tooling can be used to view the 
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
# Data Set View

The Data Set View component is a web application that serves as the browse interface for context products. The software is packaged in a WAR file.

The software can be compiled with the `mvn compile` command but in order to create the WAR file, you must execute the `mvn compile war:war` command. 

The documentation including release notes, installation, and operation of the software should accessible after you can execute the `mvn site:run` command and visiting http://localhost:8080.

In order to create a complete package for distribution, execute the following commands: 
```console
% mvn site
% mvn package
```

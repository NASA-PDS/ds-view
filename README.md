# Data Set View

The Data Set View component is a web application that serves as the browse interface for context products. The software is packaged in a WAR file.

To get the `.war` file, run `mvn package`, which will generate it as well as the distributable artifacts.

The documentation including release notes, installation, and operation of the software should accessible after you can execute the `mvn site:run` command and visiting http://localhost:8080.

In order to create a complete package for distribution, execute the following commands: 
```console
% mvn site
% mvn package
```


## Docker

See https://github.com/NASA-PDS/ds-view/tree/main/docker/README.md


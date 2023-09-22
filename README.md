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

As with most `.war` files, you install it into an application server like [Apache Tomcat](https://tomcat.apache.org/). But it requires the server to include many other special configurations and other files. To simplify things, you can make a Docker image out of this that includes the the `.war` file, the static assets, the various Tomcat configurations, and so forth.

First, gather the HTML assets (which are currently private, so you'll need permssion) by visiting https://github.com/NASA-PDS/portal-legacy and from the "Code" button choose "Download ZIP". Note that this is a huge download (as of this writing, it's 800MB and unpacked 1.8GB), so be patient. Unpack the archive and move it into the same directory as this file and the `Dockerfile`.

    docker image build --tag ds-view .

You can then start it with

    docker container run --rm --publish 8080:8080 ds-view

and point your browser at http://localhost:8080/


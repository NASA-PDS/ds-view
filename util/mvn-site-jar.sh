#!/bin/sh
# Copyright 2010-2012, by the California Institute of Technology. 
# ALL RIGHTS RESERVED. United States Government sponsorship acknowledged. 
# Any commercial use must be negotiated with the Office of Technology Transfer 
# at the California Institute of Technology. 
#
# This software is subject to U. S. export control laws and regulations 
# (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
# is subject to U.S. export control laws and regulations, the recipient has 
# the responsibility to obtain export licenses or other export authority as 
# may be required before exporting such information to foreign countries or 
# providing access to foreign nationals.
#
# $Id$

# This script traverses the module directories to build and create JAR 
# files for the associated sites.

cd ..
mvn --file pom-en.xml clean

# Install the dependent JARs locally.
cd preparation
mvn --non-recursive install clean
cd core
mvn install clean
cd ../..

cd registry
mvn --non-recursive install clean
cd registry-core
mvn install clean
cd ../..

cd report
mvn --non-recursive install clean
cd rs-update
mvn install clean
cd ../..

cd search
mvn --non-recursive install clean
cd search-core
mvn install clean
cd ../..

# Build and create a JAR of each site (recursive).
mvn --file pom-en.xml site:jar

:: Copyright 2010-2011, by the California Institute of Technology.
:: ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
:: Any commercial use must be negotiated with the Office of Technology Transfer
:: at the California Institute of Technology.
::
:: This software is subject to U. S. export control laws and regulations
:: (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
:: is subject to U.S. export control laws and regulations, the recipient has
:: the responsibility to obtain export licenses or other export authority as
:: may be required before exporting such information to foreign countries or
:: providing access to foreign nationals.
::
:: $Id$

:: Batch file that registers the list of supported objects types 
:: with the Registry Service specified by the REGISTRY_SERVICE variable.
:: Upon successful completion of this script, go to 
:: http://%REGISTRY_SERVICE%/registry/report 
:: to verify that all classification nodes and schemes were loaded.

set REGISTRY_SERVICE=http://localhost:8080/registry-service

:: Load Object Type Classification Scheme
curl -X POST -H "Content-type:application/xml" -v -d @../conf/registryObjectTypeScheme.xml "%REGISTRY_SERVICE%/registry/configure?name=Core+Objects&description=This+configures+the+core+set+of+registry+objects"

:: Load PDS Object Type Classification Nodes
curl -X POST -H "Content-type:application/xml" -v -d @../conf/PDSObjectTypes.xml "%REGISTRY_SERVICE%/registry/configure?name=PDS+Objects&description=This+configures+PDS+object+types"

# Data Set Viewer
# ===============
#
# This Dockerfile defines how to make an image for the "ds-view" component of
# the Planetary Data System's Engineering Node.
#
# Copyright © 2023, California Institute of Technology ("Caltech").
# U.S. Government sponsorship acknowledged.
#
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# • Redistributions of source code must retain the above copyright notice,
#   this list of conditions and the following disclaimer.
# • Redistributions must reproduce the above copyright notice, this list of
#   conditions and the following disclaimer in the documentation and/or other
#   materials provided with the distribution.
# • Neither the name of Caltech nor its operating division, the Jet Propulsion
#   Laboratory, nor the names of its contributors may be used to endorse or
#   promote products derived from this software without specific prior written
#   permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.


# Base Image
# ---------
#
# Newer Tomcat 10 complains about class not found, servlet exception, and issues with JSTL,
# but going back to Tomcat 8 works.
#
# We should try to stay on the leading edge, though, for security reasons.
#
# FROM tomcat:10.1.13-jdk21-openjdk

FROM tomcat:8.5.93


# Assets
# ------
#
# The ds-view.war is built locally, but you'll need to copy over `search-ui.war` from a built repo
# clone for now. Officially we should pull it from the binaries attached to a release, maybe even
# make a build-time `ARG` to specify it.

COPY ../target/ds-view.war /usr/local/ds-view/ds-view.war
COPY ../search-ui.war /usr/local/search-ui/search-ui.war

# A couple of these context configurations are turned "off" (by renaming `.xml` → `.off`) until we
# can devote more time to figuring them out

COPY ../etc/*.xml conf/Catalina/localhost/

# The rest of these assets should be downloaded from https://github.com/NASA-PDS/portal-legacy; see
# the `README.md` for more details.

COPY ../portal-legacy-main/include/ /data/www/pds/htdocs/include/
COPY ../portal-legacy-main/css/ /data/www/pds/htdocs/css/
COPY ../portal-legacy-main/js/ /data/www/pds/htdocs/js/
COPY ../portal-legacy-main/images/ /data/www/pds/htdocs/images/
COPY ../portal-legacy-main/scripts/ /data/www/pds/htdocs/scripts/

FROM alpine

#adding SAP certificates
ADD http://aia.pki.co.sap.com/aia/SAP%20Global%20Root%20CA.crt \
    http://aia.pki.co.sap.com/aia/SAPNetCA_G2.crt \
    /usr/local/share/ca-certificates/

ARG USER_HOME_DIR="/root"
ARG ANT_VERSION="1.9.8"

WORKDIR $USER_HOME_DIR

ENV PATH "$PATH:$USER_HOME_DIR/cov-analysis-linux64/bin"

COPY entrypoint.sh /entrypoint.sh
COPY coverityreport.json /coverityreport.json

#dl java, git and update cerificates
RUN apk update \
&& apk upgrade \
&& apk add --no-cache bash \
&& apk add --no-cache --virtual=build-dependencies unzip \
&& apk add --no-cache curl \
&& apk add --no-cache git \
&& update-ca-certificates \
#dl java
&& apk add --no-cache openjdk11-jre \
&& apk add --no-cache maven \
#dl ant
#Create Ant Dir
&& mkdir -p /opt/ant/ \
#Download And 1.9.8
&& wget http://archive.apache.org/dist/ant/binaries/apache-ant-${ANT_VERSION}-bin.tar.gz -P /opt/ant \
&& tar -xvzf /opt/ant/apache-ant-1.9.8-bin.tar.gz -C /opt/ant/ \
&& rm -f /opt/ant/apache-ant-1.9.8-bin.tar.gz \
#Drop Sonarqube lib
#&& wget http://downloads.sonarsource.com/plugins/org/codehaus/sonar-plugins/sonar-ant-task/2.3/sonar-ant-task-2.3.jar -P /opt/ant/apache-ant-1.9.8/lib/
#dl gcc
&& apk add --no-cache gcc \
#not sure bout that
&& apk add --no-cache make \
&& apk add --no-cache cmake \
&& chmod +x /entrypoint.sh

VOLUME "$USER_HOME_DIR/.m2"
CMD /entrypoint.sh
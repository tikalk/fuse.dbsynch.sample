#!/bin/bash

#  startup scripts for server
#  must execute it from the home folder, e.g server_home$> ./bin/server.sh
#


#the default argument is the name of a properties file that contains jvm arguments,it is parsed
#by setenv and sent to the jvm, configure jvm arguments there.

source ./bin/setenv.sh default


echo 'program arguments:'
echo "$@" 

exec "$JAVA" $JAVA_OPTS -classpath "$CLASSPATH" com.tikal.dbsynch.server.ServerLauncher classpath:/spring/spring-config-main.xml "$@"

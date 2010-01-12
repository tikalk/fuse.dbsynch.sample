#!/bin/sh

# must execute this from the application's home folder, e.g home$>./bin/collector.sh
collector_home=$(cd `dirname $0`/..; pwd)
collector_conf=${collector_home}/conf


if [ -z "$1" ] ; then
	export collector_home
	return
fi

# ------------------------------------------------------------------
# set the properties in $collector_conf/$1.properties as system properties
# the sed command will replace the placeholder in the properties file ${collector_home} with the value of
# collector_home, and the property collector.home is then injected to the jvm as -D property.
# the second sed command will remove comment lines starting with #.
# the last sed command removes empty lines.
# ------------------------------------------------------------------
JAVAOPTS=`cat ${collector_conf}/$1.properties | sed s:'${collector_home}':${collector_home}:g  | sed '/^\#/d' | sed '/^$/d'`



for i in ${JAVAOPTS}
do
    # if the file content is empty, then it will return the input string
    echo $i | grep '=' > /dev/null
    has_eq=$?
    echo $i | grep -Ee '^-XX:' > /dev/null
    has_xx=$?
    #ignore comment lines,they are removed
    #echo $i | grep -Ee '^#' > /dev/null
    #has_comment=$?
    # ignore the comment lines
    #if [ $has_comment -eq 0 ]; then
    if [ $has_eq -eq 0 ] && [ $has_xx -ne 0  ]; then
       #echo "adding system property -D$i"
       JAVA_OPTS="$JAVA_OPTS -D$i"
    else
       #echo "adding JVM argument $i"
       JAVA_OPTS="$JAVA_OPTS $i"
    fi
   #fi
done



# ------------------------------------------------------------------
# set the debug options
# ------------------------------------------------------------------
if [ -n "${XPR_DEBUG_PORT}" ]; then
    JAVA_OPTS="${JAVA_OPTS} -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=${XPR_DEBUG_PORT},suspend=${SUSPEND_UNTIL_DEBUG_ATTACHE} "
fi


#JAVA=${JAVA_HOME}/bin/java
#todo: see how to find java home
JAVA="java"

#add the conf folder to the classpath
CLASSPATH=${collector_conf}
for f in $collector_home/lib/*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done


echo Starting application with "$1" properties 
echo '----------------------------------------'
echo collector_home=$collector_home
echo '----------------------------------------'
echo JAVA_OPTS=$JAVA_OPTS
echo '----------------------------------------'
echo JAVA=$JAVA
echo '----------------------------------------'
echo Java CLASSPATH=$CLASSPATH
echo '----------------------------------------'
echo '----------------------------------------'
echo '----------------------------------------'


export JAVA_OPTS
export CLASSPATH
export JAVA



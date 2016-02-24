#!/bin/sh

JAVA_HOME=/usr/local/jrmc3

APP_HOME=$(dirname $(cd "$(dirname "$0")"; pwd))

APP_NAME=router-gateway-secondary

. /lib/lsb/init-functions

_jar=`ls ${APP_HOME}/lib/*.jar`
_classpath=`echo ${_jar} | sed -e 's/ /:/g'`

CLASSPATH=/cluster/config/saas_lite:/cluster/config/nodes/${NODE_NAME}:$APP_HOME/conf:${_classpath}

JAVA_OPTS="-Dfile.encoding=utf-8 -server -Xms512m -Xmx512m -Xmanagement:ssl=false,authenticate=false,autodiscovery=false,port=7091"

APP_OPTS="-n $2 -l $3 -m $4"

MAIN_CLASS="com.babeeta.butterfly.application.router.gateway.secondary.GatewaySecondaryRouterServer"

PID=/var/run/${APP_NAME}/$2

touch $PID

LOG=/var/log/${APP_NAME}/$2

JAVA=$JAVA_HOME/bin/java

if test -z $1 ; then
	echo 'Usage service.sh [start|debug|stop] serial_number listen_address mongodbHost:mongodbPort'
	exit 1
else
	if ! test -x $JAVA ; then
		log_failure_msg "Cannot find jvm or jvm is not executable";
		exit 1;
	fi

	if ! test -d $LOG ; then
		mkdir -p $LOG;
	fi

	if ! test -d $LOG ; then
		log_failure_msg "Cannot create directory for log:$LOG";
		exit 1;
	fi

	if ! test -e $PID ; then
		log_failure_msg "Cannot create pid file:$PID";
		exit 1;
	fi

	case $1 in
		'start' )
			if ! test -z `cat $PID` ; then
				log_failure_msg 'Router Server is still running. Its pid is '`cat $PID`
				exit 1;
			else 
				exec $JAVA_HOME/bin/java $JAVA_OPTS  -classpath $CLASSPATH $MAIN_CLASS $APP_OPTS>> $LOG/std.out 2>&1 &
				echo $! > $PID
				log_success_msg "Starting Router Server[$2] on $3....."
				exit 0;
			fi
		;;
		'debug' )
			if test -z $5 ; then
				echo "Usage service.sh [start|debug|stop] domain listen_address mongodbHost:mongodbPort debugPort"
				exit 1;
			fi
		
			if test -z `cat $PID` ; then
				log_failure_msg 'Router Server is still running. Its pid is '`cat $PID`
				exit 1;
			else
				JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,address=$5,server=y,suspend=y"
				exec $JAVA_HOME/bin/java $JAVA_OPTS  -classpath $CLASSPATH $MAIN_CLASS $APP_OPTS>> $LOG/std.out 2>&1 &
				echo $! > $PID
				log_success_msg "Starting Router Server[$2] in debug mode......"
				exit 0;
			fi
		;;
		'stop' )
			if ! test -z `cat $PID` ; then
				kill `cat $PID` 
				echo "" > $PID
				log_success_msg 'Stopping Router Server......'
				exit 0;
			else
				log_failure_msg "Router Server on $3 is not running. No need to stop it"
				exit 1;
			fi
		;;
	esac
	exit 0
fi

#!/usr/bin/env bash

export DIR_HOME="$(cd "$(dirname "$0")" && pwd -P)"
export STREAM_HOME=`cd "$DIR_HOME/.." ; pwd`
nohup java -Dfile.encoding=UTF-8 -Duser.dir=$STREAM_HOME -Dlogback.configurationFile=$STREAM_HOME/config/logback.xml -cp $STREAM_HOME/lib/vnpay-event-stream-*.jar:$STREAM_HOME/lib/* com.vnpay.event.app.VNEventStreamApp >/dev/null 2>&1 &
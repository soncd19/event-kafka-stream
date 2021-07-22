#!/usr/bin/env bash
while : ; do
    sleep 1;
    server="-X GET 127.0.0.1:9092"
    curl -f $server && break || echo "kafka server isn't responding!"
done;

while : ; do
    sleep 1;
    server="-X GET 127.0.0.1:8083"
    curl -f $server && break || echo "kafka connector isn't responding!"
done;

while : ; do
    sleep 1;
    server="-X GET 127.0.0.1:6379"
    curl -f $server && break || echo "redis server isn't responding!"
done;

java -Dfile.encoding=UTF-8 -Duser.dir=/vnpay -Dlogback.configurationFile=config/logback.xml -cp lib/vnpay-event-stream.jar:lib/* com.vnpay.event.app.VNEventStreamApp >/dev/null 2>&1
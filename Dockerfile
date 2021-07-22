FROM maven:3-alpine

LABEL maintainer = "SonCD - VNEvent Stream app"

WORKDIR /vnpay

VOLUME /var/log/vnpay/

COPY /bin/ /vnpay/bin
COPY /config/ /vnpay/config
COPY /target/lib/ /vnpay/lib
COPY /wait-for-container.sh /wait-for-container.sh
RUN chmod +x /wait-for-container.sh

ENTRYPOINT ["sh","/wait-for-container.sh"]
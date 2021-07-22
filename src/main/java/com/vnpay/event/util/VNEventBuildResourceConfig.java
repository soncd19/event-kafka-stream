package com.vnpay.event.util;

import com.vnpay.event.constant.VNEventKeyConstant;
import com.vnpay.event.process.VNEventStreamProcess;
import com.vnpay.event.redis.VNEventJedisUtil;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by SonCD on 01/04/2021
 */
public class VNEventBuildResourceConfig {
    private static final Logger logger = Logger.getLogger(VNEventBuildResourceConfig.class);
    private static final Properties properties = VNEventBuildProperties.getInstance().getProperties();

    public static List<VNEventStreamProcess> parserXMLStream() {

        List<VNEventStreamProcess> kafkaStreamers = new ArrayList<>();
        String xmlFileConf = properties.getProperty(VNEventKeyConstant.XML_STREAM_PATH);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(xmlFileConf));
            doc.getDocumentElement().normalize();
            NodeList list = doc.getElementsByTagName("stream");
            for (int temp = 0; temp < list.getLength(); temp++) {
                Node node = list.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Properties props = buildProps(element);

                    String streamClass = element.getElementsByTagName(VNEventKeyConstant.STREAM_CLASS).item(0).getTextContent();
                    Constructor<?> c = Class.forName(streamClass).getDeclaredConstructor(Properties.class);
                    c.setAccessible(true);
                    VNEventStreamProcess kafkaStreamer = (VNEventStreamProcess) c.newInstance(new Object[]{props});

                    kafkaStreamers.add(kafkaStreamer);
                }
            }

            logger.info("VNEvent: parser xml to StreamProcess with: " + list.getLength() + " class");

        } catch (ParserConfigurationException | SAXException | IOException |
                NoSuchMethodException | ClassNotFoundException |
                IllegalAccessException | InvocationTargetException | InstantiationException e) {
            logger.error("VNEvent: parser Kafka XML file config error: " + e);
        }

        return kafkaStreamers;
    }


    public static VNEventJedisUtil parserXMLJedis() throws Exception {

        String xmlFileConf = properties.getProperty(VNEventKeyConstant.XML_JEDIS_PATH);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(xmlFileConf));
            doc.getDocumentElement().normalize();
            Element element = doc.getDocumentElement();
            String host = element.getElementsByTagName("host").item(0).getTextContent();
            String port = element.getElementsByTagName("port").item(0).getTextContent();
            String password = element.getElementsByTagName("password").item(0).getTextContent();
            String timeout = element.getElementsByTagName("timeout").item(0).getTextContent();
            String maxIdle = element.getElementsByTagName("maxIdle").item(0).getTextContent();
            String maxTotal = element.getElementsByTagName("maxTotal").item(0).getTextContent();
            String testOnBorrow = element.getElementsByTagName("testOnBorrow").item(0).getTextContent();

            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxIdle(Integer.parseInt(maxIdle));
            jedisPoolConfig.setMaxTotal(Integer.parseInt(maxTotal));
            jedisPoolConfig.setTestOnBorrow(Boolean.parseBoolean(testOnBorrow));
            logger.info("VNEvent: parser xml to jedis config ");
            if (password != null && !password.isEmpty()) {
                JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, Integer.parseInt(port), Integer.parseInt(timeout), password);
                return new VNEventJedisUtil(jedisPool);
            }

            JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, Integer.parseInt(port), Integer.parseInt(timeout));
            return new VNEventJedisUtil(jedisPool);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.error("VNEvent: parser jedis XML file config error: " + e);
            throw new Exception(e);
        }
    }

    private static Properties buildProps(Element element) {
        Properties props = new Properties();

        String topicIn = element.getElementsByTagName(VNEventKeyConstant.TOPIC_IN).item(0).getTextContent();
        String topicOut = element.getElementsByTagName(VNEventKeyConstant.TOPIC_OUT).item(0).getTextContent();
        props.put(VNEventKeyConstant.TOPIC_IN, topicIn);
        props.put(VNEventKeyConstant.TOPIC_OUT, topicOut);
        return props;
    }
}

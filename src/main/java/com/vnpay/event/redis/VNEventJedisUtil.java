package com.vnpay.event.redis;

import com.vnpay.event.util.VNEventStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.util.List;

/**
 * Created by SonCD on 20/05/2021
 */
public class VNEventJedisUtil {

    private static final Logger logger = LoggerFactory.getLogger(VNEventJedisUtil.class);
    private final JedisPool jedisPool;

    public VNEventJedisUtil(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public boolean exists(String key) {
        boolean result = false;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.exists(key);
            if (logger.isDebugEnabled()) {
                logger.debug("VNEventJedisUtil exists {}", key);
            }
        } catch (Exception e) {
            logger.warn("VNEventJedisUtil exists {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public String setObject(String key, Object value, long cacheSeconds) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.set(getBytesKey(key), toBytes(value));
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("setObject {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("setObject {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public String get(String key) {
        String value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.get(key);
                value = VNEventStringUtils.isNotBlank(value) && !"nil".equalsIgnoreCase(value) ? value : null;
                if (logger.isDebugEnabled()) {
                    logger.debug("VNEventJedisUtil get {} = {}", key, value);
                }
            }
        } catch (Exception e) {
            logger.warn("VNEventJedisUtil get {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    public List<String> getList(String key) {
        List<String> value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.lrange(key, 0, -1);
                if (logger.isDebugEnabled()) {
                    logger.debug("VNEventJedisUtil getList {} = {}", key, value);
                }
            }
        } catch (Exception e) {
            logger.warn("VNEventJedisUtil getList {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    public long listAdd(String key, String... value) {
        long result = 0;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.rpush(key, value);
            if (logger.isDebugEnabled()) {
                logger.debug("VNEventJedisUtil listAdd {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("VNEventJedisUtil listAdd {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public Jedis getResource() throws JedisException {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
        } catch (JedisException e) {
            logger.warn("VNEventJedisUtil getResource.", e);
            returnBrokenResource(jedis);
            throw e;
        }
        return jedis;
    }

    public void returnBrokenResource(Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnBrokenResource(jedis);
        }
    }

    public void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnResource(jedis);
        }
    }

    public static byte[] getBytesKey(Object object) {
        if (object instanceof String) {
            return VNEventStringUtils.getBytes((String) object);
        } else {
            return VNEventStringUtils.serialize(object);
        }
    }

    public static byte[] toBytes(Object object) {
        return VNEventStringUtils.serialize(object);
    }

}

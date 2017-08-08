package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by wuchaoxiang on 2017/8/7
 */
public class RedisDao {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JedisPool jedisPool;
    //根据字节码知道有哪些属性，得到一个模式
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    public RedisDao(String host, int port) {
        jedisPool = new JedisPool(host, port);
    }

    public Seckill getSeckill(long seckillId) {
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                //jedis内部不提供序列化操作
                String key = "seckillId:" + seckillId;
                byte[] bytes = jedis.get(key.getBytes());
                if(bytes != null) {
                    //protostuff提供序列化:  对象必须是pojo
                    //提供一个空对象
                    Seckill seckill = schema.newMessage();
                    //反序列化得到Seckill对象
                    ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
                    return seckill;
                }
            } catch (Exception e) {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public String putSeckill(Seckill seckill) {
        //将一个Seckill对象序列化存入redis缓存
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckillId:" + seckill.getSeckillId();
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));

                //将字节数组放入redis中
                //缓存时间
                int timeout = 60 * 60;
                String result = jedis.setex(key.getBytes(),timeout,bytes);
                return result;
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

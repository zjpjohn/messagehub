package com.message.hub.server.channel;

import com.message.hub.common.JsonConverter;
import com.message.hub.server.cache.BaseRedis;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;

/**
 * Created by shi on 6/6/2016.
 */
@Service
public class RedisChannel extends BaseRedis  implements IBaseChannel {


    public void pub(String queue, Object message) {
        if (message == null)
            return;

        String s = JsonConverter.toJson(message);
        redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.lPush(queue.getBytes(Charset.forName("UTF-8")), s.getBytes(Charset.forName("UTF-8"))) > 0;
            }
        });
    }


    public Object sub(String queue) {
        return redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                byte[] buffer = redisConnection.rPop(queue.getBytes(Charset.forName("UTF-8")));
                if (buffer != null && buffer.length > 0) {
                    String s = new String(buffer);
                    if (s != null)
                        return JsonConverter.fromJson(s, Object.class);
                    else
                        return null;
                } else
                    return null;
            }
        });
    }
}

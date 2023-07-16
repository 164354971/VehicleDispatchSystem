package love.lingbao.vehicledispatchsystem;

import com.alibaba.fastjson.JSON;
import love.lingbao.domain.entity.Admin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;

@SpringBootTest
class VehicleDispatchSystemApplicationTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Test
    void contextLoads() {
        //System.out.println(redisTemplate.opsForValue().get("name"));
        //redisTemplate.opsForHash().put("lingbao:user:1", "name", "田园");
        stringRedisTemplate.opsForValue().set("lingbao:order:number", "0");
        Object order_no = stringRedisTemplate.opsForValue().increment("lingbao:order:number", 20);
        System.out.println(order_no);
    }
    @Test
    void testSaveUser(){
        redisTemplate.opsForValue().set("user:100", new Admin());
        Admin admin = (Admin) redisTemplate.opsForValue().get("user:100");
        System.out.println(admin);
    }

    @Test
    void testSaveAdmin(){
        //手动序列化
        String s = JSON.toJSONString(new Admin());
        stringRedisTemplate.opsForValue().set("user:101", s);
        String s1 = stringRedisTemplate.opsForValue().get("user:101");
        //手动反序列化
        Admin admin = JSON.parseObject(s1, Admin.class);
        System.out.println(admin);
    }

     @Test
    void testHash(){
        stringRedisTemplate.opsForHash().put("user:300", "name", "田园");
        stringRedisTemplate.opsForHash().put("user:300", "age", "22");

         Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries("user:300");
         System.out.println(entries);
     }
}

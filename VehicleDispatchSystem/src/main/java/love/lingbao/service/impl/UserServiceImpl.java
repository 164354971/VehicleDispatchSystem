package love.lingbao.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.lingbao.domain.dto.UserDto;
import love.lingbao.domain.entity.User;
import love.lingbao.domain.vo.UserVo;
import love.lingbao.mapper.UserMapper;
import love.lingbao.service.UserService;
import love.lingbao.utils.RedisConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveUserDto(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setName(userDto.getName());
        user.setGender(userDto.getGender());
        user.setPhone(userDto.getPhone());
        user.setCreateTime(userDto.getCreateTime());
        user.setUpdateTime(userDto.getUpdateTime());
        userMapper.insert(user);
    }

    @Override
    public User createUserWithPhone(String phone) {
        //3.2.1 随机用户名
        String randomUsername = UUID.randomUUID().toString().replaceAll("-", "");
        //3.2.2 随机密码(32位)
        Random random = new Random();
        String alphabetsInUpperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String alphabetsInLowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String allCharacters = alphabetsInLowerCase + alphabetsInUpperCase + numbers;
        StringBuilder randomPasswordBuffer = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            int randomIndex = random.nextInt(allCharacters.length());
            randomPasswordBuffer.append(allCharacters.charAt(randomIndex));
        }
        String randomPassword = randomPasswordBuffer.toString();
        //3.2.3 md5加密
        randomPassword = DigestUtils.md5DigestAsHex(randomPassword.getBytes());
        //3.2.4 保存用户
        User user = new User();
        user.setUsername(randomUsername);
        user.setPassword(randomPassword);
        return user;
    }

    @Override
    public User createUserWithThird(Boolean sex, String img) {
        //3.2.1 随机用户名
        String randomUsername = UUID.randomUUID().toString().replaceAll("-", "");
        //3.2.2 随机密码(32位)
        Random random = new Random();
        String alphabetsInUpperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String alphabetsInLowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String allCharacters = alphabetsInLowerCase + alphabetsInUpperCase + numbers;
        StringBuilder randomPasswordBuffer = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            int randomIndex = random.nextInt(allCharacters.length());
            randomPasswordBuffer.append(allCharacters.charAt(randomIndex));
        }
        String randomPassword = randomPasswordBuffer.toString();
        //3.2.3 md5加密
        randomPassword = DigestUtils.md5DigestAsHex(randomPassword.getBytes());
        //3.2.4 保存用户
        User user = new User();
        user.setUsername(randomUsername);
        user.setPassword(randomPassword);
        if(sex)
            user.setGender("男");
        else
            user.setGender("女");
        user.setImg(img);
        save(user);
        return user;
    }

    @Override
    public String createTokenSevenDays(User user) {
        //1 生成用户token
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        //2 将user转为VO
        UserVo userVo = new UserVo(user);
        //3 将userVo转为HashMap
        Map<String, Object> userMap = BeanUtil.beanToMap(userVo, new HashMap<>(), CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        //4 redis存储用户键值对 key:RedisConstants.LOGIN_USER_KEY + token value: userMap
        stringRedisTemplate.opsForHash().putAll(RedisConstants.LOGIN_USER_KEY + token, userMap);

        //6 返回token
        return token;
    }

}

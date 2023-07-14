package love.lingbao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.lingbao.domain.dto.UserDto;
import love.lingbao.domain.entity.User;
import love.lingbao.mapper.UserMapper;
import love.lingbao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{
    @Autowired
    private UserMapper userMapper;

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
}

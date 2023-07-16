package love.lingbao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import love.lingbao.domain.dto.UserDto;
import love.lingbao.domain.entity.User;

public interface UserService extends IService<User> {

    void saveUserDto(UserDto userDto);

    User createUserWithPhone(String phone);
    User createUserWithThird(Boolean sex, String img);

    String createTokenSevenDays(User user);
}

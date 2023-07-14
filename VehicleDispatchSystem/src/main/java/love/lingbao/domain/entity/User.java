package love.lingbao.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import love.lingbao.domain.dto.UserDto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigInteger id;         //int auto_increment comment '主键id'
    private String username;    //varchar(32)                            not null comment '用户名',
    private String password;    //varchar(32)           default 'e10adc3949ba59abbe56e057f20f883e' not null comment '登录密码',

    private String img;    //varchar(255)           default 'img.png' not null comment '头像',
    private String phone;       //char(11)                            not null comment '手机号',
    private String name;        //varchar(10)                            not null comment '名字',
    private String nickname;        //varchar(10)                            not null comment '昵称',

    private String idNumber;        //varchar(18)                            not null comment '身份证号码',

    private String driverType;        //varchar(5)                            not null comment '驾照类型',
    private String gender;      //enum ('男', '女') default '男'      not null comment '性别'

    private BigDecimal salary;       //decimal(9,2) not null comment '账户余额'//

    private Integer vipLevel;       //tinyint not null comment '会员等级'//

    @TableField(fill = FieldFill.INSERT) //插入时填充字段
    private LocalDateTime createTime;   //创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE) //插入和更新时填充字段
    private LocalDateTime updateTime;   //资料更新时间

    public User(UserDto userDto){
        this.id = userDto.getId();
        this.username = userDto.getUsername();
        this.password = userDto.getPassword();
        this.phone = userDto.getPhone();
        this.name = userDto.getName();
        this.nickname = userDto.getNickname();
        this.gender = userDto.getGender();
    }
}

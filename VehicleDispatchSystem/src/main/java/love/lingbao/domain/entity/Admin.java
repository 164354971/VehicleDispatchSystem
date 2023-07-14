package love.lingbao.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import love.lingbao.domain.dto.AdminDto;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigInteger id;         //int auto_increment comment '主键id'
    private String username;    //varchar(10)                            not null comment '用户名',
    private String password;    //varchar(32)           default 'e10adc3949ba59abbe56e057f20f883e' not null comment '登录密码',

    private String img;    //varchar(255)           default 'img.png' not null comment '头像',
    private String phone;       //char(11)                            not null comment '手机号',
    private String name;        //varchar(10)                            not null comment '昵称',
    private String gender;      //enum ('男', '女') default '男'      not null comment '性别'

    @TableField(fill = FieldFill.INSERT) //插入时填充字段
    private LocalDateTime createTime;   //创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE) //插入和更新时填充字段
    private LocalDateTime updateTime;   //资料更新时间

    public Admin(AdminDto adminDto){
        this.id = adminDto.getId();
        this.username = adminDto.getUsername();
        this.password = adminDto.getPassword();
        this.phone = adminDto.getPhone();
        this.name = adminDto.getName();
        this.gender = adminDto.getGender();
    }
}

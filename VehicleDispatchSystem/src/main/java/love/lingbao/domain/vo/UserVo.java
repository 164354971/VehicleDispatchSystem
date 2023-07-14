package love.lingbao.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import love.lingbao.domain.entity.User;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigInteger id;         //int auto_increment comment '主键id'
    private String username;    //varchar(32)                            not null comment '用户名',
    private String img;    //varchar(255)           default 'img.png' not null comment '头像',
    private String phone;       //char(11)                            not null comment '手机号',
    private String name;        //varchar(10)                            not null comment '名字',
    private String nickname;        //varchar(10)                            not null comment '昵称',

    private String idNumber;        //varchar(18)                            not null comment '身份证号码',

    private String driverType;        //varchar(5)                            not null comment '驾照类型',
    private String gender;      //enum ('男', '女') default '男'      not null comment '性别'

    private BigDecimal salary;       //decimal(9,2) not null comment '账户余额'//

    private Integer vipLevel;       //tinyint not null comment '会员等级'//

    public UserVo(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.img = user.getImg();
        this.phone = user.getPhone();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.idNumber = user.getIdNumber();
        this.driverType = user.getDriverType();
        this.gender = user.getGender();
        this.salary = user.getSalary();
        this.vipLevel = user.getVipLevel();
    }
}

package love.lingbao.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import love.lingbao.domain.entity.Admin;

import java.io.Serializable;
import java.math.BigInteger;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigInteger id;         //int auto_increment comment '主键id'
    private String username;    //varchar(16)                            not null comment '用户名',
    private String phone;       //varchar(11)                            not null comment '手机号',
    private String name;        //varchar(16)                            not null comment '昵称',
    private String gender;      //enum ('男', '女', '保密') default '男'      not null comment '性别'

    public AdminVo(Admin admin){
        this.id = admin.getId();
        this.username = admin.getUsername();
        this.phone = admin.getPhone();
        this.name = admin.getName();
        this.gender = admin.getGender();
    }
}

package love.lingbao.domain.entity.vehicle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleBrand implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigInteger id;         //int auto_increment comment '主键id'
    private String brand;       //varchar(32) not null unique comment '品牌'
    private String img;       //varchar(255) not null unique comment '图片链接'
    private String initial;       //char(1) not null unique comment '首字母'
    private String info;       //varchar(255) not null unique comment '品牌描述'
}

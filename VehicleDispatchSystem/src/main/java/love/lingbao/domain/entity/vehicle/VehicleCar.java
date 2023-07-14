package love.lingbao.domain.entity.vehicle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleCar implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigInteger id;// bigint auto_increment
    private BigInteger vehicleModelId;// bigint                                   not null comment '车辆型号主键',
    private BigInteger parkadeId;// bigint                                   not null comment '车辆对应停车场主键',

    private BigDecimal price;// decimal(9, 2)                            not null comment '日租价格',
    private BigDecimal deposit;// decimal(9, 2)                            not null comment '押金',

    private String name;// varchar(32)                              not null comment '车辆名称',
    private String carNumber;// char(8)               default ''         not null comment '车牌号',
    private String color;// varchar(6)            default '白色'     null comment '颜色',
    private String info;// varchar(255)          default ''         null comment '车辆信息',
    private String displacement;// varchar(5)            default ''         null comment '排量',
    private String type;// varchar(5)            default '轿车'     null comment '车辆类型',
    private String energy;// varchar(16)                              not null comment '使用能源方式',
    private String driverType;// varchar(20)           default 'C2及以上' null comment '驾驶类型',
    private String year;// char(4)               default '未知年款' not null comment '年款',
    private String area;// enum ('永川', '巴南') default '永川'     not null comment '车辆位置',
    private String brand;// varchar(32)                              not null comment '品牌名称',
    private String model;// varchar(32)                              not null comment '型号名称',
    private String marketPrice;// varchar(30)                              null comment '市售价（字符串）',
    private String modelImg;// varchar(255)                             not null comment '模型图',

    private Boolean luxury;// tinyint(1)            default 0          null comment '是否豪华品牌',
    private Boolean newEnergy;// tinyint(1)            default 0          null comment '是否绿牌',
    private Boolean noDeposit;// tinyint(1)            default 0          not null comment '是否会员免押金',

    private Integer status;// tinyint               default 1          not null comment '1可用0不可用',
    private Integer count;// int                   default 0          not null comment '租车次数',
    private Integer evaluateCount;// int                   default 0          not null comment '评价次数',


}

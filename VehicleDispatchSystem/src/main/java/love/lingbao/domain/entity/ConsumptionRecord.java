package love.lingbao.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumptionRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigInteger id;// bigint auto_increment
    private BigInteger userId;// bigint                     not null comment '申请人id',
    private BigInteger vehicleApplicationId;// bigint                     not null comment '申请记录id',
    private BigDecimal originalPrice;// decimal(9, 2)              not null comment '订单原价',
    private BigDecimal discountAmount;// decimal(9, 2) default 0.00 not null comment '优惠金额',
    private Double discount;// decimal(2, 1) default 1.0  not null comment '优惠金额',
    private BigDecimal discountPrice;// decimal(9, 2)              not null comment '优惠后订单价格',
    private String type;// varchar(16)   default '车辆调度'  not null comment '消费类型',
    private String createTime;// datetime                   not null comment '消费记录创建时间'
}

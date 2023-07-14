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
public class RechargeRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigInteger id;// bigint auto_increment
    private BigInteger userId;// bigint                 not null comment '充值人id',
    private BigDecimal salary;// decimal(9, 2)          not null comment '充值金额',
    private String type;// varchar(16)   default '微信支付'  not null comment '充值类型',
    private String createTime;// datetime               not null comment '充值记录创建时间'
}

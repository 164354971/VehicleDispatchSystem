package love.lingbao.domain.dto.vehicle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleOrderDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigInteger id;// bigint auto_increment
    private BigInteger userId;//bigint        default 0                 not null comment '用户id',
    private BigInteger vehicleCarId;//bigint        default 0                 not null comment '租借的车辆id',
    private BigDecimal totalAmount;//decimal(9, 2) default 0.00              not null comment '订单总金额',
    private BigDecimal deposit;//decimal(9, 2) default 0.00              not null comment '押金',
    private BigDecimal paymentAmount;//decimal(9, 2) default 0.00              not null comment '订单实付金额',
    private Integer payChannel;//tinyint       default 0                 not null comment '支付渠道1余额2微信3支付宝4银行卡',
    private Integer status;//tinyint       default 0                 not null comment '0未付款,1已付款,2进行中,3已完成,4退车申请,5退车中,6已退车,7取消交易',
    private LocalDateTime startTime;//datetime      default CURRENT_TIMESTAMP not null comment '租车开始时间',
    private LocalDateTime endTime;//datetime      default CURRENT_TIMESTAMP not null comment '租车结束时间',
}

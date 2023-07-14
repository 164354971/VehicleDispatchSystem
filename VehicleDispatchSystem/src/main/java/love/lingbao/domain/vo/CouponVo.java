package love.lingbao.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import love.lingbao.domain.entity.Coupon;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigInteger id;//bigint auto_increment
    private String name;
    private Integer type;//1优惠券(金额)2优惠券(天数)3折扣券(金额)4折扣券(天数)
    private BigDecimal reach;//使用券的门槛
    private BigDecimal discount;//优惠的最大金额或折扣
    private LocalDateTime startTime;//datetime      default CURRENT_TIMESTAMP not null comment '默认可使用的开始时间为现在',
    private LocalDateTime endTime;//datetime      default CURRENT_TIMESTAMP not null comment '默认可使用时间为获得时间的后7天',

    public CouponVo(Coupon coupon){
        id = coupon.getId();//bigint auto_increment
        name = coupon.getName();
        type = coupon.getType();//1优惠券(金额)2优惠券(天数)3折扣券(金额)4折扣券(天数)
        reach = coupon.getReach();//使用券的门槛
        discount = coupon.getDiscount();//优惠的最大金额或折扣
        startTime = coupon.getStartTime();//datetime      default CURRENT_TIMESTAMP not null comment '默认可使用的开始时间为现在',
        endTime = coupon.getEndTime();//datetime      default CURRENT_TIMESTAMP not null comment '默认可使用时间为获得时间的后7天',
    }
}

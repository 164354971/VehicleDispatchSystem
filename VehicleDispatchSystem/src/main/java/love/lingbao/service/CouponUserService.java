package love.lingbao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import love.lingbao.domain.entity.Coupon;
import love.lingbao.domain.entity.CouponUser;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public interface CouponUserService extends IService<CouponUser> {
    public List<Coupon> findUserForCouponList(@Param("id") BigInteger id, @Param("current_time") Date currentTime);
}

package love.lingbao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import love.lingbao.domain.entity.Coupon;
import love.lingbao.domain.entity.CouponUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Mapper
public interface CouponUserMapper extends BaseMapper<CouponUser> {
    public List<Coupon> findUserForCouponList(@Param("id")BigInteger id, @Param("current_time") Date currentTime);
}

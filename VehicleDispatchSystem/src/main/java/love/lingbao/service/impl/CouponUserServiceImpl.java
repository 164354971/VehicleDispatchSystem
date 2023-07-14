package love.lingbao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.lingbao.domain.entity.Coupon;
import love.lingbao.domain.entity.CouponUser;
import love.lingbao.mapper.CouponUserMapper;
import love.lingbao.service.CouponUserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Service
public class CouponUserServiceImpl extends ServiceImpl<CouponUserMapper, CouponUser> implements CouponUserService {
    @Autowired
    private CouponUserMapper couponUserMapper;

    public List<Coupon> findUserForCouponList(@Param("id") BigInteger id, @Param("current_time") Date currentTime){
        return couponUserMapper.findUserForCouponList(id, currentTime);
    }
}

package love.lingbao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import love.lingbao.common.R;
import love.lingbao.domain.entity.Coupon;
import love.lingbao.domain.entity.CouponUser;
import love.lingbao.domain.vo.CouponVo;
import love.lingbao.service.AdminService;
import love.lingbao.service.CouponService;
import love.lingbao.service.CouponUserService;
import love.lingbao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Controller
//@RestController
@RequestMapping("/user")
@CrossOrigin
@ResponseBody
public class UserController {

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponUserService couponUserService;

    /**
     * 查找用户所有的优惠券
     * @param id 用户的id
     * @return
     */
    @GetMapping("/listCoupon")
    public R<List<CouponVo>> listCoupon(@RequestParam("id") BigInteger id, @RequestParam("currentTime") String currentTimeString) throws ParseException {
        log.info("/user/listCoupon get -> id = {}, currentTimeString = {}; 查找用户所有的优惠券", id, currentTimeString);
        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        Date currentTime = sdf.parse(currentTimeString);
        //1.查找当前用户所有当前时段可用的优惠券id
        LambdaQueryWrapper<CouponUser> culqw = new LambdaQueryWrapper<>();
        culqw.eq(CouponUser::getUserId, id).eq(CouponUser::getStatus, 1).le(CouponUser::getStartTime, currentTime).ge(CouponUser::getEndTime, currentTime);
        List<CouponUser> couponUserList = couponUserService.list(culqw);
        //用户持有多张相同类型的券，不适合用map
        //Map<BigInteger, CouponUser> map = new HashMap<>();

        //2.查找可用的优惠券id对应的优惠券
        LambdaQueryWrapper<Coupon> clqw = new LambdaQueryWrapper<>();
        Set<BigInteger> couponIdSet = new HashSet<>();
        couponUserList.forEach(e -> {
            couponIdSet.add(e.getCouponId());
            //map.put(e.getCouponId(), e);
        });
        clqw.in(Coupon::getId, couponIdSet);
        List<Coupon> couponList = couponService.list(clqw);

        //3.返回用户可使用的优惠券（注意此时把时间从优惠劵本身的使用时间段修改为用户手中的优惠券的可使用时间段）
        List<CouponVo> couponVoList = new LinkedList<>();
        couponList.forEach(e -> {
            //创建迭代器去掉已筛选过的优惠券，减少遍历个数
            Iterator<CouponUser> iterator = couponUserList.listIterator();
            while(iterator.hasNext()){
                CouponUser couponUser = iterator.next();
                //比对成功，修改优惠券时间，并添加进voList
                if (e.getId().equals(couponUser.getCouponId())) {
                    e.setStartTime(couponUser.getStartTime());
                    e.setEndTime(couponUser.getEndTime());
                    CouponVo couponVo = new CouponVo(e);
                    couponVoList.add(couponVo);
                    iterator.remove();
                }
            }
        });
        System.out.println(couponVoList);
        return R.success(couponVoList, "用户所有的优惠券");
    }

}

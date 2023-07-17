package love.lingbao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import love.lingbao.common.R;
import love.lingbao.domain.dto.vehicle.VehicleOrderDto;
import love.lingbao.domain.entity.User;
import love.lingbao.domain.entity.vehicle.VehicleOrder;
import love.lingbao.service.AdminService;
import love.lingbao.service.CouponService;
import love.lingbao.service.CouponUserService;
import love.lingbao.service.UserService;
import love.lingbao.service.vehicle.VehicleOrderService;
import love.lingbao.utils.RedisConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Slf4j
@Controller
//@RestController
@RequestMapping("/order")
@CrossOrigin
@ResponseBody
public class OrderController {

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private RedisTemplate<String, ? extends Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponUserService couponUserService;

    @Autowired
    private CouponUserService discountService;

    @Autowired
    private VehicleOrderService vehicleOrderService;

    /**
     * 生成车辆订单
     * @param vehicleOrderDto 订单生成
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/createOrder")
    public R<VehicleOrder> createOrder(@RequestBody VehicleOrderDto vehicleOrderDto) throws IOException {
        log.info("/order/createOrder post -> vehicleOrderDto = {}; 生成车辆订单", vehicleOrderDto);
        //1 查询订单对应的用户是否存在
        BigInteger userId = vehicleOrderDto.getUserId();
        //1.1 先访问redis
        User user = (User) redisTemplate.opsForValue().get(RedisConstants.USER_KEY + userId);
        //1.2 访问未命中，进MySQL访问
        if(user == null){
            user = userService.getById(userId);
            //1.3 依然不存在，抛异常
            if(user == null){
                throw new IOException("用户异常");
            }
        }
        //2 生成订单对象
        VehicleOrder vehicleOrder = new VehicleOrder(vehicleOrderDto);
        //2.1 判断订单中租车时间是否合法或是否结束时间已低于现时间
        if(vehicleOrder.getStartTime().compareTo(vehicleOrder.getEndTime()) >= 0 || vehicleOrder.getEndTime().compareTo(LocalDateTime.now()) <= 0)
            throw new IOException("租车时间异常");
        //2.2 判断订单付款方式判断是否异常
        Integer payChannel = vehicleOrder.getPayChannel();
        if(payChannel == null || payChannel.equals(0))
            throw new IOException("付款方式异常");
        //2.3 从redis获取订单号，不存在则先设0再获取自增后的订单号
        if(stringRedisTemplate.opsForValue().get(RedisConstants.ORDER_NUMBER_KEY) == null)
            stringRedisTemplate.opsForValue().set(RedisConstants.ORDER_NUMBER_KEY, "0");

        Long orderNo = stringRedisTemplate.opsForValue().increment(RedisConstants.ORDER_NUMBER_KEY);
        //2.4 插入订单号
        vehicleOrder.setOrderNo(String.valueOf(orderNo));

        //2.5 根据用户选择的付款方式进行付款操作

        Boolean flag = false;//用户是否支付成功标识

        switch (payChannel){
            //2.5.1 用户余额付款
            case 1:
                //更新用户余额与支付状态
                flag = vehicleOrderService.salaryPay(user, vehicleOrder);
                break;
            //2.5.2 微信支付付款
            case 2:break;
            //2.5.3 支付宝付款
            case 3:break;
            //2.5.4 银行卡付款
            case 4:break;
            //
            default:break;
        }

        //2.6 无论支付成功或者失败,创建订单创建时间和订单更新时间
        vehicleOrder.setCreateTime(LocalDateTime.now());
        vehicleOrder.setUpdateTime(LocalDateTime.now());

        //2.7 将完整订单数据写入redis和mysql
        //2.7.1 写入mysql
        vehicleOrderService.save(vehicleOrder);
        //2.7.2 写入redis
        ValueOperations<String, VehicleOrder> stringVehicleOrderValueOperations = (ValueOperations<String, VehicleOrder>) redisTemplate.opsForValue();
        stringVehicleOrderValueOperations.set(RedisConstants.ORDER_KEY + vehicleOrder.getId(), vehicleOrder);

        if(!flag)
            return R.success(vehicleOrder, "订单付款失败！（原因：余额不足）");

        //2.8 将用户使用的优惠券删除
        BigInteger couponUserId = vehicleOrderDto.getCouponUserId();//用户所持有的优惠券id

        if(couponUserId != null && !couponUserId.equals(BigInteger.ZERO)){
            //2.8.1 移除mysql中使用的的优惠券
            couponUserService.removeById(couponUserId);
            //2.8.2 移除redis中使用的的优惠券
            redisTemplate.delete(RedisConstants.COUPON_USER_KEY + couponUserId);
            //2.8.3 更新mysql用户余额
            userService.updateById(user);
            //2.8.4 移除redis中的用户
            redisTemplate.delete(RedisConstants.USER_KEY + userId);
        }
        return R.success(vehicleOrder, "订单付款成功！");
    }

    @GetMapping("/getOrderInProgressNum")
    public R<Integer> getOrderInProgressNum(@RequestParam("id") BigInteger id){
        log.info("/order/getOrderInProgressNum post -> id = {}; 查询用户正在进行中的订单个数", id);
        LambdaQueryWrapper<VehicleOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(VehicleOrder::getUserId, id).eq(VehicleOrder::getStatus, 1);
        Integer count = vehicleOrderService.count(lambdaQueryWrapper);
        return R.success(count);
    }
}

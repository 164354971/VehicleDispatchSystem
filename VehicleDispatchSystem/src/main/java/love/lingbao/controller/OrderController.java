package love.lingbao.controller;

import lombok.extern.slf4j.Slf4j;
import love.lingbao.common.R;
import love.lingbao.domain.dto.vehicle.VehicleOrderDto;
import love.lingbao.domain.entity.vehicle.VehicleOrder;
import love.lingbao.service.AdminService;
import love.lingbao.service.CouponService;
import love.lingbao.service.CouponUserService;
import love.lingbao.service.UserService;
import love.lingbao.service.vehicle.VehicleOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

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
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponUserService discountService;

    @Autowired
    private VehicleOrderService vehicleOrderService;

    /**
     * 生成车辆订单
     * @param vehicleOrderDto 订单生成
     * @return
     */
    @PostMapping("/createOrder")
    public R<VehicleOrder> createOrder(@RequestBody VehicleOrderDto vehicleOrderDto){
        log.info("/order/createOrder post -> vehicleOrderDto = {}; 生成车辆订单", vehicleOrderDto);

        return R.error("");
    }

}

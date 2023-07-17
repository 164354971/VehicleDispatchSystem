package love.lingbao.service.impl.vehicle;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.lingbao.domain.entity.User;
import love.lingbao.domain.entity.vehicle.VehicleOrder;
import love.lingbao.mapper.vehicle.VehicleOrderMapper;
import love.lingbao.service.vehicle.VehicleOrderService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VehicleOrderServiceImpl extends ServiceImpl<VehicleOrderMapper, VehicleOrder> implements VehicleOrderService {

    @Autowired
    private VehicleOrderMapper vehicleOrderMapper;

    @Autowired
    private RedisTemplate<String, ? extends Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public List<VehicleOrder> findTimeFrameVehicleCarIdList(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime){
        return vehicleOrderMapper.findTimeFrameVehicleCarIdList(startTime, endTime);
    }

    @Override
    public Boolean salaryPay(User user, VehicleOrder vehicleOrder) {
        //余额不足支付
        if(user.getSalary().compareTo(vehicleOrder.getPaymentAmount()) < 0){
            //状态设置为未付款
            vehicleOrder.setStatus(0);
            return false;
        }
        //余额减去付款金额
        user.setSalary(user.getSalary().subtract(vehicleOrder.getPaymentAmount()));
        //状态设置为已付款
        vehicleOrder.setStatus(1);

        return true;
    }

    @Override
    public Boolean weChatPay(User user, VehicleOrder vehicleOrder) {
        return null;
    }

    @Override
    public Boolean alipay(User user, VehicleOrder vehicleOrder) {
        return null;
    }

    @Override
    public Boolean bankcardPay(User user, VehicleOrder vehicleOrder) {
        return null;
    }


}

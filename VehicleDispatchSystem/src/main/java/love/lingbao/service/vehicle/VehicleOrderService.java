package love.lingbao.service.vehicle;

import com.baomidou.mybatisplus.extension.service.IService;
import love.lingbao.domain.entity.User;
import love.lingbao.domain.entity.vehicle.VehicleOrder;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VehicleOrderService extends IService<VehicleOrder> {
    /**
     * 通过订单表获得当前时间段可用的车辆id,除去：3已完成、6已退车和7已取消三种状态
     * @param startTime 租车开始时间
     * @param endTime 租车结束时间
     * @return
     */
    public List<VehicleOrder> findTimeFrameVehicleCarIdList(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    Boolean salaryPay(User user, VehicleOrder vehicleOrder);
    Boolean weChatPay(User user, VehicleOrder vehicleOrder);
    Boolean alipay(User user, VehicleOrder vehicleOrder);
    Boolean bankcardPay(User user, VehicleOrder vehicleOrder);
}

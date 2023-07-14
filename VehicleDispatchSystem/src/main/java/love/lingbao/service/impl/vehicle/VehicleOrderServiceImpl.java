package love.lingbao.service.impl.vehicle;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.lingbao.domain.entity.vehicle.VehicleOrder;
import love.lingbao.mapper.vehicle.VehicleOrderMapper;
import love.lingbao.service.vehicle.VehicleOrderService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VehicleOrderServiceImpl extends ServiceImpl<VehicleOrderMapper, VehicleOrder> implements VehicleOrderService {

    @Autowired
    private VehicleOrderMapper vehicleOrderMapper;

    public List<VehicleOrder> findTimeFrameVehicleCarIdList(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime){
        return vehicleOrderMapper.findTimeFrameVehicleCarIdList(startTime, endTime);
    }

}

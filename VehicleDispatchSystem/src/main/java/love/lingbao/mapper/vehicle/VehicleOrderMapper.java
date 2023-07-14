package love.lingbao.mapper.vehicle;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import love.lingbao.domain.entity.vehicle.VehicleOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface VehicleOrderMapper extends BaseMapper<VehicleOrder> {
    public List<VehicleOrder> findTimeFrameVehicleCarIdList(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

}

package love.lingbao.service.impl.vehicle;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.lingbao.domain.entity.vehicle.VehicleCar;
import love.lingbao.mapper.vehicle.VehicleCarMapper;
import love.lingbao.service.vehicle.VehicleCarService;
import org.springframework.stereotype.Service;

@Service
public class VehicleCarServiceImpl extends ServiceImpl<VehicleCarMapper, VehicleCar> implements VehicleCarService {

}

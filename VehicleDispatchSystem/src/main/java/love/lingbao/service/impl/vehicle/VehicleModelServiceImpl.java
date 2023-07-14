package love.lingbao.service.impl.vehicle;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.lingbao.domain.entity.vehicle.VehicleModel;
import love.lingbao.mapper.vehicle.VehicleModelMapper;
import love.lingbao.service.vehicle.VehicleModelService;
import org.springframework.stereotype.Service;

@Service
public class VehicleModelServiceImpl extends ServiceImpl<VehicleModelMapper, VehicleModel> implements VehicleModelService {
}

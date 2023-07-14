package love.lingbao.service.impl.vehicle;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.lingbao.domain.entity.vehicle.VehicleBrand;
import love.lingbao.mapper.vehicle.VehicleBrandMapper;
import love.lingbao.service.vehicle.VehicleBrandService;
import org.springframework.stereotype.Service;

@Service
public class VehicleBrandServiceImpl extends ServiceImpl<VehicleBrandMapper, VehicleBrand> implements VehicleBrandService {
}

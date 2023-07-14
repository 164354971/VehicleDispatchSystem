package love.lingbao.domain.dto.vehicle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import love.lingbao.domain.entity.vehicle.VehicleBrand;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleBrandDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<VehicleBrand> vehicleBrandList;
}

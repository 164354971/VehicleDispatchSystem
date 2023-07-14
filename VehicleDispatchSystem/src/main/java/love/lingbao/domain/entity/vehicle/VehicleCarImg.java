package love.lingbao.domain.entity.vehicle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleCarImg implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigInteger id;         //int auto_increment comment '主键id'
    private BigInteger vehicleCarId;         //int comment '车辆id'
    private String img; //车辆图片
}

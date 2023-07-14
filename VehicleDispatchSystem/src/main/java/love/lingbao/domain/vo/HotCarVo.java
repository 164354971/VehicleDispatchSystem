package love.lingbao.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotCarVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigInteger vehicleCarId;
    private String name;
    private BigInteger userId;
    private String userImg;
    private String img;
    private String evaluate;
    private Integer count;
    private Integer score;
    private BigDecimal price;
}

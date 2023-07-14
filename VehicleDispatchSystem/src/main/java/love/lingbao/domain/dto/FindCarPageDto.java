package love.lingbao.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindCarPageDto {
    private BigInteger parkadeId;
    private Integer page;
    private Integer pageSize;
    private Integer sort;
    private List<String> typeList;
    private BigDecimal startPrice;
    private BigDecimal endPrice;
    private Boolean nln;
    private Boolean noDeposit;
    private Boolean luxury;
    private Boolean newEnergy;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

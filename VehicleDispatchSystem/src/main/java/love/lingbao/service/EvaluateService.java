package love.lingbao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import love.lingbao.domain.entity.Evaluate;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

public interface EvaluateService extends IService<Evaluate> {
    public List<Evaluate> findNewUpdateTimeEvaluateList(@Param("vehicleCarIdList") List<BigInteger> vehicleCarIdList);
}

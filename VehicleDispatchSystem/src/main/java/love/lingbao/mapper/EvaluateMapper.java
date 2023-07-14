package love.lingbao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import love.lingbao.domain.entity.Evaluate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

@Mapper
public interface EvaluateMapper extends BaseMapper<Evaluate> {
    public List<Evaluate> findNewUpdateTimeEvaluateList(@Param("vehicleCarIdList") List<BigInteger> vehicleCarIdList);
}

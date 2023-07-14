package love.lingbao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.lingbao.domain.entity.Evaluate;
import love.lingbao.mapper.EvaluateMapper;
import love.lingbao.service.EvaluateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class EvaluateServiceImpl extends ServiceImpl<EvaluateMapper, Evaluate> implements EvaluateService {

    @Autowired
    private EvaluateMapper evaluateMapper;

    @Override
    public List<Evaluate> findNewUpdateTimeEvaluateList(List<BigInteger> vehicleCarIdList) {
        return evaluateMapper.findNewUpdateTimeEvaluateList(vehicleCarIdList);
    }

}

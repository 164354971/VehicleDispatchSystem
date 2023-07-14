package love.lingbao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.lingbao.domain.entity.ConsumptionRecord;
import love.lingbao.mapper.ConsumptionRecordMapper;
import love.lingbao.service.ConsumptionRecordService;
import org.springframework.stereotype.Service;

@Service
public class ConsumptionRecordImpl extends ServiceImpl<ConsumptionRecordMapper, ConsumptionRecord> implements ConsumptionRecordService {
}

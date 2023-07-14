package love.lingbao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.lingbao.domain.entity.RechargeRecord;
import love.lingbao.mapper.RechargeRecordMapper;
import love.lingbao.service.RechargeRecordService;
import org.springframework.stereotype.Service;

@Service
public class RechargeRecordImpl extends ServiceImpl<RechargeRecordMapper, RechargeRecord> implements RechargeRecordService {
}

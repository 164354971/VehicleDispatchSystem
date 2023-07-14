package love.lingbao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.lingbao.domain.entity.Parkade;
import love.lingbao.mapper.ParkadeMapper;
import love.lingbao.service.ParkadeService;
import org.springframework.stereotype.Service;

@Service
public class ParkadeServiceImpl extends ServiceImpl<ParkadeMapper, Parkade> implements ParkadeService {
}

package love.lingbao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import love.lingbao.domain.entity.ThirdAccount;
import love.lingbao.mapper.ThirdAccountMapper;
import love.lingbao.service.ThirdAccountService;
import org.springframework.stereotype.Service;

@Service
public class ThirdAccountServiceImpl extends ServiceImpl<ThirdAccountMapper, ThirdAccount> implements ThirdAccountService {

}

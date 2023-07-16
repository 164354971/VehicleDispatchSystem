package love.lingbao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.extern.slf4j.Slf4j;
import love.lingbao.common.R;
import love.lingbao.common.RandomCodeUtils;
import love.lingbao.common.SendMsg;
import love.lingbao.domain.dto.AdminDto;
import love.lingbao.domain.dto.ThirdAccountDto;
import love.lingbao.domain.dto.UserDto;
import love.lingbao.domain.entity.Admin;
import love.lingbao.domain.entity.ThirdAccount;
import love.lingbao.domain.entity.User;
import love.lingbao.domain.vo.AdminVo;
import love.lingbao.domain.vo.UserVo;
import love.lingbao.service.AdminService;
import love.lingbao.service.ThirdAccountService;
import love.lingbao.service.UserService;
import love.lingbao.utils.RedisConstants;
import love.lingbao.utils.RegexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
//@RestController
@RequestMapping("/login")
@CrossOrigin
@ResponseBody
public class LoginController {

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private ThirdAccountService thirdAccountService;

    /**
     * 手机号登录、注册
     * @param phone 用户手机号
     * @param code 手机验证码
     * @return token
     */
    @Transactional(rollbackFor = Exception.class)
    @GetMapping("/phoneLogin")
    public R<String> phoneLogin(@RequestParam("phone") String phone, @RequestParam("code") String code) throws Exception {
        log.info("/login/phoneLogin get -> phoneLogin: phone = {}, code = {}; 手机号登录", phone, code);
        //1 判断手机号是否有误 phone
        if(RegexUtils.isPhoneInvalid(phone)){
            return R.error("手机号格式错误！");
        }

        //2 判断验证码是否有误 RedisConstants.LOGIN_CODE_KEY + phone
        String cacheCode = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + phone);
        if(cacheCode == null || !cacheCode.equals(code)){
            return R.error("验证码输入错误！");
        }

        //3 查找用户是否已存在
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getPhone, phone);
        User user = userService.getOne(userLambdaQueryWrapper);

        //3.1 不存在，就先创建新用户
        if(user == null){
            //创建新用户 phone
            user = userService.createUserWithPhone(phone);
        }

        //4 生成用户token,并设置有效期为7天
        String token = userService.createTokenSevenDays(user);

        //5 返回token作为用户凭证
        return R.success(token);
    }

    /**
     * 第三方用户登录
     * @param thirdAccountDto 第三方登录用户信息体Dto
     * @return token
     */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/thirdAccountLogin")
    public R<String> thirdAccountLogin(@RequestBody ThirdAccountDto thirdAccountDto) throws Exception {
        log.info("/login/thirdAccountLogin post -> thirdAccountLogin: thirdAccountDto = {}; 第三方登录用户信息体Dto", thirdAccountDto.toString());
        //1 先查找第三方用户是否有绑定账户
        LambdaQueryWrapper<ThirdAccount> thirdAccountLambdaQueryWrapper = new LambdaQueryWrapper<>();
        thirdAccountLambdaQueryWrapper.eq(ThirdAccount::getThirdUniqueAccount, thirdAccountDto.getThirdUniqueAccount());
        ThirdAccount thirdAccount = thirdAccountService.getOne(thirdAccountLambdaQueryWrapper);

        //2 如果存在则代表第三方已绑定用户，无需再创建新用户
        if(thirdAccount != null){
            User user = userService.getById(thirdAccount.getUserId());
            if(user == null)
                throw new Exception("第三方登录失败，请联系管理员");

            //4 生成用户token,并设置有效期为7天
            String token = userService.createTokenSevenDays(user);

            return R.success(token, "登录成功！");
        }

        //3 如果没有查找到绑定用户，则新建用户，再与第三方账号绑定
        thirdAccount = new ThirdAccount();

        thirdAccount.setThirdUniqueAccount(thirdAccountDto.getThirdUniqueAccount());
        thirdAccount.setType(thirdAccountDto.getType());
        thirdAccount.setBindFlag(true);

        //3.1.1 创建新用户
        User user = userService.createUserWithThird(thirdAccountDto.getSex(), thirdAccountDto.getImg());

        //3.1.2 获取用户ID
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUsername, user.getUsername());
        user = userService.getOne(userLambdaQueryWrapper);

        //3.1.3 第三方用户存储本地用户ID
        thirdAccount.setUserId(user.getId());

        //3.1.4 存储第三方用户数据
        thirdAccountService.save(thirdAccount);

        //3.1.5 生成用户token
        String token = userService.createTokenSevenDays(user);

        return R.success(token, "登录成功！");
    }

    /**
     * 本地用户登录
     * @param userDto 登录用户信息体(带验证码）
     * @return token
     */
    @PostMapping("/userLogin")
    public R<String> userLogin(@RequestBody UserDto userDto){
        log.info("/login/userLogin post -> userLogin: userDto = {}; 本地用户登录", userDto.toString());
        String code = userDto.getCode();
        //0.判断验证码
        R<String> info = verify(code);
        if(info.getCode() != 1)return R.error(info.getMsg());

        if(userDto.getUsername() == null)
            return R.error("用户名不能为空");
        if(userDto.getPassword() == null){
            return R.error("密码不能为空");
        }

        User one = new User(userDto);

        //1、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,one.getUsername());
        User user = userService.getOne(queryWrapper);

        //2、如果没有查询到则返回登录失败结果
        if(user == null){
            return R.error("用户名不存在");
        }

        //3、将页面提交的密码password进行md5加密处理
        String password = one.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //4、密码比对，如果不一致则返回登录失败结果
        if(!user.getPassword().equals(password)){
            return R.error("用户名或密码错误，请重试");
        }

        /*// 禁止同一客户端登录多个账户 如果本次hash键值对存在，表名用户已在该客户端登录
        if (redisTemplate.opsForHash().hasKey("sessionId_userId", httpSession.getId())) {
            return R.error("禁止同一客户端登录多个用户\n请退出已登录账户的客户端");
        }

        // 允许异地登录（让之前登录的客户端强制下线），如果是异地登录则需要清空redis中之前登录客户端的map结构数据
        //用户的键存在表明已存在其他客户端登录了该用户，允许多客户端登录
        if (redisTemplate.opsForHash().hasKey("userId_sessionId", u.getId())) {
            redisTemplate.opsForHash().delete("sessionId_userId", redisTemplate.opsForHash().get("userId_sessionId", u.getId()));
            redisTemplate.opsForHash().delete("userId_sessionId", u.getId());
        }
        redisTemplate.opsForHash().put("sessionId_userId", httpSession.getId(), u.getId());
        redisTemplate.opsForHash().put("userId_sessionId", u.getId(), httpSession.getId());*/

        //5、登录成功，将用户id存入Session并返回登录成功结果
        String token = userService.createTokenSevenDays(user);
        return R.success(token, "登录成功！");
    }

    /**
     * 用户退出登录
     * @param id 登录用户id
     * @return
     */
    @GetMapping("/userLogout")
    public R<String> userLogout(@RequestParam("id") BigInteger id){
        log.info("/login/userLogout get -> userLogout: id = {}; 用户退出登录", id);
        BigInteger uid = (BigInteger) httpSession.getAttribute("userId");
        if(uid == null && id == null)
            return R.error("未知错误");
        if(uid == null) uid = id;

        //redis缓存内，通过用户id获取对应的sessionId，将sessionId对应的用户id移除
        //redisTemplate.opsForHash().delete("sessionId_userId", redisTemplate.opsForHash().get("userId_sessionId", uid));
        //redis缓存内，移除用户id对应的sessionId
        //redisTemplate.opsForHash().delete("userId_sessionId", uid);
        //移除会话
        httpSession.removeAttribute("userId");
        return R.success("用户已退出");
    }

    /**
     * 管理员登录
     * @param adminDto 登录管理员信息体
     * @return
     */
    @PostMapping("/adminLogin")
    public R<AdminVo> adminLogin(@RequestBody AdminDto adminDto){//http对象用来将登录成功的用户存入session里
        log.info("/login/adminLogin post -> adminLogin: adminDto = {}; 用户登录", adminDto.toString());
        String code = adminDto.getCode();
        //0.判断验证码
        R<String> info = verify(code);
        if(info.getCode() != 1)return R.error(info.getMsg());

        if(adminDto.getUsername() == null)
            return R.error("用户名不能为空");
        if(adminDto.getPassword() == null){
            return R.error("密码不能为空");
        }

        Admin admin = new Admin(adminDto);
        //1、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername,admin.getUsername());
        Admin adm = adminService.getOne(queryWrapper);

        //2、如果没有查询到则返回登录失败结果
        if(adm == null){
            return R.error("用户名不存在");
        }

        //3、将页面提交的密码password进行md5加密处理
        String password = admin.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //4、密码比对，如果不一致则返回登录失败结果
        if(!adm.getPassword().equals(password)){
            return R.error("用户名或密码错误，请重试");
        }

        // 禁止同一客户端登录多个账户 如果本次hash键值对存在，表名用户已在该客户端登录
        if (redisTemplate.opsForHash().hasKey("sessionId_adminId", httpSession.getId())) {
            return R.error("禁止同一客户端登录多个用户\n请退出已登录账户的客户端");
        }

        // 允许异地登录（让之前登录的客户端强制下线），如果是异地登录则需要清空redis中之前登录客户端的map结构数据
        //用户的键存在表明已存在其他客户端登录了该用户，允许多客户端登录
        if (redisTemplate.opsForHash().hasKey("adminId_sessionId", admin.getId())) {
            redisTemplate.opsForHash().delete("sessionId_adminId", redisTemplate.opsForHash().get("adminId_sessionId", admin.getId()));
            redisTemplate.opsForHash().delete("adminId_sessionId", admin.getId());
        }
        redisTemplate.opsForHash().put("sessionId_adminId", httpSession.getId(), admin.getId());
        redisTemplate.opsForHash().put("adminId_sessionId", admin.getId(), httpSession.getId());

        //5、登录成功，将用户id存入Session并返回登录成功结果
        httpSession.setAttribute("adminId",adm.getId());
        httpSession.setMaxInactiveInterval(24 * 60 * 60 * 7); //服务端保存一周
        log.info(httpSession.getId());

        AdminVo adminVo = new AdminVo(adm);
        return R.success(adminVo, "登录成功！");
    }

    /**
     * 用户注册
     * @param userDto 用户信息体（带验证码）
     * @return
     */
    @PostMapping("/userRegister")
    public R<String> userRegister(@RequestBody UserDto userDto){
        //response.setHeader("Access-Control-Allow-Origin", address);
        log.info("/login/userRegister post -> userRegister: userDto = {}; 用户注册", userDto.toString());
        //先对验证码比对
        //如果不相等
        log.info((String) httpSession.getAttribute(userDto.getPhone()));
        if(httpSession.getAttribute(userDto.getPhone()) == null){
            return R.error("验证码未发送或验证码已过期！请重试");
        }
        if(!httpSession.getAttribute(userDto.getPhone()).equals(userDto.getCode())){
            return R.error("验证码输入错误！请重试");
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDto.getUsername());
        User user1 = userService.getOne(queryWrapper);
        if(user1 != null){
            return R.error("用户名已存在！");
        }
        if(userDto.getUsername().length() < 6){
            return R.error("用户名长度 < 6;\n用户名应为6~10位的由大小写字母、数字、下划线构成！请重试");
        }
        if(userDto.getUsername().length() > 10){
            return R.error("用户名长度 > 10;\n用户名应为6~10位的由大小写字母、数字、下划线构成！请重试");
        }
        if(userDto.getPassword().length() < 6){
            return R.error("密码长度 < 6;\n密码应为6~18位的由大小写字母、数字、下划线构成！请重试");
        }
        if(userDto.getPassword().length() > 18){
            return R.error("密码长度 > 18;\n密码应为6~18位的由大小写字母、数字、下划线构成！请重试");
        }
        if(httpSession.getAttribute(userDto.getPhone()) == null){
            return R.error("输入的手机号与验证的手机号不一致，请修改");
        }
        if(!userDto.getCode().equals(httpSession.getAttribute(userDto.getPhone()))){
            return R.error("验证码输入有误，请修改");
        }
        //之后，对用户的密码进行md5加密
        String password = userDto.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        //userDto.setCreateTime(LocalDateTime.now());
        //userDto.setUpdateTime(LocalDateTime.now());
        userDto.setPassword(password);
        //存储新用户
        userService.saveUserDto(userDto);

        return R.success("用户" + userDto.getUsername() + "注册成功！");
    }

    /**
     * 生成字符串登录验证码
     * @return
     */
    @GetMapping("/code")
    public R<String> code(){
        //response.setHeader("Access-Control-Allow-Origin", address);
        log.info("/login/code get -> ; 生成登录验证码");
        //生成4位数验证码
        String code = RandomCodeUtils.getFourValidationCode();
        //时长为1分钟
        httpSession.setAttribute("loginCode", code);
        httpSession.setMaxInactiveInterval(60);
        System.out.println(httpSession.getAttribute("loginCode"));
        return R.success(code);
    }

    /**
     * 生成图片登录验证码(Kaptcha)
     * @return
     */
    @GetMapping("/kaptcha")
    public R<String> defaultKaptcha(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception{
        log.info("/login/kaptcha get -> ; 生成登录验证码(Kaptcha)");
        httpServletResponse.setHeader("Cache-Control","no-store");
        httpServletResponse.setHeader("Pragma","no-cache");
        httpServletResponse.setDateHeader("Expires",0);
        httpServletResponse.setContentType("image/gif");

        //生成验证码对象,三个参数分别是宽、高、位数
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 4);
        //设置验证码的字符类型为数字和字母混合
        captcha.setCharType(Captcha.TYPE_DEFAULT);
        // 设置内置字体
        captcha.setCharType(Captcha.FONT_1);
        //设置base64编码
        String s = captcha.toBase64("");
        //验证码存入session
        httpServletRequest.getSession().setAttribute("loginCode",captcha.text().toLowerCase());
        //输出图片流
        //captcha.out(httpServletResponse.getOutputStream());
        return R.success(s);
    }

    /**
     * 验证登录验证码(Kaptcha)
     * @return
     */
    public R<String> verify(String code){
        if (!StringUtils.hasLength(code)){
            return R.error("验证码不能为空");
        }
        String kaptchaCode = httpSession.getAttribute("loginCode")+"";
        if (!StringUtils.hasLength(kaptchaCode)||!code.toLowerCase().equals(kaptchaCode)){
            return R.error("验证码错误");
        }
        return R.success("验证成功");
    }

    /**
     * 用户账号密码注册，发送短信验证码
     * @param phone 手机号码
     * @return
     */
    @GetMapping("/userRegisterSendMsg/{phone}")
    public R<String> userRegisterSendMsg(@PathVariable String phone) throws Exception {
        //response.setHeader("Access-Control-Allow-Origin", address);
        log.info("/login/userRegisterSendMsg get -> sendMsg: phone = {}; 发送短信验证码", phone);
        //判断该注册用户的手机号是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        User user = userService.getOne(queryWrapper);
        //手机号存在，返回
        if(user != null){
            return R.error("该手机号已有绑定账号，请更换手机号后再试");
        }
        //生成6位数验证码
        String code = RandomCodeUtils.getSixValidationCode();
        //短信格式插入字符串数组，第一个是验证码，第二个是时长（分钟）
        String[] params = {code, "1"};
        //发送短信，获取发送的结果
        SmsSingleSenderResult result = SendMsg.sendMsgByTxPlatform(phone, params);
        //结果的结果码为1016，表示手机号的格式输入有误，返回
        if(result.result == 1016) return R.error("手机号格式输入有误！请重新输入");
            //其他错误，抛异常
        else if(result.result != 0){
            throw new Exception("send phone validateCode is error" + result.errMsg);
        }
        //正确情况
        //存储该手机号的session,键为手机号，值为验证码，时长为1分钟
        /*httpSession.setAttribute(phone, code);
        httpSession.setMaxInactiveInterval(60);
        System.out.println(httpSession.getAttribute(phone));*/
        //将手机号和验证码存入redis
        stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_CODE_KEY + phone, code, RedisConstants.LOGIN_CODE_TTL, TimeUnit.MINUTES);
        return R.success("验证码已发送");
    }

    /**
     * 用户手机号登录注册，发送短信验证码
     * @param phone 手机号码
     * @return
     */
    @GetMapping("/sendMsg/{phone}")
    public R<String> sendMsg(@PathVariable String phone) throws Exception {
        //response.setHeader("Access-Control-Allow-Origin", address);
        log.info("/login/sendMsg get -> sendMsg: phone = {}; 发送短信验证码", phone);
        if(RegexUtils.isPhoneInvalid(phone)){
            return R.error("手机号格式错误！");
        }
        //生成6位数验证码
        String code = RandomCodeUtils.getSixValidationCode();
        //短信格式插入字符串数组，第一个是验证码，第二个是时长（分钟）
        String[] params = {code, "1"};
        //发送短信，获取发送的结果
        SmsSingleSenderResult result = SendMsg.sendMsgByTxPlatform(phone, params);
        //结果的结果码为1016，表示手机号的格式输入有误，返回
        if(result.result == 1016) return R.error("手机号格式错误!");
            //其他错误，抛异常
        else if(result.result != 0){
            throw new Exception("send phone validateCode is error" + result.errMsg);
        }
        //将手机号和验证码存入redis
        stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_CODE_KEY + phone, code, RedisConstants.LOGIN_CODE_TTL, TimeUnit.MINUTES);
        return R.success("验证码已发送");
    }

    /**
     * 发送短信验证码(模拟)
     * @param phone 手机号码
     * @return
     */
    @GetMapping("/testSendMsg/{phone}")
    public R<String> testSendMsg(@PathVariable String phone) throws Exception {
        //response.setHeader("Access-Control-Allow-Origin", address);
        /*if(phone.length() > 0 && phone.charAt(phone.length() - 1) == '='){
            phone = phone.substring(0, phone.length() - 1);
        }*/
        log.info("/login/testSendMsg get -> testSendMsg: phone = {}; 测试发送短信验证码", phone);
        //判断该注册用户的手机号是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        User user = userService.getOne(queryWrapper);
        //手机号存在，返回
        if(user != null){
            return R.error("该手机号已有绑定账号，请更换手机号后再试");
        }
        //生成6位数验证码
        String code = RandomCodeUtils.getSixValidationCode();
        log.info((String) httpSession.getAttribute(phone));
        //存储该手机号的session,键为手机号，值为验证码，时长为1分钟
        httpSession.setAttribute(phone, code);
        httpSession.setMaxInactiveInterval(60);
        log.info((String) httpSession.getAttribute(phone));

        return R.success("验证码已发送");
    }

    /**
     * 验证用户名是否存在
     * @param username 用户名
     * @return
     */
    @PostMapping("/username")
    public R<String> username(@RequestBody String username){
        //response.setHeader("Access-Control-Allow-Origin", address);
        /*if(username.length() > 0 && username.charAt(username.length() - 1) == '='){
            username = username.substring(0, username.length() - 1);
        }*/
        log.info("/login/username post -> username: username = {}; 查询用户名是否存在", username);
        //1、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = userService.getOne(queryWrapper);

        //2、如果没有查询到则返回登录失败结果
        if(user != null){
            return R.error("用户名已存在！");
        }
        return R.success("用户名未注册，可以使用");
    }

    /**
     * 验证用户是否存在
     * @param userId 用户id
     * @return
     */
    @GetMapping("/isUserLogin")
    public R<UserVo> isUserLogin(@RequestParam("id") BigInteger userId){
        log.info("/login/isUserLogin get -> isUserLogin: userId = {}; 查询用户是否登录", userId);
        Object obj = httpSession.getAttribute("userId");
        //服务器与客户端都已退出登录状态，直接返回
        if(obj == null && userId == null){
            return R.error("用户未登录");
        }
        User user = userService.getById((BigInteger) obj);
        //服务端session存在
        if(user != null){
            UserVo userVo = new UserVo(user);
            return R.success(userVo, "用户已登录");
        }
        //服务端session不存在，判断传递的客户端的用户id是否存在
        user = userService.getById(userId);
        if(user != null){
            //存在就存储session
            httpSession.setAttribute("userId", user.getId());
            httpSession.setMaxInactiveInterval(24 * 60 * 60 * 7); //服务端保存一周
            UserVo userVo = new UserVo(user);
            return R.success(userVo, "用户已登录");
        }

        return R.error("用户未登录");
    }
}

package love.lingbao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import love.lingbao.common.R;
import love.lingbao.domain.dto.FindCarPageDto;
import love.lingbao.domain.entity.Evaluate;
import love.lingbao.domain.entity.Parkade;
import love.lingbao.domain.entity.User;
import love.lingbao.domain.entity.vehicle.*;
import love.lingbao.domain.vo.HotCarVo;
import love.lingbao.domain.vo.vehicle.VehicleBrandVo;
import love.lingbao.service.AdminService;
import love.lingbao.service.EvaluateService;
import love.lingbao.service.ParkadeService;
import love.lingbao.service.UserService;
import love.lingbao.service.vehicle.*;
import love.lingbao.utils.RedisConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/vehicle")
@CrossOrigin
@ResponseBody
public class VehicleController {
    @Autowired
    private HttpSession httpSession;

    @Autowired
    private RedisTemplate<String, ? extends Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private VehicleBrandService vehicleBrandService;

    @Autowired
    private VehicleModelService vehicleModelService;

    @Autowired
    private VehicleCarService vehicleCarService;

    @Autowired
    private VehicleCarImgService vehicleCarImgService;

    @Autowired
    private VehicleOrderService vehicleOrderService;

    @Autowired
    private ParkadeService parkadeService;

    @Autowired
    private EvaluateService evaluateService;

    

    /**
     * 上传品牌图片
     * @param file 品牌图片信息体
     * @return 品牌图片保存成功后，返回保存的路径
     */
    @PostMapping("/uploadBrandImage")
    public String uploadBrandImage(MultipartFile file) {
        log.info("/vehicle/uploadImage post -> uploadBrandImage: file = {}; 将品牌图片保存在本地，并返回存储路径", file);
        String path = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("static/images")).getPath();//本地资源路径
        //String uid = (String) httpSession.getAttribute("admin");
        path = path + "/brand/";
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String fileName = UUID.randomUUID().toString() + suffix;

        try {
            file.transferTo(new File(path, Objects.requireNonNull(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path + fileName;//保存成功后返回路径
    }

    /**
     * 上传型号图片
     * @param file 型号图片信息体
     * @return 型号图片保存成功后，返回保存的路径
     */
    @PostMapping("/uploadModelImage")
    public String uploadModelImage(MultipartFile file) {
        log.info("/vehicle/uploadModelImage post -> uploadModelImage: file = {}; 将型号图片保存在本地，并返回存储路径", file);
        String path = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("static/images")).getPath();//本地资源路径
        //String uid = (String) httpSession.getAttribute("admin");
        path = path + "/model/";
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String fileName = UUID.randomUUID().toString() + suffix;

        try {
            file.transferTo(new File(path, Objects.requireNonNull(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path + fileName;//保存成功后返回路径
    }

    /**
     * 上传车辆图片
     * @param file 车辆图片信息体
     * @return 车辆图片保存成功后，返回保存的路径
     */
    @PostMapping("/uploadCarImage")
    public String uploadCarImage(MultipartFile file) {
        log.info("/vehicle/uploadCarImage post -> uploadCarImage: file = {}; 将型号图片保存在本地，并返回存储路径", file);
        String path = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("static/images")).getPath();//本地资源路径
        //String uid = (String) httpSession.getAttribute("admin");
        path = path + "/car/";
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String fileName = UUID.randomUUID().toString() + suffix;

        try {
            file.transferTo(new File(path, Objects.requireNonNull(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path + fileName;//保存成功后返回路径
    }

    /**
     * 添加品牌
     * @param vehicleBrand 品牌信息体
     * @return
     */
    @PostMapping("/insertBrand")
    public R<String> insertBrand(@RequestBody VehicleBrand vehicleBrand){

        log.info("/vehicle/insertBrand post -> insertBrand: vehicleBrand = {}; 添加品牌信息", vehicleBrand.toString());
        if(vehicleBrand.getBrand().length() > 32)
            return R.error("品牌名称长度不可超过32个字符");
        if(vehicleBrand.getBrand().equals(""))
            return R.error("品牌名称不能为空");

        //更新时间
        //LocalDateTime localDateTime = LocalDateTime.now();
        //vehicleBrand.setCreateTime(localDateTime);
        //vehicleBrand.setUpdateTime(localDateTime);

        //保存品牌信息
        vehicleBrandService.save(vehicleBrand);

        return R.success("车辆品牌信息添加成功");
    }

    /**
     * 添加型号
     * @param vehicleModel 型号信息体
     * @return
     */
    @PostMapping("/insertModel")
    public R<String> insertModel(@RequestBody VehicleModel vehicleModel){

        log.info("/vehicle/insertModel post -> insertModel : vehicleModel = {}; 添加型号信息", vehicleModel.toString());
        if(vehicleBrandService.getById(vehicleModel.getVehicleBrandId()) == null)
            return R.error("型号对应的车辆品牌不存在，请检查重试");
        if(vehicleModel.getModel().length() > 32)
            return R.error("型号名称长度不可超过32个字符");
        if(vehicleModel.getModel().equals(""))
            return R.error("型号名称不能为空");
        if(vehicleModel.getType().length() > 16)
            return R.error("型号类型长度不可超过16个字符");
        if(vehicleModel.getType().equals(""))
            return R.error("型号类型不能为空");
        if(vehicleModel.getEnergy().length() > 16)
            return R.error("型号能源使用方式长度不可超过16个字符");
        if(vehicleModel.getEnergy().equals(""))
            return R.error("型号能源使用方式不能为空");

        //更新时间
        //LocalDateTime localDateTime = LocalDateTime.now();
        //vehicleBrand.setCreateTime(localDateTime);
        //vehicleBrand.setUpdateTime(localDateTime);

        //保存品牌信息
        vehicleModelService.save(vehicleModel);

        return R.success("车辆型号信息添加成功");
    }

    /**
     * 添加车辆
     * @param vehicleCar 车辆信息体
     * @return
     */
    @PostMapping("/insertCar")
    public R<String> insertCar(@RequestBody VehicleCar vehicleCar){

        log.info("/vehicle/insertCar post -> insertCar : vehicleCar = {}; 添加车辆信息", vehicleCar.toString());
        if(vehicleModelService.getById(vehicleCar.getVehicleModelId()) == null)
            return R.error("车辆对应的车辆型号不存在，请检查重试");
        if(vehicleCar.getName().length() > 32)
            return R.error("车辆名称长度不可超过32个字符");
        if(vehicleCar.getName().equals(""))
            return R.error("车辆名称不能为空");
        if(vehicleCar.getCarNumber().length() > 7)
            return R.error("车辆车牌号长度不可超过7个字符");
        if(vehicleCar.getCarNumber().length() < 6)
            return R.error("车辆车牌号长度不可低于6个字符");

        //更新时间
        //LocalDateTime localDateTime = LocalDateTime.now();
        //vehicleBrand.setCreateTime(localDateTime);
        //vehicleBrand.setUpdateTime(localDateTime);

        //保存品牌信息
        vehicleCarService.save(vehicleCar);

        return R.success("车辆信息添加成功");
    }

    /**
     * 删除品牌
     * @param vehicleBrand 品牌信息体
     * @return
     */
    @DeleteMapping ("/deleteBrand")
    public R<String> deleteBrand(@RequestBody VehicleBrand vehicleBrand){

        log.info("/vehicle/deleteBrand delete -> deleteBrand: vehicleBrand = {}; 删除品牌信息", vehicleBrand.toString());

        //获取品牌id
        BigInteger vehicleBrandId = vehicleBrand.getId();
        //1.删除车辆信息
        //1.1 查询品牌下的所有车辆型号
        LambdaQueryWrapper<VehicleModel> vmlqw = new LambdaQueryWrapper<>();
        vmlqw.eq(VehicleModel::getVehicleBrandId, vehicleBrandId);
        List<VehicleModel> vehicleModelList = vehicleModelService.list(vmlqw);
        //1.2 删除品牌下所有型号下的所有车辆
        LambdaQueryWrapper<VehicleCar> vclqw = new LambdaQueryWrapper<>();
        vehicleModelList.forEach(e -> vclqw.eq(VehicleCar::getVehicleModelId, e.getId()));
        vehicleCarService.remove(vclqw);

        //2.删除型号信息
        vehicleModelService.remove(vmlqw);

        //3.删除品牌信息
        vehicleBrandService.removeById(vehicleBrandId);

        return R.success("车辆品牌信息删除成功");
    }

    /**
     * 删除型号
     * @param vehicleModel 型号信息体
     * @return
     */
    @DeleteMapping ("/deleteModel")
    public R<String> deleteModel(@RequestBody VehicleModel vehicleModel){

        log.info("/vehicle/deleteModel delete -> deleteModel: vehicleModel = {}; 删除型号信息", vehicleModel.toString());

        //获取型号id
        BigInteger vehicleModelId = vehicleModel.getId();
        //1.删除车辆信息
        //1.1 删除型号下的所有车辆
        LambdaQueryWrapper<VehicleCar> vclqw = new LambdaQueryWrapper<>();
        vclqw.eq(VehicleCar::getVehicleModelId, vehicleModelId);
        vehicleCarService.remove(vclqw);

        //2.删除型号信息
        vehicleBrandService.removeById(vehicleModelId);

        return R.success("车辆型号信息删除成功");
    }

    /**
     * 删除车辆
     * @param vehicleCar 车辆信息体
     * @return
     */
    @DeleteMapping ("/deleteCar")
    public R<String> deleteCar(@RequestBody VehicleCar vehicleCar){

        log.info("/vehicle/deleteCar delete -> deleteCar: vehicleCar = {}; 删除车辆信息", vehicleCar.toString());

        vehicleCarService.removeById(vehicleCar.getId());

        return R.success("车辆型号信息删除成功");
    }


    /**
     * 修改品牌
     * @param vehicleBrand 品牌信息体
     * @return
     */
    @PostMapping("/updateBrand")
    public R<String> updateBrand(@RequestBody VehicleBrand vehicleBrand){

        log.info("/vehicle/updateBrand post -> updateBrand: vehicleBrand = {}; 修改品牌信息", vehicleBrand.toString());

        if(vehicleBrand.getBrand().length() > 32)
            return R.error("品牌名称长度不可超过32个字符");
        if(vehicleBrand.getBrand().equals(""))
            return R.error("品牌名称不能为空");
        //更新品牌信息
        vehicleBrandService.updateById(vehicleBrand);

        return R.success("车辆品牌信息修改成功");
    }

    /**
     * 修改型号
     * @param vehicleModel 型号信息体
     * @return
     */
    @PostMapping("/updateModel")
    public R<String> updateModel(@RequestBody VehicleModel vehicleModel){

        log.info("/vehicle/updateModel post -> updateModel: vehicleModel = {}; 修改型号信息", vehicleModel.toString());

        if(vehicleBrandService.getById(vehicleModel.getVehicleBrandId()) == null)
            return R.error("型号对应的车辆品牌不存在，请检查重试");
        if(vehicleModel.getModel().length() > 32)
            return R.error("型号名称长度不可超过32个字符");
        if(vehicleModel.getModel().equals(""))
            return R.error("型号名称不能为空");
        if(vehicleModel.getType().length() > 16)
            return R.error("型号类型长度不可超过16个字符");
        if(vehicleModel.getType().equals(""))
            return R.error("型号类型不能为空");
        if(vehicleModel.getEnergy().length() > 16)
            return R.error("型号能源使用方式长度不可超过16个字符");
        if(vehicleModel.getEnergy().equals(""))
            return R.error("型号能源使用方式不能为空");
        //更新型号信息
        vehicleModelService.updateById(vehicleModel);

        return R.success("车辆型号信息修改成功");
    }

    /**
     * 修改车辆
     * @param vehicleCar 车辆信息体
     * @return
     */
    @PostMapping("/updateCar")
    public R<String> updateCar(@RequestBody VehicleCar vehicleCar){

        log.info("/vehicle/updateCar post -> updateCar: vehicleCar = {}; 修改车辆信息", vehicleCar.toString());

        if(vehicleModelService.getById(vehicleCar.getVehicleModelId()) == null)
            return R.error("车辆对应的车辆型号不存在，请检查重试");
        if(vehicleCar.getName().length() > 32)
            return R.error("车辆名称长度不可超过32个字符");
        if(vehicleCar.getName().equals(""))
            return R.error("车辆名称不能为空");
        if(vehicleCar.getCarNumber().length() > 7)
            return R.error("车辆车牌号长度不可超过7个字符");
        if(vehicleCar.getCarNumber().length() < 6)
            return R.error("车辆车牌号长度不可低于6个字符");

        //更新时间
        //LocalDateTime localDateTime = LocalDateTime.now();
        //vehicleBrand.setCreateTime(localDateTime);
        //vehicleBrand.setUpdateTime(localDateTime);

        //更新车辆信息
        vehicleCarService.updateById(vehicleCar);

        return R.success("车辆信息修改成功");
    }

    /**
     * 查找品牌列表
     * @param name 查找字符串
     * @return
     */
    @GetMapping("/listBrand")
    public R<List<VehicleBrandVo>> listBrand(@RequestParam("name") String name){
        log.info("/vehicle/listBrand get -> listBrand: name = {}; 品牌列表查询", name);
        //log.info(String.valueOf((Integer) httpSession.getAttribute("admin")));
        //1.获取品牌信息体的分页
        LambdaQueryWrapper<VehicleBrand> queryWrapper = new LambdaQueryWrapper<>();
        //2.如果有查找则按字符串查找
        queryWrapper.like(!StringUtils.isEmpty(name), VehicleBrand::getBrand, name);
        queryWrapper.like(!StringUtils.isEmpty(name), VehicleBrand::getInitial, name);
        //3.正序排序
        //queryWrapper.orderByAsc(VehicleBrand::getBrand);
        //4.数据导入info
        List<VehicleBrand> list = vehicleBrandService.list(queryWrapper);
        TreeMap<String, List<VehicleBrand>> treeMap = new TreeMap<>();
        list.forEach(e -> {
            String initial = e.getInitial();
            if(treeMap.containsKey(initial)){
                List<VehicleBrand> vehicleBrandList = treeMap.get(initial);
                vehicleBrandList.add(e);
                treeMap.put(initial, vehicleBrandList);
            }else{
                List<VehicleBrand> vehicleBrandList = new LinkedList<>();
                vehicleBrandList.add(e);
                treeMap.put(initial, vehicleBrandList);
            }
        });

        List<VehicleBrandVo> vehicleBrandVoList = new LinkedList<>();

        for(Map.Entry<String,List<VehicleBrand>> entry : treeMap.entrySet()){
            VehicleBrandVo vehicleBrandVo = new VehicleBrandVo();
            vehicleBrandVo.setInitial(entry.getKey());
            vehicleBrandVo.setBrandList(entry.getValue());
            vehicleBrandVoList.add(vehicleBrandVo);
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }
        return R.success(vehicleBrandVoList);
    }

    /**
     * 根据品牌ID查询型号列表
     * @param id 品牌id
     * @return
     */
    @GetMapping("/findModelList")
    public R<List<VehicleModel>> findModelList(@RequestParam("brandId") BigInteger id){
        log.info("/vehicle/findModelList get -> findModelList: brandId = {}; 根据品牌ID查询型号列表", id);
        //log.info(String.valueOf((Integer) httpSession.getAttribute("admin")));
        LambdaQueryWrapper<VehicleModel> queryWrapper = new LambdaQueryWrapper<>();
        //按品牌id查找
        queryWrapper.eq(VehicleModel::getVehicleBrandId, id);
        //4.数据导入info
        List<VehicleModel> list = vehicleModelService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 根据品牌名称查询租用次数最多的1~5辆车
     * @param name 品牌名称
     * @return
     */
    @GetMapping("/findHotCarList")
    public R<List<HotCarVo>> findHotCarList(@RequestParam("brand") String name){
        log.info("/vehicle/findHotCarList get -> findHotCarList: brandId = {}; 根据品牌名称查询租用次数最多的1~5辆车", name);
        IPage<VehicleCar> pageInfo = new Page<>(1, 5);
        LambdaQueryWrapper<VehicleCar> queryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<VehicleCarImg> vciqw = new LambdaQueryWrapper<>();

        //确定品牌
        queryWrapper.eq(VehicleCar::getBrand, name);
        //大于0次评价
        queryWrapper.gt(VehicleCar::getEvaluateCount, 0);
        //租车次数倒序排序
        queryWrapper.orderByDesc(VehicleCar::getCount);

        //获取到租车次数前五且有评价的分页列表
        vehicleCarService.page(pageInfo, queryWrapper);
        List<BigInteger> list = new LinkedList<>();
        Map<BigInteger, VehicleCar> vehicleCarMap = new HashMap<>();
        //获取这几辆车的主键id，通过id去查询对应的车辆图片
        pageInfo.getRecords().forEach(e -> {
            list.add(e.getId());
            vehicleCarMap.putIfAbsent(e.getId(), e);
        });
        vciqw.in(VehicleCarImg::getVehicleCarId, list);
        //将得到的车辆图片存入列表
        List<VehicleCarImg> vehicleCarImgList = vehicleCarImgService.list(vciqw);
        //通过hashmap对多余的图片去重（只需要第一张图片用于在型号也的轮播图中展示）
        Map<BigInteger, VehicleCarImg> map = new HashMap<>();
        /*Collections.reverse(vehicleCarImgList);
        for(VehicleCarImg item : vehicleCarImgList){
            map.put(item.getVehicleCarId(), item);
        }*/
        for(VehicleCarImg item : vehicleCarImgList){
            map.putIfAbsent(item.getVehicleCarId(), item);
        }
        System.out.println(vehicleCarImgList.toString());

        //获得这些车辆对应的最新评价列表
        List<Evaluate> newUpdateTimeEvaluateList = evaluateService.findNewUpdateTimeEvaluateList(list);
        System.out.println(newUpdateTimeEvaluateList.toString());

        //通过所获得的最新的评价列表来确定最终展示的热门车辆信息
        //也就是车辆：租车次数多 且 评价至少有一条，才被认为是热门车辆，用于展示
        List<HotCarVo> hotCarVoList = new LinkedList<>();

        newUpdateTimeEvaluateList.forEach(e -> {
            //获取评价的用户
            User user = userService.getById(e.getUserId());
            HotCarVo hotCarVo = new HotCarVo();
            //1.通过Evaluate对象对应的车辆id查询到VehicleCar对象
            VehicleCar vehicleCar = vehicleCarMap.get(e.getVehicleCarId());
            //2.通过VehicleCar对象的id查询到VehicleCarImg对象
            VehicleCarImg vehicleCarImg = map.get(vehicleCar.getId());

            //3.对hotCarVo赋值

            hotCarVo.setName(vehicleCar.getName());
            hotCarVo.setVehicleCarId(e.getVehicleCarId());
            hotCarVo.setImg(vehicleCarImg.getImg());
            hotCarVo.setUserId(user.getId());
            hotCarVo.setUserImg(user.getImg());
            hotCarVo.setEvaluate(e.getEvaluate());
            hotCarVo.setCount(vehicleCar.getCount());
            hotCarVo.setScore(e.getScore());
            hotCarVo.setPrice(vehicleCar.getPrice());

            //4.将hotCarVo加入hotCarVoList
            hotCarVoList.add(hotCarVo);
        });
        if(hotCarVoList.size() == 0)
            return R.error("");
        return R.success(hotCarVoList);
    }

    /**
     * 首页展示车型
     * @param page 页码
     * @param area 地区
     * @return
     */
    @GetMapping("/modelPage")
    public R<IPage<VehicleModel>> modelPage(@RequestParam("page") Integer page, @RequestParam("area") String area){
        log.info("/vehicle/modelPage get -> modelPage: page = {}， area = {}, 首页展示车型", page, area);
        //log.info(String.valueOf((Integer) httpSession.getAttribute("admin")));
        IPage<VehicleModel> pageInfo = new Page<>(page, 10);
        //1.获取型号信息体的分页
        LambdaQueryWrapper<VehicleModel> queryWrapper = new LambdaQueryWrapper<>();
        //2.根据地区选择

        queryWrapper.in(VehicleModel::getArea, area, "全部");
        //3.正序排序
        queryWrapper.orderByDesc(VehicleModel::getRecommend);
        //4.数据导入pageInfo
        vehicleModelService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 查找车辆
     * @param findCarPageDto 分页查询封装类
     * @return
     */
    @PostMapping("/findCarPage")
    public R<IPage<VehicleCar>> findCarPage(@RequestBody FindCarPageDto findCarPageDto){
        log.info("/vehicle/findCarPage post -> findCarPageDto = {}; 车辆分页查询", findCarPageDto.toString());
        if(findCarPageDto.getStartTime() == null || findCarPageDto.getEndTime() == null)
            throw new RuntimeException("查询时间不能为空");

        //1.品牌信息体的分页数据存放
        IPage<VehicleCar> pageInfo = new Page<>(findCarPageDto.getPage(), findCarPageDto.getPageSize());

        LambdaQueryWrapper<VehicleCar> queryWrapper = new LambdaQueryWrapper<>();

        //2.1 通过选择的停车场的ID查找对应的停车场车辆
        queryWrapper.eq(VehicleCar::getParkadeId, findCarPageDto.getParkadeId());

        //2.2 根据选择的停车场，通过订单表查询到获得当前时间段可用的车辆id (除去：3已完成、6已退车和7已取消三种状态)
        List<VehicleOrder> vehicleOrderList = vehicleOrderService.findTimeFrameVehicleCarIdList(findCarPageDto.getStartTime(), findCarPageDto.getEndTime());
        List<BigInteger> vehicleCarIdList = new LinkedList<>();
        vehicleOrderList.forEach(e -> vehicleCarIdList.add(e.getVehicleCarId()));
        if(vehicleCarIdList.size() > 0)
            queryWrapper.notIn(VehicleCar::getId, vehicleCarIdList);

        //2.3 类型
        if(!findCarPageDto.getTypeList().get(0).equals("全部"))
            queryWrapper.in(VehicleCar::getType, findCarPageDto.getTypeList());

        //2.4 价格区间
        if(!findCarPageDto.getStartPrice().equals(new BigDecimal(-1))){
            queryWrapper.ge(VehicleCar::getPrice, findCarPageDto.getStartPrice());
            if(!findCarPageDto.getEndPrice().equals(new BigDecimal(-1)))
                queryWrapper.le(VehicleCar::getPrice, findCarPageDto.getEndPrice());
        }

        if(!findCarPageDto.getNln()){
            //2.5 是否会员免押
            if(findCarPageDto.getNoDeposit()){
                queryWrapper.eq(VehicleCar::getNoDeposit, findCarPageDto.getNoDeposit());
            }

            //2.6 是否豪华
            if(findCarPageDto.getLuxury()){
                queryWrapper.eq(VehicleCar::getLuxury, findCarPageDto.getLuxury());
            }

            //2.7 是否新能源
            if(findCarPageDto.getNewEnergy()){
                queryWrapper.eq(VehicleCar::getNewEnergy, findCarPageDto.getNewEnergy());
            }
        }

        //3.排序（价格）
        if(findCarPageDto.getSort() == 1)
            queryWrapper.orderByAsc(VehicleCar::getPrice);
        else if(findCarPageDto.getSort() == 2)
            queryWrapper.orderByDesc(VehicleCar::getPrice);
        queryWrapper.orderByDesc(VehicleCar::getNoDeposit, VehicleCar::getLuxury, VehicleCar::getNewEnergy);

        //4.数据导入info
        vehicleCarService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 查找停车场
     * @param area 校区查询
     * @return
     */
    @GetMapping("/listParkade")
    public R<List<Parkade>> listParkade(@RequestParam("area") String area){
        log.info("/vehicle/listParkade get -> area = {}; 停车场列表查询", area);

        List<Parkade> parkadeList;
        ListOperations<String, Parkade> parkadeListOperationsOperations = (ListOperations<String, Parkade>)redisTemplate.opsForList();
        if(area.equals("永川")){
            parkadeList = parkadeListOperationsOperations.range(RedisConstants.PARKADE_KEY_yongchuan, 0, -1);
        }else{
            parkadeList = parkadeListOperationsOperations.range(RedisConstants.PARKADE_KEY_banan, 0, -1);
        }

        //如果没有缓存，则访问数据库
        if(parkadeList.size() == 0){
            LambdaQueryWrapper<Parkade> lqw = new LambdaQueryWrapper<>();
            lqw.eq(StringUtils.hasLength(area), Parkade::getAreaSchool, area);
            parkadeList = parkadeService.list(lqw);
            if(area.equals("永川")){
                parkadeListOperationsOperations.leftPushAll(RedisConstants.PARKADE_KEY_yongchuan, parkadeList);
            }else{
                parkadeListOperationsOperations.leftPushAll(RedisConstants.PARKADE_KEY_banan, parkadeList);
            }
            redisTemplate.expire(RedisConstants.PARKADE_KEY_yongchuan, 1L, TimeUnit.DAYS);
            redisTemplate.expire(RedisConstants.PARKADE_KEY_banan, 1L, TimeUnit.DAYS);
        }

        //如果有缓存
        log.info(parkadeList.toString());
        return R.success(parkadeList);
    }

    /**
     * 查找单个车辆对应的所有图片
     * @param id 车辆的id
     * @return
     */
    @GetMapping("/listCarImg")
    public R<List<VehicleCarImg>> listCarImg(@RequestParam("id") BigInteger id){
        log.info("/vehicle/listCarImg get -> id = {}; 单个车辆的图片列表查询", id);
        List<VehicleCarImg> vehicleCarImgList;
        ListOperations<String, VehicleCarImg> vehicleCarImgValueOperations = (ListOperations<String, VehicleCarImg>) redisTemplate.opsForList();
        vehicleCarImgList = vehicleCarImgValueOperations.range(RedisConstants.CAR_IMG_KEY + id, 0, -1);
        if(vehicleCarImgList.size() == 0){
            LambdaQueryWrapper<VehicleCarImg> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(VehicleCarImg::getVehicleCarId, id);
            vehicleCarImgList = vehicleCarImgService.list(lambdaQueryWrapper);
            vehicleCarImgValueOperations.leftPushAll(RedisConstants.CAR_IMG_KEY + id, vehicleCarImgList);
            redisTemplate.expire(RedisConstants.CAR_IMG_KEY + id, 1L, TimeUnit.DAYS);
        }
        return R.success(vehicleCarImgList, "获取车辆图片数据");
    }


}

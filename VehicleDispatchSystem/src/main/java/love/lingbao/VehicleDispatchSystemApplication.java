package love.lingbao;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@MapperScan("love.lingbao.mapper")//使用MapperScan批量扫描所有的Mapper接口；
public class VehicleDispatchSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehicleDispatchSystemApplication.class, args);
        log.info("项目启动成功。。。");
    }

}

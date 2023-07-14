package love.lingbao.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collect implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigInteger id;         //int auto_increment comment '主键id'
    private BigInteger userId;         //int auto_increment comment '用户id'
    private BigInteger vehicleCarId;         //int auto_increment comment '车辆id'
    @TableField(fill = FieldFill.INSERT) //插入时填充字段
    private LocalDateTime createTime;   //创建时间
}

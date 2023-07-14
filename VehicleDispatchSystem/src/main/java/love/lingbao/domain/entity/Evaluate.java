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
public class Evaluate implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigInteger id;//bigint auto_increment
    private BigInteger userId;//bigint      default 0                 not null comment '评价的用户id',
    private BigInteger vehicleCarId;//bigint      default 0                 not null comment '评价的车辆id',
    private Integer score;//tinyint     default 5                 not null comment '对车辆评分',
    private String evaluate;//varchar(80) default ''                not null comment '对车辆评价',
    @TableField(fill = FieldFill.INSERT) //插入时填充字段
    private LocalDateTime createTime;//datetime    default CURRENT_TIMESTAMP not null comment '评价创建时间',
    @TableField(fill = FieldFill.INSERT_UPDATE) //插入和更新时填充字段
    private LocalDateTime updateTime;//datetime    default CURRENT_TIMESTAMP not null comment '评价更新时间'
}

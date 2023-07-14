package love.lingbao.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Parkade implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigInteger id;//bigint auto_increment
    private String areaSchool;//enum ('永川', '巴南') default '永川' not null comment '停车场所在校区',
    private String name;//varchar(32)           default ''     not null comment '停车场名字',
    private String longitude;//varchar(16)           default ''     not null comment '经度',
    private String latitude;//varchar(16)           default ''     not null comment '纬度'
    private String img;//varchar(255)           default ''     not null comment '封面图'
}

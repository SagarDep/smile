package me.zsj.smile.model;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * Created by zsj on 2015/11/4 0004.
 */

@Table("girlCollect")
public class GirlCollect extends BaseModel{

    @Column("girlUrl") public String girlUrl;
    @Column("girlDate") public String girlDate;
    @Column("width") public int width;
    @Column("height") public int height;

    @Override
    public String toString() {
        return "GirlCollect{" +
                "girlUrl='" + girlUrl + '\'' +
                ", girlDate='" + girlDate + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}

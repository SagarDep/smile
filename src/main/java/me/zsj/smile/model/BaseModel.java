package me.zsj.smile.model;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;

import java.io.Serializable;

/**
 * Created by zsj on 2015/11/4 0004.
 */
public class BaseModel implements Serializable{

    @PrimaryKey(PrimaryKey.AssignType.AUTO_INCREMENT)
    @Column("_id")
    public int id;
}

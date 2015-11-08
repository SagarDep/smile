package me.zsj.smile.model;

import java.util.Date;

/**
 * Created by zsj on 2015/10/3 0003.
 */
public class Gank {

    public String url;
    public String type;
    public String desc;
    public String who;
    public boolean used;
    public Date createdAt;
    public Date updatedAt;


    @Override
    public String toString() {
        return "Gank{" +
                "url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", desc='" + desc + '\'' +
                ", who='" + who + '\'' +
                ", used=" + used +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

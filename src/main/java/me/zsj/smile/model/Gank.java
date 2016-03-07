package me.zsj.smile.model;

import java.util.Date;

/**
 * Created by zsj on 2015/10/3 0003.
 */
public class Gank {

 /*   public String url;
    public String type;
    public String desc;
    public String who;
    public boolean used;
    public Date createdAt;
    public Date updatedAt;*/


    public String _id;
    public String _ns;
    public Date createdAt;
    public String desc;
    public Date publishedAt;
    public String source;
    public String type;
    public String url;
    public boolean used;
    public String who;

    @Override
    public String toString() {
        return "Gank{" +
                "_id='" + _id + '\'' +
                ", _ns='" + _ns + '\'' +
                ", createdAt=" + createdAt +
                ", desc='" + desc + '\'' +
                ", publishedAt=" + publishedAt +
                ", source='" + source + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", used=" + used +
                ", who='" + who + '\'' +
                '}';
    }
}

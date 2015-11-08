package me.zsj.smile.model;


/**
 * Created by zsj on 6/20/15.
 */
public class Meizhi {

    public String url;
    public String who;
    public String publishedAt;
    public String desc;
    public boolean used;
    public String createdAt;
    public String updatedAt;
    public String type;
    public String objectId;


    @Override
    public String toString() {
        return "Meizhi{" +
                "who='" + who + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                ", desc='" + desc + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", used=" + used +
                ", objectId='" + objectId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
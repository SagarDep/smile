package me.zsj.smile.model;


/**
 * Created by zsj on 6/20/15.
 */
public class Meizhi {

   /* public String url;
    public String who;
    public String publishedAt;
    public String desc;
    public boolean used;
    public String createdAt;
    public String updatedAt;
    public String type;
    public String objectId;*/

    public String _id;
    public String _ns;
    public String createdAt;
    public String desc;
    public String publishedAt;
    public String source;
    public String type;
    public String url;
    public boolean used;
    public String who;

    @Override
    public String toString() {
        return "Meizhi{" +
                "_id='" + _id + '\'' +
                ", _ns='" + _ns + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", desc='" + desc + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                ", source='" + source + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", used=" + used +
                ", who='" + who + '\'' +
                '}';
    }
}
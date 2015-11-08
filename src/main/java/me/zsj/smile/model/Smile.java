package me.zsj.smile.model;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

@Table("smile")
public class Smile extends BaseModel{

	@Column("smileContent") public String smileContent;
	@Column("title") public String title;
	@Column("titleUrl") public String titleUrl;

	@Override
	public String toString() {
		return "Smile{" +
				"smileContent='" + smileContent + '\'' +
				", title='" + title + '\'' +
				", titleUrl='" + titleUrl + '\'' +
				'}';
	}
}

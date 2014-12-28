package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class Image extends Model {
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;
	
	@Required
	@Column(unique = true)
	public String url;

	public Image(String url) {
		this.url = url;
	}
	
	public static Finder<Long,Image> find = new Finder<Long,Image>(
			Long.class, Image.class
    );
}

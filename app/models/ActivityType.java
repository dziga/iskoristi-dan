package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class ActivityType extends Model {
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;
	@Required
	@Column(unique = true)
	public String name;
	
	public static Finder<Long,ActivityType> find = new Finder<Long,ActivityType>(
			Long.class, ActivityType.class
    );
}

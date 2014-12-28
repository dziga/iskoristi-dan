package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Activity extends Model {
	private static final long serialVersionUID = 1L;
	
	@Id
	public Long id;
	@Required
	@Column(unique = true)
	public String name;
	@ManyToMany(cascade = CascadeType.ALL)
	public List<Recommendation> recommendations;
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Required
	public ActivityType type;
	@ManyToMany(cascade = CascadeType.ALL)
	public List<Tag> tags;
	public boolean active;
	@ManyToMany(cascade = CascadeType.ALL)
	public List<Image> images;
	
	public Activity(Long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Activity(String name) {
		this.id = Activity.find.where().eq("name", name).findUnique().id;
		this.name = name;
	}
	
	public static Finder<Long,Activity> find = new Finder<Long,Activity>(
			Long.class, Activity.class
    );
}

package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Activity extends Model {
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;
	
	@Required
	@Column(unique = true)
	public String name;
	@Lob
	public String description;
	public Duration duration;
	public DateTime startTime;
	public DateTime endTime;
	public String imageUrl;
	@ManyToMany(cascade = CascadeType.ALL)
	public List<Tag> tags;
	public boolean active;
	
	public static Finder<Long,Activity> find = new Finder<Long,Activity>(
		Long.class, Activity.class
    );
	
}

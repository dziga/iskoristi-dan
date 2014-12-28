package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Recommendation extends Model {
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
	@ManyToMany(cascade = CascadeType.ALL)
	public List<Image> images;
	public boolean active;
	
	public static Finder<Long,Recommendation> find = new Finder<Long,Recommendation>(
		Long.class, Recommendation.class
    );
	
}
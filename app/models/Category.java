package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category extends Model {
	private static final long serialVersionUID = 1L;
	
	@Id
	public Long id;
	@Required
	@Column(unique = true)
	public String name;
	@OneToMany(cascade = CascadeType.ALL)
	public List<Activity> activities;
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Required
	public CategoryType type;
	
	public Category(Long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Category(String name) {
		this.id = Category.find.where().eq("name", name).findUnique().id;
		this.name = name;
	}
	
	public static Finder<Long,Category> find = new Finder<Long,Category>(
			Long.class, Category.class
    );
}

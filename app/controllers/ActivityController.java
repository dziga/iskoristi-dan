package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Activity;
import models.ActivityType;
import models.Recommendation;
import models.Tag;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ActivityController extends Controller {
	
	public static Result addActivity() {
		return upsertActivity(null);
	}
	
	public static Result getActivity(Long id) {
		Activity activity = Activity.find.byId(id);
		if(activity==null) {
			return notFound("Activity not found");
		}
		activity.type = ActivityType.find.byId(activity.type.id);
		return ok(Json.toJson(activity));
	}
	
	public static Result getActivities() {
		List<Activity> activities = Activity.find.all();
		if (activities==null) {
			return notFound("There are no activities");
		}
		return ok(Json.toJson(activities));
	}
	
	public static Result updateActivity(Long id) {
		return upsertActivity(id);
	}
	
	public static Result deleteActivity(Long id) {
		Activity activity = Activity.find.byId(id);
		if(activity==null) {
			return notFound("Activity not found");
		}
		activity.delete();
		return ok();
	}
	
	public static Result uploadImages() {
		 return FilesController.uploadImages("dir.images.activities");
	}
	
	public static Result getRecommendations(Long id) {
		Activity activity = Activity.find.byId(id);
		if (activity == null) {
			notFound("Activity not found");
		}
		List<Recommendation> recommendations = activity.recommendations;
		if (recommendations.size()==0) {
			notFound("No recommendations in this activity");
		}
		return ok(Json.toJson(recommendations));
	}
	
	private static Result upsertActivity(Long id) {
		JsonNode json = request().body().asJson();
		
		if(json == null) {
			return badRequest("BadJson");
		} 
		else {
			ObjectMapper mapper = new ObjectMapper();
			Activity activity = mapper.convertValue(json, Activity.class);
			try {
				activity.type = ActivityType.find.byId(json.get("activityType").asLong());
				Logger.debug("Activity type found with name {}", activity.type.name);
				if (activity.type == null) {
					return notFound("Unexisting activity type");
				}
			}
			catch (NullPointerException e) {
				return badRequest("Activity type is not provided");
			}
			List<Tag> providedTags = activity.tags;
			Logger.debug("Number of provided tags {}", activity.tags.size());
			List<Tag> newTags = saveNewTagsAndGetFullListOfTags(providedTags);
			activity.tags.clear();
			activity.tags.addAll(newTags);
			if (id!=null) {
				activity.id = id;
				activity.update();
			}
			else {
				activity.save();
			}
			return ok();
		}
	}
	
	private static List<Tag> saveNewTagsAndGetFullListOfTags(List<Tag> tags) {
		List<Tag> savedTags = new ArrayList<>();
		for (Tag tag: tags) {
			Tag existingTag = (Tag) Tag.find.where().eq("name", tag.name).findUnique();
			if (existingTag == null) {
				tag.save();
				if(!savedTags.contains(tag)) {
					Logger.debug("Saving tag with name {}", tag.name);
					savedTags.add(tag);
				}
			}
			else {
				if(!savedTags.contains(existingTag)) {
					Logger.debug("Adding existing tag with name {}", existingTag.name);
					savedTags.add(existingTag);
				}
			}
		}
		return savedTags;
	}
}

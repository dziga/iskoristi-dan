package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.Activity;
import models.Category;
import models.Tag;

import org.apache.commons.io.FileUtils;

import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class ActivityController extends Controller {
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result addActivity() {
		return upsertAction(null);
	}
	
	public static Result getActivities() {
		List<Activity> activities = Activity.find.all();
		if (activities.size() == 0) {
			return noContent();
		}
		return ok(Json.toJson(activities));
	}
	
	public static Result getActivity(Long id) {
		Activity activity = Activity.find.byId(id);
		if (activity == null) {
			return noContent();
		}
		return ok(Json.toJson(activity));
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result updateActivity(Long id) {
		return upsertAction(id);
	}
	
	public static Result deleteActivity(Long id) {
		Activity activity = Activity.find.byId(id);
		if (activity == null) {
			return notFound("Activity not found!");
		}
		Category category = Category.find.where().eq("activities.id", id).findUnique();
		if (category == null) {
			return internalServerError("Category of action not found!");
		}
		category.activities.remove(activity);
		category.save();
		activity.delete();
		return ok();
	}
	
	public static Result upload() {
		  MultipartFormData body = request().body().asMultipartFormData();
		  List<FilePart> images = body.getFiles();
		  
		  if (images.size() > 0) {
			for (FilePart image: images) {
			    File file = image.getFile();
			    try {
					FileUtils.moveFile(file, new File(Play.application().configuration().getString("dir.images.activities"), file.getName()));
				} catch (IOException e) {
					Logger.debug("Problem moving file {}", image.getFilename());
					Logger.error(e.getMessage());
					return internalServerError("Problem uploading file");
				}
			    Logger.debug("Uploading image {} at location {} with type {}", image.getFilename(), file.getAbsolutePath(), image.getContentType());
			}
		    return ok();
		  } else {
			Logger.debug("fail");
		    return badRequest();   
		  }
	}
	
	private static Result upsertAction(Long id) {
		JsonNode json = request().body().asJson();
		
		if(json == null) {
			return badRequest("BadJson");
		} 
		else {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JodaModule());
			Activity activity = mapper.convertValue(json, Activity.class);
			Long newCategoryId = json.findValue("categoryId").asLong();
			List<Tag> providedTags = activity.tags;
			Logger.debug("Number of provided tags {}", activity.tags.size());
			List<Tag> newTags = saveNewTagsAndGetFullListOfTags(providedTags);
			activity.tags.clear();
			activity.tags.addAll(newTags);
			Category category = Category.find.byId(newCategoryId);
			
			if (id!=null) {
				Category oldCategory = Category.find.where().eq("activities.id", id).findUnique();
				if (oldCategory == null || newCategoryId.equals(category.id)) {
					oldCategory = Category.find.byId(newCategoryId);
					oldCategory.update();
				}
				activity.id = id;
				activity.update(); 
			}
			else {
				activity.save();
			}
			category.activities.add(activity);
			category.update();
			activity.saveManyToManyAssociations("tags");
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

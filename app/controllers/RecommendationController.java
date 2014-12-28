package controllers;

import java.util.List;

import models.Activity;
import models.Recommendation;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class RecommendationController extends Controller {
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result addRecommendation() {
		return upsertRecommendation(null);
	}
	
	public static Result getRecommendations() {
		List<Recommendation> recommendations = Recommendation.find.all();
		if (recommendations.size() == 0) {
			return noContent();
		}
		return ok(Json.toJson(recommendations));
	}
	
	public static Result getRecommendation(Long id) {
		Recommendation recommendation = Recommendation.find.byId(id);
		if (recommendation == null) {
			return noContent();
		}
		return ok(Json.toJson(recommendation));
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result updateRecommendation(Long id) {
		return upsertRecommendation(id);
	}
	
	public static Result deleteRecommendation(Long id) {
		Recommendation recommendation = Recommendation.find.byId(id);
		if (recommendation == null) {
			return notFound("Activity not found!");
		}
		Activity activity = Activity.find.where().eq("recommendations.id", id).findUnique();
		if (activity == null) {
			return internalServerError("Activity of recommendation not found!");
		}
		activity.recommendations.remove(recommendation);
		activity.save();
		recommendation.delete();
		return ok();
	}
	
	public static Result uploadImages() {
		 return FilesController.uploadImages("dir.images.recommendations");
	}
	
	private static Result upsertRecommendation(Long id) {
		JsonNode json = request().body().asJson();
		
		if(json == null) {
			return badRequest("BadJson");
		} 
		else {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JodaModule());
			Recommendation recommendation = mapper.convertValue(json, Recommendation.class);
			Long newCategoryId = json.findValue("categoryId").asLong();
			Activity activity = Activity.find.byId(newCategoryId);
			
			if (id!=null) {
				Activity oldActivity = Activity.find.where().eq("activities.id", id).findUnique();
				if (oldActivity == null || newCategoryId.equals(activity.id)) {
					oldActivity = Activity.find.byId(newCategoryId);
					oldActivity.update();
				}
				recommendation.id = id;
				recommendation.update(); 
			}
			else {
				recommendation.save();
			}
			activity.recommendations.add(recommendation);
			activity.update();
			recommendation.saveManyToManyAssociations("tags");
			return ok();
		}
	}
}

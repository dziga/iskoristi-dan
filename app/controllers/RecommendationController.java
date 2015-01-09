package controllers;

import java.io.IOException;
import java.util.List;

import models.Activity;
import models.Image;
import models.Recommendation;
import play.Play;
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
	
	public static Result uploadImages(Long id) {
			Recommendation recommendation = Recommendation.find.byId(id);
			if (recommendation==null) {
				return notFound("Activity not found during upload image request");
			}
			try {
				List<String> urls = FilesController.uploadImages(request().body().asMultipartFormData(), 
						Play.application().configuration().getString("dir.images.recommendation"));
				for (String url: urls) {
					recommendation.images.add(new Image(url));
				}
				recommendation.update();
			} catch (IOException e) {
				return internalServerError("Problem occured while uploading images");
			}
			return ok();
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
			Long newActivityId = json.findValue("activityId").asLong();
			Activity activity = Activity.find.byId(newActivityId);
			
			if (id!=null) {
				Activity oldActivity = Activity.find.where().eq("activities.id", id).findUnique();
				if (oldActivity == null || newActivityId.equals(activity.id)) {
					oldActivity = Activity.find.byId(newActivityId);
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
			return ok();
		}
	}
}

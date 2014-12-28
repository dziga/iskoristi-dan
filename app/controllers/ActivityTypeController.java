package controllers;

import java.util.List;

import models.ActivityType;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ActivityTypeController extends Controller {
	
	public static Result addActivityType() {
		return upsertActivityType(null);
	}
	
	public static Result updateActivityType(Long id) {
		return upsertActivityType(id);
	}
	
	public static Result getActivityType(Long id) {
		ActivityType activityType = ActivityType.find.byId(id);
		if (activityType == null) {
			return notFound("Activity type not found");
		}
		return ok(Json.toJson(activityType));
	}
	
	public static Result getActivityTypes() {
		List<ActivityType> activityTypes = ActivityType.find.all();
		if (activityTypes.size()==0) {
			return notFound("There are no activity types");
		}
		return ok(Json.toJson(activityTypes));
	}
	
	public static Result deleteActivityType(Long id) {
		ActivityType activityType = ActivityType.find.byId(id);
		if (activityType==null) {
			return notFound("Activity type not found");
		}
		activityType.delete();
		return ok();
	}
	
	private static Result upsertActivityType(Long id) {
		JsonNode json = request().body().asJson();
		
		if(json == null) {
			return badRequest("BadJson");
		} 
		ObjectMapper mapper = new ObjectMapper();
		ActivityType activityType = mapper.convertValue(json, ActivityType.class);
		if (id==null) {
			activityType.save();
		}
		else {
			activityType.id = id;
			activityType.update();
		}
		return ok();
	}
}

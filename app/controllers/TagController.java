package controllers;

import java.util.List;

import models.Tag;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class TagController extends Controller {
	
	public static Result getTags(String q) {
		List<Tag> tags;
		if (q==null || q.isEmpty()) {
			Logger.debug("Fetching all tags");
			tags = Tag.find.all();
		}
		else {
			Logger.debug("Fetching tags similar to {}", q);
			tags = Tag.find.where().contains("name", q).findList();
		}
		return ok(Json.toJson(tags));
	}
}

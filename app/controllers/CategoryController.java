package controllers;

import java.util.List;

import models.Category;
import models.CategoryType;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CategoryController extends Controller {
	
	public static Result addCategory() {
		return upsertCategory(null);
	}
	
	public static Result getCategory(Long id) {
		Category category = Category.find.byId(id);
		if(category==null) {
			return notFound("Category not found");
		}
		category.type = CategoryType.find.byId(category.type.id);
		return ok(Json.toJson(category));
	}
	
	public static Result getCategories() {
		List<Category> categories = Category.find.all();
		if (categories==null) {
			return notFound("There are no categories");
		}
		return ok(Json.toJson(categories));
	}
	
	public static Result updateCategory(Long id) {
		return upsertCategory(id);
	}
	
	private static Result upsertCategory(Long id) {
		JsonNode json = request().body().asJson();
		
		if(json == null) {
			return badRequest("BadJson");
		} 
		else {
			ObjectMapper mapper = new ObjectMapper();
			Category category = mapper.convertValue(json, Category.class);
			try {
				category.type = CategoryType.find.byId(json.get("categoryType").asLong());
				Logger.debug("Category type found with name {}", category.type.name);
				if (category.type == null) {
					return notFound("Unexisting category type");
				}
			}
			catch (NullPointerException e) {
				return badRequest("Category type is not provided");
			}
			if (id!=null) {
				category.id = id;
				category.update();
			}
			else {
				category.save();
			}
			return ok();
		}
	}
}

package controllers;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import models.Activity;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

public class ActivityControllerTest {
	private static final int PORT = 3333;

	private ObjectNode activityTypeObject = new ObjectNode(JsonNodeFactory.instance);
	private ObjectNode activityTypeObjectTwo = new ObjectNode(JsonNodeFactory.instance);
	private ObjectNode activityObjectCreate = new ObjectNode(JsonNodeFactory.instance);
	private ObjectNode activityObjectUpdate = new ObjectNode(JsonNodeFactory.instance);
	private ObjectNode activityObjectList = new ObjectNode(JsonNodeFactory.instance);
	private ArrayNode tags = new ArrayNode(JsonNodeFactory.instance);
	private ArrayNode tagsUpdate = new ArrayNode(JsonNodeFactory.instance);
	
	@Before
	public void setUp() throws JSONException {
	    RestAssured.port = PORT;
	    activityTypeObject.put("id", "1");
	    activityTypeObject.put("name", "ActivityType1");
	    activityTypeObjectTwo.put("id", "2");
	    activityTypeObjectTwo.put("name", "ActivityType2");
	    activityObjectCreate.put("name", "Activity1");
	    activityObjectCreate.put("activityType", "1");
	    activityObjectCreate.put("active", "true");
	    tags.add("one");
	    tags.add("two");
	    activityObjectCreate.put("tags", tags);
	    activityObjectUpdate.put("name", "Activity2");
	    activityObjectUpdate.put("activityType", "1");
	    activityObjectUpdate.put("active", "false");
	    tagsUpdate.add("three");
	    tagsUpdate.add("four");
	    tagsUpdate.add("five");
	    activityObjectUpdate.put("tags", tagsUpdate);
	    
	    activityObjectList.put("name", "Activity3");
	    activityObjectList.put("activityType", "1");
	    activityObjectList.put("active", "false");
	    activityObjectList.put("tags", tagsUpdate);
	}
	
	@Test
	public void activityTypeCRUD() {
		running(testServer(PORT, fakeApplication(inMemoryDatabase())), new Runnable() {
			
			@Override
			public void run() {
				
				
				//create activity type
				RestAssured.given()
                .contentType(ContentType.JSON)
                .content(activityTypeObject.toString())
                .expect()
                .statusCode(200)
                .when()
                .post("/activity-types");
				
				//create activity with given type
				RestAssured.given()
                .contentType(ContentType.JSON)
                .content(activityObjectCreate.toString())
                .expect()
                .statusCode(200)
                .when()
                .post("/activities");
				
				// get
				Activity activity = RestAssured.expect()
                        .statusCode(200)
                        .when()
                        .get("/activities/1")
                        .andReturn()
                        .body()
                        .as(Activity.class);
				Assert.assertEquals("Activity1", activity.name);
				Assert.assertEquals("ActivityType1", activity.type.name);
				Assert.assertEquals(true, activity.active);
				Assert.assertEquals(2, activity.tags.size());
				Assert.assertEquals("one", activity.tags.get(0).name);
				Assert.assertEquals("two", activity.tags.get(1).name);

				//put
				RestAssured.given()
                .contentType(ContentType.JSON)
                .content(activityObjectUpdate.toString())
                .expect()
                .statusCode(200)
                .when()
                .put("/activities/1");
				
				//get
				activity = RestAssured.expect()
                        .statusCode(200)
                        .when()
                        .get("/activities/1")
                        .andReturn()
                        .body()
                        .as(Activity.class);
				Assert.assertEquals("Activity2", activity.name);
				Assert.assertEquals("ActivityType1", activity.type.name);
				Assert.assertEquals(false, activity.active);
				Assert.assertEquals(3, activity.tags.size());
				Assert.assertEquals("three", activity.tags.get(0).name);
				Assert.assertEquals("four", activity.tags.get(1).name);
				Assert.assertEquals("five", activity.tags.get(2).name);

				//delete
				RestAssured.expect()
                .statusCode(200)
                .when()
                .delete("/activities/1");
				
				//get
				RestAssured.expect()
                        .statusCode(404)
                        .when()
                        .get("/activities/1");
			}
		});
	}
	
	@Test
	public void listOfActivities() {
		running(testServer(PORT, fakeApplication(inMemoryDatabase())), new Runnable() {
			
			@Override
			public void run()  {
				
				RestAssured.given()
                .contentType(ContentType.JSON)
                .content(activityTypeObject.toString())
                .expect()
                .statusCode(200)
                .when()
                .post("/activity-types");
				
				//create two activities
				RestAssured.given()
                .contentType(ContentType.JSON)
                .content(activityObjectList.toString())
                .expect()
                .statusCode(200)
                .when()
                .post("/activities");
				
				RestAssured.given()
                .contentType(ContentType.JSON)
                .content(activityObjectCreate.toString())
                .expect()
                .statusCode(200)
                .when()
                .post("/activities");
				
				// get list
				String json = RestAssured.expect()
                        .statusCode(200)
                        .when()
                        .get("/activities")
                        .andReturn()
                        .body().asString();
				ObjectMapper objectMapper = new ObjectMapper();
				List<Activity> activities = null;
				try {
					activities = objectMapper.readValue(json, new TypeReference<List<Activity>>() {});
				} catch (IOException e) {
					e.printStackTrace();
				}
				Assert.assertEquals(activities.size(), 2);
			}
		});
	}
	
	@Test
	public void changingActivityType() {
		running(testServer(PORT, fakeApplication(inMemoryDatabase())), new Runnable() {
			
			@Override
			public void run()  {
				
				//create activity types
				RestAssured.given()
                .contentType(ContentType.JSON)
                .content(activityTypeObject.toString())
                .expect()
                .statusCode(200)
                .when()
                .post("/activity-types");
				
				RestAssured.given()
                .contentType(ContentType.JSON)
                .content(activityTypeObjectTwo.toString())
                .expect()
                .statusCode(200)
                .when()
                .post("/activity-types");
				
				RestAssured.given()
                .contentType(ContentType.JSON)
                .content(activityObjectCreate.toString())
                .expect()
                .statusCode(200)
                .when()
                .post("/activities");
				
				// change activity type
				activityObjectUpdate.remove("activityType");
				activityObjectUpdate.put("activityType", "2");

				RestAssured.given()
                .contentType(ContentType.JSON)
                .content(activityObjectUpdate.toString())
                .expect()
                .statusCode(200)
                .when()
                .put("/activities/1");
				
				//check if changed
				
				Activity activity = RestAssured.expect()
                        .statusCode(200)
                        .when()
                        .get("/activities/1")
                        .andReturn()
                        .body().as(Activity.class);
				Assert.assertEquals(2, activity.type.id.intValue());
			}
		});
	}
	
	@Test
	public void images() {
		running(testServer(PORT, fakeApplication(inMemoryDatabase())), new Runnable() {
			
			@Override
			public void run()  {
				
				//create activity types
				RestAssured.given()
                .contentType(ContentType.JSON)
                .content(activityTypeObject.toString())
                .expect()
                .statusCode(200)
                .when()
                .post("/activity-types");
				
				RestAssured.given()
                .contentType(ContentType.JSON)
                .content(activityObjectUpdate.toString())
                .expect()
                .statusCode(200)
                .when()
                .post("/activities");

				InputStream theWall = this.getClass().getResourceAsStream("/TheWall.png");
				InputStream theWall1 = this.getClass().getResourceAsStream("/TheWall1.png");

				RestAssured.given()
				.multiPart("File1", "TheWall.png", theWall)
				.multiPart("File1", "TheWall1.png", theWall1)
				.expect()
				.statusCode(200)
				.when().
				post("/activities/1/upload");
				
				Activity activity = RestAssured.expect()
                        .statusCode(200)
                        .when()
                        .get("/activities/1")
                        .andReturn()
                        .body().as(Activity.class);
				Assert.assertEquals(2, activity.images.size());
				
				RestAssured.expect()
                .statusCode(200)
                .when()
                .delete("/activities/1/remove-image/" + activity.images.get(0).id);
				
				activity = RestAssured.expect()
                        .statusCode(200)
                        .when()
                        .get("/activities/1")
                        .andReturn()
                        .body().as(Activity.class);
				Assert.assertEquals(1, activity.images.size());
				
				RestAssured.expect()
                .statusCode(200)
                .when()
                .delete("/activities/1/remove-image/" + activity.images.get(0).id);
				
				activity = RestAssured.expect()
                        .statusCode(200)
                        .when()
                        .get("/activities/1")
                        .andReturn()
                        .body().as(Activity.class);
				Assert.assertEquals(true, activity.images.isEmpty());
			}
		});
	}
	
	@Test
	public void changingTags() {
		running(testServer(PORT, fakeApplication(inMemoryDatabase())), new Runnable() {
			
			@Override
			public void run()  {
				//TODO if needed add test code
				
			}
		});
	}
}

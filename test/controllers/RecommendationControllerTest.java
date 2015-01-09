package controllers;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;
import models.Activity;
import models.Recommendation;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

public class RecommendationControllerTest {
	private static final int PORT = 3333;

	private ObjectNode activityTypeObject = new ObjectNode(JsonNodeFactory.instance);
	private ObjectNode activityObjectOne = new ObjectNode(JsonNodeFactory.instance);
	private ObjectNode activityObjectTwo = new ObjectNode(JsonNodeFactory.instance);
	private ObjectNode recommendationObjectCreate = new ObjectNode(JsonNodeFactory.instance);
	private ObjectNode recommendationObjectUpdate = new ObjectNode(JsonNodeFactory.instance);
	private ArrayNode tags = new ArrayNode(JsonNodeFactory.instance);
	
	@Before
	public void setUp() throws JSONException {
	    RestAssured.port = PORT;
	    activityTypeObject.put("id", "1");
	    activityTypeObject.put("name", "ActivityType1");
	    activityObjectOne.put("id", "1");
	    activityObjectOne.put("name", "Activity1");
	    activityObjectOne.put("activityType", "1");
	    activityObjectOne.put("active", "true");
	    tags.add("one");
	    tags.add("two");
	    activityObjectOne.put("tags", tags);
	    activityObjectTwo.put("id", "2");
	    activityObjectTwo.put("name", "Activity2");
	    activityObjectTwo.put("activityType", "1");
	    activityObjectTwo.put("active", "false");
	    tags.removeAll();
	    tags.add("three");
	    tags.add("four");
	    tags.add("five");
	    activityObjectTwo.put("tags", tags);
	    
	    recommendationObjectCreate.put("name", "recommendation1");
	    recommendationObjectCreate.put("description", "desc1");
	    //1992-08-05T23:47:00.960Z 
	    recommendationObjectCreate.put("startTime", DateTime.now().toString());
	    System.out.println(DateTime.now().toString());
	    recommendationObjectCreate.put("endTime", DateTime.now().plusDays(1).toString());
	    recommendationObjectCreate.put("location", "address");
	    recommendationObjectCreate.put("active", "true");
	    recommendationObjectCreate.put("activityId", "1");
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
                .content(activityObjectOne.toString())
                .expect()
                .statusCode(200)
                .when()
                .post("/activities");
				
				//create activity with given type
				RestAssured.given()
                .contentType(ContentType.JSON)
                .content(recommendationObjectCreate.toString())
                .expect()
                .statusCode(200)
                .when()
                .post("/recommendations");
				
				// get
				Recommendation recommendation = RestAssured.expect()
                        .statusCode(200)
                        .when()
                        .get("/recommendations/1")
                        .andReturn()
                        .body()
                        .as(Recommendation.class);
				Assert.assertEquals("recommendation1", recommendation.name);
				Assert.assertEquals("desc1", recommendation.description);
				Assert.assertEquals(true, recommendation.active);
				Assert.assertEquals("address", recommendation.location);
				Assert.assertEquals(DateTime.now().dayOfYear(), recommendation.startTime.dayOfYear());
				Assert.assertEquals(DateTime.now().plusDays(1).dayOfYear(), recommendation.endTime.dayOfYear());

//				//put
//				RestAssured.given()
//                .contentType(ContentType.JSON)
//                .content(activityObjectTwo.toString())
//                .expect()
//                .statusCode(200)
//                .when()
//                .put("/activities/1");
//				
//				//get
//				recommendation = RestAssured.expect()
//                        .statusCode(200)
//                        .when()
//                        .get("/activities/1")
//                        .andReturn()
//                        .body()
//                        .as(Activity.class);
//				Assert.assertEquals("Activity2", recommendation.name);
//				Assert.assertEquals("ActivityType1", recommendation.type.name);
//				Assert.assertEquals(false, recommendation.active);
//				Assert.assertEquals(3, recommendation.tags.size());
//				Assert.assertEquals("three", recommendation.tags.get(0).name);
//				Assert.assertEquals("four", recommendation.tags.get(1).name);
//				Assert.assertEquals("five", recommendation.tags.get(2).name);
//
//				//delete
//				RestAssured.expect()
//                .statusCode(200)
//                .when()
//                .delete("/activities/1");
//				
//				//get
//				RestAssured.expect()
//                        .statusCode(404)
//                        .when()
//                        .get("/activities/1");
			}
		});
	}
}

package controllers;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import java.io.IOException;
import java.util.List;

import models.ActivityType;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

public class ActivityTypeControllerTest {
	
	private static final int PORT = 3333;

	private ObjectNode activityTypeObjectCreate = new ObjectNode(JsonNodeFactory.instance);
	private ObjectNode activityTypeObjectUpdate = new ObjectNode(JsonNodeFactory.instance);
	private ObjectNode activityTypeObjectList = new ObjectNode(JsonNodeFactory.instance);

	@Before
	public void setUp() throws JSONException {
	    RestAssured.port = PORT;
	    activityTypeObjectCreate.put("name", "ActivityType1");
	    activityTypeObjectUpdate.put("id", "1");
	    activityTypeObjectUpdate.put("name", "ActivityType2");
	    activityTypeObjectList.put("name", "ActivityType3");
	}
	
	@Test
	public void activityTypeCRUD() {
		running(testServer(PORT, fakeApplication(inMemoryDatabase())), new Runnable() {
			
			@Override
			public void run() {

				//create
				RestAssured.given()
                .contentType(ContentType.JSON)
                .content(activityTypeObjectCreate.toString())
                .expect()
                .statusCode(200)
                .when()
                .post("/activity-types");
				
				// get
				ActivityType activityType = RestAssured.expect()
                        .statusCode(200)
                        .when()
                        .get("/activity-types/1")
                        .andReturn()
                        .body()
                        .as(ActivityType.class);
				Assert.assertEquals("ActivityType1",
						activityType.name);
				
				//put
				RestAssured.given()
                .contentType(ContentType.JSON)
                .content(activityTypeObjectUpdate.toString())
                .expect()
                .statusCode(200)
                .when()
                .put("/activity-types/1");
				
				//get
				activityType = RestAssured.expect()
                        .statusCode(200)
                        .when()
                        .get("/activity-types/1")
                        .andReturn()
                        .body()
                        .as(ActivityType.class);
				Assert.assertEquals("ActivityType2",
						activityType.name);
				
				//delete
				RestAssured.expect()
                .statusCode(200)
                .when()
                .delete("/activity-types/1");
				
				//get
				RestAssured.expect()
                        .statusCode(404)
                        .when()
                        .get("/activity-types/1");
			}
		});
	}
	
	@Test
	public void listOfActivityTypes() {
		running(testServer(PORT, fakeApplication(inMemoryDatabase())), new Runnable() {
			
			@Override
			public void run()  {
				
				//create two activity types
				RestAssured.given()
                .contentType(ContentType.JSON)
                .content(activityTypeObjectList.toString())
                .expect()
                .statusCode(200)
                .when()
                .post("/activity-types");
				
				RestAssured.given()
                .contentType(ContentType.JSON)
                .content(activityTypeObjectCreate.toString())
                .expect()
                .statusCode(200)
                .when()
                .post("/activity-types");
				
				// get list
				String json = RestAssured.expect()
                        .statusCode(200)
                        .when()
                        .get("/activity-types")
                        .andReturn()
                        .body().asString();
				ObjectMapper objectMapper = new ObjectMapper();
				List<ActivityType> activityTypes = null;
				try {
					activityTypes = objectMapper.readValue(json, new TypeReference<List<ActivityType>>() {});
				} catch (IOException e) {
					e.printStackTrace();
				}
				Assert.assertEquals(activityTypes.size(), 2);
			}
		});
	}
}

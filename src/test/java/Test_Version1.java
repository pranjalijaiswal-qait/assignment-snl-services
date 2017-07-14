
import static com.jayway.restassured.RestAssured.given;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

	public class Test_Version1
	{
	   static String jsonAsString=null;
	   static Response response;
	   static String boardID;
	   static String playerID;
	   static JSONParser parser;
	    @BeforeClass
	    public static void setupURL()
	    {
	        RestAssured.baseURI = "http://10.0.1.86/snl";
	        RestAssured.basePath = "/rest/v1";
	    }
       @Test(priority=1)
       public void Create_A_New_Board() throws ParseException
       {
    	response = given().get("/board/new.json");
   		Assert.assertEquals(response.statusCode(), 200);
   		response = given().when().get("/board/new.json").then().contentType(ContentType.JSON).extract().response();
   		jsonAsString = response.asString();
   		JSONParser parser = new JSONParser();
   		JSONObject jsonObject = (JSONObject) parser.parse(jsonAsString);
   		JSONObject response_Obj= (JSONObject) jsonObject.get("response");
   		JSONObject Board_obj = (JSONObject) response_Obj.get("board");
   		 boardID = Board_obj.get("id").toString();
       }
       @Test(priority=2)
       public void Get_List_Of_Boards()
       {
   		response = given().get("/board.json");
   		Assert.assertEquals(response.statusCode(), 200);
   		response = given().when().get("/board.json").then().contentType(ContentType.JSON).extract().response();
   		String jsonAsString_secondBoard = response.asString();
   		Assert.assertNotSame(jsonAsString, jsonAsString_secondBoard);
       }
       

//	  @Test(priority=1)
		//	    public static void NewBoard_Id() throws ParseException
//	    {
//            jsonpath =
//                when().
//	           get("/board/new.json").
//               then().
//	           contentType(ContentType.JSON). 
//              extract().jsonPath();
//            boardID=jsonpath.getString("response.board.id");  
//            System.out.println(boardID);
//	    }
        @SuppressWarnings("unchecked")
		@Test(priority = 3)
		public void Join_new_player() throws IOException 
		{
				InputStream input = this.getClass().getClassLoader().getResourceAsStream("Test_Data_Add.json");
				@SuppressWarnings("deprecation")
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = null;
				try {
					jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(input));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			jsonObject.put("board", boardID);
			response =	given().contentType(ContentType.JSON).body(jsonObject).when().post("/player.json");
			Assert.assertEquals(response.statusCode(), 200);
			playerID=(response.getBody().jsonPath().getJsonObject("response.player.id")).toString();
		}
        @Test(priority =4)
        public void Move_Player()
        {
        	response= given().get("/move/"+boardID+".json?player_id="+playerID);
    		Assert.assertEquals(response.statusCode(), 200);
    		response= given().get("/move/89.json?player_id=897"); //giving undesired values
    		Assert.assertEquals(response.statusCode(), 500);
        }
        @Test(priority =5)
        public void Check_Player_With_Id_And_Put_Delete()
        {
        	response=given()
        			.get("/player/"+playerID+".json");
    		Assert.assertEquals(response.statusCode(), 200);
    		InputStream input = this.getClass().getClassLoader().getResourceAsStream("Test_Data_Delete.json");
    		JSONParser jsonParser = new JSONParser();
    		JSONObject jsonObject = null;
    		try {
    			jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(input));
    		} catch (IOException | ParseException e) {
    			e.printStackTrace();
    		}
    		response= given().contentType(ContentType.JSON).body(jsonObject).when().put("/player/"+playerID+".json");
    		Assert.assertEquals(response.statusCode(), 200);
    		response=given().delete("/player/.json");
        }
        @Test(priority=6)
        public void Delete_Board()
        {
        	response = given()
        			.get("/board/"+boardID+".json");
    		Assert.assertEquals(response.statusCode(), 200);
    		response = given().put("/board/"+boardID+".json").andReturn();
    		response = given().delete("/board/"+boardID+".json");
    		response = given().get("/board/"+boardID+".json");
    	    Assert.assertEquals(response.statusCode(), 200);
    	    response = given().delete("/board/"+boardID+".json");
    	    Assert.assertEquals(response.statusCode(), 500);
        }
}
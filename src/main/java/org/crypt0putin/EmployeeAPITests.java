package org.crypt0putin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.*;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmployeeAPITests {

    Gson gson = new Gson(); //Gson to work with JSONs
    static HttpClient client; //HttpClient to make requests
    static String userToken; //User token for POST and PATH requests
    static int COMPANY_ID; //ID if created company for tests
    static int EMPLOYEE_ID; //ID of created employee
    static int TIMEOUT = 4; //Duration of timeout

    static JsonObject getJsonResponseFromRequest(String uri, String userToken) {
        //GET request to API
        HttpGet httpGet = new HttpGet(uri);
        client = HttpClients.createDefault();
        try {
            //Send request
            if (userToken != null)
                //Add user token if needed
                httpGet.addHeader("x-client-token", userToken);
            HttpResponse response = client.execute(httpGet);
            //Get response
            int code = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                //Parse response
                InputStream inStream = entity.getContent();
                String content = new String(inStream.readAllBytes());
                //Make a json object with code and body of request
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("statusCode", code);
                jsonObject.addProperty("response", content);
                //Close request
                entity.getContent().close();
                //Timeout just in case
                TimeUnit.SECONDS.sleep(TIMEOUT);
                return jsonObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    static JsonObject postJsonResponseFromRequest(String uri, String params, String userToken) {
        //POST request to API
        HttpPost httpPost = new HttpPost(uri);
        client = HttpClients.createDefault();
        try {
            if (params != null) {
                // Set JSON body of request
                httpPost.setEntity(new StringEntity(params, ContentType.APPLICATION_JSON));
                httpPost.setHeader("content-Type","application/json");
            }
            //Send request
            if (userToken != null)
                httpPost.addHeader("x-client-token", userToken);
            //Execute request and get response
            HttpResponse response = client.execute(httpPost);
            //Get body of response
            int code = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                //Parse bytes from response body
                InputStream inStream = entity.getContent();
                String content = new String(inStream.readAllBytes());
                //Convert response to json object
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("statusCode", code);
                jsonObject.addProperty("response", content);
                //Close request
                entity.getContent().close();
                //Timeout just in case
                TimeUnit.SECONDS.sleep(TIMEOUT);
                return jsonObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    static JsonObject patchJsonResponseFromRequest(String uri, String params, String userToken) {
        //POST request to API
        HttpPatch httpPatch = new HttpPatch(uri);
        client = HttpClients.createDefault();
        try {
            if (params != null) {
                // Set JSON body of request
                httpPatch.setEntity(new StringEntity(params, ContentType.APPLICATION_JSON));
                httpPatch.setHeader("content-Type","application/json");
            }
            //Send request
            if (userToken != null)
                httpPatch.addHeader("x-client-token", userToken);
            //Execute request and get response
            HttpResponse response = client.execute(httpPatch);
            //Get body of response
            int code = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                //Parse bytes from response body
                InputStream inStream = entity.getContent();
                String content = new String(inStream.readAllBytes());
                //Convert response to json object
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("statusCode", code);
                jsonObject.addProperty("response", content);
                //Close request
                entity.getContent().close();
                //Timeout just in case
                TimeUnit.SECONDS.sleep(TIMEOUT);
                return jsonObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    int getCodeFromResponse(JsonObject jsonObject){
        //Get status from response
        return jsonObject.get("statusCode").getAsInt();
    }

    JsonObject getBodyFromResponse(JsonObject jsonObject){
        //Get body of response
        return gson.fromJson(jsonObject.get("response").getAsString(), JsonObject.class);
    }

    @BeforeEach
    void beforeEach(){
        //Before execution of each test, get user token by login
        //Request parameteres to login
        String params = "{\"username\": \"leonardo\", \"password\": \"leads\"}";
        //Send request to get user token
        JsonObject jsonObject = postJsonResponseFromRequest("https://x-clients-be.onrender.com/auth/login", params, null);
        userToken = gson.fromJson(jsonObject.get("response").getAsString(), JsonObject.class).get("userToken").getAsString();
    }

    @BeforeAll
    static void beforeAll() {
        //Before execution of each test, get user token by login
        //Request parameteres to login
        Gson gson = new Gson();
        String params = "{\"username\": \"leonardo\", \"password\": \"leads\"}";
        //Send request to get user token
        JsonObject jsonObject = postJsonResponseFromRequest("https://x-clients-be.onrender.com/auth/login", params, null);
        userToken = gson.fromJson(jsonObject.get("response").getAsString(), JsonObject.class).get("userToken").getAsString();
        //Create a test company for employee API tests
        params = "{\"name\": \"FQF_Test\", \"description\": \"FQF_Test\"}";
        jsonObject = postJsonResponseFromRequest("https://x-clients-be.onrender.com/company", params, userToken);
        COMPANY_ID = gson.fromJson(jsonObject.get("response").getAsString(), JsonObject.class).get("id").getAsInt();
    }

    @AfterAll
    static void afterAll() {
        //Delete created company after completion of tests
        getJsonResponseFromRequest("https://x-clients-be.onrender.com/company/delete/" + COMPANY_ID, userToken);
    }


    @Test
    @Order(1)
    @DisplayName("Check response code of retrieving employee list of new company")
    void getEmployeeListCodeTest() {
        //Send request to get list of employees of company and check response code
        JsonObject jsonObject =
                getJsonResponseFromRequest("https://x-clients-be.onrender.com/employee?company=" + 511, null);
        int result = getCodeFromResponse(jsonObject);
        assert result == 200;
    }

    @Test
    @Order(2)
    @DisplayName("Check body of response of retrieving employee list of new company")
    void getEmployeeListBodyTest() {
        //Send request to get list of employees of company and check response body
        JsonObject jsonObject =
                getJsonResponseFromRequest("https://x-clients-be.onrender.com/employee?company=" + COMPANY_ID, null);
        String result = jsonObject.get("response").getAsString();
        assert result.equals("[]");
    }

    @Test
    @Order(3)
    @DisplayName("Check response code of request with incorrect company id")
    void getEmployeeWrongListCode() {
        //Send botched request to get list of employees and make sure we get an error
        JsonObject jsonObject =
                getJsonResponseFromRequest("https://x-clients-be.onrender.com/employee?company=garbage", null);
        int result = getCodeFromResponse(jsonObject);
        assert result == 500;
    }

    @Test
    @Order(4)
    @DisplayName("Response code of request for creating an employee")
    void testCodeCreateEmployeeTest() {
        //Create an instance of employee class to convert it into JSON
        TestEmployee employee = new TestEmployee(COMPANY_ID);
        //Converting to JSON
        String params = gson.toJson(employee);
        //Sending request
        JsonObject jsonObject = postJsonResponseFromRequest("https://x-clients-be.onrender.com/employee", params, userToken);
        //Get response code and make sure it's correct
        int code = getCodeFromResponse(jsonObject);
        assert code == 201;
    }

    @Test
    @Order(5)
    @DisplayName("Response body of request for creating an employee")
    void testBodyCreateEmployeeTest() {
        //Create an instance of employee class to convert it into JSON
        TestEmployee employee = new TestEmployee(COMPANY_ID);
        //Converting to JSON
        String params = gson.toJson(employee);
        //Sending request
        JsonObject jsonObject = postJsonResponseFromRequest("https://x-clients-be.onrender.com/employee", params, userToken);
        //Get response code and make sure it's correct
        EMPLOYEE_ID = getBodyFromResponse(jsonObject).get("id").getAsInt();
        assert EMPLOYEE_ID > 0;
    }

    @Test
    @Order(6)
    @DisplayName("Response code of incorrect request for creating an employee")
    void testCodeCreateWrongEmployeeTest() {
        //Create an instance of employee class to convert it into JSON
        TestEmployee employee = new TestEmployee(0);
        //Converting to JSON
        String params = gson.toJson(employee);
        //Sending request
        JsonObject jsonObject = postJsonResponseFromRequest("https://x-clients-be.onrender.com/employee", params, userToken);
        //Get response code and make sure it's correct
        int code = getCodeFromResponse(jsonObject);
        assert code == 500;
    }

    @Test
    @Order(7)
    @DisplayName("Get code of respone from requesting employee data by id")
    void testCodeGetEmployeeById(){
        //Send request to get list of employees of company and check response code
        JsonObject jsonObject =
                getJsonResponseFromRequest("https://x-clients-be.onrender.com/employee/" + EMPLOYEE_ID, null);
        int result = getCodeFromResponse(jsonObject);
        assert result == 200;
    }

    @Test
    @Order(8)
    @DisplayName("Check body of response of retrieving employee list of new company")
    void testBodyGetEmployeeById() {
        //Send request to get list of employees of company and check response body
        JsonObject jsonObject =
                getJsonResponseFromRequest("https://x-clients-be.onrender.com/employee/" + EMPLOYEE_ID, null);
        String result = jsonObject.get("response").getAsString();
        //Create instance of TestEmployee from request
        TestEmployee testEmployee = gson.fromJson(result, TestEmployee.class);
        //compare id and companyId from request with those in tests
        assert testEmployee.id == EMPLOYEE_ID;
        assert testEmployee.companyId == COMPANY_ID;
    }

    @Test
    @Order(9)
    @DisplayName("Get code of respone from requesting employee data by wrong id")
    void testCodeGetWrongEmployeeById(){
        //Send request to get list of employees of company and check response code
        JsonObject jsonObject =
                getJsonResponseFromRequest("https://x-clients-be.onrender.com/employee/garbage", null);
        int result = jsonObject.get("statusCode").getAsInt();
        assert result == 500;
    }

    @Test
    @Order(10)
    @DisplayName("Get code of respone from patching employee data by id")
    void testCodePatchEmployeeById(){
        //Make JSON with parameters
        String params = "{\"lastName\": \"Testing\", \"email\": \"Another@email.com\"," +
                "\"url\": \"phone\", \"phone\": \"url.net\"}";
        //Send request to get list of employees of company and check response code
        JsonObject jsonObject =
                patchJsonResponseFromRequest("https://x-clients-be.onrender.com/employee/" + EMPLOYEE_ID, params, userToken);
        int result = getCodeFromResponse(jsonObject);
        assert result == 200;
    }

    @Test
    @Order(11)
    @DisplayName("Get body of respone from patching employee data by id")
    void testBodyPatchEmployeeById(){
        //Make JSON with parameters
        String params = "{\"lastName\": \"Testing\", \"email\": \"Another@email.com\"," +
                "\"url\": \"url.net\", \"phone\": \"string\"}";
        //Send request to get list of employees of company and check response code
        JsonObject jsonObject =
                patchJsonResponseFromRequest("https://x-clients-be.onrender.com/employee/" + EMPLOYEE_ID, params, userToken);
        TestEmployee result = gson.fromJson(getBodyFromResponse(jsonObject), TestEmployee.class);
        //Check that correct employee data was changed
        assert result.id == EMPLOYEE_ID;
        //Check that fileds were changed
        //Example response on Swagger drastically differs from actual response,
        //Half of fields are missing from actual response!
        assert result.email.equals("Another@email.com");
        assert result.url.equals("url.net");
    }

    @Test
    @Order(12)
    @DisplayName("Get code from incorrect request to patch emplyee data")
    void testCodeWrongPatchEmployeeById(){
        //Make JSON with parameters
        String params = "{\"lastName\": \"Testing\", \"email\": \"Another@email.com\"," +
                "\"url\": \"phone\", \"phone\": \"url.net\"}";
        //Send request to get list of employees of company and check response code
        JsonObject jsonObject =
                patchJsonResponseFromRequest("https://x-clients-be.onrender.com/employee/" + 99999999, params, userToken);
        int result = getCodeFromResponse(jsonObject);
        assert result == 500;
    }
}

class TestEmployee{
    /***
     * Class that was made for single purpose:
     * to be converted to JSON
     *
     * It contains some default data
     */

    int id = 0;
    String firstName = "Test";
    String lastName = "Testovich";
    String middleName = "Testov";
    int companyId;
    String email = "FQF@Email.com";
    String url = "fqf.com";
    String phone = "string";
    String birthdate = "2023-08-15T12:25:25.165Z";
    boolean isActive = true;

    TestEmployee(int companyId){
        this.companyId = companyId;
    }
}

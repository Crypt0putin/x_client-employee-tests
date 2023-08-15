package org.shikalin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.*;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class EmployeeAPITests {

    static HttpClient client = HttpClients.createDefault();
    static String userToken;
    static int COMPANY_ID;
    static int EMPLOYEE_ID;

    static JsonObject postJsonResponseFromRequest(String uri, String params, String userToken) {
        //POST request to API
        HttpPost httpPost = new HttpPost(uri);
        try {
            if (params != null)
                httpPost.setEntity(new StringEntity(params));
            //Send request
            if (userToken != null)
                httpPost.addHeader("x-client-token", userToken);
            HttpResponse response = client.execute(httpPost);
            //Get response
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inStream = entity.getContent();
                String content = new String(inStream.readAllBytes());
                JsonObject jsonObject = new Gson().fromJson(content, JsonObject.class);
                TimeUnit.SECONDS.sleep(1);
                return jsonObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    static JsonObject getJsonResponseFromRequest(String uri, String userToken) {
        //GET request to API
        HttpGet httpGet = new HttpGet(uri);
        try {
            //Send request
            if (userToken != null)
                httpGet.addHeader("x-client-token", userToken);
            HttpResponse response = client.execute(httpGet);
            //Get response
            int code = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inStream = entity.getContent();
                String content = new String(inStream.readAllBytes());
                TimeUnit.MILLISECONDS.sleep(500);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("statusCode", code);
                jsonObject.addProperty("response", content);
                return jsonObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    @BeforeAll
    static void beforeAll() {
        //Before execution of each test, get user token by login
        //Request parameteres to login
        String loginParams = "{\"username\": \"leonardo\", \"password\": \"leads\"}";
        JsonObject jsonObject = postJsonResponseFromRequest("https://x-clients-be.onrender.com/auth/login", loginParams, null);
        if (jsonObject != null) {
            userToken = jsonObject.get("userToken").getAsString();
        }
        //Create a test company for employee API tests
        String companyParams = "{\"name\": \"SHK_Test\", \"description\": \"SHK_Test\"}";
        jsonObject = postJsonResponseFromRequest("https://x-clients-be.onrender.com/company", companyParams, userToken);
        if (jsonObject != null) {
            System.out.println(jsonObject);
        }
    }

    @AfterAll
    static void afterAll() {
        //Delete created company after completion of tests
        getJsonResponseFromRequest("https://x-clients-be.onrender.com/company/delete/" + COMPANY_ID, userToken);
    }


    @Test
    @DisplayName("Check response code of retrieving employee list of new company")
    void getEmployeeListCodeTest() {
        JsonObject jsonObject =
                getJsonResponseFromRequest("https://x-clients-be.onrender.com/employee?company=" + COMPANY_ID, null);
        int result = jsonObject.get("statusCode").getAsInt();
        assert result == 200;
    }

    @Test
    @DisplayName("Check body of response of retrieving employee list of new company")
    void getEmployeeListBodyTest() {
        JsonObject jsonObject =
                getJsonResponseFromRequest("https://x-clients-be.onrender.com/employee?company=" + COMPANY_ID, null);
        String result = jsonObject.get("response").getAsString();
        assert result.equals("[]");
    }

    @Test
    @DisplayName("Check response code of request with incorrect company id")
    void getWrongEmployeeListCodeTest() {
        JsonObject jsonObject =
                getJsonResponseFromRequest("https://x-clients-be.onrender.com/employee?company=garbage", null);
        int result = jsonObject.get("statusCode").getAsInt();
        assert result == 500;
    }

    //    @Test
//    @DisplayName("Response code of request for creating an employee")
    void codeCreateEmployeeTest() {
        String employeeCreationParams = "{\"id\": 0, \"firstName\": \"Test\", \"lastName\": \"Testovich\", " +
                "\"middleName\": \"Testov\", \"companyId\": " + COMPANY_ID + ", \"email\": \"string\", \"url\": \"string\"," +
                " \"phone\": \"string\", \"birthdate\": \"2023-08-15T12:25:25.165Z\", \"isActive\": true}";
    }
}

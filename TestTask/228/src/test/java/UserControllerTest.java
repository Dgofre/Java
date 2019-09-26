
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import hello.App.controller.UserController;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class UserControllerTest {
    String url = "http://localhost:8080/";

    @Test
    public void status_401_get_users(){
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        ResponseEntity<String> response = testRestTemplate.getForEntity(url+"users", String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
    }

    /*@Test
    public void status_400_login() throws Exception {
        TestRestTemplate testRestTemplate = new TestRestTemplate();

        JSONObject json = new JSONObject();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        ResponseEntity<JSONObject> response = testRestTemplate.postForEntity(url+"login", json, JSONObject.class, headers);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }*/



}

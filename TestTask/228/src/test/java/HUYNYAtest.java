import hello.App.Application;
import hello.App.security.LogFilter;
import hello.App.service.UserService;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Stack;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class HUYNYAtest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LogFilter logFilter;

    @MockBean
    private UserService userService;

    @Test
    public void testGetUsers() throws Exception {
        List list = new Stack();
        JSONObject obj = new JSONObject();
        obj.put("_id", "1111111111111");
        obj.put("username", "admin");
        list.add(obj);

        //Mockito.when(logFilter.validationToken(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
        Mockito.when(userService.users()).thenReturn(list);
        this.mockMvc.perform(get("/users")).andExpect(status().isOk());
    }

    @Test
    public void testPutUsers()throws Exception{
        JSONObject json = new JSONObject();
        json.put("password", "");
        json.put("username", "admin");
        this.mockMvc.perform(put("/users", json)).andExpect(status().isOk());
    }

}
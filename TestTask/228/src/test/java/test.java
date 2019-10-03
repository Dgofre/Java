import hello.App.Application;
import hello.App.domains.User;
import hello.App.exaption.IncorrectDataException;
import hello.App.exaption.UserNotFoundException;
import hello.App.exaption.UsernameTakenException;
import hello.App.repository.UserRepository;
import hello.App.service.UserService;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@SpringBootTest(classes = Application.class)
public class test {

    @Mock
    private UserRepository userRepository;

    User user = new User("admin", "admin");
    String testId = "1111111111111111";
    String notFoundId = "2222222222222222";
    String notFoundName = "no_admin";

    @InjectMocks
    private UserService userServise;

    @BeforeEach
    void setMockOutput() {


    }

    @Test
    public void testFindById(){
        when(userRepository.findbyId(testId)).thenReturn(user);
        assertEquals(user.getUsername(), userServise.findById(testId).getUsername());
    }

    @Test
    public void testExaptionFindById(){
        when(userRepository.findbyId(anyString())).thenReturn(null);
        try {
            assertEquals(user, userServise.findById(notFoundId));
        }catch (UserNotFoundException e){ }
    }

    @Test
    public void testFindByName(){
        when(userRepository.findbyUsername(user.getUsername())).thenReturn(user);
        assertEquals(user.getUsername(), userServise.findByName(user.getUsername()).getUsername());
    }

    @Test
    public void testExaptionFindByName(){
        when(userRepository.findbyUsername(anyString())).thenReturn(null);
        try {
            assertEquals(user, userServise.findByName(notFoundName));
        }catch (UserNotFoundException e){ }
    }

    @Test
    public void testSave(){

        JSONObject inputObj = new JSONObject();
        inputObj.put("password", user.getPassword());
        inputObj.put("username", user.getUsername());

        JSONObject outputObj = new JSONObject();
        outputObj.put("_id", testId);
        outputObj.put("username", user.getUsername());

        when(userRepository.saveUser(user)).thenReturn(outputObj);
        assertEquals(outputObj, userServise.save(inputObj));
    }

    @Test
    public void testSaveToUsernameTakenException(){

        JSONObject inputObj = new JSONObject();
        inputObj.put("password", user.getPassword());
        inputObj.put("username", user.getUsername());

        JSONObject outputObj = new JSONObject();
        outputObj.put("_id", testId);
        outputObj.put("username", user.getUsername());

        when(userRepository.saveUser(user)).thenThrow(new UsernameTakenException());
        try {
            assertEquals(outputObj, userServise.save(inputObj));
        }catch (UsernameTakenException e){}
    }

    @Test
    public void testSaveToIncorrectDataException(){
        //User user = new User("", "admin");

        JSONObject inputObj = new JSONObject();
        //inputObj.put("password", user.getPassword());
        //inputObj.put("username", user.getUsername());

        JSONObject outputObj = new JSONObject();
        outputObj.put("_id", testId);
        outputObj.put("username", user.getUsername());

        //when(userRepository.validJSON(inputObj, false)).thenThrow(new IncorrectDataException());
        //doThrow(new IncorrectDataException()).when( );

        try {
            assertEquals(outputObj, userServise.save(inputObj));
        }catch (IncorrectDataException e) {}
    }

    /*@Test
    public void TestPatchUser(){


        try {
            userServise.patchUser();
        }catch (IncorrectDataException e) {}
    }*/
}

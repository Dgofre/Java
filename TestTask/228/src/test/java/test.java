import hello.App.Application;
import hello.App.domains.User;
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
import static org.mockito.Mockito.when;


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
        when(userRepository.findbyId(testId)).thenReturn(user);
        when(userRepository.findbyId(notFoundId)).thenThrow(new UserNotFoundException(notFoundId));
        when(userRepository.findbyUsername(user.getUsername())).thenReturn(user);
        when(userRepository.findbyUsername(notFoundName)).thenThrow(new UserNotFoundException(notFoundName));
    }

    @Test
    public void testFindById(){
        assertEquals(user, userServise.findById(testId));
    }

    @Test
    public void testExaptionFindById(){
        try {
            assertEquals(user, userServise.findById(notFoundId));
        }catch (UserNotFoundException e){ }
    }

    @Test
    public void testFindByName(){
        assertEquals(user, userServise.findByName(user.getUsername()));
    }

    @Test
    public void testExaptionFindByName(){
        try {
            assertEquals(user, userServise.findByName(notFoundName));
        }catch (UserNotFoundException e){ }
    }

    @Test
    public void testSave(){
        JSONObject obj = new JSONObject();
        obj.put("_id", testId);
        obj.put("username", user.getUsername());

        assertEquals(obj, userServise.save());
    }
}

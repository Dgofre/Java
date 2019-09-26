package hello.App.domains;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    //This annotation marks userId as unique identificator!!! BLYAT
    @Id
    @JsonProperty
    @Null
    String userId;

    @NotBlank
    @Indexed(unique = true)
    //@JsonProperty
    String username;

    @NotBlank
    //@JsonProperty
    String password;

    String avatarId;


    /*@NotBlank
    @JsonProperty
    @Indexed(unique = true)
    String email;*/
    public String getAvatar() {
        return avatarId;
    }


    public String getPassword() {
        return this.password;
    }

    public String getUsername(){
        return  this.username;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setPassword(String newPassword){
        this.password = newPassword;
    }

    public void setUsername(String newUsername){
        this.password = newUsername;
    }


    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }


}


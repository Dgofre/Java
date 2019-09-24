package hello.App.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import hello.App.domains.User;
import hello.App.exaption.*;
import hello.App.repository.UserRepository;
import org.springframework.stereotype.Service;

import org.json.simple.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.*;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById (String id) {
        User user = userRepository.findbyId(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        else {
            return user;
        }
    }

    public User findByName (String username) {
        User user = userRepository.findbyUsername(username);
        if (user == null) {
            throw new UserNotFoundException(username);
        }
        else {
            return user;
        }
    }

    public JSONObject save(JSONObject json) {
        userRepository.validJSON(json, false);
        User user = new User(json.get("username").toString(), json.get("password").toString());
        return userRepository.saveUser(user);
    }



    public void patchUser(String userId, JSONObject json) {
        findById(userId);
        userRepository.validJSON(json, true);
        if (json.containsKey("username")) {
            userRepository.updateUsername(userId, json.get("username").toString());
        }

        if (json.containsKey("password")) {
            userRepository.updatePassword(userId, json.get("password").toString());
        }
    }

    public void delete(String userId) { userRepository.deleteUser(findById(userId));}

    public void dellAll() {userRepository.deleteAllUsers();}

    public JSONObject validationUser(JSONObject json){
        userRepository.validJSON(json, false);
        String username = json.get("username").toString();
        String password = json.get("password").toString();

        try {
            User user = findByName(username);

            if (userRepository.matchesPasswords(password, user.getPassword())) {
                String token = userRepository.createToken(user.getUserId(), username, "abc123");
                JSONObject obj = new JSONObject();
                obj.put("jwt", token);
                return obj;
            } else {
                throw new UnauthorizedException();
            }
        }catch (UserNotFoundException e) {
            throw new UnauthorizedException();
        }
    }


    public List users(){

        List list = new Stack();

        for (User user : userRepository.findAll()) {
            JSONObject obj = new JSONObject();
            obj.put("_id", user.getUserId());
            obj.put("username", user.getUsername());
            list.add(obj);
        }

        return list;
    }

    public void addPhoto(String userId, byte[] file){


        try {
            InputStream is = new ByteArrayInputStream(file);
            String fileType = URLConnection.guessContentTypeFromStream(is);
            if (fileType == null){
                throw new IncorrectDataException();
            }
            if (fileType.equals("image/png") || fileType.equals("image/jpeg")) {
                String avatarId = findById(userId).getAvatar();
                if (avatarId != null)
                    userRepository.deleteAvatar(avatarId);
                userRepository.uploadAvatar(userId, is);
            }
            else{
                throw new IncorrectDataException();
            }
        }catch (IOException e){
            throw new IncorrectDataException();
        }

    }

    public byte[] getPhoto(String userId)throws IOException{
        String avatarId = findById(userId).getAvatar();
        if (avatarId != null) return userRepository.findAvatarbyId(avatarId);
        else throw new NoAvatarSetException();
    }

}



package hello.App.repository;

import com.mongodb.client.gridfs.model.GridFSFile;
import hello.App.domains.User;
import hello.App.exaption.IncorrectDataException;
import hello.App.exaption.UsernameTakenException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.bson.types.Binary;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.bson.types.ObjectId;
import org.apache.commons.io.IOUtils;

import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Repository
public class UserRepository {
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations operations;

    private MongoTemplate mongoTemplate;

    public UserRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<User> findAll () {
        return mongoTemplate.findAll(User.class);
    }

    public User findbyId(String id){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, User.class);
    }

    public User findbyUsername(String username){
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        return mongoTemplate.findOne(query, User.class);
    }

    public byte[] findAvatarbyId(String avatarId)throws IOException {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(avatarId));
        return IOUtils.toByteArray(gridFsTemplate.getResource(gridFsTemplate.findOne(query)).getInputStream());
    }

    public JSONObject saveUser (User user) {
        user.setPassword(bCrypt(user.getPassword()));
        try {
            mongoTemplate.save(user);
        }catch (DuplicateKeyException e){
            throw new UsernameTakenException();
        }

        JSONObject obj = new JSONObject();
        obj.put("_id", user.getUserId());
        obj.put("username", user.getUsername());
        return obj;
    }



    public void deleteUser(User user){
        mongoTemplate.remove(user);
    }

    public void updateUsername(String id, String newUsername){
        try {
            mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(id)), Update.update("username", newUsername), "users");
        }catch (DuplicateKeyException e){
            throw new UsernameTakenException();
        }
    }

    public void updatePassword(String id, String newPassword){
        mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(id)), Update.update("password", bCrypt(newPassword)), "users");
    }

    public void updateAvatarId(String id, String newAvatarId){
        mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(id)), Update.update("avatarId", newAvatarId), "users");
    }

    public void uploadAvatar(String id, InputStream newAvatar){
        //mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(id)), Update.update("avatar", newAvatar), "users");

        updateAvatarId(id, gridFsTemplate.store(newAvatar, "User_id_"+id).toString());
    }


    public void deleteAvatar(String avatarId){
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(avatarId)));
    }


    public boolean validNamePass(String str){
        if (str == null)
            System.out.println("1");
        if (str.equals("")|| str.split(" ").length == 0){
            return false;
        }
        else{
            return true;
        }
    }



    public void deleteAllUsers(){
        mongoTemplate.remove(new Query(), User.class);
    }

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String bCrypt(String password) {
        return passwordEncoder.encode(password);
    };

    public boolean matchesPasswords(String password, String bCryptPassword){
        return passwordEncoder.matches(password, bCryptPassword);
    }

    /**
     * Генерирует JWT токен
     * @param userID Уникальный id пользователя
     * @param username Имя пользователя
     * @param key
     * @return JWT токен
     */
    public String createToken (String userID, String username, String key){
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("userID", userID);
        tokenData.put("username", username);
        tokenData.put("token_create_date", new Date().getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        tokenData.put("token_expiration_date", calendar.getTime());
        JwtBuilder jwtBuilder = Jwts.builder();
        jwtBuilder.setExpiration(calendar.getTime());
        jwtBuilder.setClaims(tokenData);

        return jwtBuilder.signWith(SignatureAlgorithm.HS512, key).compact();
    }



    public void validJSON(JSONObject json, boolean nameOrPass){
        try {
            if(json.size() == 2){
                if(json.containsKey("username") && json.containsKey("password")) {
                    if(json.get("username").getClass() == String.class && json.get("password").getClass() == String.class){
                        if (!validNamePass(json.get("username").toString()) && validNamePass(json.get("password").toString()))
                            {throw new IncorrectDataException();}
                    }
                    else {throw new IncorrectDataException();}
                }
                else {throw new IncorrectDataException();}
            }
            else{
                if (!nameOrPass)
                    {throw new IncorrectDataException();}
                if(json.size()==1){
                    if(json.containsKey("username")) {
                        if(json.get("username").getClass() == String.class)
                            if (!validNamePass(json.get("username").toString()))
                                {throw new IncorrectDataException();}
                        else {throw new IncorrectDataException();}
                    }
                    else{
                        if(json.containsKey("password")) {
                            if(json.get("password").getClass() == String.class)
                                if (!validNamePass(json.get("password").toString()))
                                    {throw new IncorrectDataException();}
                            else {throw new IncorrectDataException();}
                        }
                        else {throw new IncorrectDataException();}
                    }
                }
                else {throw new IncorrectDataException();}
            }
        }
        catch (NumberFormatException e){
            throw new IncorrectDataException();
        }
    }


    }


package hello.App.controller;

import hello.App.exaption.*;
import hello.App.service.UserService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping
@ControllerAdvice
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/users")
    public ResponseEntity ListUsers () {
        return ResponseEntity.ok(userService.users());
    }

    @PutMapping("/users")
    public ResponseEntity newUser (@RequestBody JSONObject json){
        return ResponseEntity.ok(userService.save(json));
    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity overwritesUsersData (@PathVariable String userId, @RequestBody JSONObject json){
        userService.patchUser(userId, json);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity DeleteUser(@PathVariable(required = false) String userId) {
        userService.delete(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/users/{userId}/avatar", produces = "image/jpeg")
    public ResponseEntity getAvatar (@PathVariable String userId)throws  IOException{
        return ResponseEntity.ok(userService.getPhoto(userId));
    }


    @PatchMapping("/users/{userId}/avatar")
    public ResponseEntity setAvatars (@PathVariable String userId, @RequestBody byte[] avatar){
        userService.addPhoto(userId, avatar);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity login (@RequestBody JSONObject json) {
            return ResponseEntity.ok(userService.validationUser(json));
    }


    @ExceptionHandler({ IncorrectDataException.class })
    public ResponseEntity<Object> IncorrectDataException(Exception ex) {
        return new ResponseEntity<Object>("Incorrect Data", new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ UserNotFoundException.class })
    public ResponseEntity<Object> UserNotFoundException(Exception ex) {
        return new ResponseEntity<Object>("User not found", new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ UsernameTakenException.class })
    public ResponseEntity<Object> UsernameTakenException(Exception ex) {
        return new ResponseEntity<Object>("Username is already taken", new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ UnauthorizedException.class })
    public ResponseEntity<Object> UnauthorizedException(Exception ex) {
        return new ResponseEntity<Object>("The username or password you entered is incorrect", new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({ NoAvatarSetException.class })
    public ResponseEntity<Object> NoAvatarSetException(Exception ex) {
        return new ResponseEntity<Object>("No avatar set", new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

}



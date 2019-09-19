package hello.App.security;


import hello.App.exaption.UserNotFoundException;
import hello.App.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import org.springframework.security.jwt.JwtHelper;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Null;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;


@Component
public class LogFilter implements Filter {
    @Autowired
    UserService userService;


    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getServletPath();
        if ((path.equals("/login") ||path.equals("/login/") ) || ((path.equals("/users") || path.equals("/users/"))&& req.getMethod().equals("PUT")))  {
            chain.doFilter(request, response);
        }
        else {
            String token = req.getHeader("Authorization");
            if (validationToken(token, "abc123")) {
                chain.doFilter(request, response);
            } else {
                HttpServletResponse hsr = (HttpServletResponse) response;
                hsr.setStatus(401);
            }


        }
    }


    boolean validationToken(String token, String key){
        if (token != null) {
            String[] tokens = token.split(" ");

            if (tokens[0].equals("Bearer") && tokens.length > 1 && tokens[1] != null) {
                token = tokens[1];

                DefaultClaims claims;
                try {
                    claims = (DefaultClaims) Jwts.parser().setSigningKey(key).parse(token).getBody();

                } catch (Exception ex) {
                    //invalid token
                    return false;
                }
                try {
                    userService.findById(claims.get("userID").toString());
                } catch (UserNotFoundException e) {
                    //user not found
                    return false;
                }

                Date date1 = new Date();
                Date date2 = new Date(Long.parseLong(claims.get("token_expiration_date").toString()));

                if (date1.after(date2)) {
                    //time is out
                    return false;
                }

                return true;
            } else {
                return false;
            }
        }
        else{
            return false;
        }

    }
}
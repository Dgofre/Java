package hello.App.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GreetingController {

    @RequestMapping(value = "/greetings")
    public ModelAndView helloWorld (ModelAndView mav, @RequestParam(required = false, defaultValue = "Dima") String name) {
        mav.addObject("name", name);
        mav.setViewName("greeting");
        return mav;
    }

}
package com.baron.webapp.controller;

import com.baron.webapp.domain.Roles;
import com.baron.webapp.domain.User;
import com.baron.webapp.domain.dto.CaptchaResponseDto;
import com.baron.webapp.service.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Controller
public class RegistrationController {

    private final static String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }

    @Value("${recaptcha.secret}")
    private String secret;


    @PostMapping("/registration")
    public String addUser(@RequestParam("password2") String passwordConfirm, @RequestParam("g-recaptcha-response") String captchaResponse, @Valid User user,
                          BindingResult bindingResult, Model model){
        String url = String.format(CAPTCHA_URL, secret, captchaResponse);
        CaptchaResponseDto response = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);
        if(!response.isSuccess()){
            model.addAttribute("captchaError", "Fill captcha");
        }
        boolean isConfirmEmpty = StringUtils.isEmpty(passwordConfirm);
        if(isConfirmEmpty){
            model.addAttribute("password2Error","Password confirmation field cannot be empty!");
        }
        if(user.getPassword() != null && !user.getPassword().equals(passwordConfirm)){
            model.addAttribute("passwordError","Passwords should match!");
        }
        if(isConfirmEmpty || bindingResult.hasErrors() || !response.isSuccess()){
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            return "registration";
        }
        if(!userService.addUser(user)){
            model.addAttribute("usernameError","User already exists!");
            return "registration";
        }
        return "redirect:/login";
    }
//haha
    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code){
        boolean isActivated = userService.activateUser(code);
        if(isActivated){
            model.addAttribute("messageType","success");
            model.addAttribute("message","User is successfully activated");
        }
        else{
            model.addAttribute("messageType","danger");
            model.addAttribute("message","Activation code is not found");
        }
        return "login";
    }
}

package com.baron.webapp.controller;

import com.baron.webapp.domain.Roles;
import com.baron.webapp.domain.User;
import com.baron.webapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String userList(Model model){
        model.addAttribute("users",userRepository.findAll());
        return "userList";
    }

    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, Model model) {
     //   User usr = userRepository.findById(Long.parseLong(user)).get();
        model.addAttribute("user", user); //this might want to be changed later
        model.addAttribute("roles", Roles.values());

        return "userEdit";
    }

    @PostMapping
    public String userSave(@RequestParam String username, @RequestParam Map<String, String> form, @RequestParam("userId") User user){
        user.setUsername(username);
        Set <String> roles = Arrays.stream(Roles.values()).map(Roles::name).collect(Collectors.toSet());
        user.getRoles().clear();
        System.out.println(form);
        for (String key: form.keySet()
             ) {
            if (roles.contains(key)){
                user.getRoles().add(Roles.valueOf(key));
            }
        }
        userRepository.save(user);
        return "redirect:/user";
    }
}

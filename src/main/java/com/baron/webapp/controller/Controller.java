package com.baron.webapp.controller;

import com.baron.webapp.domain.Message;
import com.baron.webapp.domain.User;
import com.baron.webapp.domain.dto.MessageDto;
import com.baron.webapp.repositories.MessageRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@org.springframework.stereotype.Controller
public class Controller {
    @Autowired
    private MessageRepository messageRepo;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/")
    public String welcomePage(Model model){
        return "welcomepage";
    }


    @GetMapping("/main")
    public String main(@RequestParam(required = false, defaultValue = "")String filter, Model model,
                       @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC)
    Pageable pageable, @AuthenticationPrincipal User user) {
        Page<MessageDto> page;
        if (filter != null && !filter.isEmpty()) {
           page = messageRepo.findByTag(filter, pageable, user);
        }
        else {
            page = messageRepo.findAll(pageable,user);
        }
        model.addAttribute("page", page);
        model.addAttribute("url","/main");
        model.addAttribute("filter", filter);
        return "main";
    }



    @PostMapping("/main")
    public String add(@AuthenticationPrincipal User user, @Valid Message message, BindingResult bindingResult, Model model, @RequestParam("file") MultipartFile
    file) throws IOException {
        message.setAuthor(user);
        if(bindingResult.hasErrors()){
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("message", message);
        }
        else {
            saveFile(message, file);
            model.addAttribute("message",null);
            messageRepo.save(message);
        }
        Iterable<Message> messages = messageRepo.findAll();
        model.addAttribute("messages", messages);
        return "redirect:/main";
    }

    private void saveFile(Message message, MultipartFile file) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFileName));
            message.setFileName(resultFileName);
        }
    }

    //PUT TWO METHODS BELOW IN A SEPARATE CONTROLLER LATER
    @GetMapping("/user-messages/{user}")
    public String resolveMessageEditorPage(@AuthenticationPrincipal User currentUser,
                                           @PathVariable User user, Model model,
                                           @RequestParam(required = false) Message message, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable){
        Page<MessageDto> page = messageRepo.findByAuthor(user, pageable, currentUser);

        model.addAttribute("userChannel", user);
        model.addAttribute("subscriptionsCount",user.getSubscriptions().size());
        model.addAttribute("subscribersCount",user.getSubscribers().size());
        model.addAttribute("isSubscriber", user.getSubscribers().contains(currentUser));
        model.addAttribute("page", page);
        model.addAttribute("url","/user-messages/" + user.getId());
        model.addAttribute("isCurrentUser",currentUser.equals(user));
        model.addAttribute("message",message);
        return "userMessages";
    }

    @PostMapping("/user-messages/{user}")
    public String updateMessage(@AuthenticationPrincipal User currentUser, @PathVariable("user") Long id, @RequestParam("id")Message message,
                                @RequestParam("text") String text, @RequestParam("tag") String tag, @RequestParam("file") MultipartFile file)
            throws IOException {
        if(message.getAuthor().equals(currentUser)){
            if(!StringUtils.isEmpty(text)){
                message.setText(text);
            }
            if(!StringUtils.isEmpty(tag)){
                message.setTag(tag);
            }
            saveFile(message,file);
            messageRepo.save(message);
        }
        return "redirect:/user-messages/" + id;
    }

    @GetMapping("/messages/{message}/like")
    public String like(@AuthenticationPrincipal User currentUser,
                       @PathVariable Message message,
                       RedirectAttributes redirectAttributes,
                       @RequestHeader(required = false)String referer){
        Set<User> likes = message.getLikes();
        if(likes.contains(currentUser)){
            likes.remove(currentUser);
        }else{
            likes.add(currentUser);
        }
        UriComponents components = UriComponentsBuilder.fromHttpUrl(referer).build();
        components.getQueryParams().entrySet().forEach(pair -> redirectAttributes.addAttribute(pair.getKey(),pair.getValue()));
        return "redirect:" + components.getPath();
    }
}

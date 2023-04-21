package com.baron.webapp.controller;

import com.baron.webapp.domain.Message;
import com.baron.webapp.domain.User;
import com.baron.webapp.repositories.MessageRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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
    public String main(@RequestParam(required = false, defaultValue = "")String filter, Model model, @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC)
    Pageable pageable) {
        Page<Message> page;
        if (filter != null && !filter.isEmpty()) {
            page = messageRepo.findByTag(filter, pageable);
        }
        else {
            page = messageRepo.findAll(pageable);
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
        return "main";
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
                                           @RequestParam(required = false) Message message){
        List<Message> messages = user.getMessages();
        model.addAttribute("userChannel", user);
        model.addAttribute("subscriptionsCount",user.getSubscriptions().size());
        model.addAttribute("subscribersCount",user.getSubscribers().size());
        model.addAttribute("isSubscriber", user.getSubscribers().contains(currentUser));
        model.addAttribute("messages", messages);
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
}

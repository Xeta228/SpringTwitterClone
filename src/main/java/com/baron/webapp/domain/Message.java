package com.baron.webapp.domain;

import com.baron.webapp.domain.util.MessageHelper;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;
import java.util.Set;


@Entity
public class Message {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Message cannot be empty!")
    @Length(max = 2048, message = "You have exceeded the length of 2048 symbols")
    private String text;
    @Length(max = 255, message = "Tag can be max 255 symbols long")
    private String tag;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id")
    private User author;

    @Column(name = "filename")
    private String fileName;

    @ManyToMany
    @JoinTable(name="message_likes",joinColumns = @JoinColumn(name="message_id"),inverseJoinColumns = @JoinColumn(name="user_id"))
    private Set<User> likes = new HashSet<>();

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Message() {
    }

    public String getAuthorName(){
        return MessageHelper.getAuthorName(author);
    }

    public Message(String text, String tag, User user) {
        this.author = user;
        this.text = text;
        this.tag = tag;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getFileName() {
        return fileName;
    }

    public Set<User> getLikes() {
        return likes;
    }

    public void setLikes(Set<User> likes) {
        this.likes = likes;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
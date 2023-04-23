package com.baron.webapp.domain.util;

import com.baron.webapp.domain.User;

public class MessageHelper {
    public static String getAuthorName(User author){
        return author != null ? author.getUsername() : "<none>";
    }
}

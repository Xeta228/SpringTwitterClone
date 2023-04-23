package com.baron.webapp.repositories;

import com.baron.webapp.domain.Message;
import com.baron.webapp.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<Message, Long> {

    Page<Message> findByTag(String tag, Pageable pageable);
    Page<Message> findAll(Pageable pageable);

    Page<Message> findByAuthor(User author, Pageable pageable);
}

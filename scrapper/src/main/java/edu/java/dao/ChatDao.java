package edu.java.dao;

import edu.java.dto.ChatDTO;
import java.util.List;

public interface ChatDao {

    void add(ChatDTO chat);

    void remove(Long chatId);

    List<ChatDTO> findAll();
}

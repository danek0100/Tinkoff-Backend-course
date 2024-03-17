package edu.java.service;

import edu.java.dto.ChatLinkDTO;
import java.util.Collection;

public interface ChatLinkService {

    void addLinkToChat(long chatId, long linkId);

    void removeLinkFromChat(long chatId, long linkId);

    Collection<ChatLinkDTO> findAllLinksForChat(long chatId);

    boolean existsChatsForLink(long linkId);
}

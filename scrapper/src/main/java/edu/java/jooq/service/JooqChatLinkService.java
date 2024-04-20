package edu.java.jooq.service;

import edu.java.dto.ChatLinkDTO;
import edu.java.service.ChatLinkService;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import java.util.Collection;
import static edu.java.jooq.generated.Tables.*;

@AllArgsConstructor
public class JooqChatLinkService implements ChatLinkService {

    private final DSLContext dslContext;

    @Override
    public void addLinkToChat(long chatId, long linkId) {
        dslContext.insertInto(CHAT_LINK)
                  .columns(CHAT_LINK.CHAT_ID, CHAT_LINK.LINK_ID, CHAT_LINK.SHARED_AT)
                  .values(chatId, linkId, java.time.OffsetDateTime.now())
                  .execute();
    }

    @Override
    public void removeLinkFromChat(long chatId, long linkId) {
        dslContext.deleteFrom(CHAT_LINK)
                  .where(CHAT_LINK.CHAT_ID.eq(chatId))
                  .and(CHAT_LINK.LINK_ID.eq(linkId))
                  .execute();
    }

    @Override
    public Collection<ChatLinkDTO> findAllLinksForChat(long chatId) {
        return dslContext.select(CHAT_LINK.CHAT_ID, CHAT_LINK.LINK_ID, CHAT_LINK.SHARED_AT)
            .from(CHAT_LINK)
            .join(LINK).on(CHAT_LINK.LINK_ID.eq(LINK.LINK_ID))
            .where(CHAT_LINK.CHAT_ID.eq(chatId))
            .fetch(record -> new ChatLinkDTO(
                record.get(CHAT_LINK.CHAT_ID),
                record.get(CHAT_LINK.LINK_ID),
                record.get(CHAT_LINK.SHARED_AT).toLocalDateTime()
            ));
    }

    @Override
    public Collection<ChatLinkDTO> findAllChatsForLink(long linkId) {
        return dslContext.select(CHAT_LINK.CHAT_ID, CHAT_LINK.LINK_ID, CHAT_LINK.SHARED_AT)
            .from(CHAT_LINK)
            .join(CHAT).on(CHAT_LINK.CHAT_ID.eq(CHAT.CHAT_ID))
            .where(CHAT_LINK.LINK_ID.eq(linkId))
            .fetch(record -> new ChatLinkDTO(
                record.get(CHAT_LINK.CHAT_ID),
                record.get(CHAT_LINK.LINK_ID),
                record.get(CHAT_LINK.SHARED_AT).toLocalDateTime()
            ));
    }

    @Override
    public boolean existsChatsForLink(long linkId) {
        return dslContext.fetchExists(
                dslContext.selectOne()
                          .from(CHAT_LINK)
                          .where(CHAT_LINK.LINK_ID.eq(linkId))
        );
    }
}

package edu.java.jooq.service;

import edu.java.exception.ChatAlreadyRegisteredException;
import edu.java.exception.ChatNotFoundException;
import edu.java.service.ChatService;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import java.time.OffsetDateTime;
import static edu.java.jooq.generated.Tables.CHAT;

@AllArgsConstructor
public class JooqChatService implements ChatService {

    private final DSLContext dslContext;

    @Override
    public void register(long chatId) {
        boolean exists = dslContext.fetchExists(
            dslContext.selectOne()
                      .from(CHAT)
                      .where(CHAT.CHAT_ID.eq(chatId))
        );

        if (!exists) {
            dslContext.insertInto(CHAT)
                      .columns(CHAT.CHAT_ID, CHAT.CREATED_AT)
                      .values(chatId, OffsetDateTime.now())
                      .execute();
        } else {
            throw new ChatAlreadyRegisteredException("Chat with id " + chatId + " already exists.");
        }
    }

    @Override
    public void unregister(long chatId) {

        boolean exists = dslContext.fetchExists(
            dslContext.selectOne()
                .from(CHAT)
                .where(CHAT.CHAT_ID.eq(chatId))
        );

        if (exists) {
            dslContext.deleteFrom(CHAT)
                .where(CHAT.CHAT_ID.eq(chatId))
                .execute();
        } else {
            throw new ChatNotFoundException("Chat with Id " + chatId + " not found.");
        }
    }
}

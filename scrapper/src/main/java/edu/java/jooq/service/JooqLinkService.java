package edu.java.jooq.service;

import edu.java.dto.LinkDTO;
import edu.java.jooq.generated.Tables;
import edu.java.jooq.generated.tables.records.LinkRecord;
import edu.java.service.LinkService;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static edu.java.jooq.generated.Tables.LINK;

@AllArgsConstructor
public class JooqLinkService implements LinkService {

    private final DSLContext dslContext;

    @Override
    public LinkDTO add(String url, String description) {
        OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);

        LinkRecord record = dslContext.insertInto(LINK, LINK.URL, LINK.DESCRIPTION, LINK.CREATED_AT)
                                      .values(url, description, nowUtc)
                                      .returning(LINK.LINK_ID, LINK.URL, LINK.DESCRIPTION, LINK.CREATED_AT)
                                      .fetchOne();
        return new LinkDTO(record.getLinkId(), record.getUrl(), record.getDescription(),
            record.getCreatedAt().toLocalDateTime(), null, null);
    }

    @Override
    public void remove(String url) {
        dslContext.deleteFrom(LINK)
                  .where(LINK.URL.eq(url))
                  .execute();
    }

    @Override
    public Collection<LinkDTO> listAll() {
        List<LinkRecord> records = dslContext.selectFrom(LINK).fetch();
        return records.stream()
                      .map(record -> new LinkDTO(record.getLinkId(),
                          record.getUrl(),
                          record.getDescription(),
                          record.getCreatedAt().toLocalDateTime(),
                          record.getLastCheckTime() == null ? null : record.getLastCheckTime().toLocalDateTime(),
                          record.getLastUpdateTime() == null ? null : record.getLastUpdateTime().toLocalDateTime()))
                      .collect(Collectors.toList());
    }

    @Override
    public void update(LinkDTO link) {
        dslContext.update(LINK)
                  .set(LINK.URL, link.getUrl())
                  .set(LINK.DESCRIPTION, link.getDescription())
                  .where(LINK.LINK_ID.eq(link.getLinkId()))
                  .execute();
    }

    @Override
    public Collection<LinkDTO> findLinksToCheck(LocalDateTime sinceTime) {
        OffsetDateTime sinceTimeOffset = sinceTime.atOffset(ZoneOffset.UTC);

        List<LinkRecord> records = dslContext.selectFrom(LINK)
                                              .where(LINK.LAST_CHECK_TIME.lessThan(sinceTimeOffset))
                                              .fetch();
        return records.stream()
                      .map(record -> new LinkDTO(record.getLinkId(),
                          record.getUrl(),
                          record.getDescription(),
                          record.getCreatedAt().toLocalDateTime(),
                          record.getLastCheckTime() == null ? null : record.getLastCheckTime().toLocalDateTime(),
                          record.getLastUpdateTime() == null ? null : record.getLastUpdateTime().toLocalDateTime())
                      )
                      .collect(Collectors.toList());
    }

    @Override
    public LinkDTO findById(Long linkId) {
        LinkRecord record = dslContext.selectFrom(LINK)
                                      .where(LINK.LINK_ID.eq(linkId))
                                      .fetchOne();
        return record == null ? null :
            new LinkDTO(record.getLinkId(),
            record.getUrl(),
            record.getDescription(),
            record.getCreatedAt().toLocalDateTime(),
            record.getLastCheckTime() == null ? null : record.getLastCheckTime().toLocalDateTime(),
            record.getLastUpdateTime() == null ? null : record.getLastUpdateTime().toLocalDateTime()
            );
    }

    @Override
    public LinkDTO findByUrl(String linkUrl) {
        LinkRecord record = dslContext.selectFrom(LINK)
                                      .where(LINK.URL.eq(linkUrl))
                                      .fetchOne();
        return record == null ? null
            : new LinkDTO(record.getLinkId(),
            record.getUrl(),
            record.getDescription(),
            record.getCreatedAt().toLocalDateTime(),
            record.getLastCheckTime() == null ? null : record.getLastCheckTime().toLocalDateTime(),
            record.getLastUpdateTime() == null ? null : record.getLastUpdateTime().toLocalDateTime()
            );
    }
}

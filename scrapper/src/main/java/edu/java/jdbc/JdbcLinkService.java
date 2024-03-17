package edu.java.jdbc;

import edu.java.dao.LinkDao;
import edu.java.dto.LinkDTO;
import edu.java.exception.LinkAlreadyAddedException;
import edu.java.exception.LinkNotFoundException;
import edu.java.service.LinkService;
import java.time.LocalDateTime;
import java.util.Collection;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class JdbcLinkService implements LinkService {

    private final LinkDao linkDao;
    private final static String NOT_FOUND = "not found";

    @Override
    public LinkDTO add(String url, String description) throws LinkAlreadyAddedException {
        if (linkDao.existsByUrl(url)) {
            throw new LinkAlreadyAddedException(url + " already exists.");
        }
        LinkDTO link = new LinkDTO(null, url, description, LocalDateTime.now(), null, null);
        link.setLinkId(linkDao.add(link));
        return link;
    }

    @Override
    public void remove(String url) {
        if (!linkDao.existsByUrl(url)) {
            throw new LinkNotFoundException(url + " " + NOT_FOUND + ".");
        }
        linkDao.remove(url);
    }

    @Override
    public Collection<LinkDTO> listAll() {
        return linkDao.findAll();
    }

    @Override
    public void update(LinkDTO link) {
        if (!linkDao.existsByUrl(link.getUrl())) {
            throw new LinkNotFoundException(link.getUrl() + " " + NOT_FOUND + ".");
        }
        linkDao.update(link);
    }

    @Override
    public LinkDTO findById(Long linkId) {
        LinkDTO foundDTO = linkDao.findById(linkId);
        if (foundDTO == null) {
            throw new LinkNotFoundException(linkId + " " + NOT_FOUND + ".");
        }
        return foundDTO;
    }

    @Override
    public LinkDTO findByUrl(String linkUrl) {
        LinkDTO foundDTO = linkDao.findByUrl(linkUrl);
        if (foundDTO == null) {
            throw new LinkNotFoundException(linkUrl + " " + NOT_FOUND + ".");
        }
        return foundDTO;
    }

    @Override
    public Collection<LinkDTO> findLinksToCheck(LocalDateTime sinceTime) {
        return linkDao.findLinksNotCheckedSince(sinceTime);
    }
}

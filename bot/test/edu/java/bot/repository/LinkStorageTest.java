package edu.java.bot.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LinkStorageTest {

    private final Long id = -1L;

    @BeforeEach
    void setup() {
        LinkStorage.clear();
    }

    @Test
    void whenAddLink_thenLinkIsStored() {
        String url = "http://example.com";
        assertTrue(LinkStorage.addLink(id, url));
        assertTrue(LinkStorage.getLinks(id).contains(url));
    }

    @Test
    void whenAddDuplicateLink_thenLinkIsNotStoredTwice() {
        String url = "http://example.com";
        LinkStorage.addLink(id, url);
        assertFalse(LinkStorage.addLink(id, url));
        assertEquals(1, LinkStorage.getLinks(id).size());
    }

    @Test
    void whenRemoveLink_thenLinkIsNoLongerStored() {
        String url = "http://example.com";
        LinkStorage.addLink(id, url);
        assertTrue(LinkStorage.removeLink(id, url));
        assertFalse(LinkStorage.getLinks(id).contains(url));
    }
}

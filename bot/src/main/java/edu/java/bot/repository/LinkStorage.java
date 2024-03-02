package edu.java.bot.repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LinkStorage {

    private LinkStorage() {}

    private static final Map<Long, Set<String>> LINKS = Collections.synchronizedMap(new HashMap<>());

    public static boolean addLink(Long chatId, String url) {
        LINKS.computeIfAbsent(chatId, k -> Collections.synchronizedSet(new HashSet<>()));
        return LINKS.get(chatId).add(url);
    }

    public static boolean removeLink(Long chatId, String url) {
        Set<String> chatLinks = LINKS.get(chatId);
        if (chatLinks != null) {
            return chatLinks.remove(url);
        }
        return false;
    }

    public static Set<String> getLinks(Long chatId) {
        return new HashSet<>(LINKS.getOrDefault(chatId, Collections.emptySet()));
    }

    public static void clear() {
        LINKS.clear();
    }
}

package edu.java.dao;

import edu.java.dto.LinkDTO;
import java.util.List;

public interface LinkDao {
    void add(LinkDTO link);

    void remove(Long linkId);

    List<LinkDTO> findAll();
}

package com.whiteboard.service;

import com.whiteboard.dao.model.Board;
import com.whiteboard.dao.model.TextEntity;

import java.util.List;

public interface TextBoardConService {

    List<Integer> getAllTextIds(Board board);

    void save(Integer textEntityId);

    void remove(TextEntity textEntity);
}

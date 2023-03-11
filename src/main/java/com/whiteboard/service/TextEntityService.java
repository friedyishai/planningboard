package com.whiteboard.service;

import com.whiteboard.dao.model.TextEntity;

import java.util.List;

public interface TextEntityService {

    List<TextEntity> getAllTexts(List<Integer> textsIds);

    TextEntity save(TextEntity textEntity);

    void remove(TextEntity textEntity);
}

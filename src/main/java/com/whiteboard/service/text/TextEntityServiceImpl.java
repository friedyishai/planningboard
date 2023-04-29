package com.whiteboard.service.text;

import com.whiteboard.dao.model.TextEntity;
import com.whiteboard.dao.repository.TextRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class TextEntityServiceImpl implements TextEntityService {

    private final TextRepository textRepository;

    @Override
    public List<TextEntity> getAllTexts(List<Integer> textsIds) {
        return textRepository.findByIdInAndIsActive(textsIds, true);
    }

    @Override
    public TextEntity save(TextEntity textEntity) {
        return textRepository.saveAndFlush(textEntity);
    }

    @Override
    public void remove(TextEntity textEntity) {
        textEntity.setIsActive(false);
        save(textEntity);
    }

    @Override
    public TextEntity findById(int textEntityId) {
        return textRepository.findById(textEntityId).get();
    }
}

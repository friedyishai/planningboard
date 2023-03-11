package com.whiteboard.general;

import com.whiteboard.dao.model.Board;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class DisplayedBoard {

    private Board board;
}

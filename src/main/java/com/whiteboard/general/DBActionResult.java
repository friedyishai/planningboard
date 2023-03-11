package com.whiteboard.general;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class DBActionResult {

    private boolean isSuccess;
    private String failureReason;
}

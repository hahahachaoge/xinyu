package com.example.web.dto;

import java.util.List;
import lombok.Data;

@Data
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class AssessmentSubmitInput {
    private Integer scaleInfoId;
    private List<Answer> answers;
    @Data
    public static class Answer {
        private Integer questionId;
        /** Zero-based position in the stored option array. */
        private Integer optionIndex;
    }
}

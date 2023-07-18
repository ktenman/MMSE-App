package ee.tenman.mmse.service.dto;

import ee.tenman.mmse.domain.enumeration.QuestionId;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class AnswerDTO implements Serializable {

    @EqualsAndHashCode.Include
    private Long id;

    private String answerText;

    @NotNull
    private QuestionId questionId;

}

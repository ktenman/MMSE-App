package ee.tenman.mmse.service;

import ee.tenman.mmse.domain.OrientationToPlaceAnswer;
import ee.tenman.mmse.domain.PatientProfile;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.repository.OrientationToPlaceAnswerRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrientationToPlaceAnswerService {

    @Resource
    private OrientationToPlaceAnswerRepository orientationToPlaceAnswerRepository;

    public Optional<OrientationToPlaceAnswer> findByPatientProfileAndQuestionId(PatientProfile patientProfile, QuestionId questionId) {
        return orientationToPlaceAnswerRepository.findByPatientProfileAndQuestionId(patientProfile, questionId);
    }

    public void save(OrientationToPlaceAnswer newAnswer) {
        orientationToPlaceAnswerRepository.save(newAnswer);
    }

    public List<OrientationToPlaceAnswer> saveAll(Iterable<OrientationToPlaceAnswer> newAnswers) {
        List<OrientationToPlaceAnswer> answersToSave = new ArrayList<>();

        for (OrientationToPlaceAnswer newAnswer : newAnswers) {
            Optional<OrientationToPlaceAnswer> existingAnswer = orientationToPlaceAnswerRepository
                .findByQuestionIdAndPatientProfileId(newAnswer.getQuestionId(), newAnswer.getPatientProfile().getId());

            if (existingAnswer.isPresent()) {
                OrientationToPlaceAnswer answer = existingAnswer.get();
                answer.setCorrectAnswer(newAnswer.getCorrectAnswer());
                answer.setAnswerOptions(newAnswer.getAnswerOptions());
                answersToSave.add(answer);
            } else {
                answersToSave.add(newAnswer);
            }
        }

        return orientationToPlaceAnswerRepository.saveAll(answersToSave);
    }
}

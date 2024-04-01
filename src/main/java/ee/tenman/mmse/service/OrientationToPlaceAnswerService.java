package ee.tenman.mmse.service;

import ee.tenman.mmse.domain.OrientationToPlaceAnswer;
import ee.tenman.mmse.domain.PatientProfile;
import ee.tenman.mmse.domain.enumeration.QuestionId;
import ee.tenman.mmse.repository.OrientationToPlaceAnswerRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

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
}

package ee.tenman.mmse.service.external.prediction;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class PredictionService {

    private static final Logger log = LoggerFactory.getLogger(PredictionService.class);

    @Resource
    private PredictionClient predictionClient;

    public boolean isGrammaticallyCorrect(String sentence) {
        log.info("Checking if sentence is grammatically correct: {}", sentence);
        PredictionRequest request = new PredictionRequest(sentence);
        PredictionResponse response = predictionClient.predict(request);
        log.info("Prediction response: {}", response);
        return response.isGrammaticallyCorrect();
    }

}

package ee.tenman.mmse.service.prediction;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "predictionClient", url = "http://localhost:5001")
public interface PredictionClient {

    @PostMapping("/predict")
    PredictionResponse predict(@RequestBody PredictionRequest request);

}

package ee.tenman.mmse.service.external.prediction;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "predictionClient", url = "http://127.0.0.1:61234")
public interface PredictionClient {

    @PostMapping("/predict")
    PredictionResponse predict(@RequestBody PredictionRequest request);

}

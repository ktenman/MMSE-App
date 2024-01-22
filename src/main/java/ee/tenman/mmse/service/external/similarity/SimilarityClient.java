package ee.tenman.mmse.service.external.similarity;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "similarityClient", url = "http://127.0.0.1:61237")
public interface SimilarityClient {

    @PostMapping("/compare")
    SimilarityResponse compare(@RequestBody SimilarityRequest request);

}

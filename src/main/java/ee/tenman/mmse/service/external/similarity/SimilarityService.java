package ee.tenman.mmse.service.external.similarity;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class SimilarityService {

    private static final Logger log = LoggerFactory.getLogger(SimilarityService.class);

    private static final double SIMILARITY_UPPER_LIMIT = 0.6;

    @Resource
    private SimilarityClient similarityClient;

    public boolean isSimilar(SimilarityRequest request) {
        log.info("Checking similarity. Similarity request: {}", request);
        SimilarityResponse response = similarityClient.compare(request);
        log.info("Similarity response: {}", response);
        return response.getSimilarity() >= SIMILARITY_UPPER_LIMIT;
    }

}

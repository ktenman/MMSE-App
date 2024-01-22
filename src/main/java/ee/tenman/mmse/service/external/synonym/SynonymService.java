package ee.tenman.mmse.service.external.synonym;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class SynonymService {

    private static final Logger log = LoggerFactory.getLogger(SynonymService.class);

    private static final double SYNONYM_THRESHOLD = 0.6;

    @Resource
    private SynonymClient synonymClient;

    public boolean isSynonym(SynonymRequest request) {
        log.info("Checking synonym: {}", request);
        SynonymResponse response = synonymClient.synonym(request);
        log.info("Synonym response: {}", response);
        return response.getSimilarityScore() >= SYNONYM_THRESHOLD;
    }

}

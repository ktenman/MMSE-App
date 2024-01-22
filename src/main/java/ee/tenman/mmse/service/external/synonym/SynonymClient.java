package ee.tenman.mmse.service.external.synonym;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "synonymClient", url = "http://127.0.0.1:61238")
public interface SynonymClient {

    @PostMapping("/synonym")
    SynonymResponse synonym(@RequestBody SynonymRequest request);

}

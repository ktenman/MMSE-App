package ee.tenman.mmse.service.external.dolphin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "dolphinClient", url = "http://127.0.0.1:11434")
public interface DolphinClient {
    @PostMapping("/api/generate")
    DolphinResponse generate(@RequestBody DolphinRequest request);
}

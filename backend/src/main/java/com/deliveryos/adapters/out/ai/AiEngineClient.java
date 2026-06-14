package com.deliveryos.adapters.out.ai;

import com.deliveryos.adapters.out.ai.dto.Co2Request;
import com.deliveryos.adapters.out.ai.dto.Co2Response;
import com.deliveryos.adapters.out.ai.dto.VrpRequest;
import com.deliveryos.adapters.out.ai.dto.VrpResponse;
import com.deliveryos.config.AiEngineConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.deliveryos.adapters.out.ai.dto.EtaRequest;
import com.deliveryos.adapters.out.ai.dto.EtaResponse;

@FeignClient(
        name = "ai-engine",
        url = "${ai-engine.base-url}",
        configuration = AiEngineConfig.class
)
public interface AiEngineClient {

    @PostMapping("/api/vrp/optimize")
    VrpResponse optimizeRoutes(@RequestBody VrpRequest request);

    @PostMapping("/api/co2/predict")
    Co2Response predictCo2(@RequestBody Co2Request request);

    @GetMapping("/health")
    Object healthCheck();

    @PostMapping("/api/eta/predict")
    EtaResponse predictEta(@RequestBody EtaRequest request);
}
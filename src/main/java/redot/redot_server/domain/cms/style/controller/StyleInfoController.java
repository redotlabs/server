package redot.redot_server.domain.cms.style.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.cms.style.dto.StyleInfoResponse;
import redot.redot_server.domain.cms.style.dto.StyleInfoUpdateRequest;
import redot.redot_server.domain.cms.style.service.StyleInfoService;
import redot.redot_server.support.customer.resolver.annotation.CurrentCustomer;

@RestController
@RequestMapping("/api/v1/customer/cms/style-info")
@RequiredArgsConstructor
public class StyleInfoController {
    private final StyleInfoService styleInfoService;

    @GetMapping
    public ResponseEntity<StyleInfoResponse> getStyleInfo(@CurrentCustomer Long customerId) {
        StyleInfoResponse styleInfoResponse = styleInfoService.getStyleInfo(customerId);
        return ResponseEntity.ok(styleInfoResponse);
    }

    @PatchMapping
    public ResponseEntity<StyleInfoResponse> updateStyleInfo(@CurrentCustomer Long customerId, @RequestBody StyleInfoUpdateRequest request) {
        return ResponseEntity.ok().body(styleInfoService.updateStyleInfo(customerId, request));
    }
}

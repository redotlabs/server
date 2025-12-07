package redot.redot_server.domain.cms.inquiry.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.cms.inquiry.controller.docs.RedotAppInquiryControllerDocs;
import redot.redot_server.domain.cms.inquiry.dto.request.RedotAppInquiryCreateRequest;
import redot.redot_server.domain.cms.inquiry.dto.response.RedotAppInquiryResponse;
import redot.redot_server.domain.cms.inquiry.dto.request.RedotAppInquirySearchCondition;
import redot.redot_server.domain.cms.inquiry.service.RedotAppInquiryService;
import redot.redot_server.global.util.dto.response.PageResponse;
import redot.redot_server.global.redotapp.resolver.annotation.CurrentRedotApp;
import redot.redot_server.global.security.principal.JwtPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/app/cms/inquiries")
public class RedotAppInquiryController implements RedotAppInquiryControllerDocs {

    private final RedotAppInquiryService inquiryService;

    @PostMapping
    @Override
    public ResponseEntity<RedotAppInquiryResponse> createInquiry(@CurrentRedotApp Long redotAppId,
                                                                 @Valid @RequestBody RedotAppInquiryCreateRequest request) {
        return ResponseEntity.ok(inquiryService.createInquiry(redotAppId, request));
    }

    @GetMapping("/{inquiryId}")
    @Override
    public ResponseEntity<RedotAppInquiryResponse> getInquiry(@CurrentRedotApp Long redotAppId,
                                                              @PathVariable("inquiryId") Long inquiryId) {
        return ResponseEntity.ok(inquiryService.getInquiry(redotAppId, inquiryId));
    }

    @GetMapping
    @Override
    public ResponseEntity<PageResponse<RedotAppInquiryResponse>> getAllInquiries(
            @CurrentRedotApp Long redotAppId,
            RedotAppInquirySearchCondition searchCondition,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<RedotAppInquiryResponse> response = inquiryService
                .getAllInquiriesBySearchCondition(redotAppId, searchCondition, pageable);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{inquiryId}/complete")
    @Override
    public ResponseEntity<Void> markInquiryAsCompleted(@CurrentRedotApp Long redotAppId,
                                                       @PathVariable("inquiryId") Long inquiryId,
                                                       @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        inquiryService.markInquiryAsCompleted(redotAppId, inquiryId, jwtPrincipal.id());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{inquiryId}/reopen")
    @Override
    public ResponseEntity<Void> reopenInquiry(@CurrentRedotApp Long redotAppId,
                                              @PathVariable("inquiryId") Long inquiryId) {
        inquiryService.reopenInquiry(redotAppId, inquiryId);
        return ResponseEntity.ok().build();
    }
}

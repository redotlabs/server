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
import redot.redot_server.domain.cms.inquiry.dto.CustomerInquiryCreateRequest;
import redot.redot_server.domain.cms.inquiry.dto.CustomerInquiryDTO;
import redot.redot_server.domain.cms.inquiry.dto.CustomerInquirySearchCondition;
import redot.redot_server.domain.cms.inquiry.service.CustomerInquiryService;
import redot.redot_server.support.common.dto.PageResponse;
import redot.redot_server.support.customer.resolver.annotation.CurrentCustomer;
import redot.redot_server.support.security.principal.JwtPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer/inquiries")
public class CustomerInquiryController {

    private final CustomerInquiryService inquiryService;

    @PostMapping
    public ResponseEntity<CustomerInquiryDTO> createInquiry(@CurrentCustomer Long customerId,
                                                            @Valid @RequestBody CustomerInquiryCreateRequest request) {
        return ResponseEntity.ok(inquiryService.createInquiry(customerId, request));
    }

    @GetMapping("/{inquiryId}")
    public ResponseEntity<CustomerInquiryDTO> getInquiry(@CurrentCustomer Long customerId,
                                                         @PathVariable("inquiryId") Long inquiryId) {
        return ResponseEntity.ok(inquiryService.getInquiry(customerId, inquiryId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<CustomerInquiryDTO>> getAllInquiries(
            @CurrentCustomer Long customerId,
            CustomerInquirySearchCondition searchCondition,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<CustomerInquiryDTO> response = inquiryService
                .getAllInquiriesBySearchCondition(customerId, searchCondition, pageable);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{inquiryId}/complete")
    public ResponseEntity<Void> markInquiryAsCompleted(@CurrentCustomer Long customerId,
                                                       @PathVariable("inquiryId") Long inquiryId,
                                                       @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        inquiryService.markInquiryAsCompleted(customerId, inquiryId, jwtPrincipal.id());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{inquiryId}/reopen")
    public ResponseEntity<Void> reopenInquiry(@CurrentCustomer Long customerId,
                                              @PathVariable("inquiryId") Long inquiryId) {
        inquiryService.reopenInquiry(customerId, inquiryId);
        return ResponseEntity.ok().build();
    }
}

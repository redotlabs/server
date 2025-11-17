package redot.redot_server.domain.cms.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.cms.customer.dto.CustomerInfoResponse;
import redot.redot_server.domain.cms.customer.service.CustomerService;
import redot.redot_server.support.customer.resolver.annotation.CurrentCustomer;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<CustomerInfoResponse> getCustomerInfo(@CurrentCustomer Long customerId) {
        return ResponseEntity.ok(customerService.getCustomerInfo(customerId));
    }
}

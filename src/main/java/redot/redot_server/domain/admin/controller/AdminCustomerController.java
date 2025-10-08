package redot.redot_server.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.admin.service.AdminCustomerService;
import redot.redot_server.domain.cms.dto.CustomerCreateRequest;
import redot.redot_server.domain.cms.dto.CustomerCreateResponse;

@RestController
@RequestMapping("/api/v1/admin/customer")
@RequiredArgsConstructor
public class AdminCustomerController {

    private final AdminCustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerCreateResponse> createCustomer(@RequestBody CustomerCreateRequest request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }
}

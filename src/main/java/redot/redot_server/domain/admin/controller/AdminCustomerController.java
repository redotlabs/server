package redot.redot_server.domain.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redot.redot_server.domain.admin.dto.AdminCustomerResponse;
import redot.redot_server.domain.admin.dto.CustomerCreateRequest;
import redot.redot_server.domain.admin.service.AdminCustomerService;

@RestController
@RequestMapping("/admin/customers")
@RequiredArgsConstructor
public class AdminCustomerController {

    private final AdminCustomerService service;

    @PostMapping
    public AdminCustomerResponse create(@Valid @RequestBody CustomerCreateRequest req) {
        return service.create(req);
    }
}

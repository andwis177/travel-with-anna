package com.andwis.travel_with_anna.role;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("role")
@RequiredArgsConstructor
@Tag(name = "Role")
public class RoleController {
    private final RoleService service;

    @GetMapping("/all-names")
    public ResponseEntity<List<String>> getAllRoleNames() {
        List<String> roles = service.getAllRoleNames();
        return ResponseEntity.ok(roles);
    }
}

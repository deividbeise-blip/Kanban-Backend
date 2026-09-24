package br.com.devbeise.spring_boot_kanban.controller;

import br.com.devbeise.spring_boot_kanban.dto.UserDto;
import br.com.devbeise.spring_boot_kanban.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid UserDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> findByName(@RequestParam String name) {
        return ResponseEntity.ok(userService.findByName(name));
    }

    @PatchMapping("/{id}/login-timestamp")
    public ResponseEntity<Void> updateLoginTimestamp(@PathVariable Long id) {
        userService.updateLoginTimestamp(id);
        return ResponseEntity.noContent().build();
    }
}

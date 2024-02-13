package ru.practicum.controller.adminapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserAdminController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid NewUserRequest userDto) {
        log.info("Запрос на добавление пользователя: {}", userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDto));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam(value = "ids") Optional<List<Long>> ids,
                                                  @RequestParam(value = "from", defaultValue = "0") @Min(value = 0) Integer from,
                                                  @RequestParam(value = "size", defaultValue = "10") @Min(value = 1) Integer size) {
        log.info("Запрос на получение пользователя / пользователей с id {}", ids.orElse(List.of()));
        return ResponseEntity.ok(userService.getUsers(ids, from, size));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Positive Long userId) {
        log.info("Запрос на удаление пользователя с id {}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}

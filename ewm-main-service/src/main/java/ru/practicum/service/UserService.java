package ru.practicum.service;

import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto createUser(NewUserRequest userDto);

    List<UserDto> getUsers(Optional<List<Long>> ids, Integer from, Integer size);

    void deleteUser(Long userId);
}

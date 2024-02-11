package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest userDto) {
        User user = userMapper.userDtoRequestToUser(userDto);
        return userMapper.userToUserDtoResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(Optional<List<Long>> ids, Integer from, Integer size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        if (ids.isEmpty()) {
            return userMapper.userToUserDtoResponse(userRepository.findAll(pageRequest).getContent());
        } else {
            return userMapper.userToUserDtoResponse(userRepository.findAllByIdIn(ids.get(), pageRequest).getContent());
        }
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "users", key = "#userId")
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("User with id=%d was not found", userId));
        }
        userRepository.deleteById(userId);
    }
}

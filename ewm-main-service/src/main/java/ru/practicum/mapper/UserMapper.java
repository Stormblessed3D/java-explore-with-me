package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.UserShortDto;
import ru.practicum.model.User;
import ru.practicum.dto.NewUserRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    public static final UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    public abstract User userDtoRequestToUser(NewUserRequest newUserRequest);

    public abstract UserDto userToUserDtoResponse(User user);

    public abstract UserShortDto userToUserShortDtoResponse(User user);

    public abstract List<UserDto> userToUserDtoResponse(List<User> users);

}

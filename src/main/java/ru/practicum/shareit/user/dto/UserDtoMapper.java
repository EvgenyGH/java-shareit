package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class UserDtoMapper {
    public static UserDto UserToDto(User user){
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}

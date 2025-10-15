package com.i2i.usermanagement.mapper;

import com.i2i.usermanagement.dto.UserResponseDTO;
import com.i2i.usermanagement.dto.UserCreateDTO;
import com.i2i.usermanagement.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Simple MapStruct mapper for converting between User entity and DTOs.
 * 
 * @author Agnel Ruban
 * @version 1.0
 * @since 13-10-2025
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts User entity to UserResponseDTO.
     */
    UserResponseDTO toDTO(User user);

    /**
     * Converts UserCreateDTO to User entity.
     */
    User toEntity(UserCreateDTO userCreateDTO);

    /**
     * Converts a list of User entities to a list of UserResponseDTOs.
     */
    List<UserResponseDTO> toDTOList(List<User> users);
}

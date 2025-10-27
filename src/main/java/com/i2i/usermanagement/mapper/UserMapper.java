package com.i2i.usermanagement.mapper;

import com.i2i.usermanagement.dto.UserResponseDTO;
import com.i2i.usermanagement.dto.UserCreateDTO;
import com.i2i.usermanagement.entity.User;
import com.i2i.usermanagement.document.UserProfile;
import org.mapstruct.Mapper;

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
     * Converts UserCreateDTO to User entity.
     */
    User toEntity(UserCreateDTO userCreateDTO);

    /**
     * Converts User entity and UserProfile document to combined UserResponseDTO.
     * This method combines data from both PostgreSQL and MongoDB.
     */
    default UserResponseDTO toCombinedDTO(User user, UserProfile profile) {
        if (user == null) {
            return null;
        }

        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .age(profile.getAge())
                .phoneNumber(profile.getPhoneNumber())
                .address(profile.getAddress())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

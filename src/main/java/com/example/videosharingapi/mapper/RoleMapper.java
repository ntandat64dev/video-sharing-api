package com.example.videosharingapi.mapper;

import com.example.videosharingapi.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    default String toString(Role role) {
        return role.getName();
    }
}

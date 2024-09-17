package com.cortex.backend.user.internal;

import com.cortex.backend.user.api.dto.UserResponse;
import com.cortex.backend.user.domain.Role;
import com.cortex.backend.user.domain.User;
import com.cortex.backend.media.domain.Media;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "avatarUrl", source = "avatar", qualifiedByName = "mediaToUrl")
  @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToStringList")
  UserResponse toUserResponse(User user);

  @Named("mediaToUrl")
  default String mediaToUrl(Media media) {
    return media != null ? media.getUrl() : null;
  }

  @Named("rolesToStringList")
  default List<String> rolesToStringList(List<Role> roles) {
    return roles.stream()
        .map(Role::getName)
        .toList();
  }
}
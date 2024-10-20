package com.cortex.backend.user.internal;

import com.cortex.backend.core.domain.Media;
import com.cortex.backend.user.api.dto.UserResponse;
import com.cortex.backend.core.domain.Role;
import com.cortex.backend.core.domain.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {
  @Mapping(target = "avatarUrl", source = "avatar", qualifiedByName = "mediaToUrl")
  @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToStringList")
  @Mapping(target = "firstName", source = "firstName")
  @Mapping(target = "lastName", source = "lastName")
  @Mapping(target = "fullName", expression = "java(user.getFullName())")
  @Mapping(target = "dateOfBirth", source = "dateOfBirth")
  @Mapping(target = "countryCode", source = "countryCode")
  @Mapping(target = "accountLocked", source = "accountLocked")
  @Mapping(target = "createdAt", source = "createdAt")
  @Mapping(target = "updatedAt", source = "updatedAt")
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
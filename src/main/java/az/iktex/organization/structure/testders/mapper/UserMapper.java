package az.iktex.organization.structure.testders.mapper;

import az.iktex.organization.structure.testders.request.UserRequest;
import az.iktex.organization.structure.testders.response.UserResponse;
import az.iktex.organization.structure.testders.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = PassportMapper.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passport", ignore = true)
    User toEntity(UserRequest request);

    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passport", ignore = true)
    void updateEntity(UserRequest request, @MappingTarget User user);
}

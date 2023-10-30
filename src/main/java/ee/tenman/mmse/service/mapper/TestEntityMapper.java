package ee.tenman.mmse.service.mapper;

import ee.tenman.mmse.domain.TestEntity;
import ee.tenman.mmse.domain.User;
import ee.tenman.mmse.service.dto.TestEntityDTO;
import ee.tenman.mmse.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TestEntity} and its DTO {@link TestEntityDTO}.
 */
@Mapper(componentModel = "spring")
public interface TestEntityMapper extends EntityMapper<TestEntityDTO, TestEntity> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    TestEntityDTO toDto(TestEntity s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}

package ee.tenman.mmse.service.mapper;

import ee.tenman.mmse.domain.TestEntity;
import ee.tenman.mmse.domain.UserAnswer;
import ee.tenman.mmse.service.dto.TestEntityDTO;
import ee.tenman.mmse.service.dto.UserAnswerDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link UserAnswer} and its DTO {@link UserAnswerDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserAnswerMapper extends EntityMapper<UserAnswerDTO, UserAnswer> {
    @Mapping(target = "testEntity", source = "testEntity", qualifiedByName = "testEntityId")
    UserAnswerDTO toDto(UserAnswer s);

    @Named("testEntityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TestEntityDTO toDtoTestEntityId(TestEntity testEntity);
}

package ee.tenman.mmse.service.mapper;

import ee.tenman.mmse.domain.PatientProfile;
import ee.tenman.mmse.domain.TestEntity;
import ee.tenman.mmse.domain.User;
import ee.tenman.mmse.service.dto.PatientProfileDTO;
import ee.tenman.mmse.service.dto.TestEntityDTO;
import ee.tenman.mmse.service.dto.UserDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link TestEntity} and its DTO {@link TestEntityDTO}.
 */
@Mapper(componentModel = "spring")
public interface TestEntityMapper extends EntityMapper<TestEntityDTO, TestEntity> {

    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "patientProfile", source = "patientProfile", qualifiedByName = "patientProfileId")
    @Mapping(target = "hash", source = "testEntityHash.hash")
    TestEntityDTO toDto(TestEntity testEntity);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("patientProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "patientId", source = "patientId")
    @Mapping(target = "name", source = "name")
    PatientProfileDTO toDtoPatientProfileId(PatientProfile patientProfile);
}

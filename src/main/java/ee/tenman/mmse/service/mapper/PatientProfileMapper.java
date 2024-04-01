package ee.tenman.mmse.service.mapper;

import ee.tenman.mmse.domain.PatientProfile;
import ee.tenman.mmse.service.dto.PatientProfileDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link PatientProfile} and its DTO {@link PatientProfileDTO}.
 */
@Mapper(componentModel = "spring")
public interface PatientProfileMapper extends EntityMapper<PatientProfileDTO, PatientProfile> {
}

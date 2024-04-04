package ee.tenman.mmse.service.dto;

import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Optional;

public class PatientProfileRequest {

    private Long id;

    @NotEmpty
    private String patientId;

    @NotEmpty
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientId() {
        return Optional.ofNullable(patientId)
            .map(String::toUpperCase)
            .orElse(null);
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return Optional.ofNullable(name)
            .map(StringUtils::strip)
            .orElse(null);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientProfileRequest that = (PatientProfileRequest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // prettier-ignore

    @Override
    public String toString() {
        return "PatientProfileRequest{" +
            "patientId='" + patientId + '\'' +
            ", name='" + name + '\'' +
            '}';
    }

}

package ee.tenman.mmse.service.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class PatientProfileRequest {

    private Long id;

    @NotNull
    private String patientId;

    @NotNull
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
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

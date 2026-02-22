package de.ruu.app.jeeeraaah.backend.common.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import de.ruu.app.jeeeraaah.backend.persistence.jpa.TaskGroupJPA;
import de.ruu.app.jeeeraaah.backend.persistence.jpa.TaskJPA;
import de.ruu.app.jeeeraaah.common.api.ws.rs.TaskDTO;
import de.ruu.lib.mapstruct.ReferenceCycleTracking;

public class MapTaskMappingTest
{
    @Test
    public void roundtrip_descriptionAndDates_areMapped() {
        TaskGroupJPA group = new TaskGroupJPA("group-1");
        TaskJPA jpa = new TaskJPA(group, "task-1");
        jpa.description("desc-jpa");
        jpa.start(LocalDate.of(2025,12,31));
        jpa.end(LocalDate.of(2026,1,1));

        ReferenceCycleTracking ctx = new ReferenceCycleTracking();
        TaskDTO dto = Mappings.toDTO(jpa, ctx);

        // check forward mapping (JPA -> DTO) using fluent API
        assertThat(dto.description().orElse(null)).isEqualTo("desc-jpa");
        assertThat(dto.start().orElse(null)).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(dto.end().orElse(null)).isEqualTo(LocalDate.of(2026, 1, 1));

        // change dto fields (fluent setters) and map back to JPA
        dto.description("desc-dto");
        dto.start(LocalDate.of(2024,1,1));
        dto.end(LocalDate.of(2024,12,31));

        TaskJPA jpa2 = Mappings.toJPA(dto, new ReferenceCycleTracking());

        assertThat(jpa2.description().orElse(null)).isEqualTo("desc-dto");
        assertThat(jpa2.start().orElse(null)).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(jpa2.end().orElse(null)).isEqualTo(LocalDate.of(2024, 12, 31));
    }
}
package de.ruu.app.jeeeraaah.common.api.mapping.bean_dto;
import de.ruu.app.jeeeraaah.common.api.bean.TaskBean;
import de.ruu.app.jeeeraaah.common.api.bean.TaskGroupBean;
import de.ruu.app.jeeeraaah.common.api.domain.Task;
import de.ruu.app.jeeeraaah.common.api.domain.TaskEntity;
import de.ruu.app.jeeeraaah.common.api.domain.TaskGroup;
import de.ruu.app.jeeeraaah.common.api.domain.TaskGroupEntity;
import de.ruu.app.jeeeraaah.common.api.ws.rs.TaskDTO;
import de.ruu.app.jeeeraaah.common.api.ws.rs.TaskGroupDTO;
import de.ruu.lib.mapstruct.ReferenceCycleTracking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
/**
 * Tests for {@link TaskMapper} - bidirectional Task Bean ↔ DTO mappings.
 */
class TaskMapperTest {
    private ReferenceCycleTracking context;
    private TaskGroupBean testGroup;
    private TaskGroupDTO testGroupDTO;
    @BeforeEach
    void setUp() {
        context = new ReferenceCycleTracking();
        testGroup = new TaskGroupBean("Test Group");
        testGroupDTO = new TaskGroupDTO(testGroup);
    }
    @Test
    void mapperInstanceExists() {
        assertThat(TaskMapper.INSTANCE).as("Mapper instance should exist").isNotNull();
    }
    @Test
    void beanToDTO_shouldMapBasicFields() {
        TaskBean bean = new TaskBean(testGroup, "Test Task");
        TaskDTO dto = TaskMapper.INSTANCE.toDTO(bean, context);
        assertThat(dto).isNotNull();
        assertThat(dto.name()).isEqualTo(bean.getName());
    }
    @Test
    void beanToDTO_shouldMapOptionalFields() {
        TaskBean bean = new TaskBean(testGroup, "Test Task");
        bean.description("Test Description");
        bean.start(LocalDate.now());
        bean.end(LocalDate.now().plusDays(7));
        TaskDTO dto = TaskMapper.INSTANCE.toDTO(bean, context);
        assertThat(dto).isNotNull();
        assertThat(dto.description()).isEqualTo(bean.description());
        assertThat(dto.start()).isEqualTo(bean.start());
        assertThat(dto.end()).isEqualTo(bean.end());
    }
    @Test
    void dtoToBean_shouldMapBasicFields() {
        TaskDTO dto = new TaskDTO(testGroupDTO, "Test Task");
        TaskBean bean = TaskMapper.INSTANCE.toBean(dto, context);
        assertThat(bean).isNotNull();
        assertThat(bean.getName()).isEqualTo(dto.name());
    }
    @Test
    void dtoToBean_shouldMapOptionalFields() {
        TaskDTO dto = new TaskDTO(testGroupDTO, "Test Task");
        dto.description("Test Description");
        dto.start(LocalDate.now());
        dto.end(LocalDate.now().plusDays(7));
        TaskBean bean = TaskMapper.INSTANCE.toBean(dto, context);
        assertThat(bean).isNotNull();
        assertThat(bean.description()).isEqualTo(dto.description());
        assertThat(bean.start()).isEqualTo(dto.start());
        assertThat(bean.end()).isEqualTo(dto.end());
    }
    @Test
    void bidirectionalMapping_shouldPreserveData() {
        TaskBean originalBean = new TaskBean(testGroup, "Original Task");
        originalBean.description("Original Description");
        originalBean.start(LocalDate.now());
        originalBean.end(LocalDate.now().plusDays(7));
        TaskDTO dto = TaskMapper.INSTANCE.toDTO(originalBean, context);
        ReferenceCycleTracking newContext = new ReferenceCycleTracking();
        TaskBean resultBean = TaskMapper.INSTANCE.toBean(dto, newContext);
        assertThat(resultBean).isNotNull();
        assertThat(resultBean.getName()).isEqualTo(originalBean.getName());
        assertThat(resultBean.description()).isEqualTo(originalBean.description());
    }
    @Test
    void cyclicReferenceDetection_shouldPreventInfiniteLoops() {
        TaskBean bean = new TaskBean(testGroup, "Test Task");
        TaskDTO dto1 = TaskMapper.INSTANCE.toDTO(bean, context);
        TaskDTO dto2 = TaskMapper.INSTANCE.toDTO(bean, context);
        assertThat(dto2).as("Context should prevent duplicate mappings").isSameAs(dto1);
    }
}

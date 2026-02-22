package de.ruu.app.jeeeraaah.common.api.mapping.bean_dto;
import de.ruu.app.jeeeraaah.common.api.bean.TaskGroupBean;
import de.ruu.app.jeeeraaah.common.api.ws.rs.TaskGroupDTO;
import de.ruu.lib.mapstruct.ReferenceCycleTracking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
/**
 * Tests for {@link TaskGroupMapper} - bidirectional TaskGroup Bean ↔ DTO mappings.
 */
class TaskGroupMapperTest {
    private ReferenceCycleTracking context;
    @BeforeEach
    void setUp() {
        context = new ReferenceCycleTracking();
    }
    @Test
    void mapperInstanceExists() {
        assertThat(TaskGroupMapper.INSTANCE).as("Mapper instance should exist").isNotNull();
    }
    @Test
    void beanToDTO_shouldMapBasicFields() {
        TaskGroupBean bean = new TaskGroupBean("Test Group");
        TaskGroupDTO dto = TaskGroupMapper.INSTANCE.toDTO(bean, context);
        assertThat(dto).isNotNull();
        assertThat(dto.name()).isEqualTo(bean.name());
    }
    @Test
    void beanToDTO_shouldMapDescriptionField() {
        TaskGroupBean bean = new TaskGroupBean("Test Group");
        bean.description("Test Description");
        TaskGroupDTO dto = TaskGroupMapper.INSTANCE.toDTO(bean, context);
        assertThat(dto).isNotNull();
        assertThat(dto.description()).isEqualTo(bean.description());
        assertThat(dto.description().isPresent()).isEqualTo(true);
        assertThat(dto.description().get()).isEqualTo("Test Description");
    }
    @Test
    void dtoToBean_shouldMapBasicFields() {
        TaskGroupBean sourceBean = new TaskGroupBean("Test Group");
        TaskGroupDTO dto = TaskGroupMapper.INSTANCE.toDTO(sourceBean, context);
        ReferenceCycleTracking newContext = new ReferenceCycleTracking();
        TaskGroupBean bean = TaskGroupMapper.INSTANCE.toBean(dto, newContext);
        assertThat(bean).isNotNull();
        assertThat(bean.name()).isEqualTo(dto.name());
    }
    @Test
    void dtoToBean_shouldMapDescriptionField() {
        TaskGroupBean sourceBean = new TaskGroupBean("Test Group");
        sourceBean.description("Test Description");
        TaskGroupDTO dto = TaskGroupMapper.INSTANCE.toDTO(sourceBean, context);
        ReferenceCycleTracking newContext = new ReferenceCycleTracking();
        TaskGroupBean bean = TaskGroupMapper.INSTANCE.toBean(dto, newContext);
        assertThat(bean).isNotNull();
        assertThat(bean.description()).isEqualTo(dto.description());
        assertThat(bean.description().isPresent()).isEqualTo(true);
        assertThat(bean.description().get()).isEqualTo("Test Description");
    }
    @Test
    void bidirectionalMapping_shouldPreserveData() {
        TaskGroupBean originalBean = new TaskGroupBean("Original Group");
        originalBean.description("Original Description");
        TaskGroupDTO dto = TaskGroupMapper.INSTANCE.toDTO(originalBean, context);
        ReferenceCycleTracking newContext = new ReferenceCycleTracking();
        TaskGroupBean resultBean = TaskGroupMapper.INSTANCE.toBean(dto, newContext);
        assertThat(resultBean).isNotNull();
        assertThat(resultBean.name()).isEqualTo(originalBean.name());
        assertThat(resultBean.description()).isEqualTo(originalBean.description());
    }
    @Test
    void cyclicReferenceDetection_shouldPreventInfiniteLoops() {
        TaskGroupBean bean = new TaskGroupBean("Test Group");
        TaskGroupDTO dto1 = TaskGroupMapper.INSTANCE.toDTO(bean, context);
        TaskGroupDTO dto2 = TaskGroupMapper.INSTANCE.toDTO(bean, context);
        assertThat(dto2).as("Context should prevent duplicate mappings").isSameAs(dto1);
    }
    @Test
    void emptyOptionalFields_shouldMapToEmptyOptionals() {
        TaskGroupBean bean = new TaskGroupBean("Test Group");
        TaskGroupDTO dto = TaskGroupMapper.INSTANCE.toDTO(bean, context);
        assertThat(dto).isNotNull();
        assertThat(dto.description().isPresent()).as("Description should be empty").isEqualTo(false);
    }
}

package de.ruu.app.jeeeraaah.common.api.mapping.bean_flat;
import de.ruu.app.jeeeraaah.common.api.bean.TaskGroupBean;
import de.ruu.app.jeeeraaah.common.api.domain.flat.TaskGroupFlat;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
/**
 * Tests for {@link TaskGroupMapper} - bidirectional TaskGroup Bean ↔ Flat mappings.
 */
class TaskGroupMapperTest {
    @Test
    void mapperInstanceExists() {
        assertThat(TaskGroupMapper.INSTANCE).as("Mapper instance should exist").isNotNull();
    }
    @Test
    void beanToFlat_shouldMapBasicFields() {
        TaskGroupBean bean = new TaskGroupBean("Test Group");
        TaskGroupFlat flat = TaskGroupMapper.INSTANCE.toFlat(bean);
        assertThat(flat).isNotNull();
        assertThat(flat.name()).isEqualTo(bean.name());
    }
    @Test
    void beanToFlat_shouldMapDescriptionField() {
        TaskGroupBean bean = new TaskGroupBean("Test Group");
        bean.description("Test Description");
        TaskGroupFlat flat = TaskGroupMapper.INSTANCE.toFlat(bean);
        assertThat(flat).isNotNull();
        assertThat(flat.description()).isEqualTo(bean.description());
        assertThat(flat.description().isPresent()).isEqualTo(true);
        assertThat(flat.description().get()).isEqualTo("Test Description");
    }
    @Test
    void flatToBean_shouldMapBasicFields() {
        TaskGroupBean sourceBean = new TaskGroupBean("Test Group");
        TaskGroupFlat flat = TaskGroupMapper.INSTANCE.toFlat(sourceBean);
        TaskGroupBean bean = TaskGroupMapper.INSTANCE.toBean(flat);
        assertThat(bean).isNotNull();
        assertThat(bean.name()).isEqualTo(flat.name());
    }
    @Test
    void flatToBean_shouldMapDescriptionField() {
        TaskGroupBean sourceBean = new TaskGroupBean("Test Group");
        sourceBean.description("Test Description");
        TaskGroupFlat flat = TaskGroupMapper.INSTANCE.toFlat(sourceBean);
        TaskGroupBean bean = TaskGroupMapper.INSTANCE.toBean(flat);
        assertThat(bean).isNotNull();
        assertThat(bean.description()).isEqualTo(flat.description());
        assertThat(bean.description().isPresent()).isEqualTo(true);
        assertThat(bean.description().get()).isEqualTo("Test Description");
    }
    @Test
    void bidirectionalMapping_shouldPreserveData() {
        TaskGroupBean originalBean = new TaskGroupBean("Original Group");
        originalBean.description("Original Description");
        TaskGroupFlat flat = TaskGroupMapper.INSTANCE.toFlat(originalBean);
        TaskGroupBean resultBean = TaskGroupMapper.INSTANCE.toBean(flat);
        assertThat(resultBean).isNotNull();
        assertThat(resultBean.name()).isEqualTo(originalBean.name());
        assertThat(resultBean.description()).isEqualTo(originalBean.description());
    }
    @Test
    void emptyOptionalFields_shouldMapToEmptyOptionals() {
        TaskGroupBean sourceBean = new TaskGroupBean("Test Group");
        TaskGroupFlat flat = TaskGroupMapper.INSTANCE.toFlat(sourceBean);
        TaskGroupBean bean = TaskGroupMapper.INSTANCE.toBean(flat);
        assertThat(bean).isNotNull();
        assertThat(bean.description().isPresent()).as("Description should be empty").isEqualTo(false);
    }
}

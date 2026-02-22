package de.ruu.app.jeeeraaah.common.api.mapping.bean_lazy;
import de.ruu.app.jeeeraaah.common.api.bean.TaskGroupBean;
import de.ruu.app.jeeeraaah.common.api.domain.lazy.TaskGroupLazy;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
/**
 * Tests for {@link TaskGroupMapper} - bidirectional TaskGroup Bean ↔ Lazy mappings.
 * <p>
 * Note: Lazy mappings require persisted entities with non-null IDs.
 */
class TaskGroupMapperTest {
    private TaskGroupBean createTaskGroupBean(String name, String description) throws Exception {
        TaskGroupBean bean = new TaskGroupBean(name);
        if (description != null) {
            bean.description(description);
        }
        // Use reflection to set ID and version since they have no public setters
        Field idField = TaskGroupBean.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(bean, 1L);
        Field versionField = TaskGroupBean.class.getDeclaredField("version");
        versionField.setAccessible(true);
        versionField.set(bean, (short) 1);
        return bean;
    }
    @Test
    void mapperInstanceExists() {
        assertThat(TaskGroupMapper.INSTANCE).as("Mapper instance should exist").isNotNull();
    }
    @Test
    void beanToLazy_shouldMapBasicFields() throws Exception {
        TaskGroupBean bean = createTaskGroupBean("Test Group", null);
        TaskGroupLazy lazy = TaskGroupMapper.INSTANCE.toLazy(bean);
        assertThat(lazy).isNotNull();
        assertThat(lazy.name()).isEqualTo(bean.name());
    }
    @Test
    void beanToLazy_shouldMapDescriptionField() throws Exception {
        TaskGroupBean bean = createTaskGroupBean("Test Group", "Test Description");
        TaskGroupLazy lazy = TaskGroupMapper.INSTANCE.toLazy(bean);
        assertThat(lazy).isNotNull();
        assertThat(lazy.description()).isEqualTo(bean.description());
        assertThat(lazy.description().isPresent()).isEqualTo(true);
        assertThat(lazy.description().get()).isEqualTo("Test Description");
    }
    @Test
    void lazyToBean_shouldMapBasicFields() throws Exception {
        TaskGroupBean sourceBean = createTaskGroupBean("Test Group", null);
        TaskGroupLazy lazy = TaskGroupMapper.INSTANCE.toLazy(sourceBean);
        TaskGroupBean bean = TaskGroupMapper.INSTANCE.toBean(lazy);
        assertThat(bean).isNotNull();
        assertThat(bean.name()).isEqualTo(lazy.name());
    }
    @Test
    void lazyToBean_shouldMapDescriptionField() throws Exception {
        TaskGroupBean sourceBean = createTaskGroupBean("Test Group", "Test Description");
        TaskGroupLazy lazy = TaskGroupMapper.INSTANCE.toLazy(sourceBean);
        TaskGroupBean bean = TaskGroupMapper.INSTANCE.toBean(lazy);
        assertThat(bean).isNotNull();
        assertThat(bean.description()).isEqualTo(lazy.description());
        assertThat(bean.description().isPresent()).isEqualTo(true);
        assertThat(bean.description().get()).isEqualTo("Test Description");
    }
    @Test
    void bidirectionalMapping_shouldPreserveData() throws Exception {
        TaskGroupBean originalBean = createTaskGroupBean("Original Group", "Original Description");
        TaskGroupLazy lazy = TaskGroupMapper.INSTANCE.toLazy(originalBean);
        TaskGroupBean resultBean = TaskGroupMapper.INSTANCE.toBean(lazy);
        assertThat(resultBean).isNotNull();
        assertThat(resultBean.name()).isEqualTo(originalBean.name());
        assertThat(resultBean.description()).isEqualTo(originalBean.description());
    }
    @Test
    void emptyOptionalFields_shouldMapToEmptyOptionals() throws Exception {
        TaskGroupBean sourceBean = createTaskGroupBean("Test Group", null);
        TaskGroupLazy lazy = TaskGroupMapper.INSTANCE.toLazy(sourceBean);
        TaskGroupBean bean = TaskGroupMapper.INSTANCE.toBean(lazy);
        assertThat(bean).isNotNull();
        assertThat(bean.description().isPresent()).as("Description should be empty").isEqualTo(false);
    }
}

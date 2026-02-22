package de.ruu.app.jeeeraaah.common.api.mapping.bean_lazy;
import de.ruu.app.jeeeraaah.common.api.bean.TaskBean;
import de.ruu.app.jeeeraaah.common.api.bean.TaskGroupBean;
import de.ruu.app.jeeeraaah.common.api.domain.lazy.TaskLazy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
/**
 * Tests for {@link TaskMapper} - bidirectional Task Bean ↔ Lazy mappings.
 * <p>
 * Note: Lazy mappings require persisted entities with non-null IDs.
 */
class TaskMapperTest {
    private TaskGroupBean testGroup;
    @BeforeEach
    void setUp() throws Exception {
        testGroup = new TaskGroupBean("Test Group");
        // Use reflection to set ID and version
        Field idField = TaskGroupBean.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(testGroup, 1L);
        Field versionField = TaskGroupBean.class.getDeclaredField("version");
        versionField.setAccessible(true);
        versionField.set(testGroup, (short) 1);
    }
    private TaskBean createTaskBean(String name, LocalDate start, LocalDate end, String description) throws Exception {
        TaskBean bean = new TaskBean(testGroup, name);
        if (description != null) bean.description(description);
        if (start != null) bean.start(start);
        if (end != null) bean.end(end);
        // Use reflection to set ID and version
        Field idField = TaskBean.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(bean, 1L);
        Field versionField = TaskBean.class.getDeclaredField("version");
        versionField.setAccessible(true);
        versionField.set(bean, (short) 1);
        return bean;
    }
    @Test
    void mapperInstanceExists() {
        assertThat(TaskMapper.INSTANCE).as("Mapper instance should exist").isNotNull();
    }
    @Test
    void beanToLazy_shouldMapBasicFields() throws Exception {
        TaskBean bean = createTaskBean("Test Task", null, null, null);
        TaskLazy lazy = TaskMapper.INSTANCE.toLazy(bean);
        assertThat(lazy).isNotNull();
        assertThat(lazy.name()).isEqualTo(bean.name());
    }
    @Test
    void beanToLazy_shouldMapOptionalFields() throws Exception {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(7);
        TaskBean bean = createTaskBean("Test Task", start, end, "Test Description");
        TaskLazy lazy = TaskMapper.INSTANCE.toLazy(bean);
        assertThat(lazy).isNotNull();
        assertThat(lazy.description()).isEqualTo(bean.description());
        assertThat(lazy.start()).isEqualTo(bean.start());
        assertThat(lazy.end()).isEqualTo(bean.end());
    }
    @Test
    void lazyToBean_shouldMapBasicFields() throws Exception {
        TaskBean sourceBean = createTaskBean("Test Task", null, null, null);
        TaskLazy lazy = TaskMapper.INSTANCE.toLazy(sourceBean);
        TaskBean bean = TaskMapper.INSTANCE.toBean(testGroup, lazy);
        assertThat(bean).isNotNull();
        assertThat(bean.name()).isEqualTo(lazy.name());
    }
    @Test
    void lazyToBean_shouldMapOptionalFields() throws Exception {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(7);
        TaskBean sourceBean = createTaskBean("Test Task", start, end, "Test Description");
        TaskLazy lazy = TaskMapper.INSTANCE.toLazy(sourceBean);
        TaskBean bean = TaskMapper.INSTANCE.toBean(testGroup, lazy);
        assertThat(bean).isNotNull();
        assertThat(bean.description()).isEqualTo(lazy.description());
        assertThat(bean.start()).isEqualTo(lazy.start());
        assertThat(bean.end()).isEqualTo(lazy.end());
    }
    @Test
    void bidirectionalMapping_shouldPreserveData() throws Exception {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(7);
        TaskBean originalBean = createTaskBean("Original Task", start, end, "Original Description");
        TaskLazy lazy = TaskMapper.INSTANCE.toLazy(originalBean);
        TaskBean resultBean = TaskMapper.INSTANCE.toBean(testGroup, lazy);
        assertThat(resultBean).isNotNull();
        assertThat(resultBean.name()).isEqualTo(originalBean.name());
        assertThat(resultBean.description()).isEqualTo(originalBean.description());
    }
    @Test
    void emptyOptionalFields_shouldMapCorrectly() throws Exception {
        TaskBean sourceBean = createTaskBean("Test Task", null, null, null);
        TaskLazy lazy = TaskMapper.INSTANCE.toLazy(sourceBean);
        TaskBean bean = TaskMapper.INSTANCE.toBean(testGroup, lazy);
        assertThat(bean).isNotNull();
        assertThat(bean.description().isPresent()).as("Description should be empty").isEqualTo(false);
    }
}

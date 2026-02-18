/**
 * <h2>Mapping: Bean ↔ Lazy (TaskBean/TaskGroupBean to/from TaskLazy/TaskGroupLazy)</h2>
 * 
 * <p><b>Purpose:</b> Converts between full domain beans and lazy-loading optimized representations.</p>
 * 
 * <h3>Use Cases:</h3>
 * <ul>
 *   <li>Performance optimization - avoiding loading full object graphs</li>
 *   <li>Lazy entity representations use IDs instead of full related entities</li>
 *   <li>Used in scenarios where related entities are loaded separately</li>
 * </ul>
 * 
 * <h3>Dependencies:</h3>
 * <ul>
 *   <li>{@code de.ruu.app.jeeeraaah.common.api.bean} - Bean domain objects</li>
 *   <li>{@code de.ruu.app.jeeeraaah.common.api.ws.rs} - Lazy DTO objects</li>
 *   <li>{@code org.mapstruct} - Mapper framework</li>
 * </ul>
 * 
 * <h3>Exports:</h3>
 * <ul>
 *   <li>{@code de.ruu.app.jeeeraaah.common.api.mapping.bean.lazy} - Bean → Lazy mappers</li>
 *   <li>{@code de.ruu.app.jeeeraaah.common.api.mapping.lazy.bean} - Lazy → Bean mappers</li>
 * </ul>
 * 
 * @since 0.0.1
 */
module de.ruu.app.jeeeraaah.common.api.mapping.bean.lazy
{
	// Exports will be added when mappers are moved to this module
	// exports de.ruu.app.jeeeraaah.common.api.mapping.bean.lazy;
	// exports de.ruu.app.jeeeraaah.common.api.mapping.lazy.bean;

	requires transitive de.ruu.app.jeeeraaah.common.api.bean;
	requires transitive de.ruu.app.jeeeraaah.common.api.ws.rs;
	requires transitive de.ruu.app.jeeeraaah.common.api.domain;
	requires transitive org.mapstruct;
	requires transitive de.ruu.lib.mapstruct;
	requires jakarta.annotation;

	requires static lombok;
	requires static java.compiler; // Needed for MapStruct generated code
}

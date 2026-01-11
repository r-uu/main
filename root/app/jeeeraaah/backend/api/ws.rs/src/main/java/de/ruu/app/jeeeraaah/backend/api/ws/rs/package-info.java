/**
 * NON-JPMS MODULE REASONING
 * =========================
 * 
 * This WAR module (backend.api.ws.rs) cannot use JPMS (Java Platform Module System) due to:
 * 
 * 1. **OpenLiberty WAR Deployment Architecture**
 *    - WAR files are deployed on the classpath, not the module path
 *    - Servlet container manages dependencies through shared libraries
 *    - Module boundaries are incompatible with WAR deployment mechanism
 * 
 * 2. **Jakarta EE Provided Dependencies**
 *    - Jakarta EE APIs (jakarta.ws.rs, jakarta.cdi, etc.) have scope "provided"
 *    - These APIs are not available as modules during compilation
 *    - OpenLiberty provides them at runtime via shared libraries
 * 
 * 3. **Annotation Processing Constraints**
 *    - Lombok and MapStruct require classpath-based processing
 *    - JPMS module boundaries interfere with annotation processor discovery
 *    - Liberty dev mode hot-reload expects classpath structure
 * 
 * 4. **CDI Bean Discovery**
 *    - CDI scans for beans on the classpath using beans.xml
 *    - Module-path beans require different discovery mechanisms
 *    - OpenLiberty CDI implementation expects traditional WAR structure
 * 
 * WORKAROUND FOR MAPPER INJECTION:
 * =================================
 * 
 * Since this module cannot use JPMS to import MappingService from backend.persistence.jpa:
 * - Use MappingServiceCDI concrete class directly from backend.common.mapping
 * - CDI will inject the implementation at runtime
 * - This maintains CDI benefits while avoiding JPMS constraints
 * 
 * ALTERNATIVE ARCHITECTURES CONSIDERED:
 * ======================================
 * 
 * 1. **EAR Deployment with EJB Module**
 *    - Pros: Better module separation, can use JPMS in EJB modules
 *    - Cons: More complex deployment, requires EAR packaging
 * 
 * 2. **MicroProfile / Quarkus / Helidon**
 *    - Pros: Modern microservices architecture, better JPMS support
 *    - Cons: Complete platform migration required
 * 
 * 3. **OSGi**
 *    - Pros: Dynamic module system, well-established
 *    - Cons: Different from JPMS, additional complexity
 * 
 * CONCLUSION:
 * ===========
 * For traditional Jakarta EE WAR deployment in OpenLiberty, classpath-based architecture
 * is the appropriate choice. JPMS is used in library modules (backend.persistence.jpa,
 * backend.common.mapping, etc.) where it provides clear benefits for modularity and
 * encapsulation.
 * 
 * @author r-uu
 * @since 2025-11-27
 */
package de.ruu.app.jeeeraaah.backend.api.ws.rs;

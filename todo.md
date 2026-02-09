# Project TODO List

**Last Updated:** 2026-02-09  
**Status:** After major cleanup completion

---

## ✅ Recently Completed (2026-02-09)

- ✅ Translate German comments to English
- ✅ Archive obsolete documentation (29+ files)
- ✅ Fix DashController.java compilation error
- ✅ Resolve JPMS module conflicts (weld-se-core)
- ✅ Consolidate gantt2 package into gantt
- ✅ Delete obsolete scripts (remove-old-gantt-package.sh)
- ✅ Update INTELLIJ-CACHE-CLEANUP.md

---

## 🔥 Priority 1 - Critical (Do Next)

### Build & Compilation
- [x] Update JavaFX runtime to version 25.x (currently 24.0.2, FXML expects 25) - **✅ DONE: Updated to v25**
- [x] Fix DataItemFactory CDI warning (add explicit constructor) - **✅ NOT NEEDED: Already removed**
- [x] Implement proper TaskBean to TaskTreeTableDataItem mapping - **✅ NOT NEEDED: Already solved via TaskFlat**
- [x] Run full Maven build and verify all modules compile - **✅ BUILD SUCCESS**

### JPMS & Dependencies
- [x] Test weld-se-core exclusions work correctly - **✅ Working**
- [x] Verify no remaining "reads package from both" errors - **✅ No errors**
- [x] Review and update module-info.java files if needed - **✅ Not needed**

---

## 📋 Priority 2 - Important (This Week)

### Code Quality
- [x] Consolidate multi-line log statements using text blocks (`"""`) - **✅ HealthCheckRunner updated**
- [x] Remove recursion guard in TaskTreeTableController if not needed - **✅ KEPT: Necessary for circular refs**
- [ ] Fix remaining compiler warnings in DashController.java
- [ ] Review and fix unused parameter warnings

### Documentation
- [ ] Consolidate startup guides (QUICKSTART.md + GETTING-STARTED.md + STARTUP-QUICK-GUIDE.md)
- [ ] Merge credentials documentation (3 files → 1)
- [x] Update DOCUMENTATION-INDEX.md with current structure - **✅ Updated**
- [ ] Translate remaining German documentation (readme.de.md files)

### Infrastructure
- [ ] Check why there's no Dockerfile for JasperReports
- [ ] Verify JasperReports is activated in docker-compose.yml
- [ ] Test Docker environment health checks

---

## 🚀 Priority 3 - Recommended (This Month)

### Testing
- [ ] Add unit tests for task hierarchy edge cases
- [ ] Implement integration tests for REST API
- [ ] Add JaCoCo for code coverage measurement
- [ ] Target: 70%+ test coverage

### CI/CD
- [ ] Set up GitHub Actions or GitLab CI pipeline
- [ ] Add automated build on push/pull request
- [ ] Add automated tests in CI
- [ ] Add dependency vulnerability scanning (OWASP, Snyk)

### Architecture
- [ ] Implement ArchUnit tests for layer boundaries
- [ ] Enforce lib/* must not depend on app/*
- [ ] Add architecture documentation
- [ ] Review and document module dependencies

### Code Quality Tools
- [ ] Add SpotBugs plugin to Maven
- [ ] Add PMD plugin to Maven
- [ ] Add Checkstyle configuration
- [ ] Configure SonarQube (optional)

---

## 💡 Priority 4 - Nice to Have (Next Quarter)

### Monitoring & Observability
- [ ] Add Micrometer for application metrics
- [ ] Set up Prometheus endpoint
- [ ] Add structured logging (JSON format)
- [ ] Implement distributed tracing (OpenTelemetry)

### Security
- [ ] Add security headers to REST API
- [ ] Implement comprehensive input validation
- [ ] Review credential management (move to env vars/secrets)
- [ ] Add OWASP dependency check to build

### Performance
- [ ] Profile application startup time
- [ ] Optimize Docker image sizes
- [ ] Add caching where appropriate
- [ ] Review database query performance

### Documentation
- [ ] Generate JavaDoc for all modules
- [ ] Create architecture decision records (ADRs)
- [ ] Add API documentation (OpenAPI/Swagger)
- [ ] Create developer onboarding guide

---

## 📝 Ongoing Tasks

### Maintenance
- [ ] Keep dependencies up to date
- [ ] Review and archive obsolete documentation regularly
- [ ] Update PROJECT-STATUS.md periodically
- [ ] Respond to security advisories

### Best Practices
- [ ] Use text blocks for multi-line strings
- [ ] Prefer method references over lambdas
- [ ] Keep methods focused and small
- [ ] Document public APIs

---

## 🎯 Long-term Goals

- Achieve 80%+ test coverage
- Fully automated CI/CD pipeline
- Comprehensive monitoring and alerting
- Zero critical security vulnerabilities
- Clean, maintainable codebase
- Complete English documentation
- Active community contributions

---

## 📚 Reference Documents

- **PROJECT-IMPROVEMENTS.md** - Detailed improvement recommendations
- **FINAL-SUMMARY.md** - Cleanup completion summary
- **BEREINIGUNG-ABSCHLUSS.md** - German cleanup summary
- **PROJECT-CLEANUP-2026-02-09.md** - Detailed cleanup log

---

**Next Review:** After completing Priority 1 tasks

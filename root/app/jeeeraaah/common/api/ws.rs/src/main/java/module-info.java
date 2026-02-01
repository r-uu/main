module de.ruu.app.jeeeraaah.common.api.ws.rs
{
    // Export the package to any module that needs it
    exports de.ruu.app.jeeeraaah.common.api.ws.rs;

    requires transitive de.ruu.app.jeeeraaah.common.api.domain;
    requires de.ruu.lib.util;
    requires com.fasterxml.jackson.annotation;
    requires jakarta.annotation;
    requires static lombok;

    // Open the package for reflection (needed by Jackson, Lombok, etc.)
    opens de.ruu.app.jeeeraaah.common.api.ws.rs;
}
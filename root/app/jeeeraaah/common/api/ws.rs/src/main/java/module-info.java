module de.ruu.app.jeeeraaah.common.api.ws.rs
{
    // Export the package to any module that needs it
    exports de.ruu.app.jeeeraaah.common.api.ws.rs;

    requires transitive de.ruu.app.jeeeraaah.common.api.domain;
    requires com.fasterxml.jackson.annotation;
    requires jakarta.annotation;
    requires de.ruu.lib.util;
    requires de.ruu.lib.jpa.core;
    requires jakarta.xml.bind;
    requires static com.fasterxml.jackson.databind;

    // Open the package for reflection
    opens de.ruu.app.jeeeraaah.common.api.ws.rs to com.fasterxml.jackson.databind;
}
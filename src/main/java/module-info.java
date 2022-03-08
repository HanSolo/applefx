module eu.hansolo.applefx {
    // Java
    requires java.base;
    requires java.net.http;
    requires java.desktop;

    // Java-FX
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires transitive javafx.swing;

    // 3rd party
    requires transitive eu.hansolo.jdktools;
    requires transitive eu.hansolo.toolbox;
    requires transitive eu.hansolo.toolboxfx;

    opens eu.hansolo.applefx to eu.hansolo.jdktools, eu.hansolo.toolbox, eu.hansolo.toolboxfx;
    opens eu.hansolo.applefx.tools to eu.hansolo.jdktools, eu.hansolo.toolbox, eu.hansolo.toolboxfx;
    opens eu.hansolo.applefx.event to eu.hansolo.jdktools, eu.hansolo.toolbox, eu.hansolo.toolboxfx;
    opens eu.hansolo.applefx.fonts to eu.hansolo.jdktools, eu.hansolo.toolbox, eu.hansolo.toolboxfx;

    exports eu.hansolo.applefx;
    exports eu.hansolo.applefx.tools;
    exports eu.hansolo.applefx.event;
    exports eu.hansolo.applefx.fonts;
}
/*
 * Copyright (c) 2018 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.applefx.tools;

import eu.hansolo.jdktools.OperatingSystem;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eu.hansolo.toolbox.Helper.getOperatingSystem;


public class Helper {

    private Helper() {}

    public static final Map<Integer, Color[]> MACOS_ACCENT_COLOR_MAP = Map.of(-1, new Color[] { MacosSystemColor.GRAPHITE.aqua, MacosSystemColor.GRAPHITE.dark },
                                                                              0, new Color[]  { MacosSystemColor.RED.aqua, MacosSystemColor.RED.dark },
                                                                              1, new Color[]  { MacosSystemColor.ORANGE.aqua, MacosSystemColor.ORANGE.dark },
                                                                              2, new Color[]  { MacosSystemColor.YELLOW.aqua, MacosSystemColor.YELLOW.dark },
                                                                              3, new Color[]  { MacosSystemColor.GREEN.aqua, MacosSystemColor.GREEN.dark },
                                                                              4, new Color[]  { MacosSystemColor.BLUE.aqua, MacosSystemColor.BLUE.dark },
                                                                              5, new Color[]  { MacosSystemColor.PURPLE.aqua, MacosSystemColor.PURPLE.dark },
                                                                              6, new Color[]  { MacosSystemColor.PINK.aqua, MacosSystemColor.PINK.dark });

    private static final String   REGQUERY_UTIL      = "reg query ";
    private static final String   REGDWORD_TOKEN     = "REG_DWORD";
    private static final String   DARK_THEME_CMD     = REGQUERY_UTIL + "\"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize\"" + " /v AppsUseLightTheme";


    public static final void enableNode(final Node node, final boolean enable) {
        node.setVisible(enable);
        node.setManaged(enable);
    }

    public static final <T, U> HashMap copy(final HashMap<T, U> original) {
        HashMap<T, U> copy = new HashMap<>();
        original.entrySet().forEach(entry -> copy.put(entry.getKey(), entry.getValue()));
        return copy;
    }

    public static final double clamp(final double min, final double max, final double value) {
        if (value < min) { return min; }
        if (value > max) { return max; }
        return value;
    }

    public static final boolean isDarkMode() {
        switch(getOperatingSystem()) {
            case WINDOWS: return isWindowsDarkMode();
            case MACOS  : return isMacOsDarkMode();
            default     : return false;
        }
    }

    private static final boolean isMacOsDarkMode() {
        try {
            final Runtime           runtime = Runtime.getRuntime();
            final Process           process = runtime.exec("defaults read -g AppleInterfaceStyle");
            final InputStreamReader isr     = new InputStreamReader(process.getInputStream());
            final BufferedReader    rdr     = new BufferedReader(isr);
            boolean isDarkMode = false;
            String  line;
            while((line = rdr.readLine()) != null) {
                if (line.equals("Dark")) { isDarkMode = true; }
            }
            int rc = process.waitFor();  // Wait for the process to complete
            return 0 == rc && isDarkMode;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    private static final boolean isWindowsDarkMode() {
        try {
            final Process      process = Runtime.getRuntime().exec(DARK_THEME_CMD);
            final StreamReader reader  = new StreamReader(process.getInputStream());

            reader.start();
            process.waitFor();
            reader.join();

            final String result = reader.getResult();
            final int    p      = result.indexOf(REGDWORD_TOKEN);

            if (p == -1) { return false; }

            // 1 == Light Mode, 0 == Dark Mode
            final String temp = result.substring(p + REGDWORD_TOKEN.length()).trim();
            return ((Integer.parseInt(temp.substring("0x".length()), 16))) == 0;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static final MacosAccentColor getMacosAccentColor() {
        if (OperatingSystem.MACOS != getOperatingSystem()) { return MacosAccentColor.MULTI_COLOR; }
        final boolean isDarkMode = isMacOsDarkMode();
        try {
            Integer           colorKey    = null;
            Runtime           runtime    = Runtime.getRuntime();
            Process           process    = runtime.exec("defaults read -g AppleAccentColor");
            InputStreamReader isr        = new InputStreamReader(process.getInputStream());
            BufferedReader    rdr        = new BufferedReader(isr);
            String            line;
            while((line = rdr.readLine()) != null) {
                colorKey = Integer.valueOf(line);
            }
            int rc = process.waitFor();  // Wait for the process to complete
            if (0 == rc) {
                Integer key = colorKey;
                return MacosAccentColor.getAsList().stream().filter(macOSAccentColor -> macOSAccentColor.key == key).findFirst().orElse(MacosAccentColor.MULTI_COLOR);
            } else {
                return MacosAccentColor.MULTI_COLOR;
            }
        } catch (IOException | InterruptedException e) {
            return MacosAccentColor.MULTI_COLOR;
        }
    }
    public static final Color getMacosAccentColorAsColor() {
        if (OperatingSystem.MACOS != getOperatingSystem()) { return MacosAccentColor.MULTI_COLOR.getColorAqua(); }
        final boolean isDarkMode = isMacOsDarkMode();
        try {
            Integer           colorKey    = null;
            Runtime           runtime    = Runtime.getRuntime();
            Process           process    = runtime.exec("defaults read -g AppleAccentColor");
            InputStreamReader isr        = new InputStreamReader(process.getInputStream());
            BufferedReader    rdr        = new BufferedReader(isr);
            String            line;
            while((line = rdr.readLine()) != null) {
                colorKey = Integer.valueOf(line);
            }
            int rc = process.waitFor();  // Wait for the process to complete
            if (0 == rc) {
                return isDarkMode ? MACOS_ACCENT_COLOR_MAP.get(colorKey)[1] : MACOS_ACCENT_COLOR_MAP.get(colorKey)[0];
            } else {
                return isDarkMode ? MACOS_ACCENT_COLOR_MAP.get(4)[1] : MACOS_ACCENT_COLOR_MAP.get(4)[0];
            }
        } catch (IOException | InterruptedException e) {
            return isDarkMode ? MACOS_ACCENT_COLOR_MAP.get(4)[1] : MACOS_ACCENT_COLOR_MAP.get(4)[0];
        }
    }

    public static final List<Node> getAllNodes(Parent root) {
        List<Node> nodes = new ArrayList<Node>();
        addAllDescendents(root, nodes);
        return nodes;
    }
    private static final void addAllDescendents(Parent parent, List<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent) { addAllDescendents((Parent)node, nodes); }
        }
    }


    // ******************** Internal Classes **********************************
    static class StreamReader extends Thread {
        private final InputStream  is;
        private final StringWriter sw;

        StreamReader(final InputStream is) {
            this.is = is;
            sw = new StringWriter();
        }

        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1) { sw.write(c); }
            } catch (IOException e) { }
        }

        String getResult() { return sw.toString(); }
    }
}

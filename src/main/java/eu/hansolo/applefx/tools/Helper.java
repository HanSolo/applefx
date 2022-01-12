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

import eu.hansolo.toolbox.OperatingSystem;
import javafx.scene.Node;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static eu.hansolo.toolbox.Helper.getOperatingSystem;


public class Helper {
    public static final Map<Integer, Color[]> MACOS_ACCENT_COLOR_MAP = Map.of(-1, new Color[] { MacOSSystemColor.GRAY.colorAqua, MacOSSystemColor.GRAY.colorDark },
                                                                              0, new Color[] { MacOSSystemColor.RED.colorAqua, MacOSSystemColor.RED.colorDark },
                                                                              1, new Color[] { MacOSSystemColor.ORANGE.colorAqua, MacOSSystemColor.ORANGE.colorDark },
                                                                              2, new Color[] { MacOSSystemColor.YELLOW.colorAqua, MacOSSystemColor.YELLOW.colorDark },
                                                                              3, new Color[] { MacOSSystemColor.GREEN.colorAqua, MacOSSystemColor.GREEN.colorDark },
                                                                              4, new Color[] { MacOSSystemColor.BLUE.colorAqua, MacOSSystemColor.BLUE.colorDark },
                                                                              5, new Color[] { MacOSSystemColor.PURPLE.colorAqua, MacOSSystemColor.PURPLE.colorDark },
                                                                              6, new Color[] { MacOSSystemColor.PINK.colorAqua, MacOSSystemColor.PINK.colorDark });

    private static final String   REGQUERY_UTIL      = "reg query ";
    private static final String   REGDWORD_TOKEN     = "REG_DWORD";
    private static final String   DARK_THEME_CMD     = REGQUERY_UTIL + "\"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize\"" + " /v AppsUseLightTheme";



    public static final int ANIMATION_DURATION = 75;

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
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static final boolean isDarkMode() {
        switch(getOperatingSystem()) {
            case WINDOWS: return isWindowsDarkMode();
            case MACOS  : return isMacOsDarkMode();
            case LINUX  :
            case SOLARIS:
            default     : return false;
        }
    }

    public static final boolean isMacOsDarkMode() {
        try {
            boolean           isDarkMode = false;
            Runtime           runtime = Runtime.getRuntime();
            Process           process = runtime.exec("defaults read -g AppleInterfaceStyle");
            InputStreamReader isr     = new InputStreamReader(process.getInputStream());
            BufferedReader    rdr     = new BufferedReader(isr);
            String            line;
            while((line = rdr.readLine()) != null) {
                if (line.equals("Dark")) { isDarkMode = true; }
            }
            int rc = process.waitFor();  // Wait for the process to complete
            return 0 == rc && isDarkMode;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public static boolean isWindowsDarkMode() {
        try {
            Process      process = Runtime.getRuntime().exec(DARK_THEME_CMD);
            StreamReader reader  = new StreamReader(process.getInputStream());

            reader.start();
            process.waitFor();
            reader.join();

            String result = reader.getResult();
            int p = result.indexOf(REGDWORD_TOKEN);

            if (p == -1) { return false; }

            // 1 == Light Mode, 0 == Dark Mode
            String temp = result.substring(p + REGDWORD_TOKEN.length()).trim();
            return ((Integer.parseInt(temp.substring("0x".length()), 16))) == 0;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static MacOSAccentColor getMacOSAccentColor() {
        if (OperatingSystem.MACOS != getOperatingSystem()) { return MacOSAccentColor.MULTI_COLOR; }
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
                return MacOSAccentColor.getAsList().stream().filter(macOSAccentColor -> macOSAccentColor.key == key).findFirst().orElse(MacOSAccentColor.MULTI_COLOR);
            } else {
                return MacOSAccentColor.MULTI_COLOR;
            }
        } catch (IOException | InterruptedException e) {
            return MacOSAccentColor.MULTI_COLOR;
        }
    }
    public static Color getMacOSAccentColorAsColor() {
        if (OperatingSystem.MACOS != getOperatingSystem()) { return MacOSAccentColor.MULTI_COLOR.getColorAqua(); }
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


    // ******************** Internal Classes **********************************
    static class StreamReader extends Thread {
        private InputStream  is;
        private StringWriter sw;

        StreamReader(InputStream is) {
            this.is = is;
            sw = new StringWriter();
        }

        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1)
                    sw.write(c);
            } catch (IOException e) { ; }
        }

        String getResult() { return sw.toString(); }
    }
}

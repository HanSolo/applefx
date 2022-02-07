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

package eu.hansolo.applefx.fonts;

import javafx.scene.text.Font;


public class Fonts {
    private static final String SF_PRO_THIN_NAME;
    private static final String SF_PRO_LIGHT_NAME;
    private static final String SF_PRO_REGULAR_NAME;
    private static final String SF_PRO_MEDIUM_NAME;
    private static final String SF_PRO_BOLD_NAME;
    private static final String SF_ICON_SETS;

    private static String sfProThinName;
    private static String sfProLightName;
    private static String sfProRegularName;
    private static String sfProMediumName;
    private static String sfProBoldName;
    private static String sfIconSetsName;


    static {
        try {
            sfProThinName    = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/applefx/fonts/SF-Pro-Display-Thin.ttf"), 10).getName();
            sfProLightName   = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/applefx/fonts/SF-Pro-Display-Light.ttf"), 10).getName();
            sfProRegularName = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/applefx/fonts/SF-Pro-Display-Regular.ttf"), 10).getName();
            sfProMediumName  = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/applefx/fonts/SF-Pro-Display-Medium.ttf"), 10).getName();
            sfProBoldName    = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/applefx/fonts/SF-Pro-Display-Bold.ttf"), 10).getName();
            sfIconSetsName   = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/applefx/fonts/sficonsets.ttf"), 10).getName();
        } catch (Exception exception) { }
        SF_PRO_THIN_NAME    = sfProThinName;
        SF_PRO_LIGHT_NAME   = sfProLightName;
        SF_PRO_REGULAR_NAME = sfProRegularName;
        SF_PRO_MEDIUM_NAME  = sfProMediumName;
        SF_PRO_BOLD_NAME    = sfProBoldName;
        SF_ICON_SETS        = sfIconSetsName;
    }


    // ******************** Methods *******************************************
    public static Font sfProThin(final double size) { return new Font(SF_PRO_THIN_NAME, size); }
    public static Font sfProLight(final double size) { return new Font(SF_PRO_LIGHT_NAME, size); }
    public static Font sfProRegular(final double size) { return new Font(SF_PRO_REGULAR_NAME, size); }
    public static Font sfProMedium(final double size) { return new Font(SF_PRO_MEDIUM_NAME, size); }
    public static Font sfProBold(final double size) { return new Font(SF_PRO_BOLD_NAME, size); }

    public static Font sfIconSets(final double size) { return new Font(SF_ICON_SETS, size); }
}

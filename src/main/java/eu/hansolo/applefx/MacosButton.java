/*
 * Copyright (c) 2022 by Gerrit Grunwald
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

package eu.hansolo.applefx;

import eu.hansolo.applefx.tools.Helper;
import eu.hansolo.applefx.tools.MacosAccentColor;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Button;


public class MacosButton extends Button implements MacosControlWithAccentColor {
    private static final PseudoClass                      DARK_PSEUDO_CLASS    = PseudoClass.getPseudoClass("dark");
    private static final PseudoClass                      DEFAULT_PSEUDO_CLASS = PseudoClass.getPseudoClass("def");
    private              boolean                          _dark;
    private              BooleanProperty                  dark;
    private              boolean                          _def;
    private              BooleanProperty                  def;
    private              MacosAccentColor                 _accentColor;
    private              ObjectProperty<MacosAccentColor> accentColor;


    // ******************** Constructors **************************************
    public MacosButton() {
        super();
        init(false);
    }
    public MacosButton(final String text) {
        super(text);
        init(false);
    }
    public MacosButton(final String text, final boolean isDefault) {
        super(text);
        init(true);
    }
    public MacosButton(final String text, final Node graphic, final boolean isDefault) {
        super(text, graphic);
        init(isDefault);
    }


    // ******************** Initialization ************************************
    private void init(final boolean isDefault) {
        getStyleClass().add("macos-button");
        _dark        = false;
        _def         = isDefault;
        _accentColor = Helper.getMacosAccentColor();
        pseudoClassStateChanged(DEFAULT_PSEUDO_CLASS, isDefault);
    }


    // ******************** Methods *******************************************
    @Override public final boolean isDark() {
        return null == dark ? _dark : dark.get();
    }
    @Override public final void setDark(final boolean dark) {
        if (null == this.dark) {
            _dark = dark;
            pseudoClassStateChanged(DARK_PSEUDO_CLASS, dark);
        } else {
            this.dark.set(dark);
        }
    }
    @Override public final BooleanProperty darkProperty() {
        if (null == dark) {
            dark = new BooleanPropertyBase() {
                @Override protected void invalidated() {
                    pseudoClassStateChanged(DARK_PSEUDO_CLASS, get());
                }
                @Override public Object getBean() { return MacosButton.this; }
                @Override public String getName() { return "dark"; }
            };
        }
        return dark;
    }

    public final boolean isDefault() {
        return null == def ? _def : def.get();
    }
    public final void setDefault(final boolean def) {
        if (null == this.def) {
            _def = def;
            pseudoClassStateChanged(DEFAULT_PSEUDO_CLASS, def);
        } else {
            this.def.set(def);
        }
    }
    public final BooleanProperty defaultProperty() {
        if (null == def) {
            def = new BooleanPropertyBase() {
                @Override protected void invalidated() {
                    pseudoClassStateChanged(DEFAULT_PSEUDO_CLASS, get());
                }
                @Override public Object getBean() { return MacosButton.this; }
                @Override public String getName() { return "default"; }
            };
        }
        return def;
    }

    @Override public MacosAccentColor getAccentColor() { return null == accentColor ? _accentColor : accentColor.get(); }
    @Override public void setAccentColor(final MacosAccentColor accentColor) {
        if (null == this.accentColor) {
            _accentColor = accentColor;
            setStyle(isDark() ? new StringBuilder("-button-color: ").append(accentColor.getDarkStyleClass()).append(";").toString() : new StringBuilder("-button-color: ").append(accentColor.getDarkStyleClass()).append(";").toString());
        } else {
            this.accentColor.set(accentColor);
        }
    }
    @Override public ObjectProperty<MacosAccentColor> accentColorProperty() {
        if (null == accentColor) {
            accentColor = new ObjectPropertyBase<>(_accentColor) {
                @Override protected void invalidated() { setStyle(isDark() ? new StringBuilder("-accent-color-dark: ").append(get().getDarkStyleClass()).append(";").toString() : new StringBuilder("-accent-color: ").append(get().getDarkStyleClass()).append(";").toString()); }
                @Override public Object getBean() { return MacosButton.this; }
                @Override public String getName() { return "accentColor"; }
            };
            _accentColor = null;
        }
        return accentColor;
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() { return MacosButton.class.getResource("apple.css").toExternalForm(); }
}

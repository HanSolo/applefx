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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.control.Skin;
import javafx.scene.control.Slider;


public class IosSlider extends Slider {
    private static final PseudoClass     BALANCE_PSEUDO_CLASS = PseudoClass.getPseudoClass("balance");
    private static final PseudoClass     DARK_PSEUDO_CLASS    = PseudoClass.getPseudoClass("dark");
    private              BooleanProperty balance              = new BooleanPropertyBase(false) {
        @Override protected void invalidated() { pseudoClassStateChanged(BALANCE_PSEUDO_CLASS, get()); }
        @Override public Object getBean() { return IosSlider.this; }
        @Override public String getName() { return "balance"; }
    };
    private              boolean         _dark;
    private              BooleanProperty dark;


    public IosSlider() {
        super();
        init();
    }
    public IosSlider(final double min, final double max, final double value) {
        super(min, max, value);
        init();
    }


    private void init() {
        getStyleClass().add("ios-slider");
        _dark = false;
    }


    public boolean getBalance() { return balance.get(); }
    public void setBalance(final boolean balance) { this.balance.set(balance); }
    public BooleanProperty balanceProperty() { return balance; }

    public final boolean isDark() {
        return null == dark ? _dark : dark.get();
    }
    public final void setDark(final boolean dark) {
        if (null == this.dark) {
            _dark = dark;
            pseudoClassStateChanged(DARK_PSEUDO_CLASS, dark);
        } else {
            darkProperty().set(dark);
        }
    }
    public final BooleanProperty darkProperty() {
        if (null == dark) {
            dark = new BooleanPropertyBase() {
                @Override protected void invalidated() {
                    pseudoClassStateChanged(DARK_PSEUDO_CLASS, get());
                }
                @Override public Object getBean() { return IosSlider.this; }
                @Override public String getName() { return "dark"; }
            };
        }
        return dark;
    }

    public double getRange() { return (getMax() - getMin()); }

    public double getBalanceValue() { return getValue() - (getRange() * 0.5); }


    @Override protected Skin<?> createDefaultSkin() {
        return new IosSliderSkin(this);
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() {
        return IosSlider.class.getResource("apple.css").toExternalForm();
    }
}

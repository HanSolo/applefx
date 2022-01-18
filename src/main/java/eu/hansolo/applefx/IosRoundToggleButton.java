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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;


public class IosRoundToggleButton extends ToggleButton {
    private static final PseudoClass     INACTIVE_PSEUDO_CLASS = PseudoClass.getPseudoClass("inactive");
    private              BooleanProperty inactive;


    public IosRoundToggleButton() {
        super();
        inactive = new BooleanPropertyBase(false) {
            @Override protected void invalidated() { pseudoClassStateChanged(INACTIVE_PSEUDO_CLASS, true); }
            @Override public Object getBean() { return IosRoundToggleButton.this; }
            @Override public String getName() { return "inactive"; }
        };
        initGraphics();
        registerListeners();
    }
    public IosRoundToggleButton(final String text) {
        this();
    }


    private void initGraphics() {
        getStyleClass().add("ios-round-toggle-button");

        Node graphics = getGraphic();
        if (null != graphics) { graphics.getStyleClass().add("graphics"); }

        setMinSize(54, 54);
        setMaxSize(54, 54);
        setPrefSize(54, 54);
        setAlignment(Pos.CENTER);
    }

    private void registerListeners() {
        graphicProperty().addListener(o -> {
            Node graphics = getGraphic();
            if (null != graphics) {
                graphics.getStyleClass().setAll("graphics");
                //double graphicsWidth  = graphics.getLayoutBounds().getWidth();
                //double graphicsHeight = graphics.getLayoutBounds().getHeight();
                //graphics.resize();
            }
        });
    }

    public boolean isInactive() { return inactive.get(); }
    public void setInactive(final boolean inactive) { this.inactive.set(inactive); }
    public BooleanProperty inactiveProperty() { return inactive; }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() { return IosRoundToggleButton.class.getResource("apple.css").toExternalForm(); }
}

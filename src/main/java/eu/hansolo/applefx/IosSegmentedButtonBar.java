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

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.HBox;


public class IosSegmentedButtonBar extends HBox {

    public IosSegmentedButtonBar() {
        super();
        getStyleClass().add("ios-segmented-button-bar");

        registerListeners();
    }
    public IosSegmentedButtonBar(final double spacing) {
        this();
        setSpacing(0);
    }
    public IosSegmentedButtonBar(final Node... children) {
        super();
        getChildren().addAll(children);
        getStyleClass().addAll("apple", "ios-segmented-button-bar");
        adjustStyles();

        registerListeners();
    }
    public IosSegmentedButtonBar(final double SPACING, final Node... children) {
        this();
        setSpacing(0);
        getChildren().addAll(children);
    }


    private void registerListeners() {
        spacingProperty().addListener(o -> setSpacing(0));

        getChildren().addListener((ListChangeListener<Node>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    adjustStyles();
                } else if (change.wasRemoved()) {
                    adjustStyles();
                }
            }
        });
    }

    private void adjustStyles() {
        if (getChildren().isEmpty()) { return; }
        int noOfChildren = getChildren().size();
        for (int i = 0 ; i < noOfChildren ; i++) {
            Node node = getChildren().get(i);
            if (node.getStyleClass().contains("first")) { node.getStyleClass().remove("first"); }
            if (node.getStyleClass().contains("last")) { node.getStyleClass().remove("last"); }

            if (i == 0) { node.getStyleClass().add("first"); }
            if (i > 0 && i == noOfChildren - 1) { node.getStyleClass().add("last"); }
        }
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() {
        return IosSegmentedButtonBar.class.getResource("apple.css").toExternalForm();
    }
}

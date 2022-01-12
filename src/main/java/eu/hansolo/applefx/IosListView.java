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

import eu.hansolo.applefx.event.MacEvt;
import eu.hansolo.applefx.tools.Helper;
import eu.hansolo.toolbox.evt.Evt;
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.util.Duration;


public class IosListView extends ListView<IosEntry> implements EvtObserver<MacEvt> {
    private Timeline timeline;

    public IosListView() {
        this(FXCollections.observableArrayList());
    }
    public IosListView(final ObservableList<IosEntry> entries) {
        super(entries);
        timeline = new Timeline();
        getStylesheets().add(IosListView.class.getResource("apple.css").toExternalForm());
        getStyleClass().addAll("apple", "ios-list-view");

        registerListeners();
    }


    private void registerListeners() {
        getItems().forEach(entry -> entry.addMacEvtObserver(MacEvt.DELETE_ENTRY, IosListView.this));

        getItems().addListener((ListChangeListener<IosEntry>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(addedItem -> addedItem.addMacEvtObserver(MacEvt.DELETE_ENTRY, IosListView.this));
                } else if (change.wasRemoved()) {
                    change.getRemoved().forEach(removedItem -> removedItem.removeMacEvtObserver(MacEvt.DELETE_ENTRY, IosListView.this));
                }
            }
        });
    }

    @Override public void handle(final MacEvt evt) {
        EvtType<? extends Evt> type = evt.getEvtType();
        if (MacEvt.DELETE_ENTRY.equals(type)) {
            IosEntry entry = (IosEntry) evt.getSource();
            KeyValue kvEntryHeightStart = new KeyValue(entry.prefHeightProperty(), entry.getPrefHeight(), Interpolator.EASE_BOTH);
            KeyValue kvEntryHeightEnd   = new KeyValue(entry.prefHeightProperty(), 0, Interpolator.EASE_BOTH);

            KeyFrame kf0 = new KeyFrame(Duration.ZERO, kvEntryHeightStart);
            KeyFrame kf1 = new KeyFrame(Duration.millis(2 * Helper.ANIMATION_DURATION), kvEntryHeightEnd);

            timeline.getKeyFrames().setAll(kf0, kf1);
            timeline.setOnFinished(e -> getItems().remove(entry));
            timeline.play();
        }
    }
}

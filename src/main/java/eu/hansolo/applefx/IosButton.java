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
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class IosButton extends Label {
    private final MacEvt                                  pressedEvt  = new MacEvt(IosButton.this, MacEvt.PRESSED);
    private final MacEvt                                  releasedEvt = new MacEvt(IosButton.this, MacEvt.RELEASED);
    private       Map<EvtType, List<EvtObserver<MacEvt>>> observers;
    private       EventHandler<MouseEvent>                mouseHandler;


    public IosButton() {
        super();
        init();
    }
    public IosButton(final String text) {
        super(text);
        init();
    }
    public IosButton(final String text, final Node graphic) {
        super(text, graphic);
        init();
    }


    private void init() {
        observers = new ConcurrentHashMap<>();
        initGraphics();
        registerListeners();
    }

    private void initGraphics() {
        getStyleClass().add("ios-button");
        setPrefSize(100, 25);
    }

    private void registerListeners() {
        mouseHandler = e -> {
            final EventType<? extends MouseEvent> TYPE = e.getEventType();
            if (MouseEvent.MOUSE_PRESSED.equals(TYPE)) {
                fireMacEvt(pressedEvt);
            } else if (MouseEvent.MOUSE_RELEASED.equals(TYPE)) {
                fireMacEvt(releasedEvt);
            }
        };
        addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
        addEventHandler(MouseEvent.MOUSE_RELEASED, mouseHandler);
    }

    public void dispose() {
        removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
        removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseHandler);
    }


    // ******************** Event handling ************************************
    public void addMacEvtObserver(final EvtType type, final EvtObserver<MacEvt> observer) {
        if (!observers.containsKey(type)) { observers.put(type, new CopyOnWriteArrayList<>()); }
        if (observers.get(type).contains(observer)) { return; }
        observers.get(type).add(observer);
    }
    public void removeMacEvtObserver(final EvtType type, final EvtObserver<MacEvt> observer) {
        if (observers.containsKey(type)) {
            if (observers.get(type).contains(observer)) {
                observers.get(type).remove(observer);
            }
        }
    }
    public void removeAllMacEvtObservers() { observers.clear(); }

    public void fireMacEvt(final MacEvt evt) {
        final EvtType type = evt.getEvtType();
        observers.entrySet().stream().filter(entry -> entry.getKey().equals(MacEvt.ANY)).forEach(entry -> entry.getValue().forEach(observer -> observer.handle(evt)));
        if (observers.containsKey(type) && !type.equals(MacEvt.ANY)) {
            observers.get(type).forEach(observer -> observer.handle(evt));
        }
    }


    // ******************** Style related *************************************
    @Override public String getUserAgentStylesheet() { return IosButton.class.getResource("apple.css").toExternalForm(); }
}

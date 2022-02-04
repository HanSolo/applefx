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

package eu.hansolo.applefx.event;


import eu.hansolo.toolbox.evt.EvtPriority;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolbox.evt.type.ChangeEvt;


public class MacEvt extends ChangeEvt {
    public static final EvtType<MacEvt> ANY                = new EvtType<>(ChangeEvt.ANY, "ANY");
    public static final EvtType<MacEvt> SELECTED           = new EvtType<>(MacEvt.ANY, "SELECTED");
    public static final EvtType<MacEvt> DESELECTED         = new EvtType<>(MacEvt.ANY, "DESELECTED");
    public static final EvtType<MacEvt> DELETE_ENTRY       = new EvtType<>(MacEvt.ANY, "DELETE_ENTRY");
    public static final EvtType<MacEvt> ADD_ENTRY          = new EvtType<>(MacEvt.ANY, "ADD_ENTRY");
    public static final EvtType<MacEvt> PRESSED            = new EvtType<>(MacEvt.ANY, "PRESSED");
    public static final EvtType<MacEvt> RELEASED           = new EvtType<>(MacEvt.ANY, "RELEASED");
    public static final EvtType<MacEvt> INCREASE           = new EvtType<>(MacEvt.ANY, "INCREASE");
    public static final EvtType<MacEvt> DECREASE           = new EvtType<>(MacEvt.ANY, "DECREASE");
    public static final EvtType<MacEvt> APPEARANCE_CHANGED = new EvtType<>(MacEvt.ANY, "APPEARANCE_CHANGED");
    public static final EvtType<MacEvt> ADD                = new EvtType<>(MacEvt.ANY, "ADD");
    public static final EvtType<MacEvt> REMOVE             = new EvtType<>(MacEvt.ANY, "REMOVE");


    // ******************** Constructors **************************************
    public MacEvt(final Object src, final EvtType<? extends MacEvt> evtType) {
        super(src, evtType);
    }
    public MacEvt(final Object src, final EvtType<? extends MacEvt> evtType, final EvtPriority priority) {
        super(src, evtType, priority);
    }
}

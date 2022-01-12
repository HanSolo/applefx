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

import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;


public class IosEntryCell extends ListCell<IosEntry> {

    @Override protected void updateItem(final IosEntry entry, final boolean isEmpty) {
        super.updateItem(entry, isEmpty);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setPadding(Insets.EMPTY);
        if (isEmpty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(null);
            setGraphic(entry);
        }
    }
}

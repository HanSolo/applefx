package eu.hansolo.applefx.tools;

import eu.hansolo.applefx.MacosWindow;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


public class ResizeHelper {
    private static final double MIN_WIDTH  = 70;
    private static final double MIN_HEIGHT = 40;


    // ******************** Methods *******************************************
    public static void addResizeListener(final Stage stage) {
        ResizeListener resizeListener = new ResizeListener(stage);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_MOVED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_DRAGGED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED, resizeListener);
        stage.getScene().addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, resizeListener);
        ObservableList<Node> children = stage.getScene().getRoot().getChildrenUnmodifiable();
        for (Node child : children) {
            addListenerDeeply(child, resizeListener);
        }
    }

    private static void addListenerDeeply(Node node, EventHandler<MouseEvent> listener) {
        node.addEventHandler(MouseEvent.MOUSE_MOVED, listener);
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, listener);
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, listener);
        node.addEventHandler(MouseEvent.MOUSE_EXITED, listener);
        node.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, listener);
        if (node instanceof Parent) {
            Parent parent = (Parent) node;
            ObservableList<Node> children = parent.getChildrenUnmodifiable();
            for (Node child : children) {
                addListenerDeeply(child, listener);
            }
        }
    }


    // ******************** Inner Classes *************************************
    private static class ResizeListener implements EventHandler<MouseEvent> {
        private Stage  stage;
        private Cursor cursorEvent  = Cursor.DEFAULT;
        private int    border       = 8;
        private double outerBorder  = MacosWindow.OFFSET;
        private double startX       = 0;
        private double startY       = 0;
        private double startScreenX = 0;
        private double startScreenY = 0;


        // ******************** Constructors **********************************
        public ResizeListener(Stage stage) {
            this.stage = stage;
        }


        // ******************** Methods ***************************************
        @Override public void handle(MouseEvent e) {
            EventType<? extends MouseEvent> type  = e.getEventType();
            Scene                           scene = stage.getScene();

            double mouseEventX = e.getSceneX();
            double mouseEventY = e.getSceneY();
            double sceneWidth  = scene.getWidth() - 2 * outerBorder;
            double sceneHeight = scene.getHeight() - 2 * outerBorder;

            if (MouseEvent.MOUSE_MOVED.equals(type)) {
                if (mouseEventX < border && mouseEventY < border) {
                    cursorEvent = Cursor.NW_RESIZE;
                } else if (mouseEventX < border && mouseEventY > sceneHeight - border ) {
                    cursorEvent = Cursor.SW_RESIZE;
                } else if (mouseEventX > sceneWidth - border && mouseEventY < border) {
                    cursorEvent = Cursor.NE_RESIZE;
                } else if (mouseEventX > sceneWidth - border && mouseEventY > sceneHeight - border) {
                    cursorEvent = Cursor.SE_RESIZE;
                } else if (mouseEventX < border) {
                    //cursorEvent = Cursor.W_RESIZE;
                } else if (mouseEventX > sceneWidth - border) {
                    cursorEvent = Cursor.E_RESIZE;
                } else if (mouseEventY < border) {
                    //cursorEvent = Cursor.N_RESIZE;
                } else if (mouseEventY > sceneHeight - border) {
                    cursorEvent = Cursor.S_RESIZE;
                } else {
                    cursorEvent = Cursor.DEFAULT;
                }
                scene.setCursor(cursorEvent);
            } else if (MouseEvent.MOUSE_EXITED.equals(type) || MouseEvent.MOUSE_EXITED_TARGET.equals(type)) {
                scene.setCursor(Cursor.DEFAULT);
            } else if (MouseEvent.MOUSE_PRESSED.equals(type)) {
                startX = stage.getWidth() - mouseEventX;
                startY = stage.getHeight() - mouseEventY;
            } else if (MouseEvent.MOUSE_DRAGGED.equals(type)) {
                if (!Cursor.DEFAULT.equals(cursorEvent)) {
                    if (!Cursor.W_RESIZE.equals(cursorEvent) && !Cursor.E_RESIZE.equals(cursorEvent)) {
                        double minHeight = MIN_HEIGHT; //stage.getMinHeight() > (border * 2) ? stage.getMinHeight() : (border * 2);
                        double maxHeight = stage.getMaxHeight();
                        if (Cursor.NW_RESIZE.equals(cursorEvent) || Cursor.N_RESIZE.equals(cursorEvent) || Cursor.NE_RESIZE.equals(cursorEvent)) {
                            double newHeight = Helper.clamp(MIN_HEIGHT, Double.MAX_VALUE, stage.getHeight() - (e.getScreenY() - stage.getY()));
                            if (newHeight >= minHeight && newHeight <= maxHeight) {
                                stage.setHeight(newHeight);
                                stage.setY(e.getScreenY());
                            } else {
                                newHeight = Helper.clamp(MIN_HEIGHT, Double.MAX_VALUE, Math.min(Math.max(newHeight, minHeight), maxHeight));
                                // y1 + h1 = y2 + h2
                                // y1 = y2 + h2 - h1
                                stage.setY(stage.getY() + stage.getHeight() - newHeight);
                                stage.setHeight(newHeight);
                            }
                        } else {
                            stage.setHeight(Helper.clamp(MIN_HEIGHT, Double.MAX_VALUE, Math.min(Math.max(mouseEventY + startY, minHeight), maxHeight)));
                        }
                    }

                    if (!Cursor.N_RESIZE.equals(cursorEvent) && !Cursor.S_RESIZE.equals(cursorEvent)) {
                        double minWidth = MIN_WIDTH; //stage.getMinWidth() > (border * 2) ? stage.getMinWidth() : (border * 2);
                        double maxWidth = stage.getMaxWidth();
                        if (Cursor.NW_RESIZE.equals(cursorEvent) || Cursor.W_RESIZE.equals(cursorEvent) || Cursor.SW_RESIZE.equals(cursorEvent)) {
                            double newWidth = Helper.clamp(MIN_WIDTH, Double.MAX_VALUE, stage.getWidth() - (e.getScreenX() - stage.getX()));
                            if (newWidth >= minWidth && newWidth <= maxWidth) {
                                stage.setWidth(newWidth);
                                stage.setX(e.getScreenX());
                            } else {
                                newWidth = Helper.clamp(MIN_WIDTH, Double.MAX_VALUE, Math.min(Math.max(newWidth, minWidth), maxWidth));
                                // x1 + w1 = x2 + w2
                                // x1 = x2 + w2 - w1
                                stage.setX(stage.getX() + stage.getWidth() - newWidth);
                                stage.setWidth(newWidth);
                            }
                        } else {
                            stage.setWidth(Helper.clamp(MIN_WIDTH, Double.MAX_VALUE, Math.min(Math.max(mouseEventX + startX, minWidth), maxWidth)));
                        }
                    }
                }
            }
        }
    }
}

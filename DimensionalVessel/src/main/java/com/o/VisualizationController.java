package com.o;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.collections.ObservableList;
import javafx.animation.AnimationTimer;

public class VisualizationController {
    @FXML
    private Canvas visualizationCanvas;
    private ObservableList<DataDimensionalVessel> dataDimensionalVessels;
    ListManager listManager = ListManager.getInstance();

    public void setDataDimensionalVessels(ObservableList<DataDimensionalVessel> dataDimensionalVessels) {
        this.dataDimensionalVessels = dataDimensionalVessels;
        startAnimation();
    }
   
    private void startAnimation() {
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                drawSimulation();
            }
        };
        animationTimer.start();
    }

    public void drawSimulation() {
        GraphicsContext gc = visualizationCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, visualizationCanvas.getWidth(), visualizationCanvas.getHeight());
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, visualizationCanvas.getWidth(), visualizationCanvas.getHeight());
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeLine(DataDimensionalVessel.SPACE_WIDTH / 2, 0, DataDimensionalVessel.SPACE_WIDTH / 2, DataDimensionalVessel.SPACE_HEIGHT);
        gc.strokeLine(0, DataDimensionalVessel.SPACE_HEIGHT / 2, DataDimensionalVessel.SPACE_WIDTH, DataDimensionalVessel.SPACE_HEIGHT / 2);
        String text = "Cartesian Plan 0";
        gc.setFill(Color.WHITE);
        gc.fillText(text, 800 - 150, 20);

        for (DataDimensionalVessel cartesianPlane : dataDimensionalVessels) { 
            DataDimensionalVessel highestCartesianPlane = listManager.findDimensionalVesselById(listManager.highestCartesianPlaneIDforPlane(cartesianPlane));
            int cartesianPlaneGeneration = cartesianPlane.getGeneration();
            int cartesianPlaneGenerationPosition = cartesianPlane.getGenerationPosition();
            int objectRepresentationID = cartesianPlane.getObjectRepresentationID();

            if (cartesianPlaneGeneration == 0 || objectRepresentationID == 999) {
                continue;
            } else {
                DataDimensionalVessel origineCartesianPlane = listManager.findDimensionalVesselById(0);
                DataModel model = listManager.findModelById(objectRepresentationID);
                double modelRadius = cartesianPlane.getMaxRadius();
                double highestRadius = highestCartesianPlane.getMaxRadius();
                Vector3D globalPosition = listManager.convertLocalToGlobalPosition(cartesianPlane).add(origineCartesianPlane.getPosition());
                double axeX = globalPosition.getX();
                double correctedAxeY = DataDimensionalVessel.SPACE_HEIGHT - globalPosition.getY();
                double highestAngle = Math.atan2(highestCartesianPlane.getAngularPosition().getY(), highestCartesianPlane.getAngularPosition().getX());
                double rotationAngleDegrees = Math.toDegrees(highestAngle);
                
                boolean isClockwise = highestCartesianPlane.getAngularMomentum().getZ() > 0;
                if (!isClockwise) {
                    rotationAngleDegrees = -rotationAngleDegrees;
                }

                gc.save();
                gc.translate(axeX, correctedAxeY);
                
                if (cartesianPlaneGenerationPosition == 1) {
                    gc.setFill(model.getColor());
                    gc.fillOval(-modelRadius, -modelRadius, 2 * modelRadius, 2 * modelRadius);
                }
                
                if (cartesianPlaneGenerationPosition == 1 && cartesianPlaneGeneration == 1) {
                
                    String idText = String.valueOf((int) cartesianPlane.getCartesianPlaneID());
                    gc.setFill(Color.WHITE);
                    gc.fillText(idText, gc.getFont().getSize() * idText.length() / 4, gc.getFont().getSize() / 4);
                } 

                if (cartesianPlaneGenerationPosition == 2) {
                
                    String idText = String.valueOf((int) cartesianPlane.getCartesianPlaneID());
                    gc.setFill(Color.WHITE);
                    gc.fillText(idText, gc.getFont().getSize() * idText.length() / 4, gc.getFont().getSize() / 4);
                } 

                if (cartesianPlaneGenerationPosition == 1 && cartesianPlaneGeneration == 1) {

                    gc.rotate(rotationAngleDegrees);
                    gc.setStroke(Color.WHITE);
                    gc.strokeRect(-highestRadius, -highestRadius, 2 * highestRadius, 2 * highestRadius);
                    gc.setLineWidth(1);
                    gc.strokeLine(-highestRadius, 0, highestRadius, 0);
                    gc.strokeLine(0, -highestRadius, 0, highestRadius);
                }

                if (cartesianPlaneGenerationPosition == 2) {

                    gc.rotate(rotationAngleDegrees);
                    gc.setStroke(Color.WHITE);
                    gc.strokeRect(-highestRadius, -highestRadius, highestRadius * 2, highestRadius * 2);
                    gc.setLineWidth(1);
                    gc.strokeLine(-highestRadius, 0, highestRadius, 0);
                    gc.strokeLine(0, -highestRadius, 0, highestRadius);
                }

                gc.restore();
            }
        }
    }
}

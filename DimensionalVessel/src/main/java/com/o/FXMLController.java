package com.o;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import javafx.animation.AnimationTimer;

public class FXMLController {
    private boolean theOrigine = false;
    private boolean simulationRunning = false;
    private static AnimationTimer animationTimer; 
    private long lastUpdate = 0;
    private Random random = new Random();
    private double mass = 1;
    private double radius = 10;
    ListManager listManager = ListManager.getInstance();

    @FXML
    public void initialize() {
        tableViewModel.setItems(listManager.getDataModels());
        tableViewPlan.setItems(listManager.getDataDimensionalVessels());
        originalCartesianPlan();
    }

    @FXML
    private TableView<DataModel> tableViewModel;
    @FXML
    private TableView<DataDimensionalVessel> tableViewPlan;
    @FXML
    private TextField numberField;

    @FXML
    private void originalCartesianPlan() {
        if (theOrigine == false) {
            DataDimensionalVessel origineCartesianPlane = new DataDimensionalVessel(
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                new Vector3D(DataDimensionalVessel.SPACE_WIDTH / 2,DataDimensionalVessel.SPACE_HEIGHT / 2,0),
                new Vector3D(0,0,0),
                new Vector3D(0,0,0),
                new Vector3D(0,0,0),
                new Vector3D(0,0,0)
            );
            listManager.addDataDimensionalVessel(origineCartesianPlane);
            theOrigine = true;
        }
    }

    @FXML
    private void handleAddData() {
        try {
            int numberToAdd = Integer.parseInt(numberField.getText());
            for (int i = 0; i < numberToAdd; i++) {
                int category = DataModel.generateBiasedLowerNumber();
                double electronegativity = DataModel.generateElectronegativity(category);
                Color color = DataModel.generateRandomColor(category);
                double initialMass = mass;
                double initialRadius = radius;
                
                double speed = Math.random() * 2;
                double angleInDegrees = Math.random() * 360;
                double angleInRadians = Math.toRadians(angleInDegrees);
                double velocityX = speed * Math.cos(angleInRadians);
                double velocityY = speed * Math.sin(angleInRadians);

                int cartesianPlaneID = listManager.lastDimensionalVesselIndex();
                Vector3D localCartesianPlaneVelocity = new Vector3D(velocityX, velocityY, 0);
                Vector3D localCartesianPlaneAngularPosition = new Vector3D(1, 0, 0);
                Vector3D localCartesianPlaneAngularVelocity = new Vector3D(0, 0, 0);
                Vector3D localCartesianPlanelAngularMomentum = new Vector3D(0, 0, 0);

                int modelID = listManager.lastModelIndex();
                
                boolean superposition;
                Vector3D newCartesianPlanePosition;
                do {
                    double newX = (DataDimensionalVessel.SPACE_WIDTH * Math.random()) - (DataDimensionalVessel.SPACE_WIDTH / 2);
                    double newY = (DataDimensionalVessel.SPACE_HEIGHT * Math.random()) - (DataDimensionalVessel.SPACE_HEIGHT / 2);
                    double newZ = 0;
                    newCartesianPlanePosition = new Vector3D(newX, newY, newZ);
                
                    superposition = false;
                    for (DataModel existingModel : listManager.getDataModels()) {
                        DataDimensionalVessel existingCartesianPlane = listManager.findDimensionalVesselById(existingModel.getCartesianPlaneID());
                        Vector3D vectorBetweenCenters = newCartesianPlanePosition.subtract(existingCartesianPlane.getPosition());
                        double checkDistance = vectorBetweenCenters.norm();
                        double minDistance = existingModel.getRadius() + initialRadius;
                        if (checkDistance < minDistance) {
                            superposition = true;
                            break;
                        }
                    }
                } while (superposition);
            
                DataDimensionalVessel localCartesianPlane = new DataDimensionalVessel(
                    0,
                    1,
                    cartesianPlaneID,
                    0,
                    1,
                    initialMass,
                    initialRadius,
                    initialRadius,
                    newCartesianPlanePosition,
                    localCartesianPlaneAngularPosition,
                    localCartesianPlaneVelocity,
                    localCartesianPlaneAngularVelocity,
                    localCartesianPlanelAngularMomentum
                );
                listManager.addDataDimensionalVessel(localCartesianPlane);

                DataModel model = new DataModel(
                    0,
                    cartesianPlaneID,
                    modelID,    
                    category,
                    color,
                    electronegativity,
                    initialMass,
                    initialRadius
                );
                listManager.addDataModel(model);
                localCartesianPlane.setObjectRepresentationID(modelID);
                setupSimulation();
            }
        } catch (NumberFormatException e) {
        }
    }

    private void checkModelInteractions(List<DataModel> dataModels) {
        long currentTime = System.nanoTime();
        long collisionThreshold = 100000000;

        for (int i = 0; i < dataModels.size(); i++) {
            for (int j = i + 1; j < dataModels.size(); j++) {
                DataModel model1 = dataModels.get(i);
                DataModel model2 = dataModels.get(j);
                DataDimensionalVessel localCartesianPlane1 = listManager.findDimensionalVesselById(model1.getCartesianPlaneID());
                DataDimensionalVessel localCartesianPlane2 = listManager.findDimensionalVesselById(model2.getCartesianPlaneID());
                int highestCartesianPlaneID1 = listManager.highestCartesianPlaneIDforModel(model1);
                int highestCartesianPlaneID2 = listManager.highestCartesianPlaneIDforModel(model2);
                int objectRepresentationID1 = localCartesianPlane1.getObjectRepresentationID();
                int objectRepresentationID2 = localCartesianPlane2.getObjectRepresentationID();

                if ((currentTime - localCartesianPlane1.getLastCollisionTime() > collisionThreshold) &&
                    (currentTime - localCartesianPlane2.getLastCollisionTime() > collisionThreshold)) {
                    if (highestCartesianPlaneID1 != highestCartesianPlaneID2 && objectRepresentationID1 != 999 && objectRepresentationID2 != 999 ) {
                        if (areModelsApproaching(model1, model2)) {
                            checkModelImpact(model1, model2);
                        }
                    }
                }
            }
        }
    }
    
    private boolean areModelsApproaching(DataModel model1, DataModel model2) {
        DataDimensionalVessel localCartesianPlane1 = listManager.findDimensionalVesselById(model1.getCartesianPlaneID());
        DataDimensionalVessel localCartesianPlane2 = listManager.findDimensionalVesselById(model2.getCartesianPlaneID());
        
        Vector3D globalPosition1 = listManager.convertLocalToGlobalPosition(listManager.findDimensionalVesselById(model1.getCartesianPlaneID()));
        Vector3D globalPosition2 = listManager.convertLocalToGlobalPosition(listManager.findDimensionalVesselById(model2.getCartesianPlaneID()));
        
        Vector3D velocity1 = localCartesianPlane1.getVelocity();
        Vector3D velocity2 = localCartesianPlane2.getVelocity();
    
        Vector3D displacement = globalPosition2.subtract(globalPosition1);

        Vector3D relativeVelocity = velocity2.subtract(velocity1);

        double distanceBetweenCenters = displacement.norm();

        double sumOfRadii = model1.getRadius() + model2.getRadius();

        double dotProduct = relativeVelocity.dotProduct(displacement);

        return (dotProduct > 0 && distanceBetweenCenters > sumOfRadii) || (distanceBetweenCenters <= sumOfRadii);
    }

    private void checkModelImpact(DataModel model1, DataModel model2) {

        Vector3D globalPosition1 = listManager.convertLocalToGlobalPosition(listManager.findDimensionalVesselById(model1.getCartesianPlaneID()));
        Vector3D globalPosition2 = listManager.convertLocalToGlobalPosition(listManager.findDimensionalVesselById(model2.getCartesianPlaneID()));

        Vector3D difference = globalPosition1.subtract(globalPosition2);
        double distance = difference.norm();

        int highestCartesianPlaneID1 = listManager.highestCartesianPlaneIDforModel(model1);
        int highestCartesianPlaneID2 = listManager.highestCartesianPlaneIDforModel(model2);

        if ((distance < model1.getRadius() + model2.getRadius()) 
        && (highestCartesianPlaneID1 != highestCartesianPlaneID2)) {
            DataSnapShot snapShot = listManager.snapShot(model1, model2);

            if (random.nextDouble() <= 0.75) {
                applyBounce(snapShot);
            } else {
                double electronegativitiesDifference = Math.abs(model1.getElectronegativity() - model2.getElectronegativity());
                if (electronegativitiesDifference < 0.4 || (0.4 <= electronegativitiesDifference && electronegativitiesDifference <= 1.7)) {
                    groupIDcounter(snapShot);

                } else {
                    applyBounce(snapShot);
                }
            }
        }
    }
    
    private void applyBounce(DataSnapShot snapShot) {
        DataDimensionalVessel originalParent1 = snapShot.getOriginalParent1();
        DataDimensionalVessel originalParent2 = snapShot.getOriginalParent2();
        DataDimensionalVessel snapShotParent1 = snapShot.getSnapShotParent1();
        DataDimensionalVessel snapShotParent2 = snapShot.getSnapShotParent2();

        Vector3D velocity1 = snapShotParent1.getVelocity();
        Vector3D velocity2 = snapShotParent2.getVelocity();
        double mass1 = snapShotParent1.getCumulMass();
        double mass2 = snapShotParent2.getCumulMass();
    
        Vector3D position1 = snapShotParent1.getPosition();
        Vector3D position2 = snapShotParent2.getPosition();
        Vector3D difference = position2.subtract(position1);
        Vector3D directionOfImpact = difference.normalize();
    
        Vector3D relativeVelocity = velocity1.subtract(velocity2);
        double velocityAlongNormal = relativeVelocity.dotProduct(directionOfImpact);
    
        double impulse = (2 * velocityAlongNormal) / (1 / mass1 + 1 / mass2);
        Vector3D impulseVector = directionOfImpact.multiply(impulse);
    
        Vector3D newVelocity1 = velocity1.subtract(impulseVector.divide(mass1));
        Vector3D newVelocity2 = velocity2.add(impulseVector.divide(mass2));
    
        originalParent1.setVelocity(newVelocity1);
        originalParent2.setVelocity(newVelocity2);
    
        double distanceImpact = difference.magnitude();
        double kineticEnergyBeforeImpact = 0.5 * mass1 * velocity1.magnitudeSquared() + 0.5 * mass2 * velocity2.magnitudeSquared();
        double angularImpulseMagnitude = kineticEnergyBeforeImpact * distanceImpact;
    
        Vector3D angularMomentum1 = originalParent1.getAngularMomentum();
        Vector3D angularMomentum2 = originalParent2.getAngularMomentum();
    
        Vector3D angularMomentumChange = directionOfImpact.multiply(angularImpulseMagnitude);
        Vector3D newAngularMomentum1 = angularMomentum1.add(angularMomentumChange.multiply(mass1 / (mass1 + mass2)));
        Vector3D newAngularMomentum2 = angularMomentum2.add(angularMomentumChange.multiply(mass2 / (mass1 + mass2)));
        
        calculateInertia(originalParent1, snapShotParent1);
        double newAngularVelocityMagnitude1 = newAngularMomentum1.magnitude() / (originalParent1.getInertia());
        Vector3D newAngularVelocity1 = newAngularMomentum1.normalize().multiply(newAngularVelocityMagnitude1);

        calculateInertia(originalParent2, snapShotParent2);
        double newAngularVelocityMagnitude2 = newAngularMomentum2.magnitude() / (originalParent2.getInertia());
        Vector3D newAngularVelocity2 = newAngularMomentum2.normalize().multiply(newAngularVelocityMagnitude2);

        originalParent1.setAngularMomentum(newAngularMomentum1);
        originalParent1.setAngularVelocity(newAngularVelocity1);
        originalParent2.setAngularMomentum(newAngularMomentum2);
        originalParent2.setAngularVelocity(newAngularVelocity2);
    
        long currentTime = System.nanoTime();
        originalParent1.setLastCollisionTime(currentTime);
        originalParent2.setLastCollisionTime(currentTime);

        listManager.removeDataSnapShot(snapShot);
    }
    
    private void groupIDcounter(DataSnapShot snapShot) {

        DataModel originalModel1 = snapShot.getOriginalModel1();
        DataModel originalModel2 = snapShot.getOriginalModel2();

        DataDimensionalVessel originalLocalCartesianPlaneModel1 = snapShot.getOriginalCartesianPlanModel1();
        DataDimensionalVessel originalLocalCartesianPlaneModel2 = snapShot.getOriginalCartesianPlanModel2();
        DataDimensionalVessel originalHighestCartesianPlane1 = snapShot.getOriginalParent1();
        DataDimensionalVessel originalHighestCartesianPlane2 = snapShot.getOriginalParent2();
        DataDimensionalVessel snapShotHighestCartesianPlane1 = snapShot.getSnapShotParent1();
        DataDimensionalVessel snapShotHighestCartesianPlane2 = snapShot.getSnapShotParent2();

        int snapShotGenerationModel1 = snapShotHighestCartesianPlane1.getGeneration();
        int snapShotGenerationModel2 = snapShotHighestCartesianPlane2.getGeneration();

        int originalHighestCartesianPlaneID1 = listManager.highestCartesianPlaneIDforModel(originalModel1);

        if (snapShotGenerationModel1 == 1 && snapShotGenerationModel2 == 1) {
           
            DataDimensionalVessel newLocalCartesianPlane = newLocalCartesianPlane(snapShot);
            int newDimensionalVesselID = newLocalCartesianPlane.getCartesianPlaneID();
            
            postVelocity(snapShot, newLocalCartesianPlane);
            postMassCenter(snapShot, newLocalCartesianPlane);

            newLocalCartesianPlane.addCartesianPlaneKid(originalLocalCartesianPlaneModel1);
            originalLocalCartesianPlaneModel1.setCartesianPlaneParentID(newDimensionalVesselID);
            originalModel1.setCartesianPlaneParentID(newDimensionalVesselID);
            originalLocalCartesianPlaneModel1.setGeneration(2);
            
            newLocalCartesianPlane.addCartesianPlaneKid(originalLocalCartesianPlaneModel2);
            originalLocalCartesianPlaneModel2.setCartesianPlaneParentID(newDimensionalVesselID);
            originalModel2.setCartesianPlaneParentID(newDimensionalVesselID);
            originalLocalCartesianPlaneModel2.setGeneration(2);

            listManager.removeDataSnapShot(snapShot);

        } else if (snapShotGenerationModel1 == 2 && snapShotGenerationModel2 == 1) {
            
            postVelocity(snapShot, originalHighestCartesianPlane1);
            postMassCenter(snapShot, originalHighestCartesianPlane1);
        
            originalHighestCartesianPlane1.addCartesianPlaneKid(originalLocalCartesianPlaneModel2);
            originalLocalCartesianPlaneModel2.setCartesianPlaneParentID(originalHighestCartesianPlaneID1);
            originalModel2.setCartesianPlaneParentID(originalHighestCartesianPlaneID1);
            originalLocalCartesianPlaneModel2.setGeneration(2);  

            listManager.removeDataSnapShot(snapShot);

        } else {
            int originalHighestCartesianPlaneMergeID = originalHighestCartesianPlane1.getCartesianPlaneID();

            postVelocity(snapShot, originalHighestCartesianPlane1);
            postMassCenter(snapShot, originalHighestCartesianPlane1);

            List<DataDimensionalVessel> kids = originalHighestCartesianPlane2.getCartesianPlaneKids();
            for (DataDimensionalVessel kid : kids) {
                originalHighestCartesianPlane1.addCartesianPlaneKid(kid);
                kid.setCartesianPlaneParentID(originalHighestCartesianPlane1.getCartesianPlaneID());
                DataModel originalModelFromOldPlane = listManager.findModelById(kid.getObjectRepresentationID());
                originalModelFromOldPlane.setCartesianPlaneParentID(originalHighestCartesianPlaneMergeID);
            }
            
            long currentTime = System.nanoTime();
            originalHighestCartesianPlane1.setLastCollisionTime(currentTime);
            originalHighestCartesianPlane2.setLastCollisionTime(currentTime);
            
            listManager.removeDataDimensionalVessel(originalHighestCartesianPlane2);
            listManager.removeDataSnapShot(snapShot);
        } 
    }

    private DataDimensionalVessel newLocalCartesianPlane(DataSnapShot snapShot) {
        DataModel model1 = snapShot.getSnapShotModel1();
        int newDimensionalVesselID = listManager.lastDimensionalVesselIndex();
        double initializeMass = model1.getMass();
        double initializeRadius = model1.getRadius();
        Vector3D initializePosition = snapShot.getImpactPoint();
        
        DataDimensionalVessel newLocalCartesianPlane = new DataDimensionalVessel(
            0,
            2,
            newDimensionalVesselID,
            0,
            2,
            initializeMass,
            initializeRadius,
            initializeRadius,
            initializePosition,
            new Vector3D(0, 0, 0),
            new Vector3D(0, 0, 0),
            new Vector3D(0, 0, 0),
            new Vector3D(0, 0, 0)
        );
        listManager.addDataDimensionalVessel(newLocalCartesianPlane);

            return newLocalCartesianPlane;            
    }

    private void postVelocity(DataSnapShot snapShot, DataDimensionalVessel parent) {

        DataDimensionalVessel originalParent1 = snapShot.getOriginalParent1();
        DataDimensionalVessel originalParent2 = snapShot.getOriginalParent2();
        DataDimensionalVessel snapShotParent1 = snapShot.getSnapShotParent1();
        DataDimensionalVessel snapShotParent2 = snapShot.getSnapShotParent2();
        DataDimensionalVessel originalCartesianPlanModel1 = snapShot.getOriginalCartesianPlanModel1();
        DataDimensionalVessel originalCartesianPlanModel2 = snapShot.getOriginalCartesianPlanModel2();
        DataDimensionalVessel snapShotCartesianPlanModel1 = snapShot.getSnapShotCartesianPlanModel1();
        DataDimensionalVessel snapShotCartesianPlanModel2 = snapShot.getSnapShotCartesianPlanModel2();

        double mass1 = snapShotParent1.getCumulMass();
        double mass2 = snapShotParent2.getCumulMass();
        double totalMass = mass1 + mass2;
    
        Vector3D velocity1 = snapShotParent1.getVelocity();
        Vector3D velocity2 = snapShotParent2.getVelocity();
        Vector3D totalVelocity = (velocity1.multiply(mass1).add(velocity2.multiply(mass2))).divide(totalMass);
    
        Vector3D positionDifference = snapShotCartesianPlanModel1.getPosition().subtract(snapShotCartesianPlanModel2.getPosition());
        double impactAngle = Math.atan2(positionDifference.getY(), positionDifference.getX());
        Vector3D impactDirection = new Vector3D(Math.cos(impactAngle), Math.sin(impactAngle), 0);

        double kineticEnergy1 = 0.5 * mass1 * velocity1.magnitudeSquared();
        double kineticEnergy2 = 0.5 * mass2 * velocity2.magnitudeSquared();
        double totalKineticEnergy = kineticEnergy1 + kineticEnergy2;
        double distanceImpact = positionDifference.magnitude();
        double angularMomentumImpact = totalKineticEnergy * distanceImpact * Math.sin(impactAngle);
    
        Vector3D angularMomentum1 = snapShotParent1.getAngularMomentum();
        Vector3D angularMomentum2 = snapShotParent2.getAngularMomentum();
        Vector3D totalAngularMomentum = angularMomentum1.add(angularMomentum2);
        Vector3D newAngularMomentum = totalAngularMomentum.add(impactDirection.multiply(angularMomentumImpact));

        calculateInertia(originalParent1, snapShotParent1);
        calculateInertia(originalParent2, snapShotParent2);
        double totalInertia = originalParent1.getInertia() + originalParent2.getInertia();
        double newAngularVelocityMagnitude = newAngularMomentum.magnitude() / (totalInertia);
        Vector3D newAngularVelocity = newAngularMomentum.normalize().multiply(newAngularVelocityMagnitude);

        if (parent != null) {
            parent.setVelocity(totalVelocity);
            parent.setAngularMomentum(newAngularMomentum);
            parent.setAngularVelocity(newAngularVelocity);
            parent.setInertia(totalInertia);
            
            if (originalCartesianPlanModel1.getGeneration() > 1) {
                List<DataDimensionalVessel> kids1 = originalParent1.getCartesianPlaneKids();
                for (DataDimensionalVessel kid : kids1) {
                    kid.setInertia(kid.getMaxRadius());
                    kid.setAngularVelocity(new Vector3D(0,0,0));
                    kid.setAngularMomentum(new Vector3D(0,0,0));
                }
            } else {
                originalCartesianPlanModel1.setInertia(originalCartesianPlanModel1.getMaxRadius());
                originalCartesianPlanModel1.setAngularVelocity(new Vector3D(0,0,0));
                originalCartesianPlanModel1.setAngularMomentum(new Vector3D(0,0,0));
            }

            if (originalCartesianPlanModel2.getGeneration() > 1) {
                List<DataDimensionalVessel> kids2 = originalParent2.getCartesianPlaneKids();
                for (DataDimensionalVessel kid : kids2) {
                    kid.setInertia(kid.getMaxRadius());
                    kid.setAngularVelocity(new Vector3D(0,0,0));
                    kid.setAngularMomentum(new Vector3D(0,0,0));
                }
            } else {
                originalCartesianPlanModel2.setInertia(originalCartesianPlanModel2.getMaxRadius());
                originalCartesianPlanModel2.setAngularVelocity(new Vector3D(0,0,0));
                originalCartesianPlanModel2.setAngularMomentum(new Vector3D(0,0,0));
            }
        }
    }
    
    private void calculateInertia(DataDimensionalVessel originalHighestCartesianPlane, DataDimensionalVessel snapShotHighestCartesianPlane) {

        List<DataDimensionalVessel> kids = originalHighestCartesianPlane.getCartesianPlaneKids();
        double calculatedInertia = 0;
        if (snapShotHighestCartesianPlane.getGeneration() > 1){
            for (DataDimensionalVessel kid : kids) {
                double distanceToAxis = kid.getPosition().distance(snapShotHighestCartesianPlane.getPosition());
                calculatedInertia += kid.getCumulMass() * Math.pow(distanceToAxis, 2);
            }
        } else {
            double distanceToImpact = snapShotHighestCartesianPlane.getMaxRadius();
            calculatedInertia = snapShotHighestCartesianPlane.getCumulMass() * Math.pow(distanceToImpact, 2);
        }
        originalHighestCartesianPlane.setInertia(calculatedInertia);
    }
    
    private void postMassCenter(DataSnapShot snapShot, DataDimensionalVessel parent) {

        DataDimensionalVessel originalLocalCartesianPlaneModel1 = snapShot.getOriginalCartesianPlanModel1();
        DataDimensionalVessel originalLocalCartesianPlaneModel2 = snapShot.getOriginalCartesianPlanModel2();
        DataDimensionalVessel snapShotLocalCartesianPlaneModel1 = snapShot.getSnapShotCartesianPlanModel1();
        DataDimensionalVessel snapShotLocalCartesianPlaneModel2 = snapShot.getSnapShotCartesianPlanModel2();
        DataDimensionalVessel originalHighestCartesianPlane1 = snapShot.getOriginalParent1();
        DataDimensionalVessel originalHighestCartesianPlane2 = snapShot.getOriginalParent2();
        DataDimensionalVessel snapShotHighestCartesianPlane1 = snapShot.getSnapShotParent1();
        DataDimensionalVessel snapShotHighestCartesianPlane2 = snapShot.getSnapShotParent2();
        Vector3D snapShotPositionModel1 = snapShotLocalCartesianPlaneModel1.getPosition();
        Vector3D snapShotPositionModel2 = snapShotLocalCartesianPlaneModel2.getPosition();
        Vector3D snapShotMassCenter1 = snapShotHighestCartesianPlane1.getPosition();
        Vector3D snapShotMassCenter2 = snapShotHighestCartesianPlane2.getPosition();
        double snapShotCumulMass1 = snapShotHighestCartesianPlane1.getCumulMass();
        double snapShotCumulMass2 = snapShotHighestCartesianPlane2.getCumulMass();

        double snapShotTotalCumulMass = snapShotCumulMass1 + snapShotCumulMass2;
        Vector3D totalMassCenter = (snapShotMassCenter1.multiply(snapShotCumulMass1).add(snapShotMassCenter2.multiply(snapShotCumulMass2))).divide(snapShotTotalCumulMass);
        double newTotalX = (totalMassCenter.getX());
        double newTotalY = (totalMassCenter.getY());
        double newTotalZ = 0;

        double minX = -DataDimensionalVessel.SPACE_WIDTH/2, maxX = DataDimensionalVessel.SPACE_WIDTH/2;
        double minY = -DataDimensionalVessel.SPACE_HEIGHT/2, maxY = DataDimensionalVessel.SPACE_HEIGHT/2;
        double rangeX = DataDimensionalVessel.SPACE_WIDTH;
        double rangeY = DataDimensionalVessel.SPACE_HEIGHT;

        if (newTotalX > maxX) {
            newTotalX = minX + (newTotalX - maxX) % rangeX;
        } else if (newTotalX < minX) {
            newTotalX = maxX + (newTotalX - minX) % rangeX;
        }

        if (newTotalY > maxY) {
            newTotalY = minY + (newTotalY - maxY) % rangeY;
        } else if (newTotalY < minY) {
            newTotalY = maxY + (newTotalY - minY) % rangeY;
        }

        if (parent != null) {
            parent.setPosition(new Vector3D(newTotalX, newTotalY, newTotalZ));

            Vector3D realParentNewPosition = parent.getPosition();
            Vector3D ajustment1 = snapShotMassCenter1.subtract(realParentNewPosition);
            Vector3D ajustment2 = snapShotMassCenter2.subtract(realParentNewPosition);
            List<DataDimensionalVessel> kids1 = originalHighestCartesianPlane1.getCartesianPlaneKids();
            if (kids1 != null && !kids1.isEmpty()) {
                for (DataDimensionalVessel kid : kids1) {
                    
                    Vector3D kidDistancetoCenter = (kid.getPosition().add(ajustment1));
                    kid.setPosition(kidDistancetoCenter);
                    kid.setVelocity(new Vector3D(0,0,0));
                }
            } else {
                Vector3D cartesianPlaneDistancetoCenter = (snapShotPositionModel1).subtract(realParentNewPosition);
                originalLocalCartesianPlaneModel1.setPosition(cartesianPlaneDistancetoCenter);
                originalLocalCartesianPlaneModel1.setVelocity(new Vector3D(0,0,0));
            }
            
            List<DataDimensionalVessel> kids2 = originalHighestCartesianPlane2.getCartesianPlaneKids();
            if (kids2 != null && !kids2.isEmpty()) {
                for (DataDimensionalVessel kid : kids2) {
                    
                    Vector3D kidDistancetoCenter = (kid.getPosition().add(ajustment2));
                    kid.setPosition(kidDistancetoCenter);
                    kid.setVelocity(new Vector3D(0,0,0));
                }
            } else {
                Vector3D cartesianPlaneDistancetoCenter = (snapShotPositionModel2).subtract(realParentNewPosition);
                originalLocalCartesianPlaneModel2.setPosition(cartesianPlaneDistancetoCenter);
                originalLocalCartesianPlaneModel2.setVelocity(new Vector3D(0,0,0));
            }
        }
    }

    @FXML
    private void handlePlayPause() {
        if (simulationRunning) {
            animationTimer.stop();
        } else {
            animationTimer.start();
        }
        simulationRunning = !simulationRunning;
    }

    private void setupSimulation() {
        if (animationTimer == null) {
            animationTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (now - lastUpdate >= 10_000_000) {
                        checkModelInteractions(listManager.getDataModels());;

                        for (DataDimensionalVessel cartesianPlane : listManager.getDataDimensionalVessels()){
                            
                            int objectRepresentationID = (cartesianPlane.getObjectRepresentationID());
                            int cartesianPlaneID = cartesianPlane.getCartesianPlaneID();
                            if (cartesianPlaneID != 0 && objectRepresentationID != 999) {
                                listManager.updateCumulativeProperties(cartesianPlane);
                                cartesianPlane.updateDimensionalVesselPosition();
                            }
                        }
                        tableViewModel.refresh();
                        tableViewPlan.refresh();
                        lastUpdate = now;
                    }
                }
            };
        }
        if (!simulationRunning) {
            animationTimer.start();
            simulationRunning = true;
        }
    }

    public void showVisualizationWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VisualizationView.fxml"));
        Parent root = loader.load();

        VisualizationController visualizationController = loader.getController();
        visualizationController.setDataDimensionalVessels(listManager.getDataDimensionalVessels());

        Stage stage = new Stage();
        stage.setTitle("Visualisation");
        stage.setScene(new Scene(root, 800, 800));
        stage.show();
    }

}

package com.o;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

public class ListManager {
    private static ListManager instance;
    private ObservableList<DataModel> dataModels = FXCollections.observableArrayList();
    private ObservableList<DataDimensionalVessel> dataDimensionalVessels = FXCollections.observableArrayList();
    private ObservableList<DataSnapShot> dataSnapShots = FXCollections.observableArrayList();
    private int snapShotIndex = 1;
    private int modelIndex = 1;
    private int dimensionalVesselIndex = 1;

    public ListManager() {
    }

    public int lastModelIndex() {
        return modelIndex++;
    }

    public int lastDimensionalVesselIndex() {
        return dimensionalVesselIndex++;
    } 
    
    public static synchronized ListManager getInstance() {
        if (instance == null) {
            instance = new ListManager();
        }
        return instance;
    }
    
    public DataModel findModelById(int modelID) {
        for (DataModel model : dataModels) {
            if (model.getModelID() == modelID) {
                return model;
            }
        }
        return null;
    }

    public DataDimensionalVessel findDimensionalVesselById(int cartesianPlaneID) {
        for (DataDimensionalVessel cartesianPlane : dataDimensionalVessels) {
            if (cartesianPlane.getCartesianPlaneID() == cartesianPlaneID) {
                return cartesianPlane;
            }
        }
        return null;
    }
    
    public DataSnapShot findSnapShot(int snapShotID) {
        for (DataSnapShot snapShot : dataSnapShots) {
            if (snapShot.getSnapShotID() == snapShotID) {
                return snapShot;
            }
        }
        return null;
    }

    public Vector3D convertLocalToGlobalPosition(DataDimensionalVessel CartesianPlane) {

        int cartesianPlaneGeneration = CartesianPlane.getGeneration();
        int cartesianPlaneGenerationPosition = CartesianPlane.getGenerationPosition();

        if (cartesianPlaneGenerationPosition == 1 && cartesianPlaneGeneration == 2) {
            DataDimensionalVessel highestCartesianPlane = findDimensionalVesselById(highestCartesianPlaneIDforPlane(CartesianPlane));

            Vector3D globalPosition = highestCartesianPlane.getPosition().add(CartesianPlane.getPosition());

            return globalPosition;

        } else {
            return CartesianPlane.getPosition();
        }
    }

    public int highestCartesianPlaneIDforModel(DataModel model) {
        DataDimensionalVessel localCartesianPlane = findDimensionalVesselById(model.getCartesianPlaneID());
        int generations = localCartesianPlane.getGeneration();
        if (generations > 1) {
            return localCartesianPlane.getCartesianPlaneParentID();
        } else {
            return localCartesianPlane.getCartesianPlaneID();
        }
    }  

    public int highestCartesianPlaneIDforPlane(DataDimensionalVessel localCartesianPlane) {
        int generations = localCartesianPlane.getGeneration();
        int generationPosition = localCartesianPlane.getGenerationPosition();
        if (generationPosition <2 && generations > 1) {
            return localCartesianPlane.getCartesianPlaneParentID();
        } else {
            return localCartesianPlane.getCartesianPlaneID();
        }
    }  

    public void updateCumulativeProperties(DataDimensionalVessel CartesianPlane) {
        int cartesianPlaneGeneration = CartesianPlane.getGeneration();
        double totalMass = 0;
        double maxRadius = 0;

        if (cartesianPlaneGeneration > 1) {
            
            for (DataDimensionalVessel kid : CartesianPlane.getCartesianPlaneKids()) {
                totalMass += kid.getCumulMass();
                
                double distanceToKid = kid.getPosition().norm() + kid.getMaxRadius();
                maxRadius = Math.max(maxRadius, distanceToKid);
                
                CartesianPlane.setCumulMass(totalMass);
                CartesianPlane.setMaxRadius(maxRadius);
            }
        }
    }

    public double getAngularVelocityFromMomentum(DataDimensionalVessel parent) {
        double momentOfInertia = parent.getCumulMass() * Math.pow(parent.getMaxRadius(), 2);
        double angularVelocity = parent.getAngularMomentum().norm() / momentOfInertia;
        
        return angularVelocity;
    }

    public DataSnapShot snapShot(DataModel originalModelA, DataModel originalModelB) {

        DataDimensionalVessel originalCartesianPlaneModelA = findDimensionalVesselById(originalModelA.getCartesianPlaneID());
        DataDimensionalVessel originalCartesianPlaneModelB = findDimensionalVesselById(originalModelB.getCartesianPlaneID());

        int originalCartesianPlaneModelGenerationA = originalCartesianPlaneModelA.getGeneration();
        int originalCartesianPlaneModelGenerationB = originalCartesianPlaneModelB.getGeneration();

        DataModel originalModel1 = (originalCartesianPlaneModelGenerationB > originalCartesianPlaneModelGenerationA) ? originalModelB : originalModelA;
        DataModel originalModel2 = (originalModel1 == originalModelA) ? originalModelB : originalModelA;

        DataDimensionalVessel originalCartesianPlaneModel1 = findDimensionalVesselById(originalModel1.getCartesianPlaneID());
        DataDimensionalVessel originalCartesianPlaneModel2 = findDimensionalVesselById(originalModel2.getCartesianPlaneID());
    
        DataDimensionalVessel originalParent1 = findDimensionalVesselById(highestCartesianPlaneIDforModel(originalModel1));
        DataDimensionalVessel originalParent2 = findDimensionalVesselById(highestCartesianPlaneIDforModel(originalModel2));
        Vector3D originalGlobalPositionModel1 = convertLocalToGlobalPosition(originalCartesianPlaneModel1);
        Vector3D originalGlobalPositionModel2 = convertLocalToGlobalPosition(originalCartesianPlaneModel2);

        int snapShotID = snapShotIndex++;

        Vector3D impactPoint = originalCartesianPlaneModel1.getPosition().add(originalCartesianPlaneModel2.getPosition()).divide(2.0);

        int parent1CartesianPlaneParentID1 = lastDimensionalVesselIndex();
        int parent1Generation1 = originalParent1.getGeneration();
        int parent1CartesianPlaneID1 = lastDimensionalVesselIndex();
        int parent1ObjectRepresentationID1 = 999;
        int parent1GenerationPosition1 = originalParent1.getGenerationPosition();
        final double parent1CumulMass1 = originalParent1.getCumulMass();
        final double parent1CumulRadius1 = originalParent1.getMaxRadius();
        final double parent1Inertia = originalParent1.getInertia();
        final Vector3D parent1Position1 = originalParent1.getPosition();
        final Vector3D parent1AngularPosition1 = originalParent1.getAngularPosition();
        final Vector3D parent1Velocity1 = originalParent1.getVelocity();
        final Vector3D parent1AngularVelocity1 = originalParent1.getAngularVelocity();
        final Vector3D parent1AngularMomentum1 = originalParent1.getAngularMomentum();
        
        DataDimensionalVessel snapShotParent1 = new DataDimensionalVessel(
            parent1CartesianPlaneParentID1,
            parent1Generation1,
            parent1CartesianPlaneID1,
            parent1ObjectRepresentationID1,
            parent1GenerationPosition1,
            parent1CumulMass1,
            parent1CumulRadius1,
            parent1Inertia,
            parent1Position1,
            parent1AngularPosition1,
            parent1Velocity1,
            parent1AngularVelocity1,
            parent1AngularMomentum1
        );
        addDataDimensionalVessel(snapShotParent1);

        int parent2CartesianPlaneParentID2 = lastDimensionalVesselIndex();
        int parent2Generation2 = originalParent2.getGeneration();
        int parent2CartesianPlaneID2 = lastDimensionalVesselIndex();
        int parent2ObjectRepresentationID2 = 999;
        int parent2GenerationPosition2 = originalParent2.getGenerationPosition();
        final double parent2CumulMass2 = originalParent2.getCumulMass();
        final double parent2MaxRadius2 = originalParent2.getMaxRadius();
        final double parent2Inertia2 = originalParent2.getInertia();
        final Vector3D parent2Position2 = originalParent2.getPosition();
        final Vector3D parent2AngularPosition2 = originalParent2.getAngularPosition();
        final Vector3D parent2Velocity2 = originalParent2.getVelocity();
        final Vector3D parent2AngularVelocity2 = originalParent2.getAngularVelocity();
        final Vector3D parent2AngularMomentum2 = originalParent2.getAngularMomentum();
        
        DataDimensionalVessel snapShotParent2 = new DataDimensionalVessel(
            parent2CartesianPlaneParentID2,
            parent2Generation2,
            parent2CartesianPlaneID2,
            parent2ObjectRepresentationID2,
            parent2GenerationPosition2,
            parent2CumulMass2,
            parent2MaxRadius2,
            parent2Inertia2,
            parent2Position2,
            parent2AngularPosition2,
            parent2Velocity2,
            parent2AngularVelocity2,
            parent2AngularMomentum2
        );
        addDataDimensionalVessel(snapShotParent2);

        int cartesianPlanModel1Generation1 = originalCartesianPlaneModel1.getGeneration();
        int cartesianPlanModel1CartesianPlaneID1 = lastDimensionalVesselIndex();
        int cartesianPlanModel1ObjectRepresentationID1 = 999;
        int cartesianPlanModel1GenerationPosition1 = originalCartesianPlaneModel1.getGenerationPosition();
        final double cartesianPlanModel1CumulMass1 = originalCartesianPlaneModel1.getCumulMass();
        final double cartesianPlanModel1MaxRadius1 = originalCartesianPlaneModel1.getMaxRadius();
        final double cartesianPlanModel1Inertia1 = originalCartesianPlaneModel1.getInertia();
        final Vector3D cartesianPlanModel1Position1 = originalCartesianPlaneModel1.getPosition();
        final Vector3D cartesianPlanModel1AngularPosition1 = originalCartesianPlaneModel1.getAngularPosition();
        final Vector3D cartesianPlanModel1Velocity1 = originalCartesianPlaneModel1.getVelocity();
        final Vector3D cartesianPlanModel1AngularVelocity1 = originalCartesianPlaneModel1.getAngularVelocity();
        final Vector3D cartesianPlanModel1AngularMomentum1 = originalCartesianPlaneModel1.getAngularMomentum();
            
        DataDimensionalVessel snapShotCartesianPlanModel1 = new DataDimensionalVessel(
            parent1CartesianPlaneParentID1,
            cartesianPlanModel1Generation1,
            cartesianPlanModel1CartesianPlaneID1,
            cartesianPlanModel1ObjectRepresentationID1,
            cartesianPlanModel1GenerationPosition1,
            cartesianPlanModel1CumulMass1,
            cartesianPlanModel1MaxRadius1,
            cartesianPlanModel1Inertia1,
            cartesianPlanModel1Position1,
            cartesianPlanModel1AngularPosition1,
            cartesianPlanModel1Velocity1,
            cartesianPlanModel1AngularVelocity1,
            cartesianPlanModel1AngularMomentum1
        );
        addDataDimensionalVessel(snapShotCartesianPlanModel1);

        int model1ModelID = lastModelIndex();
        int model1Category = originalModel1.getCategory();
        Color model1Color = originalModel1.getColor();
        double model1Electronegativity = originalModel1.getElectronegativity();
        double model1Mass = originalModel1.getMass();
        double model1Radius = originalModel1.getRadius();
        
        DataModel snapShotModel1 = new DataModel(
            parent1CartesianPlaneParentID1,
            cartesianPlanModel1Generation1,
            model1ModelID,
            model1Category,
            model1Color,
            model1Electronegativity,
            model1Mass,
            model1Radius
        );
        addDataModel(snapShotModel1);

        int cartesianPlanModel2Generation2 = originalCartesianPlaneModel2.getGeneration();
        int cartesianPlanModel2CartesianPlaneID2 = lastDimensionalVesselIndex();
        int cartesianPlanModel2ObjectRepresentationID2 = 999;
        int cartesianPlanModel2GenerationPosition2 = originalCartesianPlaneModel2.getGenerationPosition();
        final double cartesianPlanModel2CumulMass2 = originalCartesianPlaneModel2.getCumulMass();
        final double cartesianPlanModel2MaxRadius2 = originalCartesianPlaneModel2.getMaxRadius();
        final double cartesianPlanModel2Inertia2 = originalCartesianPlaneModel2.getInertia();
        final Vector3D cartesianPlanModel2Position2 = originalCartesianPlaneModel2.getPosition();
        final Vector3D cartesianPlanModel2AngularPosition2 = originalCartesianPlaneModel2.getAngularPosition();
        final Vector3D cartesianPlanModel2Velocity2 = originalCartesianPlaneModel2.getVelocity();
        final Vector3D cartesianPlanModel2AngularVelocity2 = originalCartesianPlaneModel2.getAngularVelocity();
        final Vector3D cartesianPlanModel2AngularMomentum2 = originalCartesianPlaneModel2.getAngularMomentum();
        
        DataDimensionalVessel snapShotCartesianPlanModel2 = new DataDimensionalVessel(
            parent2CartesianPlaneParentID2,
            cartesianPlanModel2Generation2,
            cartesianPlanModel2CartesianPlaneID2,
            cartesianPlanModel2ObjectRepresentationID2,
            cartesianPlanModel2GenerationPosition2,
            cartesianPlanModel2CumulMass2,
            cartesianPlanModel2MaxRadius2,
            cartesianPlanModel2Inertia2,
            cartesianPlanModel2Position2,
            cartesianPlanModel2AngularPosition2,
            cartesianPlanModel2Velocity2,
            cartesianPlanModel2AngularVelocity2,
            cartesianPlanModel2AngularMomentum2
        );
        addDataDimensionalVessel(snapShotCartesianPlanModel2);

        int model2ModelID = lastModelIndex();
        int model2Category = originalModel2.getCategory();
        Color model2Color = originalModel2.getColor();
        double model2Electronegativity = originalModel2.getElectronegativity();
        double model2Mass = originalModel2.getMass();
        double model2Radius = originalModel2.getRadius();
        
        DataModel snapShotModel2 = new DataModel(
            parent2CartesianPlaneParentID2,
            cartesianPlanModel2CartesianPlaneID2,
            model2ModelID,
            model2Category,
            model2Color,
            model2Electronegativity,
            model2Mass,
            model2Radius
        );
        addDataModel(snapShotModel2);

        DataSnapShot snapShot = new DataSnapShot(
            snapShotID,
            impactPoint,
            originalParent1,
            snapShotParent1,
            originalCartesianPlaneModel1,
            snapShotCartesianPlanModel1,
            originalModel1,
            snapShotModel1,
            originalParent2,
            snapShotParent2,
            originalCartesianPlaneModel2,
            snapShotCartesianPlanModel2,
            originalModel2,
            snapShotModel2
            );
            addSnapShot(snapShot);

        Vector3D difference = originalGlobalPositionModel2.subtract(originalGlobalPositionModel1);
        double distance = difference.norm();
        double overlap = originalCartesianPlaneModel1.getMaxRadius() + originalCartesianPlaneModel2.getMaxRadius() - distance;

        if (overlap > 0) {
            Vector3D correction = difference.normalize().multiply(overlap / 2.0);
            
            if (snapShotParent1 != null) {
                snapShotParent1.setPosition(parent1Position1.add(correction));
            }
            
            if (snapShotParent2 != null) {
                snapShotParent2.setPosition(parent2Position2.add(correction));
            }
        }
        return snapShot;
    }
   
    public void addDataModel(DataModel model) {dataModels.add(model);}
    public void addDataDimensionalVessel(DataDimensionalVessel vessel) {dataDimensionalVessels.add(vessel);}
    public void addSnapShot(DataSnapShot snapShot) {dataSnapShots.add(snapShot);}

    public ObservableList<DataModel> getDataModels() {return dataModels;}
    public ObservableList<DataDimensionalVessel> getDataDimensionalVessels() {return dataDimensionalVessels;}

    public void removeDataModel(DataModel model) {dataModels.remove(model);}
    public void removeDataDimensionalVessel(DataDimensionalVessel vessel) {dataDimensionalVessels.remove(vessel);}
    public void removeDataSnapShot(DataSnapShot snapShot) {
        removeDataDimensionalVessel(snapShot.getSnapShotParent1());
        removeDataDimensionalVessel(snapShot.getSnapShotCartesianPlanModel1());
        removeDataModel(snapShot.getSnapShotModel1());
        removeDataDimensionalVessel(snapShot.getSnapShotParent2());
        removeDataDimensionalVessel(snapShot.getSnapShotCartesianPlanModel2());
        removeDataModel(snapShot.getSnapShotModel2());
        dataSnapShots.remove(snapShot);
    }
}

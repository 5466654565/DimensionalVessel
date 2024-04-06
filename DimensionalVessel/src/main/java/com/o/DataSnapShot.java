package com.o;

public class DataSnapShot {
    private double snapShotID;
    private Vector3D impactPoint;
    private DataDimensionalVessel originalParent1;
    private DataDimensionalVessel snapShotParent1;
    private DataDimensionalVessel originalCartesianPlanModel1;
    private DataDimensionalVessel snapShotCartesianPlanModel1;
    private DataModel originalModel1;
    private DataModel snapShotModel1;
    private DataDimensionalVessel originalParent2;
    private DataDimensionalVessel snapShotParent2;
    private DataDimensionalVessel originalCartesianPlanModel2;
    private DataDimensionalVessel snapShotCartesianPlanModel2;
    private DataModel originalModel2;
    private DataModel snapShotModel2;

    @Override
    public String toString() {
        return "DataSnapShot{" + "\n\n" +
                "SnapShotID=" + snapShotID + "\n\n" +
                "ImpactPoint=" + impactPoint + "\n\n" +
                "OriginalParent1=" + originalParent1 + "\n\n" +
                "SnapShotParent1=" + snapShotParent1 + "\n\n" +
                "OriginalCartesianPlanModel1=" + originalCartesianPlanModel1 + "\n\n" +
                "SnapShotCartesianPlanModel1=" + snapShotCartesianPlanModel1 + "\n\n" +
                "OriginalModel1=" + originalModel1 + "\n\n" +
                "SnapShotModel1=" + snapShotModel1 + "\n\n" +
                "OriginalParent2=" + originalParent2 + "\n\n" +
                "SnapShotParent2=" + snapShotParent2 + "\n\n" +
                "OriginalCartesianPlanModel2=" + originalCartesianPlanModel2 + "\n\n" +
                "SnapShotCartesianPlanModel2=" + snapShotCartesianPlanModel2 + "\n\n" +
                "OriginalModel2=" + originalModel2 + "\n\n" +
                "SnapShotModel2=" + snapShotModel2 + "\n\n" +
                '}';
    }

    public DataSnapShot(
        double snapShotID,
        Vector3D impactPoint,

        DataDimensionalVessel originalParent1,
        DataDimensionalVessel snapShotParent1,
        DataDimensionalVessel originalCartesianPlanModel1,
        DataDimensionalVessel snapShotCartesianPlanModel1,
        DataModel originalModel1, 
        DataModel snapShotModel1,
        DataDimensionalVessel originalParent2,
        DataDimensionalVessel snapShotParent2,
        DataDimensionalVessel originalCartesianPlanModel2,
        DataDimensionalVessel snapShotCartesianPlanModel2,
        DataModel originalModel2,
        DataModel snapShotModel2 
    ) 
        {
        this.snapShotID = snapShotID;
        this.impactPoint = impactPoint;
        this.originalParent1 = originalParent1;
        this.snapShotParent1 = snapShotParent1;
        this.originalCartesianPlanModel1 = originalCartesianPlanModel1;
        this.snapShotCartesianPlanModel1 = snapShotCartesianPlanModel1;
        this.originalModel1 = originalModel1;
        this.snapShotModel1 = snapShotModel1;
        this.originalParent2 = originalParent2;
        this.snapShotParent2 = snapShotParent2;
        this.originalCartesianPlanModel2 = originalCartesianPlanModel2;
        this.snapShotCartesianPlanModel2 = snapShotCartesianPlanModel2;
        this.originalModel2 = originalModel2;
        this.snapShotModel2 = snapShotModel2;
    }

    public double getSnapShotID() { return snapShotID; }
    public Vector3D getImpactPoint() { return impactPoint; }
    public DataDimensionalVessel getOriginalParent1() { return originalParent1; }
    public DataDimensionalVessel getSnapShotParent1() { return snapShotParent1; }
    public DataDimensionalVessel getOriginalCartesianPlanModel1() { return originalCartesianPlanModel1; }
    public DataDimensionalVessel getSnapShotCartesianPlanModel1() { return snapShotCartesianPlanModel1; }
    public DataModel getOriginalModel1() { return originalModel1; }
    public DataModel getSnapShotModel1() { return snapShotModel1; }
    public DataDimensionalVessel getOriginalParent2() { return originalParent2; }
    public DataDimensionalVessel getSnapShotParent2() { return snapShotParent2; }
    public DataDimensionalVessel getOriginalCartesianPlanModel2() { return originalCartesianPlanModel2; }
    public DataDimensionalVessel getSnapShotCartesianPlanModel2() { return snapShotCartesianPlanModel2; }
    public DataModel getOriginalModel2() { return originalModel2; }
    public DataModel getSnapShotModel2() { return snapShotModel2; }

    public void setSnapShotID(double snapshotID) { this.snapShotID = snapshotID; }
    public void setImpactPoint(Vector3D impactPoint) { this.impactPoint = impactPoint; }
    public void setOriginalParent1(DataDimensionalVessel originalParent1) { this.originalParent1 = originalParent1; }
    public void setSnapShotParent1(DataDimensionalVessel snapShotParent1) { this.snapShotParent1 = snapShotParent1; }
    public void setOriginalCartesianPlanModel1(DataDimensionalVessel originalCartesianPlanModel1) { this.originalCartesianPlanModel1 = originalCartesianPlanModel1; }
    public void setSnapShotCartesianPlanModel1(DataDimensionalVessel snapShotCartesianPlanModel1) { this.snapShotCartesianPlanModel1 = snapShotCartesianPlanModel1; }
    public void setOriginalModel1(DataModel originalModel1) { this.originalModel1 = originalModel1; }
    public void setSnapShotModel1(DataModel snapShotModel1) { this.snapShotModel1 = snapShotModel1; }
    public void setOriginalParent2(DataDimensionalVessel originalParent2) { this.originalParent2 = originalParent2; }
    public void setSnapShotParent2(DataDimensionalVessel snapShotParent2) { this.snapShotParent2 = snapShotParent2; }
    public void setOriginalCartesianPlanModel2(DataDimensionalVessel originalCartesianPlanModel2) { this.originalCartesianPlanModel2 = originalCartesianPlanModel2; }
    public void setSnapShotCartesianPlanModel2(DataDimensionalVessel snapShotCartesianPlanModel2) { this.snapShotCartesianPlanModel2 = snapShotCartesianPlanModel2; }
    public void setOriginalModel2(DataModel originalModel2) { this.originalModel2 = originalModel2; }
    public void setSnapShotModel2(DataModel snapShotModel2) { this.snapShotModel2 = snapShotModel2; }

}

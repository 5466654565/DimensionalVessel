package com.o;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DataDimensionalVessel {
    private int cartesianPlaneParentID;
    private int generation;
    private int cartesianPlaneID;
    private int objectRepresentationID;
    private int generationPosition;
    private double cumulMass;
    private double maxRadius;
    private double inertia;
    private Vector3D position;
    private Vector3D angularPosition;
    private Vector3D velocity;
    private Vector3D angularVelocity;
    private Vector3D angularMomentum;
    private StringProperty positionDisplay;
    private StringProperty angularPositionDisplay;
    private StringProperty velocityDisplay;
    private StringProperty angularVelocityDisplay;
    private StringProperty angularMomentumDisplay;
    private List<DataDimensionalVessel> cartesianPlaneKids = new ArrayList<>();
    private long lastCollisionTime = 0;
    public static final double SPACE_WIDTH = 800.0;
    public static final double SPACE_HEIGHT = 800.0;
    ListManager listManager = ListManager.getInstance();

    public DataDimensionalVessel(
        int cartesianPlaneParentID, 
        int generation,
        int cartesianPlaneID,
        int objectRepresentationID,
        int generationPosition,
        double cumulMass,
        double maxRadius,
        double inertia,
        Vector3D position, 
        Vector3D angularPosition,
        Vector3D velocity, 
        Vector3D angularVelocity,
        Vector3D angularMomentum) 
        {
        this.cartesianPlaneParentID = cartesianPlaneParentID;
        this.generation = generation;
        this.cartesianPlaneID = cartesianPlaneID;
        this.objectRepresentationID = objectRepresentationID;
        this.generationPosition = generationPosition;
        this.cumulMass = cumulMass;
        this.maxRadius = maxRadius;
        this.inertia = inertia;
        this.position = (position == null) ? new Vector3D(0, 0, 0) : position;
        this.angularPosition = (angularPosition == null) ? new Vector3D(0, 0, 0) : angularPosition;
        this.velocity = (velocity == null) ? new Vector3D(0, 0, 0) : velocity;
        this.angularVelocity = (angularVelocity == null) ? new Vector3D(0, 0, 0) : angularVelocity;
        this.angularMomentum = (angularMomentum == null) ? new Vector3D(0, 0, 0) : angularMomentum;
        this.positionDisplay = new SimpleStringProperty(positionToString(position));
        this.angularPositionDisplay = new SimpleStringProperty(angularPositionToString(angularPosition));
        this.velocityDisplay = new SimpleStringProperty(velocityToString(velocity));
        this.angularVelocityDisplay = new SimpleStringProperty(angularVelocityToString(angularVelocity));
        this.angularMomentumDisplay = new SimpleStringProperty(angularMomentumToString(angularMomentum));
    }

    private String positionToString(Vector3D vector) {
        return String.format("(%f, %f, %f)", vector.getX(), vector.getY(), vector.getZ());
    }

    private String angularPositionToString(Vector3D vector) {
        return String.format("(%f, %f, %f)", vector.getX(), vector.getY(), vector.getZ());
    }

    private String velocityToString(Vector3D vector) {
        return String.format("(%f, %f, %f)", vector.getX(), vector.getY(), vector.getZ());
    }

    private String angularVelocityToString(Vector3D vector) {
        return String.format("(%f, %f, %f)", vector.getX(), vector.getY(), vector.getZ());
    }

    private String angularMomentumToString(Vector3D vector) {
        return String.format("(%f, %f, %f)", vector.getX(), vector.getY(), vector.getZ());
    }

    @Override
    public String toString() {
        return "DataDimensionalVessel{" +
                ", CartesianPlaneParentID=" + cartesianPlaneParentID +
                ", Generation=" + generation +
                ", CartesianPlaneID=" + cartesianPlaneID +
                ", ObjectRepresentation=" + objectRepresentationID +
                ", GenerationPosition=" + generationPosition +
                ", CumulMass=" + cumulMass +
                ", MaxRadius=" + maxRadius +
                ", Inertia=" + inertia +
                ", Position=" + position +
                ", AngularPosition=" + angularPosition +
                ", Velocity=" + velocity +
                ", AngularVelocity=" + angularVelocity +
                ", AngularMomentum=" + angularMomentum +
                '}';
    }

    public void addCartesianPlaneKid(DataDimensionalVessel cartesianPlaneKid) {
        this.cartesianPlaneKids.add(cartesianPlaneKid);
    }

    public List<DataDimensionalVessel> getCartesianPlaneKids() {
        return new ArrayList<>(this.cartesianPlaneKids);
    }
     
    public void updateDimensionalVesselPosition() {
        
        this.position = this.position.add(velocity);

        double minX = -SPACE_WIDTH/2, maxX = SPACE_WIDTH/2;
        double minY = -SPACE_HEIGHT/2, maxY = SPACE_HEIGHT/2;
        double rangeX = SPACE_WIDTH;
        double rangeY = SPACE_HEIGHT;
    
        double newX = this.position.getX();
        double newY = this.position.getY();
        double newZ = this.position.getZ();
    
        if (newX > maxX) {
            newX = minX + (newX - maxX) % rangeX;
        } else if (newX < minX) {
            newX = maxX + (newX - minX) % rangeX;
        }

        if (newY > maxY) {
            newY = minY + (newY - maxY) % rangeY;
        } else if (newY < minY) {
            newY = maxY + (newY - minY) % rangeY;
        }

        this.position.setX(newX);
        this.position.setY(newY);
        this.position.setZ(newZ);
        this.positionDisplay.set(positionToString(this.position));

        Vector3D newAngularPosition = this.getAngularPosition().add(angularVelocity);
        this.setAngularPosition(newAngularPosition);

        this.angularPositionDisplay.set(positionToString(this.angularPosition));
        if (this.getGenerationPosition() == 2) {
            updateKidsOrbitalPosition();
        }
    }
    
    public void updateKidsOrbitalPosition() {
        double angle = Math.atan2(this.position.getY(), this.position.getX());

        for (DataDimensionalVessel kid : this.getCartesianPlaneKids()) {
            double distanceToCenter = this.getPosition().norm();

            double orbitX = Math.cos(angle) * distanceToCenter;
            double orbitY = Math.sin(angle) * distanceToCenter;

            kid.setAngularPosition(new Vector3D(orbitX, orbitY, 0));
        
            this.positionDisplay.set(positionToString(this.position));
        }
    }
    
    public int getCartesianPlaneParentID() { return cartesianPlaneParentID; }
    public int getGeneration() { return generation; }
    public int getCartesianPlaneID() { return cartesianPlaneID; }
    public int getObjectRepresentationID() { return objectRepresentationID; }
    public int getGenerationPosition() { return generationPosition; }
    public double getCumulMass() { return cumulMass; }
    public double getMaxRadius() { return maxRadius; }
    public double getInertia() { return inertia; }
    public Vector3D getPosition() { return position; }
    public Vector3D getAngularPosition() { return angularPosition; }
    public Vector3D getVelocity() { return velocity; }
    public Vector3D getAngularVelocity() { return angularVelocity; }
    public Vector3D getAngularMomentum() { return angularMomentum; }
    public long getLastCollisionTime() { return lastCollisionTime; }

    public void setCartesianPlaneParentID(int cartesianPlaneParentID) { this.cartesianPlaneParentID = cartesianPlaneParentID; }
    public void setGeneration(int generation) { this.generation = generation; }
    public void setCartesianPlaneID(int cartesianPlaneID) { this.cartesianPlaneID = cartesianPlaneID; }
    public void setObjectRepresentationID(int objectRepresentationID) { this.objectRepresentationID = objectRepresentationID; }
    public void setGenerationPosition(int generationPosition) { this.generationPosition = generationPosition; }
    public void setCumulMass(double cumulMass) { this.cumulMass = cumulMass; }
    public void setMaxRadius(double maxRadius) { this.maxRadius = maxRadius; }
    public void setInertia(double inertia) { this.inertia = inertia; }
    public void setPosition(Vector3D position) { this.position = position; }
    public void setAngularPosition(Vector3D angularPosition) { this.angularPosition = angularPosition; }
    public void setVelocity(Vector3D velocity) { this.velocity = velocity; }
    public void setAngularVelocity(Vector3D angularVelocity) { this.angularVelocity = angularVelocity; }
    public void setAngularMomentum(Vector3D angularMomentum) { this.angularMomentum = angularMomentum; }
    public void setLastCollisionTime(long lastCollisionTime) { this.lastCollisionTime = lastCollisionTime; }
}   

package com.o;

import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DataModel {
    private int cartesianPlaneParentID;
    private int cartesianPlaneID;
    private int modelID;
    private int category;
    private Color color;
    private double electronegativity;
    private static final int minType = 1;
    private static final int maxType = 100;
    private static final double minElectronegativity = 0.7;
    private static final double maxElectronegativity = 4.0;
    private double mass;
    private double radius; 
    private static Map<Integer, Color> categoryColors = new HashMap<>();
    private static Random random = new Random();
    ListManager listManager = ListManager.getInstance();

    @Override
    public String toString() {
        return "DataModel{" +
                ", CartesianPlaneParentID=" + cartesianPlaneParentID +
                ", CartesianPlaneID=" + cartesianPlaneID +
                ", ModelID=" + modelID +
                ", Category=" + category +
                ", Color=" + color + 
                ", Electronegativity=" + electronegativity +
                ", Mass=" + mass +
                ", Radius=" + radius +
                '}';
    }

    public DataModel(
        int cartesianPlaneParentID,
        int cartesianPlaneID, 
        int modelID, 
        int category, 
        Color color, 
        double electronegativity, 
        double mass, 
        double radius) 
        {
        this.cartesianPlaneParentID = cartesianPlaneParentID;
        this.cartesianPlaneID = cartesianPlaneID;
        this.modelID = modelID;
        this.category = category;
        this.color = generateRandomColor(category);
        this.electronegativity = generateElectronegativity(category);
        this.mass = mass;
        this.radius = radius;
    }
        
    public static int generateBiasedLowerNumber() {
        Random rand = new Random();
        int upperBound = rand.nextInt(maxType) + minType;
        return rand.nextInt(upperBound) + minType;
    }

    public static Color generateRandomColor(int category) {
        if (!categoryColors.containsKey(category)) {
            double red = random.nextDouble();
            double green = random.nextDouble();
            double blue = random.nextDouble();
            Color newColor = new Color(red, green, blue, 1.0);
            categoryColors.put(category, newColor);
        }
        return categoryColors.get(category);
    }

    public static double generateElectronegativity(int category) {
        double minfactor = 1.0 / 100 * (maxElectronegativity - minElectronegativity);
        int sizeTable = (int) Math.sqrt(maxType);
        int x = category % sizeTable;
        int y = sizeTable - (int) (category / sizeTable);
        double electronegativity = (minElectronegativity - minfactor) + (x * y / 100.0) * (maxElectronegativity - minfactor); 
        return electronegativity;
    }
    
    public int getCartesianPlaneParentID() { return cartesianPlaneParentID; }
    public int getCartesianPlaneID() { return cartesianPlaneID; }
    public int getModelID() { return modelID; }
    public int getCategory() { return category; }
    public Color getColor() { return color; }
    public double getElectronegativity() { return electronegativity; }
    public double getMass() { return mass; }
    public double getRadius() { return radius; }

    public void setCartesianPlaneParentID(int cartesianPlaneParentID) { this.cartesianPlaneParentID = cartesianPlaneParentID; }
    public void setModelID(int modelID) { this.modelID = modelID; }
    public void setCategory(int category) { this.category = category; }
    public void setColor(Color color) { this.color = color; }
    public void setElectronegativity(double electronegativity) { this.electronegativity = electronegativity; }
    public void setMass(double mass) { this.mass = mass; }
    public void setRadius(double radius) { this.radius = radius; }
}

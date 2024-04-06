package com.o;

public class Vector3D {
    private double x;
    private double y;
    private double z;

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return String.format("(%f, %f, %f)", x, y, z);
    }

    public Vector3D add(Vector3D other) {
        return new Vector3D(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vector3D subtract(Vector3D other) {
        return new Vector3D(this.x - other.x, this.y - other.y, this.z - other.z);
    }
    
    public Vector3D multiply(double scalar) {
        return new Vector3D(this.x * scalar, this.y * scalar, this.z * scalar);
    }
    
    public double angleWithXAxis() {
        return Math.toDegrees(Math.atan2(this.y, this.x));
    }

    public Vector3D divide(double scalar) {
        if (scalar == 0) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }
        return new Vector3D(this.x / scalar, this.y / scalar, this.z / scalar);
    }
    
    public double dotProduct(Vector3D other) {
        double result = this.x * other.x + this.y * other.y + this.z * other.z;
        return result;
    }
    
    public Vector3D crossProduct(Vector3D other) {
        double newX = this.y * other.z - this.z * other.y;
        double newY = this.z * other.x - this.x * other.z;
        double newZ = this.x * other.y - this.y * other.x;
        return new Vector3D(newX, newY, newZ);
    }
    
    public Vector3D mod(double width, double height, double depth) {
        return new Vector3D(
            (this.x + width) % width,
            (this.y + height) % height,
            this.z
        );
    }

    public double magnitudeSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public double magnitude() {
        return Math.sqrt(this.magnitudeSquared());
    }

    public double norm() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public Vector3D normalize() {
        double norm = norm();
        if (norm == 0) {
            throw new ArithmeticException("Cannot normalize the zero vector");
        }
        return divide(norm);
    }
    
    public double angle(Vector3D other) {
        double dotProduct = dotProduct(other);
        double thisNorm = norm();
        double otherNorm = other.norm();
        if (thisNorm == 0 || otherNorm == 0) {
            throw new ArithmeticException("Cannot compute angle with zero vector");
        }
        return Math.acos(dotProduct / (thisNorm * otherNorm));
    }
    
    public static Vector3D fromPolar(double magnitude, double angleRadians) {
        return new Vector3D(
            magnitude * Math.cos(angleRadians),
            magnitude * Math.sin(angleRadians),
            0
        );
    }

    public Vector3D project(Vector3D other) {
        double dotProduct = dotProduct(other);
        double otherNormSquared = other.dotProduct(other);
        if (otherNormSquared == 0) {
            throw new ArithmeticException("Cannot project onto the zero vector");
        }
        double scalar = dotProduct / otherNormSquared;
        return other.multiply(scalar);
    }
    
    public double distance(Vector3D other) {
        return subtract(other).norm();
    }
    
    public static Vector3D add(Vector3D... vectors) {
        double sumX = 0;
        double sumY = 0;
        double sumZ = 0;
        for (Vector3D vector : vectors) {
            sumX += vector.x;
            sumY += vector.y;
            sumZ += vector.z;
        }
        return new Vector3D(sumX, sumY, sumZ);
    }
    
    public static Vector3D average(Vector3D... vectors) {
        return add(vectors).divide(vectors.length);
    }
    
    public double sumOfSquares() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setZ(double z) { this.z = z; }
}

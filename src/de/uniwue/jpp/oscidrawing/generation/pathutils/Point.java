package de.uniwue.jpp.oscidrawing.generation.pathutils;

public class Point {
    double x;
    double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;

    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double distanceTo(Point p) {
        double Xdifferenz = p.x - x;
        double Ydifferenz = p.y - y;
        double Xsquare = Xdifferenz * Xdifferenz;
        double Ysquare = Ydifferenz * Ydifferenz;
        double root = Math.sqrt(Xsquare + Ysquare);
        return root;
    }

    @Override
    public String toString() {
        //Point{x=<x>, y=<y>}

        return "Point{x=" + String.valueOf(x) + ", y=" + String.valueOf(y) + "}";
    }

    public Point interpolateTo(Point p, double factor) {
//        Sei xd der Vektor, der von diesem Punkt zum Punkt p zeigt.
//        Der zurückgegebene Punkt soll "dieser Punkt"+factor*xd sein. Insbesondere gilt dann:
//        Falls factor gleich 0 ist, soll der zurückgegebene Punkt die selben Koordinaten wie dieser Punkt haben.
//        Falls factor gleich 1 ist, soll der zurückgegebene Punkt die selben Koordinaten wie der Punkt p haben.
//

        if (factor == 0) {
            return this;
        } else if (factor == 1) {
            return p;
        } else {
            double vektorX = p.x - x;
            double vektorY = p.y - y;

            double newX = x + factor * vektorX;
            double newY = y + factor * vektorY;
            return new Point(newX, newY);

        }


    }
}

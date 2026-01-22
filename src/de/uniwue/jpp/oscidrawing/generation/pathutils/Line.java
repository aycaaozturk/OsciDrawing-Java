package de.uniwue.jpp.oscidrawing.generation.pathutils;

public class Line {
    Point p1;
    Point p2;

    public Line(Point p1, Point p2) {
        this.p1=p1;
        this.p2=p2;
    }

    public Point getStart() {
        return p1;
    }

    public Point getEnd() {
        return p2;
    }

    public double length() {
        return p1.distanceTo(p2);
    }

    public Point getPointAt(double percentage) {
        double newX = p1.getX() + percentage * (p2.getX() - p1.getX());
        double newY = p1.getY() + percentage * (p2.getY() - p1.getY());
        return new Point(newX, newY);
    }

    @Override
    public String toString() {
        //Line{p1=<p1>, p2=<p2>
        return "Line{p1=" +p1.toString()+", p2="+p2.toString()+"}";

    }
}

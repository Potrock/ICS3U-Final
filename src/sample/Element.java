package sample;

import javafx.scene.Node;

import javafx.geometry.Point2D;

/**
 * Created by pwbla on 2017-04-24.
 */
public class Element {
    private Node view;
    private Point2D velocity = new Point2D(0, 0);
    public boolean isAlive = true;


    public Element(Node view) {
        this.view = view;
    }


    public void updateLocation(double constant) {
        view.setTranslateX(view.getTranslateX() + velocity.getX() * constant);
        view.setTranslateY(view.getTranslateY() + velocity.getY() * constant);
    }

    public void setVelocity(Point2D velocity) {
        this.velocity = velocity;
    }

    public Point2D getVelocity() {
        return velocity;
    }

    public boolean alive() {
        return isAlive;
    }

    public boolean dead() {
        return !isAlive;
    }

    public void setStatus(boolean status) {
        this.isAlive = status;
    }

    public double getRotate() {
        return view.getRotate();
    }

    public Node getView() {
        return view;
    }

    public void rotateRight() {
        view.setRotate(view.getRotate() + 5);
        setVelocity(new Point2D(Math.cos(Math.toRadians(getRotate())), Math.sin(Math.toRadians(getRotate()))));
    }

    public void rotateLeft() {
        view.setRotate(view.getRotate() - 5);
        setVelocity(new Point2D(Math.cos(Math.toRadians(getRotate())), Math.sin(Math.toRadians(getRotate()))));
    }

    public boolean isHitting(Node other) {
        return getView().getBoundsInParent().intersects(other.getBoundsInParent());
    }


}

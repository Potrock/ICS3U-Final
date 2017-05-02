package sample;

import javafx.scene.Node;

import javafx.geometry.Point2D;

/**
 * Created by pwbla on 2017-04-24.
 */
class Element {
    private Node view;
    private Point2D velocity = new Point2D(0, 0);
    private boolean isAlive = true;
    int counter = 0;


    Element(Node view) {
        this.view = view;
    }


    void updateLocation(double constant) {
        view.setTranslateX(view.getTranslateX() + velocity.getX() * constant);
        view.setTranslateY(view.getTranslateY() + velocity.getY() * constant);
    }

    void setVelocity(Point2D velocity) {
        this.velocity = velocity;
    }

    Point2D getVelocity() {
        return velocity;
    }

    boolean alive() {
        return isAlive;
    }

    int getCounter() { return counter;}

    boolean dead() {
        return !isAlive;
    }

    void setStatus(boolean status) {
        this.isAlive = status;
    }

    private double getRotate() {
        return view.getRotate();
    }

    Node getView() {
        return view;
    }

    void rotateRight() {
        view.setRotate(view.getRotate() + 5);
        setVelocity(new Point2D(Math.cos(Math.toRadians(getRotate())), Math.sin(Math.toRadians(getRotate()))));
    }

    void rotateLeft() {
        view.setRotate(view.getRotate() - 5);
        setVelocity(new Point2D(Math.cos(Math.toRadians(getRotate())), Math.sin(Math.toRadians(getRotate()))));
    }

    boolean isHitting(Node other) {
        return getView().getBoundsInParent().intersects(other.getBoundsInParent());
    }


}

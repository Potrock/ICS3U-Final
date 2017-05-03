package sample;

import javafx.scene.Node;

import javafx.geometry.Point2D;

/**
 * Created by pwbla on 2017-04-24.
 */
class Element {

    /*
    The Element class is for all the game objects, it contains the methods that each object is going to need on top of the native JavaFX methods.
     */
    private Node view; //Need to force Element objects to have JavaFX Node characteristics, so when you see elementName.getView(); in the main file, it's because I'm referencing JavaFX methods or values.
    private Point2D velocity = new Point2D(0, 0); //Starts with a basic velocity
    private boolean isAlive = true; //Defaults to being alive
    int counter = 0; //The counter is used to represent how long something has been "alive", which helps with killing bullets after they've been around for too long.


    Element(Node view) {
        this.view = view; //The basic constructor giving the objects their node properties.
    }


    /*
      Moves the element depending on where they are, and their velocity.
      The constant is to allow me to move them backwards by have a velocity of -x.
     */
    void updateLocation(double constant) {
        view.setTranslateX(view.getTranslateX() + velocity.getX() * constant);
        view.setTranslateY(view.getTranslateY() + velocity.getY() * constant);
    }

    /*
    Takes in a velocity as an argument and assigns it to the velocity declared above.
     */
    void setVelocity(Point2D velocity) {
        this.velocity = velocity;
    }

    /*
    Returns the velocity
     */
    Point2D getVelocity() {
        return velocity;
    }

    /*
    Returns whether the object is "alive" or not.
     */
    boolean alive() {
        return isAlive;
    }

    /*
    Gets the counter of how many iterations the object has been alive.
     */
    int getCounter() { return counter;}

    /*
    Returns whether the object is "dead", always the opposite of whether it's alive.
     */
    boolean dead() {
        return !isAlive;
    }

    /*
    Sets the status of the object (dead or alive)
     */
    void setStatus(boolean status) {
        this.isAlive = status;
    }

    /*
    Gets the rotation of the object, native to JavaFX.
     */
    private double getRotate() {
        return view.getRotate();
    }

    /*
    Returns the view of the object which allows us to apply JavaFX methods to the object.
     */
    Node getView() {
        return view;
    }

    /*
    Rotates the object by 5 Degrees to the right (Math from https://www.youtube.com/watch?v=l2XhUHW8Oa4)
     */
    void rotateRight() {
        view.setRotate(view.getRotate() + 5);
        setVelocity(new Point2D(Math.cos(Math.toRadians(getRotate())), Math.sin(Math.toRadians(getRotate()))));
    }

    /*
    Rotates the object by 5 degrees to the left (Math from https://www.youtube.com/watch?v=l2XhUHW8Oa4)
     */
    void rotateLeft() {
        view.setRotate(view.getRotate() - 5);
        setVelocity(new Point2D(Math.cos(Math.toRadians(getRotate())), Math.sin(Math.toRadians(getRotate()))));
    }

    /*
    Checks for collision between the object and another object. The other object has to be a node, not an element so you would use elementName.getView() in place for the name.
     */
    boolean isHitting(Node other) {
        return getView().getBoundsInParent().intersects(other.getBoundsInParent());
    }


}

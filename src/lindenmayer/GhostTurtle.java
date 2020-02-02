package lindenmayer;

import java.awt.geom.Point2D;
import java.util.Stack;

public class GhostTurtle implements Turtle {

    private Stack<State> states; // used to save the turtle's state
    private State currentState; // current position and angle of the turtle
    private double step;
    private double delta;

    public GhostTurtle() {
        this.states = new Stack<>();
    }

    /**
     * Nested class to encapsulate the turtle's position on the place and angle
     */
    private class State {

        private Point2D.Double position;
        private double angle;

        /**
         * Constructor
         * @param xPos x-position
         * @param yPos y-position
         * @param angle angle
         */
        public State(double xPos, double yPos, double angle) {
            this.position = new Point2D.Double(xPos, yPos);
            this.angle = angle;
        }
    }

    /**
     * Getters
     */
    public double getAngle(){
        return this.currentState.angle;
    }

    public Point2D getPosition() {
        return this.currentState.position;
    }

    /**
     * Setters
     */
    public void setUnits(double step, double delta) {
        this.step = step;
        this.delta = delta;
    }

    /**
     * Draws a line of unit length
     */
    public void draw() {

        double angle = this.getAngle();
        double newXpos = this.getPosition().getX() + this.step * Math.cos(Math.toRadians(angle));
        double newYpos = this.getPosition().getY() + this.step * Math.sin(Math.toRadians(angle));

        this.currentState = new State(newXpos, newYpos, angle);
    }
    public void move() {

        double angle = this.getAngle();
        double newXpos = this.getPosition().getX() + this.step * Math.cos(Math.toRadians(angle));
        double newYpos = this.getPosition().getY() + this.step * Math.sin(Math.toRadians(angle));

        this.currentState = new State(newXpos, newYpos, angle);
    }

    /**
     * Turn right (clockwise) by unit angle.
     */
    public void turnR() {

        double newAngle = this.getAngle() + this.delta;
        double xPos = this.getPosition().getX();
        double yPos = this.getPosition().getY();

        this.currentState = new State(xPos, yPos, newAngle);

    }

    /**
     * Turn left (counter-clockwise) by unit angle.
     */
    public void turnL() {

        double newAngle = this.getAngle() - this.delta;
        double xPos = this.getPosition().getX();
        double yPos = this.getPosition().getY();

        this.currentState = new State(xPos, yPos, newAngle);
    }

    /**
     * Saves turtle state
     */
    public void push() { this.states.push(this.currentState); }

    /**
     * Recovers turtle state
     */
    public void pop() {
        this.currentState = this.states.pop();
    }

    /**
     * Lets the turtle relax
     */
    public void stay() {
        return;
    }

    /**
     * Initializes the turtle state (and clears the state stack)
     * @param pos turtle position
     * @param angle_deg angle in degrees (90 = up, 0 = right)
     */
    public void init(Point2D pos, double angle_deg) {

        // Clears the stack
        this.states.clear();

        // Initializes the position
        this.currentState = new State(pos.getX(), pos.getY(), angle_deg);
    }
}

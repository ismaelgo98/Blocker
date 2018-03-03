import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Blocker extends PApplet {


Block[] blocks;
Line lit;
Ball ball;
Player player;

boolean gameStarted = false;

int red = color(255, 0, 0);
int green = color(0, 255, 0);
int blue = color(0, 0, 255);
int yellow = color(255, 255, 0);

int initRow = 3;

public void setup(){
    
    

    blocks = new Block[60];
    lit = new Line();
    ball = new Ball();
    player = new Player("Pepe");

    int a = 0;

    /*

    for (int i = 0; i < 60; i++) {
        for (int j = 0; j < 10; j++){
            blocks[a]= new Block(j, i, red);
            a++;
        }
        for (int j = 0; j < 10; j++){
            blocks[a]= new Block(j, i, red);
            a++;
        }
        for (int j = 0; j < 10; j++){
            blocks[a]= new Block(j, i, red);
            a++;
        }
    }

    */

    for (int i = initRow; i < initRow+2; i++){
        for (int j = 0; j < 10; j++){
            blocks[a++]= new Block(j, i, 100, 30, red);
        }
    }

    for (int i = initRow+2; i < initRow+4; i++){
        for (int j = 0; j < 10; j++){
            blocks[a++]= new Block(j, i, 100, 30, green);
        }
    }

    for (int i = initRow+4; i < initRow+6; i++){
        for (int j = 0; j < 10; j++){
            blocks[a++]= new Block(j, i, 100, 30, blue);
        }
    }
}

public void reset() {
    ball.restore();
    lit.restore();
    gameStarted=false;
}

public void draw(){
    background(0);

    if(gameStarted) {
        // Only if you want to use mouse
        // lit.update();
        ball.update(lit);
        if(ball.isTouchingDown()){
            player.looseLives();
            reset();
        }
    }

    lit.show();

    for (int i = 0; i < blocks.length; i++){
        boolean isAlive = blocks[i].isAlive();
        if (isAlive) {
            blocks[i].show();
            blocks[i].update(ball, player);
        }
    }

    player.show();
    ball.show();

    fill(255);
    text("Created by Ismael and Carlos   (C) 2018", 20, 700);

}

public void keyPressed() {
    if (!player.lost()) {
        if(key==32) {
            gameStarted=true;
        }
        if(gameStarted) {
            lit.update(keyCode);
        }
    } else {
        if(key==32) {
            reset();
        }
    }
}
class Ball extends Point {

    HitBox hb;
    Vector vector;
    Velocity velo;

    private final float diameter = 15;

    Ball() {
        super(width/2, height - 80);
        velo = new Velocity(4, 4);
        hb = new HitBox(diameter, diameter, this);
        restore();
    }

    private void initPos() {
        setX(width/2); setY(height - 80);
    }

    private void initVector() {
        vector = new Vector(random(1, -1), -1);
    }

    public float getRadius() {
        return diameter / 2;
    }

    private boolean isTouchingLine(Line line) {
        boolean[] touched = this.hb.hit(line.hb);
        return touched[HitBox.HIT] && touched[HitBox.TOP];
    }

    private boolean isTouchingTopBorder() {
        return getY() - getRadius() <= 0;
    }

    private boolean isTouchingSideBorders() {
        return getX() - getRadius() <= 0
            || getX() + getRadius() >= width;
    }

    public boolean isTouchingDown() {
        return getY() + getRadius() >= height;
    }

    public void update() {
        if (isTouchingSideBorders()) {
            vector.changeWayX();
        }
        if (isTouchingTopBorder()) {
            vector.changeWayY();
        }
        move(vector.getX()*velo.getXVelocity(), vector.getY()*velo.getYVelocity());
        hb.update(this);
    }

    public void update(Line line) {
        if (isTouchingLine(line)) {
            vector.changeWayY();
        }
        update();
    }

    public void restore(){
        initPos();
        initVector();
    }

    public void show(){
        fill(255);
        ellipseMode(CENTER);
        ellipse(getX(), getY(), diameter, diameter);
    }
}
class Block extends Point {

    HitBox hb;
    int c;
    boolean alive = true;

    Block(int i, int j, float w, float h) {
        super(i * w + w/2, j * h);
        hb = new HitBox(w, h, this);
        c = color(random(1, 255), random(1, 255), random(1, 255));
    }

    Block(int i, int j, float w, float h, float r, float g, float b){
        super(i * w + w/2, j * h);
        hb = new HitBox(w, h, this);
        c = color(r, g, b);
    }

    Block(int i, int j, float w, float h, int c) {
        super(i * w + w/2, j * h);
        hb = new HitBox(w, h, this);
        this.c = c;
    }

    private boolean isTouchedBy(Ball ball) {
        boolean[] touched = ball.hb.hit(this.hb);

        if (touched[HitBox.HIT]) {
            if (touched[HitBox.TOP]  || touched[HitBox.BOTTOM]) {
                ball.vector.changeWayY();
            }
            if (touched[HitBox.LEFT] || touched[HitBox.RIGHT]) {
                ball.vector.changeWayX();
            }
        }
        return touched[HitBox.HIT];
    }

    public boolean isAlive() {
        return alive;
    }

    public void update(Ball ball, Player player){
        if (isTouchedBy(ball)) {
            player.addPoints();
            alive = false;
        }
    }

    public void show(){
        fill(c);
        rectMode(CENTER);
        rect(getX(), getY(), hb.getWidth(), hb.getHeight());
    }
}
class Coordinate {
    private float x, y;

    Coordinate(float x, float y) {
        this.x = x; this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void moveX(float x) {
        setX(getX() + x);
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void moveY(float y) {
        setY(getY() + y);
    }

    public void move(float x, float y) {
        moveX(x);
        moveY(y);
    }
}
class HitBox {
    private Point up, down, left, right;
    Point upleft, upright, downleft, downright;

    static final int HIT      = 0;
    static final int LEFT     = 1;
    static final int RIGHT    = 2;
    static final int TOP      = 3;
    static final int BOTTOM   = 4;

    Point centre;

    private Vector corner, vertical, horizontal;

    private float w, h;

    HitBox(float w, float h, Point centre) {
        setWidth(w);
        setHeight(h);
        update(centre);
    }

    private void setPoints() {
        up      = new Point(
            centre.getX(), centre.getY() - getHeight()/2
        );
        down    = new Point(
            centre.getX(), centre.getY() + getHeight()/2
        );
        left    = new Point(
            centre.getX() - getWidth()/2, centre.getY()
        );
        right   = new Point(
            centre.getX() + getWidth()/2, centre.getY()
        );

        // ------------------ //
        upleft      = new Point(
            left.getX(), up.getY()
        );
        upright     = new Point(
            right.getX(), up.getY()
        );
        downleft    = new Point(
            left.getX(), down.getY()
        );
        downright   = new Point(
            right.getX(), down.getY()
        );
        // ------------------ //
    }

    private void setVectors() {
        corner      = new Vector(centre, downright);
        horizontal  = new Vector(centre, right);
        vertical    = new Vector(centre, down);
    }

    public float getWidth() {
        return w;
    }

    public float getHeight() {
        return h;
    }

    public void setWidth(float w) {
        this.w = w;
    }

    public void setHeight(float h) {
        this.h = h;
    }

    public void setCentre(Point centre) {
        this.centre = centre;
    }

    public float getDistance(HitBox hb) {
        // r(t) = min(R, w*abs(sec(t)), h*abs(csc(t))
        return new Vector(centre, hb.centre).getLength();
    }

    public boolean[] hit(HitBox shape) {
        boolean[] side = new boolean[5];

        // hit  -    left    -    right    -    top    -    bottom
        side[HIT] = side[LEFT] = side[RIGHT] = side[TOP] = side[BOTTOM] = false;

        // temporary variables to set edges for testing
        float testX = centre.getX();
        float testY = centre.getY();

        // which edge is closest?
        if (centre.getX() < shape.centre.getX()) {
            side[LEFT] = true;
            testX = shape.centre.getX();                                    // test left edge
        } else if (centre.getX() > shape.centre.getX() + shape.getWidth()) {
            side[RIGHT] = true;
            testX = shape.centre.getX() + shape.getWidth();                 // right edge
        }
        if (centre.getY() < shape.centre.getY()) {
            side[TOP] = true;
            testY = shape.centre.getY();                                    // top edge
        } else if (centre.getY() > shape.centre.getY() + shape.getHeight()) {
            side[BOTTOM] = true;
            testY = shape.centre.getY() + shape.getHeight();                // bottom edge
        }

        // get distance from closest edges
        float distX = centre.getX() - testX;
        float distY = centre.getY() - testY;
        float distance = sqrt( (distX*distX) + (distY*distY) );

        // if the distance is less than the radius, collision!
        if (distance <= getWidth()/2) {
            side[HIT] = true;
        }
        return side;
    }

    public void update(Point centre) {
        setCentre(centre);
        setPoints();
        setVectors();
    }
}
class Line extends Point {

    HitBox hb;
    Velocity velocity;

    Line() {
        super(width/2, height - 65);
        velocity = new Velocity(35, 0);
        hb = new HitBox(100, 5, this);
        restore();
    }

    private void initPos() {
        setX(width/2); setY(height - 65);
        hb.update(this);
    }

    private void move(float x) {
        moveX(x);
    }

    private void moveTo(float x) {
        setX(x);
    }

    public void restore() {
        initPos();
    }

    public float getWidth() {
        return hb.getWidth();
    }

    public float getHeight() {
        return hb.getHeight();
    }

    public void update(int k){
        switch(k){
          case 37:
          if(hb.upleft.getX() > 0)
            move(-1*velocity.getXVelocity());
          break;
          case 39:
          if(hb.upright.getX() < width)
            move(velocity.getXVelocity());
          break;
          default:
          break;
        }
        hb.update(this);
    }

    public void update() {
        if (getX() > 0 + hb.getWidth()/2 && getX() < width - hb.getWidth()/2)
            moveTo(mouseX);

        hb.update(this);
    }

    public void show(){
        fill(255);
        rectMode(CENTER);
        rect(getX(), getY(), getWidth(), getHeight());
    }
}
class Player {
    String name;
    private float score;
    private byte lives;

    Player(String s){
        name=s;
        score=0;
        lives=30;
    }

    public void show(){
        fill(255);
        text(name + " : " + score, width-80, height-40);
        text("lives: " + lives, width-80, height-30);
    }

    public boolean lost(){
        return lives == 0;
    }

    public void looseLives() {
        if (lives > 0)
            lives -= 1;
    }

    public void addPoints() {
        addPoints(100);
    }

    public void addPoints(float points) {
        score += points;
    }
}
class Point extends Coordinate {
    Point(float x, float y) {
        super(x, y);
    }

    public float distance(Point b) {
        return sqrt(
            (b.getX() - this.getX())
          * (b.getX() - this.getX())
          + (b.getY() - this.getY())
          * (b.getY() - this.getY())
        );
    }
}
class Vector extends Coordinate {

    private float mod;
    private Point a, b;

    Vector(float x, float y) {
        super(x, y);
    }

    Vector(Point a, Point b) {
        super(b.getX() - a.getX(), b.getY() - a.getY());
        mod = abs(a.distance(b));
    }

    Vector(Point o, float angle, float modulus) {
        super((modulus * cos(angle)) - o.getX(), (modulus * sin(angle)) - o.getY());
    }

    public float getLength() {
        return mod;
    }

    public Point getOrigin() {
        return a;
    }

    public Point getEnd() {
        return b;
    }

    public float getAngle(Vector v) {
        return acos(cos(scalarMult(v) / (getLength() * v.getLength())));
    }

    private float scalarMult(Vector v) {
        return (getOrigin().getX() * getEnd().getX())
             + (v.getOrigin().getY() * v.getEnd().getY());
    }

    private float vectorialMult(Vector v) {
        return 0;
    }

    public void changeWayX() {
        setX(getX() * -1);
    }

    public void changeWayY() {
        setY(getY() * -1);
    }
}
class Velocity {
    private float x, y;

    Velocity(float x, float y) {
        setVelocity(x, y);
    }

    Velocity() {
        this(1, 1);
    }

    public void setVelocity(float x, float y) {
         setXVelocity(x); setYVelocity(y);
    }

    public void setXVelocity(float x) {
        this.x = x;
    }

    public void setYVelocity(float y) {
        this.y = y;
    }

    public float getXVelocity() {
        return x;
    }

    public float getYVelocity() {
        return y;
    }

    public void faster(float times) {
        setVelocity(x + times, y + times);
    }

    public void slower(float times) {
        if (x - times > 0 && y - times > 0)
            setVelocity(x - times, y - times);
    }

    public void stop() {
        setVelocity(0, 0);
    }
}
  public void settings() {  size(1000, 720);  smooth(2); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Blocker" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}

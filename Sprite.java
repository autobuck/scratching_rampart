/* Sprite.java
 * Scratching  -- Scratch for Processing
 *
 * This file seeks to implement Scratch blocks and sprites in
 * Processing, in order to facilitate a transition from Scratch
 * into p.
 * See: http://wiki.scratch.mit.edu/wiki/Blocks
 *
 * Sound blocks are NOT included (for sanity's sake). 
 * Data & list blocks are eradicated - use variables instead!
 *
 * Points are stored in the 'PVector' type because Processing
 * contains built-in functions for accessing and manipulating such
 * objects.
 *
 * Avoid changing this file in any way! Do not use the Sprite class!
 * Instead, make a new tab and make a new .java file with a new name
 * such as Player.java
 * 
 * Add the following double-slashed //code, uncomment it, and adapt for your needs.
 * 
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;
import java.util.ArrayList;
import processing.core.PGraphics;
import java.util.Arrays; 

public class Player extends Sprite {
  Player (PApplet parent, Stage stage) {
    super(parent, stage); // super invokes the Sprite's own constructor (set up code)
  }
}

 */

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.core.PGraphics;
import java.util.ArrayList;


public class Sprite {

  // without this, built-in functions are broken. use p.whatever to access functionality
  public PApplet p;
  static int rotationStyle_360degrees=0;
  static int rotationStyle_leftRight=1;
  static int rotationStyle_dontRotate=2;
  public int rotationStyle;
  public int costumeNumber, numberOfCostumes;
  public float size; 
  public boolean visible;
  public float ghostEffect;
  public float colorEffect;
  public float brightnessEffect;
  public float saturationEffect;
  public ArrayList<PImage> costumes = new ArrayList<PImage>();
  public PVector pos = new PVector(0, 0);
  ArrayList <PGraphics> trails;
  boolean[] keys = new boolean[256];

  public boolean remove = false;

  // pen related variables
  public boolean penDown;
  public PGraphics pen;
  boolean localpen = false;

  // say/text variables
  public PGraphics dialog;
  public boolean speaking = false;
  int sayMargin = 10;
  int lineHeight = 25;
  float sayX = 0;
  float sayY = 0;
  float sayWidth = 0;
  int numberOfLines = 0;
  float sayHeight = 0;
  int dialogEndTime = -1;
  // add more variables (such as "int health") below to extend the Sprite's capabilities
  
  /* DIRECTION IS IN DEGREES! any math will require conversion.
   * This for end-user simplicity.
   * Use degrees() to convert to degrees; radians() to convert to
   * radians.
   */
  public float direction = 0;
  Sprite (PApplet parent,Stage stage,boolean[] parent_keys) {
    p = parent;
    costumeNumber=0;
    visible = true;
    numberOfCostumes=0;
    size=100;
    rotationStyle=rotationStyle_360degrees;
    ghostEffect = 0;
    colorEffect = 0;
    brightnessEffect = 0;
    saturationEffect = 0;
    p.imageMode(p.CENTER);
    drawOnStage(stage);
    renderOnStage(stage);
    keys = parent_keys;
 }
 
   public void renderOnStage(Stage stage) {
    trails = stage.trails;
    //localpen = false;
  }

  /* ==== Drawing ====
   * 
   * In order to draw sprites to the screen, each one will have
   * an Image object. This image object can be individually
   * manipulated during the program.
   *
   * The .update() function must be called for all sprites.
   * It may be easiest to store sprites in an array of Sprites,
   * and looping through the array to redraw all sprites.
   */

  public void draw() {
    stamp(pos.x, pos.y);
    if (speaking) {
      if (trails.size() > 0)  trails.get(trails.size()-1).pushMatrix();
      else p.pushMatrix();
      float xMod = 0-50;
      if (size>100) xMod = 0-(50*(size/100)); //0-(costumes.get(costumeNumber).width)*(size/100);
      float yMod = 0-(sayHeight+((costumes.get(costumeNumber).height/2)*(size/100)))-30;
      if (xMod+pos.x < 0) xMod += p.abs(0-(xMod+pos.x));
      if (xMod+pos.x+sayWidth+(sayMargin*2) > p.width) xMod -= p.abs(p.width-(xMod+pos.x+sayWidth+(sayMargin*2)));
      if (yMod+pos.y < 0) yMod += p.abs(0-(yMod+pos.y));
      if (yMod+pos.y+sayHeight+(sayMargin*2) > p.width) yMod -= p.abs(p.width-(yMod+pos.y+sayHeight+(sayMargin*2)));
      if (trails.size() > 0) {
        trails.get(trails.size()-1).translate(pos.x, pos.y); // move Sprite to x,y position
        trails.get(trails.size()-1).image(dialog.get(0, 0, p.width, p.height), p.width/2+xMod, p.height/2+yMod);
      } else {
        p.translate(pos.x, pos.y); // move Sprite to x,y position
        p.image(dialog.get(0, 0, p.width, p.height), p.width/2+xMod, p.height/2+yMod);
      }
      if (dialogEndTime != -1 && dialogEndTime < p.millis()) { 
        speaking = false; 
        dialogEndTime = -1;
      }
      if (trails.size() > 0)  trails.get(trails.size()-1).popMatrix();
      else p.popMatrix();
    }
  }

  public void stamp(float x, float y) {
    PImage costumeToDraw = p.createImage(costumes.get(costumeNumber).width, costumes.get(costumeNumber).height, p.ARGB);
    costumeToDraw.loadPixels();
    for (int i = 0; i < costumes.get (costumeNumber).pixels.length; i++) {
      costumeToDraw.pixels[i] = costumes.get(costumeNumber).pixels[i];
    }
    costumeToDraw.updatePixels();

    p.pushMatrix(); // save old visual style for other sprites
    p.translate(x, y); // move Sprite to x,y position
    if (localpen) p.image(pen.get(0, 0, p.width, p.height), p.width/2-pos.x, p.height/2-pos.y);
    if (visible) {
      // adjust hue for colorEffect
      if (colorEffect != 0) {
        //costumeToDraw.loadPixels();
        p.colorMode(p.HSB);
        for (int i = 0; i < costumeToDraw.pixels.length-1; i++) {
          int newColor = costumeToDraw.pixels[i];
          float mappedColorEffect = p.map(colorEffect % 100, 100, 0, 0, 255);
          float newHue = p.hue(newColor)+(mappedColorEffect);
          if (newColor != 0) newColor = p.color(newHue, p.saturation(newColor), p.brightness(newColor));
          costumeToDraw.pixels[i] = newColor;
        }
        costumeToDraw.updatePixels();
        p.colorMode(p.RGB);
      }
      // adjust exposure for brightnessEffect (- works good, + not so much)
      if (brightnessEffect != 0) {
        if (brightnessEffect < 0) { // decrease brightness
          p.colorMode(p.HSB);
          //costumeToDraw.loadPixels();
          for (int i = 0; i < costumeToDraw.pixels.length-1; i++) {
            int newColor = costumeToDraw.pixels[i];
            float mappedBright = p.map(brightnessEffect, -100, 100, -255, 255);
            float newBrightness = p.brightness(newColor)+(mappedBright);
            if (newColor != 0) newColor = p.color(p.hue(newColor), p.saturation(newColor), newBrightness);
            costumeToDraw.pixels[i] = newColor;
          }
          costumeToDraw.updatePixels();
          p.colorMode(p.RGB);
        } else { // increase brightness with RGB, HSB works less well
          //costumeToDraw.loadPixels();
          for (int i = 0; i < costumeToDraw.pixels.length-1; i++) {
            float mappedBright = p.map(brightnessEffect, -100, 100, -255, 255);
            int newColor = costumeToDraw.pixels[i];
            float newRed = p.red(newColor)+(mappedBright);
            float newGreen = p.green(newColor)+(mappedBright);
            float newBlue = p.blue(newColor)+(mappedBright);
            p.constrain(newRed, 0, 255);
            p.constrain(newGreen, 0, 255);
            p.constrain(newBlue, 0, 255);
            if (newColor != 0) newColor = p.color(newRed, newGreen, newBlue);
            costumeToDraw.pixels[i] = newColor;
          }
          costumeToDraw.updatePixels();
        }
      }
      // adjust saturation for saturationEffect. again, - is good, + not so much.
      if (saturationEffect != 0) {
        p.colorMode(p.HSB, 255);
        for (int i = 0; i < costumeToDraw.pixels.length-1; i++) {
          int newColor = costumeToDraw.pixels[i];
          float mappedSaturation = p.map(saturationEffect, -100, 100, -255, 255);
          float newSaturation = p.saturation(newColor)+(mappedSaturation);
          if (newColor != 0) newColor = p.color(p.hue(newColor), newSaturation, p.brightness(newColor));
          costumeToDraw.pixels[i] = newColor;
        }
        costumeToDraw.updatePixels();
        p.colorMode(p.RGB);
      }
      // apply "ghost effect" to fade Sprite
      if (ghostEffect > 0) {
        int calculatedAlpha = (int)p.map(ghostEffect, 100, 0, 0, 255); // use "map" to translate 0-100 "ghostEffect" range to 255-0 "alpha" range        
        // set up alpha mask
        int[] alpha = new int[costumeToDraw.width*costumeToDraw.height];
        for (int i=0; i<alpha.length; i++) {
          // only fade non-zero pixels; 0 is full-transparency
          if (costumeToDraw.pixels[i]!=0) alpha[i]=calculatedAlpha;
        }
        costumeToDraw.mask(alpha);
      }
      // finally, draw adjusted image
      if (trails.size() >= 1) {
        trails.get(trails.size()-1).pushMatrix();
        trails.get(trails.size()-1).translate(x, y); // move Sprite to x,y position
        if (rotationStyle==rotationStyle_360degrees) trails.get(trails.size()-1).rotate(p.radians((-direction)+90));
        if (((direction%360<=270) & (direction%360>=90)) & rotationStyle==rotationStyle_leftRight) trails.get(trails.size()-1).scale(-1.0f, 1.0f);
        trails.get(trails.size()-1).imageMode(p.CENTER);
        trails.get(trails.size()-1).image(costumeToDraw, 0, 0, costumeToDraw.width*(size/100), costumeToDraw.height*(size/100));
        trails.get(trails.size()-1).popMatrix();
      } else {
        if (rotationStyle==rotationStyle_360degrees) p.rotate(p.radians(-direction+90));
        if (((direction%360<=270) & (direction%360>=90)) & rotationStyle==rotationStyle_leftRight) p.scale(-1.0f, 1.0f);
        //p.image(costumeToDraw, pos.x, pos.y, costumeToDraw.width*(size/100), costumeToDraw.height*(size/100));
        p.image(costumeToDraw, 0, 0, costumeToDraw.width*(size/100), costumeToDraw.height*(size/100));
      }
      //      pen.image(costumeToDraw, 0, 0, costumeToDraw.width*(size/100), costumeToDraw.height*(size/100));
    }
    p.popMatrix(); // restore default visual style
    // now add dialog layer if Sprite is "speaking"
  }

  // set visual effects
  public void setGhostEffect(int newAlpha) {
    ghostEffect = newAlpha;
  }
  public void setColorEffect(int newModifier) {
    colorEffect = newModifier;
  }
  public void setBrightnessEffect(int newModifier) {
    brightnessEffect = newModifier;
  }
  public void setsaturationEffect(int newModifier) {
    saturationEffect = newModifier;
  }

  // moves sprite "distance" pixels in current direction
  public void move(float distance) {
    /* Create a new vector, representing the desired motion (angle + distance) 
     * fromAngle() makes a unit vector (length 1)
     * negative on direction is b/c processing flips the cartesian y axis
     */
    float oldX=0, oldY=0;
    if (penDown) {
      oldX = pos.x; 
      oldY = pos.y;
    }
    PVector temp = PVector.fromAngle(p.radians(-direction));
    temp.mult(distance);
    pos.add(temp);
    if (penDown) {
      pen.beginDraw();
      pen.line(oldX, oldY, pos.x, pos.y);
      pen.endDraw();
    }
  }

  // load "Scratch" cat costumes
  public void addDefaultCostumes() {
    addCostume("images/cat.costume1.png");
    addCostume("images/cat.costume2.png");
  }

  // add costume from bitmap image file
  public void addCostume(String filePath) {
    numberOfCostumes++;
    costumes.add(p.loadImage(filePath));
  }

  // change to next costume
  public void nextCostume() { 
    costumeNumber++;
    if (costumeNumber > numberOfCostumes-1) costumeNumber=0;
  }

  // change to previous costume
  public void previousCostume() {
    costumeNumber--;
    if (costumeNumber < 0) costumeNumber=numberOfCostumes-1;
  }

  // switch to specific costume
  public void setCostume(int newCostumeNumber) {
    costumeNumber=newCostumeNumber;
  }

  // set "visible" variable to make sprite appear
  public void show() {
    visible=true;
  }

  // set not visible to hide
  public void hide() {
    visible=false;
  }

  // draws a text bubble with triangle arrow indicating speaking sprite
  public void say(String what) {
    if (dialog==null) dialog = p.createGraphics(p.width, p.height);
    say(what, -1);
  }

  // draws a text bubble with triangle arrow indicating speaking sprite
  public void say(String what, int seconds) {
    if (seconds != -1) dialogEndTime = p.millis()+(seconds*1000);
    dialogCalc(what);
    dialog.beginDraw();
    dialog.clear();
    dialog.strokeWeight(2);
    dialog.fill(255);
    dialog.stroke(0);
    dialog.rect(sayX-sayMargin, sayY-sayMargin, sayWidth+(sayMargin*2), sayHeight+5, 10);
    dialog.triangle(sayX+10, sayY-sayMargin+sayHeight+5, sayX+20, sayY-sayMargin+sayHeight+5, sayX+20, sayY-sayMargin+sayHeight+20+5);
    dialog.noStroke();
    dialog.triangle(sayX+10, sayY-sayMargin+sayHeight-4+5, sayX+20, sayY-sayMargin+sayHeight-4+5, sayX+20, sayY-sayMargin+sayHeight+20-4+5);
    dialog.endDraw();
    dialogWrite(what);
  }

  // draw a text bubble with "thinking" bubbles indicating speaker
  public void think(String what) {
    if (dialog==null) dialog = p.createGraphics(p.width, p.height);
    think(what, -1);
  }

  // draw a text bubble with "thinking" bubbles indicating speaker
  public void think(String what, int seconds) {
    dialogCalc(what);
    if (seconds != -1) dialogEndTime = p.millis()+(seconds*1000);
    dialog.beginDraw();
    dialog.clear();
    dialog.strokeWeight(2);
    dialog.fill(255);
    dialog.stroke(0);
    dialog.ellipse(sayX+20, sayY-sayMargin+sayHeight+25, 7, 7);
    dialog.ellipse(sayX+13, sayY-sayMargin+sayHeight+17, 10, 10);
    dialog.ellipse(sayX+7, sayY-sayMargin+sayHeight+7, 15, 15);
    dialog.rect(sayX-sayMargin, sayY-sayMargin, sayWidth+(sayMargin*2), sayHeight+5, 10);
    dialog.endDraw();
    dialogWrite(what);
  }

  // helper function for say/think. calculates text bubble position.
  public void dialogCalc(String what) {
    sayMargin = 10;
    lineHeight = 26;
    sayX = 0-costumes.get(costumeNumber).width*(size/100); //pos.x - 50;
    sayY = 0-costumes.get(costumeNumber).height*(size/100); //pos.y - 100;
    sayWidth = 0;
    numberOfLines = 1+(int)(what.length() / 30);
    sayHeight = lineHeight*(numberOfLines);

    if (numberOfLines == 1) sayWidth = p.textWidth(what);
    else sayWidth = 250;

    if (sayX-sayMargin < 0) sayX = 0+sayMargin;
    if (sayY-sayMargin < 0) sayY = 0+sayMargin;
    if (sayX+sayWidth+sayMargin > p.width) sayX -= (sayX+sayWidth+sayMargin)-p.width;
    if (sayY+sayHeight+sayMargin > p.height) sayY -= (sayY+sayHeight+sayMargin)-p.height;
  }

  // helper function for say/think. Writes dialog in text bubble.
  public void dialogWrite(String what) {
    speaking = true;  
    dialog.beginDraw();
    dialog.fill(0);
    dialog.textAlign(p.CENTER);
    dialog.textFont(p.createFont("Helvetica", 18), 18);
    dialog.text(what, sayX, sayY-1, sayWidth, sayHeight);
    dialog.endDraw();
  }

  // turn any angle
  public void turn(float angle) {
    direction += angle;
    /*if (direction>360) direction=direction-360;
     else if (direction<0) direction=direction+360;*/
    direction = direction % 360;
  }

  // turn right
  public void turnLeft(float angle) {
    direction += angle;
    //if (direction>360) direction=direction-360;
    direction = direction % 360;
  }

  // turn left
  public void turnRight(float angle) { 
    direction -= angle;
    //if (direction<0) direction=direction+360;
    direction = direction % 360;
  }

  // point To arbitrary grid position
  public void pointToXY(int x, int y) {
    PVector targetVector;
    targetVector = new PVector(x, y);
    direction = (p.degrees(p.atan2(pos.x - (targetVector.x), pos.y - (targetVector.y))))+90;
    if (direction < 0) direction += 360;
  }

  // absolute heading
  public void pointInDirection(float angle) {
    direction = angle;
  }

  /* Sets the direction to point To another Sprite. */
  public void pointToSprite(Sprite target) {
    pointToXY((int)target.pos.x, (int)target.pos.y);
  }

  /* Same as above, but for mouse. */
  public void pointToMouse() {
    pointToXY(p.mouseX, p.mouseY);
  }

  // change location by X, Y vector  
  public void changeXY(float x, float y) {
    goToXY(pos.x+x, pos.y+y);
  }
  
  // change X by distance
  public void changeX(float x) {
    goToXY(pos.x+x,pos.y);
  }

  // change Y position by distance
  public void changeY(float y) {
    goToXY(pos.x,pos.y+y);
  }

  /* move to specific location on grid */
  public void goToXY(float x, float y) {
    float oldX, oldY;
    oldX = pos.x;
    oldY = pos.y; 
    pos.x = x; 
    pos.y = y;
    if (penDown) {
      pen.beginDraw();
      pen.line(oldX, oldY, pos.x, pos.y);
      pen.endDraw();
    }
  }

  // move to position of Sprite object
  public void goToSprite(Sprite target) { 
    goToXY(target.pos.x, target.pos.y);
  }

  // check if a Sprite is touching another Sprite using simple rectangular hit box
  public boolean touchingSprite(Sprite target) {
    if (!target.visible) return false;
    boolean touchingX, touchingY;
    PVector testVector;
    touchingX=false; 
    touchingY=false;
    testVector=new PVector(target.pos.x, pos.y);
    if (pos.dist(testVector) < ((target.costumes.get(target.costumeNumber).width*(target.size/100))/2)+(costumes.get(costumeNumber).width*(size/100))/2) {
      touchingX = true;
    }
    testVector=new PVector(pos.x, target.pos.y);
    if (pos.dist(testVector) < ((target.costumes.get(target.costumeNumber).height*(target.size/100))/2)+(costumes.get(costumeNumber).height*(size/100))/2) {
      touchingY = true;
    }
    if (touchingX & touchingY) return true;
    else return false;
  }

  // check if a sprite is touching a sprite using spherical "distance" calculation
  public boolean touchingRoundSprite(Sprite target) {
    if (!target.visible) return false;
    if (distanceToSprite(target)-(costumes.get(costumeNumber).width/2)*(size/100)-(target.costumes.get(target.costumeNumber).width/2)*(target.size/100) <= 0 ) return true;
    else return false;
  }

  // return distance to arbitrary grid position  
  public float distanceToXY(float x, float y) { 
    PVector temp = new PVector(x, y);
    return pos.dist(temp);
  }

  // return distance to arbitrary grid position  
  public float distanceToMouse() { 
    /*PVector temp = new PVector(p.mouseX, p.mouseY);
     return pos.dist(temp);*/
    return distanceToXY(p.mouseX, p.mouseY);
  }

  // return distance to Sprite object
  public float distanceToSprite(Sprite target) { 
    return distanceToXY((int)target.pos.x, (int)target.pos.y);
  }

  // causes the Sprite to "wrap" from the left to right, bottom to top, etc, when off-stage
  void wrapAtEdges() {
    if (pos.x>p.width) pos.x -= p.width;
    if (pos.x<0) pos.x += p.width;
    if (pos.y>p.height) pos.y -= p.height;
    if (pos.y<0) pos.y += p.height;
  }  

  // returns X and Y difference for a given movement speed, for the current Sprite's direction 
  PVector vectorForSpeed(float distance) {
    PVector i = PVector.fromAngle(p.radians(-direction));
    PVector j = new PVector(pos.x, pos.y);
    i.mult(distance);
    j.add(i);
    j.x = j.x - pos.x;
    j.y = j.y - pos.y;
    return j;
  }

  // conversely, returns a direction for given X and Y vector 
  float directionForVector(float x, float y) {
    return directionToXY(pos.x+x, pos.y+y);
  }

  // will return "move" speed of given x, y vector
  float speedForVector(float x, float y) {
    return distanceToXY(pos.x+x, pos.y+y);
  }

  // returns direction pointing towards given X, Y coordinates
  public float directionToXY(float x, float y) {
    PVector targetVector;
    targetVector = new PVector(x, y);
    float a = (p.degrees(p.atan2(pos.x - (targetVector.x), pos.y - (targetVector.y))))+90;
    if (a < 0) a += 360;
    return a;
  }

  // returns direction pointing towards Sprite's position
  float directionToSprite(Sprite target) {
    /*PVector temp = new PVector(target.pos.x,target.pos.y);
     float a = (p.degrees(p.atan2(pos.x - (target.pos.x), pos.y - (target.pos.y))))+90;
     if (a < 0) a += 360;
     return a;*/
    return directionToXY(target.pos.x, target.pos.y);
  }

  float directionToMouse() {
    return directionToXY(p.mouseX,p.mouseY);
  }

  boolean withinSightRange(Sprite target, float range) {
    float directionTo = directionToSprite(target); //direction to other sprite
    float diff = p.abs(directionTo-direction);
    if (diff < 0+(range/2) || diff > 360-(range/2)) return true;
    else return false;
  }

  // return "true" if target is ahead of Sprite
  boolean facingSprite(Sprite target) {
    float directionTo = directionToSprite(target); //direction to other sprite
    float diff = p.abs(directionTo-direction);
    float otherDirection = p.abs(direction-360);
    if (diff < 90 || diff > 270) return true;
    else return false;
  }


  // check if target is within 180 degrees on right side of Sprite
  boolean seesSpriteOnRight(Sprite target) {
    float directionTo = directionToSprite(target); //direction to other sprite
    float diff = p.abs(directionTo-direction);
    if (direction > directionTo && diff < 180 || direction < directionTo && diff > 180) return true;
    else return false;
  }

  // check if target is within 180 degrees on left side of Sprite
  boolean seesSpriteOnLeft(Sprite target) {
    float directionTo = directionToSprite(target); //direction to other sprite
    float diff = p.abs(directionTo-direction);
    if (direction < directionTo && diff < 180 || direction > directionTo && diff > 180) return true;
    else return false;
  }

  // ***** Pen Actions *************************
  // * The Pen uses a PGraphics object to render art on a canvas beneath the Sprite.
  // * The Pen *must* be attached to a PGraphics canvas using drawOnStage(Stage) or drawOwnCanvas()

  // attaches "pen" actions to a Stage's canvas. This canvas is rendered on top of the Stage during
  // Stage.draw() and is shared between all Sprites which are attached to the Stage.
  public void drawOnStage(Stage stage) {
    pen = stage.pen;
    localpen = false;
  }

  // This sets up the pen to draw on a unique canvas attached to the Sprite.
  // It is not shared between Sprites. Each new canvas adds a little lag, so use this sparingly!
  public void drawOwnCanvas() {
    pen = p.createGraphics(p.width, p.height);
    localpen = true;
  }

  // sets the pen color using RGB values
  public void penColor(int r, int g, int b) {
    pen.beginDraw();
    pen.stroke(p.color(r, g, b));
    pen.endDraw();
  }

  // sets with width of the pen line
  public void penWidth(int penWidth) {
    pen.beginDraw();
    pen.strokeWeight(penWidth);
    pen.endDraw();
  }

  // erases the pen layer
  public void penClear() {
    pen.beginDraw();
    pen.clear();
    pen.endDraw();
  }

  public void penUp() {
    penDown = false;
  }

  public void penDown() {
    penDown = true;
  }
  
  
  // next 10 functions for touchingEdge/offStage detection
  public boolean touchingEdge() {
    if (pos.x + (costumes.get(costumeNumber).width/2) > p.width
        || pos.x - (costumes.get(costumeNumber).width/2) < 0
        || pos.y + (costumes.get(costumeNumber).height/2) > p.height
        || pos.y - (costumes.get(costumeNumber).height/2) < 0) return true;
        else return false;
  }
  
  public boolean touchingTopEdge() {
    if (pos.y - (costumes.get(costumeNumber).height/2) < 0) return true;
    else return false;
  }
  
  public boolean touchingBottomEdge() {
    if (pos.y + (costumes.get(costumeNumber).height/2) > p.height) return true;
    else return false;
  }
  
  public boolean touchingLeftEdge() {
    if (pos.x - (costumes.get(costumeNumber).width/2) < 0) return true;
    else return false;
  }
  
  public boolean touchingRightEdge() {
    if (pos.x + (costumes.get(costumeNumber).width/2) > p.width) return true;
    else return false;
  }
  
  public boolean isOffStage() {
    if (pos.x - (costumes.get(costumeNumber).width/2) > p.width
        || pos.x + (costumes.get(costumeNumber).width/2) < 0
        || pos.y - (costumes.get(costumeNumber).height/2) > p.height
        || pos.y + (costumes.get(costumeNumber).height/2) < 0) return true;
        else return false;
  }
  
    public boolean isOffStageTop() {
    if (pos.y + (costumes.get(costumeNumber).height/2) < 0) return true;
    else return false;
  }
  
  public boolean isOffStageBottom() {
    if (pos.y - (costumes.get(costumeNumber).height/2) > p.height) return true;
    else return false;
  }
  
  public boolean isOffStageLeft() {
    if (pos.x + (costumes.get(costumeNumber).width/2) < 0) return true;
    else return false;
  }
  
  public boolean isOffStageRight() {
    if (pos.x - (costumes.get(costumeNumber).width/2) > p.width) return true;
    else return false;
  }
}

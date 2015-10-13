
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;
import java.util.ArrayList;
import processing.core.PGraphics;
import java.util.Arrays; 

public class cannonBall extends Sprite {

  float targetX = 0;
  float targetY = 0;
  float targetDistance = 0;
  
  cannonBall (PApplet parent, Stage stage) {
    super(parent, stage);
    size = 5;
  }
}


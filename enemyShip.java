
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;
import java.util.ArrayList;
import processing.core.PGraphics;
import java.util.Arrays; 

public class enemyShip extends Sprite {
  int firingTimeout = 30;
  float course = p.random(-1,1);
  enemyShip (PApplet parent, Stage stage) {
    super(parent, stage);
    addCostume("images/ship.png");
    addCostume("images/ship-fire.png");
    direction = 180;
    size = 75;
    firingTimeout = 30+(int)p.random(0,30);
  }
}


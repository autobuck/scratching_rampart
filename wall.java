
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;
import java.util.ArrayList;
import processing.core.PGraphics;
import java.util.Arrays; 

public class wall extends Sprite {
  int gridX = 0;
  int gridY = 0;
  
  wall (PApplet parent, Stage stage) {
    super(parent, stage);
    addCostume("images/wall-32px.png");
    addCostume("images/brokenwall-32px.png");
    costumeNumber = 0;
  }
  
  public void nextCostume() {
    if (costumeNumber==0) previousCostume(); 
  }
}


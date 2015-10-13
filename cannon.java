
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;
import java.util.ArrayList;
import processing.core.PGraphics;
import java.util.Arrays; 

public class cannon extends Sprite {
  public int firingTimeout = 0;
  public boolean enabled = true;
  public int gridX = 0;
  public int gridY = 0;
  
  cannon (PApplet parent, Stage stage) {
    super(parent, stage);
  }
}


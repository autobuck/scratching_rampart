/* Stage.java
 * Scratching  -- Scratch for Processing
 *
 * This file seeks to implement Scratch blocks and sprites in
 * Processing, in order to facilitate a transition from Scratch
 * into p.
 * See: http://wiki.scratch.mit.edu/wiki/Blocks
 *
 * This Stage class has just a few simple functions for handling
 * the background. 
 *
 * switchToBackdrop(#); can replace the background(#);
 * command at the top of your draw() loop.
 *
 * The backdrop size should match y/* Stage.java
 * Scratching  -- Scratch for Processing
 *
 * This file seeks to implement Scratch blocks and sprites in
 * Processing, in order to facilitate a transition from Scratch
 * into p.
 * See: http://wiki.scratch.mit.edu/wiki/Blocks
 *
 * This Stage class has just a few simple functions for handling
 * the background. 
 *
 * switchToBackdrop(#); can replace the background(#);
 * command at the top of your draw() loop.
 *
 * The backdrop size should match your stage size.
 * Who knows what might happen if it does not?!
 *
 */

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;
import java.util.ArrayList;
import processing.core.PGraphics;
import java.util.Arrays; 

public class Stage {

  // without this, built-in functions are broken. use p.whatever to access functionality
  PApplet p;

  // listing our backgrounds here lets us access them by name instead of number in our main program
  // ie, switchToBackdrop(bg_title); instead of switchToBackdrop(1).
  //
  // You may use your own art for your own project by adding PNG or JPG art to the file folder,
  // and changing the "addDefaultBackdrops()" function below.
  // 
  // Use Stage.addDefaultBackdrop(); for the the X/Y grid, for debugging movement
  public int backdropNumber, numberOfBackdrops;
  public ArrayList<PImage> backdrops = new ArrayList<PImage>();
  ArrayList <Float> timers = new ArrayList<Float>();
  int scrollX, scrollY;
  public PGraphics pen;
  ArrayList <PGraphics> trails = new ArrayList<PGraphics>();

  boolean askingQuestion = false;
  String question = "What is your quest?";
  String questionText = "";
  String theAnswer = "";
  PFont questionFont;
  int fadeColor = 0;

  Stage (PApplet parent) {
    p = parent;
    backdropNumber=0;
    numberOfBackdrops=0;
    scrollX = 0; 
    scrollY = 0;
    pen = p.createGraphics(p.width, p.height);
    questionFont = p.createFont("Helvetica", 18); 
    p.textFont(questionFont, 18);
    p.imageMode(p.CENTER);
    addTimer();
  }

  public void addTimer() {
    float temp = p.millis();
    float t2 = temp/1000;
    timers.add(t2);
  }

  // the timer returns seconds, in whole numbers (integer)
  public float timer() {
    float temp = p.millis();
    float t2 = temp/1000;
    return t2-timers.get(0);
  } 

  public float timer(int timerNumber) {
    float temp = p.millis();
    float t2 = temp/1000;
    return t2-timers.get(timerNumber);
  } 

  // reset the stage timer
  public void resetTimer() {
    float temp = p.millis();
    float t2 = temp/1000;
    timers.set(0, t2);
  }

  // reset the extra timers
  public void resetTimer(int number) {
    float temp = p.millis();
    float t2 = temp/1000;
    timers.set(number, t2);
  }


  public void drawTiled() {
    p.pushMatrix();
    p.imageMode(p.CORNER);
    int x = 0;
    int y = 0;
    while (x < p.width) {
      y = 0;
      while (y < p.height) {
        p.image(backdrops.get(backdropNumber), x, y, backdrops.get(backdropNumber).width, 
        backdrops.get(backdropNumber).height);
        y += backdrops.get(backdropNumber).height;
      }
      x += backdrops.get(backdropNumber).width;
    }
    p.image(pen.get(0, 0, p.width, p.height), 0, 0);
    p.imageMode(p.CENTER);
    p.popMatrix();
  }

  public void draw() {    
    int scrollXmod = scrollX % p.width;
    int scrollYmod = scrollY % p.height;
    // current logic doesn't check direction of scroll & draws unnecessary off-screen backdrops!
    if ( (scrollXmod) != 0 && (scrollYmod) == 0) {
      // scrolling X only. draw stages Y center
      p.image(backdrops.get(backdropNumber), (p.width/2)+scrollXmod, (p.height/2), backdrops.get(backdropNumber).width, 
      backdrops.get(backdropNumber).height);
      // for scrolling right, draw to the left of stage
      p.image(backdrops.get(backdropNumber), 0-(p.width/2)+scrollXmod, (p.height/2), backdrops.get(backdropNumber).width, 
      backdrops.get(backdropNumber).height);
      // for scrolling left, draw to the right of stage
      p.image(backdrops.get(backdropNumber), p.width+(p.width/2)+scrollXmod, (p.height/2), backdrops.get(backdropNumber).width, 
      backdrops.get(backdropNumber).height);
    } else if ( (scrollXmod) == 0 && (scrollYmod) != 0) {
      // scrolling Y only. draw center stage
      p.image(backdrops.get(backdropNumber), (p.width/2), (p.height/2)-scrollYmod, backdrops.get(backdropNumber).width, 
      backdrops.get(backdropNumber).height);
      // for scrolling right, draw to the left of stage
      p.image(backdrops.get(backdropNumber), (p.width/2), 0-(p.height/2)-scrollYmod, backdrops.get(backdropNumber).width, 
      backdrops.get(backdropNumber).height);
      // for scrolling left, draw to the right of stage
      p.image(backdrops.get(backdropNumber), (p.width/2), p.height+(p.height/2)-scrollYmod, backdrops.get(backdropNumber).width, 
      backdrops.get(backdropNumber).height);
    } else if ( (scrollXmod) != 0 && (scrollYmod) != 0) {
      //*************** scrolling X and Y. draw stage Y top
      p.image(backdrops.get(backdropNumber), (p.width/2)+scrollXmod, (p.height/2)-scrollYmod, backdrops.get(backdropNumber).width, 
      backdrops.get(backdropNumber).height);
      // for scrolling right, draw to the left of stage
      p.image(backdrops.get(backdropNumber), 0-(p.width/2)+scrollXmod, (p.height/2)-scrollYmod, backdrops.get(backdropNumber).width, 
      backdrops.get(backdropNumber).height);
      // for scrolling left, draw to the right of stage
      p.image(backdrops.get(backdropNumber), p.width+(p.width/2)+scrollXmod, (p.height/2)-scrollYmod, backdrops.get(backdropNumber).width, 
      backdrops.get(backdropNumber).height);
      //********** scrolling X and Y. draw center stages, 
      p.image(backdrops.get(backdropNumber), (p.width/2)+scrollXmod, 0-(p.height/2)-scrollYmod, backdrops.get(backdropNumber).width, 
      backdrops.get(backdropNumber).height);
      // for scrolling right, draw to the left of stage
      p.image(backdrops.get(backdropNumber), 0-(p.width/2)+scrollXmod, 0-(p.height/2)-scrollYmod, backdrops.get(backdropNumber).width, 
      backdrops.get(backdropNumber).height);
      // for scrolling left, draw to the right of stage
      p.image(backdrops.get(backdropNumber), p.width+(p.width/2)+scrollXmod, 0-(p.height/2)-scrollYmod, backdrops.get(backdropNumber).width, 
      backdrops.get(backdropNumber).height);
      //********** scrolling X and Y. draw bottom stages 
      p.image(backdrops.get(backdropNumber), (p.width/2)+scrollXmod, p.height+(p.height/2)-scrollYmod, backdrops.get(backdropNumber).width, 
      backdrops.get(backdropNumber).height);
      // for scrolling right, draw to the left of stage
      p.image(backdrops.get(backdropNumber), 0-(p.width/2)+scrollXmod, p.height+(p.height/2)-scrollYmod, backdrops.get(backdropNumber).width, 
      backdrops.get(backdropNumber).height);
      // for scrolling left, draw to the right of stage
      p.image(backdrops.get(backdropNumber), p.width+(p.width/2)+scrollXmod, p.height+(p.height/2)-scrollYmod, backdrops.get(backdropNumber).width, 
      backdrops.get(backdropNumber).height);
    } else {
      p.image(backdrops.get(backdropNumber), (p.width/2), (p.height/2), backdrops.get(backdropNumber).width, 
      backdrops.get(backdropNumber).height);
    }
    p.image(pen.get(0, 0, p.width, p.height), (p.width/2), (p.height/2));
    if (trails.size()>0) drawTrails();
  }

  public void drawTrails() {
    //    p.image(trails.get(0).get(0,0,p.width,p.height), (p.width/2), (p.height/2));
    //render each
    for (int i = 0; i < trails.size (); i++) {
      p.image(trails.get(i).get(0, 0, p.width, p.height), (p.width/2), (p.height/2));
    }

    // remove 1 and refresh new top layer
    int trailRate = 1;
    if (p.frameCount % trailRate == 0) {
      trails.remove(0);
      trails.add(p.createGraphics(p.width, p.height));
      // fade out older layers
      if (trails.size() > 1) {
        for (int i = 0; i < trails.size ()-1; i++) {
          trails.get(i).beginDraw();
          trails.get(i).pushStyle();
          trails.get(i).noStroke();
          trails.get(i).fill(fadeColor, 100/trails.size() );
          trails.get(i).rect(0, 0, p.width, p.height);
//          trails.get(i).tint(255,120);
          trails.get(i).popStyle();
          trails.get(i).endDraw();
        }
      }
    } else {
      trails.get(trails.size()-1).clear();
    }
  }

  public void addTrail() {
    trails.add(p.createGraphics(p.width, p.height));
  }

  public void addTrails(int number) {
    for (int i = 0; i < number; i++) {
      addTrail();
    }
  }

  public void setTrails(int number) {
    if (number > trails.size() ) {
      for (int i = trails.size (); i < number; i++) {
        addTrail();
      }
    } else if (number < trails.size() ) {
      do {
        trails.remove(0);
      } 
      while (trails.size () > number);
    }
  }

  public void removeTrail() {
    if (trails.size() > 0) trails.remove(0);
  }

  // load xy grid as backdrop 0
  public void addDefaultBackdrop() {
    addBackdrop("images/xy-grid.png");
  }


  // add costume from bitmap image file
  public void addBackdrop(String filePath) {
    numberOfBackdrops++;
    backdrops.add(p.loadImage(filePath));
  }

  // change to next backdrop
  public void nextBackdrop() { 
    backdropNumber++;
    if (backdropNumber > numberOfBackdrops + 1) backdropNumber=0;
  }

  // change to previous backdrop
  public void previousBackdrop() {
    backdropNumber--;
    if (backdropNumber < 0) backdropNumber=backdropNumber;
  }

  // switch to specific costume
  public void setBackdrop(int newBackdropNumber) {
    backdropNumber=newBackdropNumber;
  }

  // "scrolls" backdrop in any direction. Backdrop repeats.  
  public void scrollBackdrop(float x, float y) {
    scrollX += x;
    scrollY += y;
  }

  public void questionKeycheck() {
    if (p.key != p.CODED) {
      if (p.key==p.BACKSPACE)
        questionText = questionText.substring(0, p.max(0, questionText.length()-1));
      else if (p.key==p.TAB)
        questionText += "    ";
      else if (p.key==p.ENTER|p.key==p.RETURN) {
        theAnswer = questionText;
        questionText="";
        askingQuestion = false;
      } else if (p.key==p.ESC|p.key==p.DELETE) {
      } else questionText += p.key;
    }
  }

  public String answer() {
    String finalResponse;
    if (theAnswer!="") { 
      finalResponse=theAnswer; 
      return finalResponse;
    } else return "";
  }

  public void ask(String newQuestion) {
    drawQuestionText();
    theAnswer = "";
    askingQuestion = true;
    question = newQuestion;
  }

  public void drawQuestionText() {
    p.pushStyle();
    p.stroke(0);
    p.fill(0, 125, 175);
    p.rect(20, p.height-65, p.width-40, 45, 15);
    p.fill(255);
    p.rect(23, p.height-62, p.width-46, 40, 15);
    p.fill(0, 0, 0);
    p.textFont(questionFont, 18);
    p.text(question+" "+questionText+(p.frameCount/10 % 2 == 0 ? "_" : ""), 30, p.height-35);
    p.popStyle();
  }
}


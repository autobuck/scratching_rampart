// these values are "static," which means they do not change.
static int rotationStyle_360degrees=0;
static int rotationStyle_leftRight=1;
static int rotationStyle_dontRotate=2;
static int upArrow=38;
static int downArrow=40;
static int leftArrow=37;
static int rightArrow=39;
// These arrays contain boolean values for each key and arrow key
boolean[] keys = new boolean[256];

// User variables and objects are "declared", or listed, here.
// Our sample includes one Stage object, called stage, and one Sprite object, called cat.
Stage stage;
Sprite cat;

// Setup runs once, and sets some default values
void setup() {
  // first, create a Processing window 500 px by 500 px
  size(500, 500);
  // next, initialize a Stage object with the X-Y grid backdrop
  stage = new Stage(this);
  stage.addDefaultBackdrop();
  // now add a "cat" Sprite object & attach it to our stage. Go to the center of the screen.
  cat = new Sprite(this, stage, keys);
  cat.addDefaultCostumes();
  cat.goToXY(width/2, height/2);
  cat.direction = 90;
}

void draw() {
  cat.goToXY(mouseX, mouseY);

  // finally, draw the stage and then draw the cat 
  stage.draw();
  cat.draw();  
  frame.setTitle("FPS: "+frameRate);

  if (mousePressed) stage.addTrail();
  else stage.removeTrail();
}

// the code below is essential for Scratching keyboard functions. Do not change keyPressed
// or keyReleased unless you're absolute sure you know what you're doing!
void keyPressed() { 
  keyDownCheck();
}

void keyReleased() {
  keyUpCheck();
}

void keyUpCheck() {
  if (key<256) {
    keys[key] = false;
  }
  if (key==CODED) {
    keys[keyCode] = true;
  }
}

void keyDownCheck() {
  if (stage.askingQuestion) stage.questionKeycheck();
  if (key<256) {
    keys[key] = true;
  }
  if (key==CODED) {
    keys[keyCode] = true;
  } 
}

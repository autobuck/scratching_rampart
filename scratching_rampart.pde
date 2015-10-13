// these values are "static," which means they do not change.
static int rotationStyle_360degrees=0;
static int rotationStyle_leftRight=1;
static int rotationStyle_dontRotate=2;
static int upArrow=0;
static int downArrow=1;
static int leftArrow=2;
static int rightArrow=3;
// These arrays contain boolean values for each key and arrow key
boolean[] keyIsDown = new boolean[256];
boolean[] arrowIsDown = new boolean[4];

// 5 important variables and objects
String gamestate = "placingC";
Sprite cursor;
ArrayList<castle> castles = new ArrayList<castle>();
ArrayList<wall> walls = new ArrayList<wall>();
ArrayList<cannon> cannons = new ArrayList<cannon>();
ArrayList<enemyShip> enemyShips = new ArrayList<enemyShip>();
int currentCannon = 0;
ArrayList<cannonBall> cannonBalls = new ArrayList<cannonBall>();
int money = 500;
int life = 100;
int tileSize = 32;
int gridSize = 28;
int gameGrid[][] = new int[gridSize][gridSize];
int testGrid[][] = new int[gridSize+2][gridSize+2]; // 2 bigger in each dimension to leave margin for testing interiors
int newWall[][] = new int[3][3];
int players = 1;
int roundTime = 15;
int level = 0;
int cannonsToPlace = 3;

// User variables and objects are "declared", or listed, here.
// Our sample includes one Stage object, called stage, and one Sprite object, called cursor.
Stage stage;

// Setup runs once, and sets some default values
void setup() {
  // first, create a Processing window 500 px by 500 px
  size(tileSize*((gridSize*2)+3), tileSize*gridSize);
  // next, initialize a Stage object with the X-Y grid backdrop
  stage = new Stage(this);
  stage.addBackdrop("images/grass-32px.png");
  //stage.addDefaultBackdrop();


  // now add a "cursor" Sprite object & attach it to our stage. Go to the center of the screen.
  cursor = new Sprite(this, stage);
  cursor.addCostume("images/grass-32px.png");
  cursor.addCostume("images/wall-32px.png");
  cursor.addCostume("images/floor-32px.png");
  cursor.addCostume("images/cannon-64px.png");
  cursor.addCostume("images/river-32px.png");
  cursor.costumeNumber = 1; // default wall
  cursor.show();
  cursor.rotationStyle = rotationStyle_dontRotate;
  
  stage.setTrails(0);
  clearGameGrid();
  addCastle(gridSize/2, gridSize/2);
  int i = 0;
  do {
    if (addCastle((int)random(2, gridSize-1), (int)random(2, gridSize-1))) i++;
  } 
  while (i<4);


  surroundCastle(castles.get(0));
  checkForInteriors();
  pickNewWall();
}

void spawnEnemyShips() {
  int i = 0;
  do {
    if (addEnemyShip((int)i / 5)) i++;
  } 
  while (enemyShips.size () < (level*level)+3);
}

boolean addEnemyShip(int wave) {
  boolean spawnOK = true;
  int newest = enemyShips.size();
  enemyShips.add(new enemyShip(this, stage));
  enemyShips.get(newest).goToXY(width+(wave*tileSize), random(0, height));
  for (int i=0; i<newest; i++) {
    if (enemyShips.get(newest).touchingSprite(enemyShips.get(i))) spawnOK = false;
  }
  if (spawnOK) {
  } else {
    enemyShips.remove(newest);
  }
  return spawnOK;
}

void moveEnemyShips() {
  for (int i=0; i<enemyShips.size (); i++) {
    enemyShips.get(i).firingTimeout--;
    enemyShips.get(i).move(1);
    //if (random(0,100) < 50) enemyShips.get(i).turnLeft(enemyShips.get(i).course);
    if (random(0, 100) < 5) if (cannons.size() > 0) enemyShips.get(i).pointToSprite(cannons.get((int)random(0, cannons.size())));
    if (enemyShips.get(i).firingTimeout < 0) {
      enemyShips.get(i).firingTimeout = 120;
      fireAtTarget(enemyShips.get(i), walls.get((int)random(0, walls.size())));
    }
    enemyShips.get(i).draw();
  }
}

void pickNewWall() {
  int piece = (int)random(1,14);
  switch (piece) {
    case 1: newWall = new int[][] { { 0,1,0 },
                                     {0,1,1},
                                     {0,0,1} }; break;
    case 2: newWall = new int[][] { { 0,1,0 },
                                     {1,1,0},
                                     {1,0,0} }; break;
    case 3: newWall = new int[][] { { 0,0,0 },
                                     {1,1,0},
                                     {0,1,0} }; break;
    case 4: newWall = new int[][] { { 0,1,0 },
                                      {0,1,0},
                                      {0,1,1} }; break;
    case 5: newWall = new int[][] { { 0,0,0 },
                                     {1,1,1},
                                     {0,1,0} }; break;
    case 6: newWall = new int[][] { { 0,0,0 },
                                     {0,1,0},
                                     {0,1,0} }; break;
    case 7: newWall = new int[][] { { 0,0,0 },
                                     {0,1,0},
                                     {0,0,0} }; break;
    case 8: newWall = new int[][] { { 0,1,0 },
                                     {0,1,0},
                                     {1,1,0} }; break;
    case 9: newWall = new int[][] { { 0,1,0 },
                                     {0,1,0},
                                     {0,1,0} }; break;
    case 10: newWall = new int[][] { { 0,1,0 }
                                     ,{1,1,1}
                                     ,{0,1,0} }; break;
    case 11: newWall = new int[][] { { 1,1,0 }
                                     ,{0,1,0}
                                     ,{1,1,0} }; break;
    case 12: newWall = new int[][] { { 0,1,1 }
                                     ,{0,1,0}
                                     ,{1,1,0} }; break;
    case 13: newWall = new int[][] { { 1,1,0 }
                                     ,{0,1,0}
                                     ,{0,1,1} }; break;
    default: newWall = new int[][] { { 1,1,1 }
                                     ,{0,1,0}
                                     ,{0,1,0} }; break;

  }
}

void clearGameGrid() {
  for (int x = 0; x<20; x++) {
    for (int y=0; y<20; y++) {
      gameGrid[x][y] = 0;
    }
  }
}

void copyTestGrid() {
  //zero test grid
  for (int x = 0; x<gridSize+2; x++) {
    for (int y=0; y<gridSize+2; y++) {
      testGrid[x][y] = 0;
    }
  }
  // copy game grid to test grid interior
  for (int x = 0; x<gridSize; x++) {
    for (int y=0; y<gridSize; y++) {
      testGrid[x+1][y+1] = gameGrid[x][y];
    }
  }
}


//4 game control functions here -- more below
void draw() {
  stage.draw();
  drawAllTiles();
  drawAllWalls();
  drawAllCastles();
  drawAllCannons();
  if (gamestate=="playing") gameLoop();
  if (gamestate=="placingW") placeWallLoop();
  if (gamestate=="placingC") placeCannonLoop();
  if (gamestate=="placingW") if (stage.timer() > roundTime) nextGameMode();
  if (gamestate=="placingC") if (stage.timer() > roundTime || cannonsToPlace < 1) nextGameMode();
  if (gamestate=="playing") if (enemyShips.size() == 0 && cannonBalls.size() == 0) nextGameMode();
  drawLabels();
}

void gameLoop() {
  aimAllCannons();
  moveEnemyShips();
  moveCannonBalls();
}

void aimAllCannons() {
  for (int i=0; i<cannons.size (); i++) {
    if (cannons.get(i).enabled) cannons.get(i).pointToMouse();
  }
}

void placeWallLoop() {
  // finally, draw the stage and then draw the cursor 
  int cursorX = ((int)(mouseX/tileSize));
  int cursorY = ((int)(mouseY/tileSize));
  if (cursorX > gridSize-1) cursorX = gridSize-1;
  if (cursorY > gridSize-1) cursorY = gridSize-1;
  cursor.costumeNumber = 1;
  cursor.brightnessEffect = 25;
  for (int x = 0; x<3; x++) {
    for (int y = 0; y<3; y++) {
      if (newWall[x][y]==1) {
        cursor.stamp((tileSize/2)+((cursorX+x-1)*tileSize), (tileSize/2)+((cursorY+y-1)*tileSize));
      }
    }
  }
  cursor.brightnessEffect = 0;
}

void placeCannonLoop() {
  // finally, draw the stage and then draw the cursor 
  int cursorX = ((int)(mouseX/tileSize));
  int cursorY = ((int)(mouseY/tileSize));
  if (cursorX > gridSize-1) cursorX = gridSize-1;
  if (cursorY > gridSize-1) cursorY = gridSize-1;
  cursor.costumeNumber = 3;
  cursor.brightnessEffect = 25;
  cursor.stamp((tileSize)+((cursorX-1)*tileSize), (tileSize)+((cursorY-1)*tileSize));
  cursor.brightnessEffect = 0;
}


void nextGameMode() {
  if (gamestate=="playing") {
    removeAll(cannonBalls);
    gamestate = "placingW";
    roundTime = 32;
    clearBrokenWalls();
    clearInteriorsFromTest();
    stage.resetTimer();
  } else if (gamestate=="placingW") {
    gamestate="placingC";
    roundTime = 15;
    cannonsToPlace = 3;
    checkForInteriors();
    stage.resetTimer();
  } else {
    checkIfCannonsCanFire();
    level++;
    spawnEnemyShips();
    gamestate = "playing";
  }
}

void clearBrokenWalls() {
  for (int i=0; i<walls.size (); i++) {
    if (walls.get(i).costumeNumber>0) {
      walls.remove(i);
      i--;
    }
  }
}

void rotateNewWallLeft() {
  int rotatedWall[][] = new int[3][3];
  for (int x = 0; x<3; x++) {
    for (int y = 0; y<3; y++) {
      rotatedWall[x][y] = newWall[x][y];
    }
  }
  newWall[0][1] = rotatedWall[1][0];
  newWall[1][0] = rotatedWall[2][1];
  newWall[2][1] = rotatedWall[1][2];
  newWall[1][2] = rotatedWall[0][1];
  
  newWall[0][0] = rotatedWall[2][0];
  newWall[2][0] = rotatedWall[2][2];
  newWall[2][2] = rotatedWall[0][2];
  newWall[0][2] = rotatedWall[0][0];
}

void rotateNewWallRight() {
  int rotatedWall[][] = new int[3][3];
  for (int x = 0; x<3; x++) {
    for (int y = 0; y<3; y++) {
      rotatedWall[x][y] = newWall[x][y];
    }
  }
  newWall[0][1] = rotatedWall[1][2];
  newWall[1][2] = rotatedWall[2][1];
  newWall[2][1] = rotatedWall[1][0];
  newWall[1][0] = rotatedWall[0][1];

  newWall[0][0] = rotatedWall[0][2];
  newWall[0][2] = rotatedWall[2][2];
  newWall[2][2] = rotatedWall[2][0];
  newWall[2][0] = rotatedWall[0][0];
}

int floodCount(int blockX, int blockY) {
  int target = 0;
  int replacement = 1;
  PVector node = new PVector(blockX, blockY);
  int floodCount = 0;
  int[][] savedGrid = new int[20][20];
  for (int x = 0; x<20; x++) {
    for (int y = 0; y<20; y++) {
      savedGrid[x][y] = gameGrid[x][y];
    }
  }
  if (target != replacement) {
    //Deque<PVector> queue = new LinkedList<PVector>();
    ArrayList<PVector> queue = new ArrayList<PVector>();
    do {
      if (queue.size() > 0) {
        node = queue.get(0);
        queue.remove(0);
      }
      int x = (int)node.x;
      int y = (int)node.y;
      while (x > 0 && gameGrid[x - 1][y] == target) {
        x--;
      }
      boolean spanUp = false;
      boolean spanDown = false;
      while (x < gridSize && gameGrid[x][y] == target) {
        gameGrid[x][y] = replacement;
        floodCount++;
        if (!spanUp && y > 0 && gameGrid[x][y - 1] == target) {
          queue.add(new PVector(x, y - 1));
          spanUp = true;
        } else if (spanUp && y > 0 && gameGrid[x][y - 1] != target) {
          spanUp = false;
        }
        if (!spanDown && y < gridSize - 1 && gameGrid[x][y + 1] == target) {
          queue.add(new PVector(x, y + 1));
          spanDown = true;
        } else if (spanDown && y < gridSize - 1 && gameGrid[x][y + 1] != target) {
          spanDown = false;
        }
        x++;
      }
    } 
    while ( queue.size () > 0);
  }
  for (int x = 0; x<20; x++) {
    for (int y = 0; y<20; y++) {
      gameGrid[x][y] = savedGrid[x][y];
    }
  }
  return floodCount;
}

int floodFill(int blockX, int blockY) {
  int target = 0;
  int replacement = 1;
  PVector node = new PVector(blockX, blockY);
  int floodCount = 0;
  if (target != replacement) {
    //Deque<PVector> queue = new LinkedList<PVector>();
    ArrayList<PVector> queue = new ArrayList<PVector>();
    do {
      if (queue.size() > 0) {
        node = queue.get(0);
        queue.remove(0);
      }
      int x = (int)node.x;
      int y = (int)node.y;
      while (x > 0 && gameGrid[x - 1][y] == target) {
        x--;
      }
      boolean spanUp = false;
      boolean spanDown = false;
      while (x < gridSize && gameGrid[x][y] == target) {
        gameGrid[x][y] = replacement;
        addWall(x, y);
        floodCount++;
        if (!spanUp && y > 0 && gameGrid[x][y - 1] == target) {
          queue.add(new PVector(x, y - 1));
          spanUp = true;
        } else if (spanUp && y > 0 && gameGrid[x][y - 1] != target) {
          spanUp = false;
        }
        if (!spanDown && y < gridSize - 1 && gameGrid[x][y + 1] == target) {
          queue.add(new PVector(x, y + 1));
          spanDown = true;
        } else if (spanDown && y < gridSize - 1 && gameGrid[x][y + 1] != target) {
          spanDown = false;
        }
        x++;
      }
    } 
    while ( queue.size () > 0);
  }
  return floodCount;
}

int floorFillTestGrid(int blockX, int blockY) {
  int target = 0;
  int replacement = 9;
  PVector node = new PVector(blockX, blockY);
  int floodCount = 0;
  if (target != replacement) {
    ArrayList<PVector> queue = new ArrayList<PVector>();
    do {
      if (queue.size() > 0) {
        node = queue.get(0);
        queue.remove(0);
      }
      int x = (int)node.x;
      int y = (int)node.y;
      while (x > 0 && testGrid[x - 1][y] == target) {
        x--;
      }
      boolean spanUp = false;
      boolean spanUpLeft = false;
      boolean spanUpRight = false;
      boolean spanDown = false;
      boolean spanDownLeft = false;
      boolean spanDownRight = false;
      while (x < gridSize+2 && testGrid[x][y] == target) {
        testGrid[x][y] = replacement;
        floodCount++;
        if (!spanUp && y > 0 && testGrid[x][y - 1] == target) {
          queue.add(new PVector(x, y - 1));
          spanUp = true;
        } else if (spanUp && y > 0 && testGrid[x][y - 1] != target) {
          spanUp = false;
        }
        if (x<gridSize+1) 
        if (!spanUpRight && y > 0 && testGrid[x+1][y - 1] == target) {
          queue.add(new PVector(x+1, y - 1));
          spanUpRight = true;
        } else if (spanUpRight && y > 0 && testGrid[x+1][y - 1] != target) {
          spanUpRight = false;
        }
        if (x>0)
        if (!spanUpLeft && y > 0 && testGrid[x-1][y - 1] == target) {
          queue.add(new PVector(x-1, y - 1));
          spanUpLeft = true;
        } else if (spanUpLeft && y > 0 && testGrid[x-1][y - 1] != target) {
          spanUpLeft = false;
        }
        if (!spanDown && y < gridSize  && testGrid[x][y + 1] == target) {
          queue.add(new PVector(x, y + 1));
          spanDown = true;
        } else if (spanDown && y < gridSize && testGrid[x][y + 1] != target) {
          spanDown = false;
        }
        if (x<gridSize+1) 
        if (!spanDownRight && y < gridSize && testGrid[x+1][y + 1] == target) {
          queue.add(new PVector(x+1, y + 1));
          spanDownRight = true;
        } else if (spanDownRight && y > 0 && testGrid[x+1][y + 1] != target) {
          spanDownRight = false;
        }
        if (x>0)
        if (!spanDownLeft && y < gridSize && testGrid[x-1][y + 1] == target) {
          queue.add(new PVector(x-1, y + 1));
          spanDownLeft = true;
        } else if (spanDownLeft && y > 0 && testGrid[x-1][y + 1] != target) {
          spanDownLeft = false;
        }
        x++;
      }
    } 
    while ( queue.size () > 0);
  }
  return floodCount;
}


boolean addWall(int blockX, int blockY) {
  if (blockX<0 || blockX>gridSize-1) return false;
  if (blockY<0 || blockY>gridSize-1) return false;
  int placementCounter = 0;
  // count blocks to place
  for (int x = 0; x<3; x++) {
    for (int y = 0; y<3; y++) {
      if (newWall[x][y]==1) {
        placementCounter++;
      }
    }
  }
  // make sure all blocks are placeable on the board
  for (int x = 0; x<3; x++) {
    for (int y = 0; y<3; y++) {
      // if blocks are out of bounds, return "false" b/c we cannot place the wall here
      if (newWall[x][y]==1 && (x+blockX-1 < 0 || y+blockY-1 < 0)) return false;
      else if (newWall[x][y]==1 && (blockX+x-1 > gridSize-1 || blockY+y-1>gridSize-1)) return false;
      else if (newWall[x][y]==1 && gameGrid[x+blockX-1][y+blockY-1] == 0) {
        placementCounter--;
      }
    }
  }
  // actually place the blocks
  if (placementCounter==0) {
    for (int x = 0; x<3; x++) {
      for (int y = 0; y<3; y++) {
        if (newWall[x][y]==1 && gameGrid[x+blockX-1][y+blockY-1] == 0) {
          gameGrid[x+blockX-1][y+blockY-1]=1;
          addWallBlock(x+blockX-1, y+blockY-1);
        }
      }
    }
    return true; // return "true" only if successful
  } else return false;
}

// create a new wall sprite and place it on the board
void addWallBlock(int blockX, int blockY) {
  int newest = walls.size();
  walls.add(new wall(this, stage));
  walls.get(newest).goToXY((tileSize/2)+blockX*tileSize, (tileSize/2)+blockY*tileSize);
  walls.get(newest).gridX=blockX;
  walls.get(newest).gridY=blockY;
}

void drawAllWalls() {
  for (int i=0; i<walls.size (); i++) {
    walls.get(i).draw();
  }
}

void drawAllTiles() {
  for (int y = 0; y<gridSize; y++) {
    for (int x = 0; x<gridSize; x++) {
      cursor.costumeNumber = gameGrid[x][y];
      //if (gamestate=="playing" || gamestate=="placingC") {
      if (gameGrid[x][y]>2) cursor.costumeNumber = 2; 
      cursor.stamp((tileSize/2)+x*tileSize, (tileSize/2)+y*tileSize);
      /*}
       else if (gamestate=="placingW") {
       if (cursor.costumeNumber==0) cursor.stamp((tileSize/2)+x*tileSize, (tileSize/2)+y*tileSize);
       }*/
    }
  }
  cursor.costumeNumber=4;
  for (int y=0; y<gridSize; y++) {
    cursor.stamp((tileSize/2)+(gridSize)*tileSize, (tileSize/2)+(y*tileSize));
    cursor.stamp((tileSize/2)+(gridSize+1)*tileSize, (tileSize/2)+(y*tileSize));
    cursor.stamp((tileSize/2)+(gridSize+2)*tileSize, (tileSize/2)+(y*tileSize));
  }
  if (players==1) {
    for (int y=0; y<gridSize; y++) {
      for (int x=gridSize+3; x< (gridSize*2)+3; x++) {
        cursor.stamp((tileSize/2)+(x*tileSize), (tileSize/2)+(y*tileSize));
      }
    }
  }
}

void fireAtMouse(Sprite shooter) {
  int newest = cannonBalls.size();
  cannonBalls.add(new cannonBall(this, stage));
  cannonBalls.get(newest).goToSprite(shooter); 
  cannonBalls.get(newest).direction = shooter.directionToMouse();
  cannonBalls.get(newest).size = 25;
  cannonBalls.get(newest).targetX = mouseX;
  cannonBalls.get(newest).targetY = mouseY;
  cannonBalls.get(newest).targetDistance = cannonBalls.get(newest).distanceToXY(mouseX, mouseY);
  cannonBalls.get(newest).addDefaultCostumes();
  cannonBalls.get(newest).brightnessEffect = -100;
  cannonBalls.get(newest).move(25);
}

void fireAtTarget(Sprite shooter, Sprite target) {
  int newest = cannonBalls.size();
  cannonBalls.add(new cannonBall(this, stage));
  cannonBalls.get(newest).goToSprite(shooter); 
  cannonBalls.get(newest).direction = shooter.directionToSprite(target);
  cannonBalls.get(newest).size = 25;
  cannonBalls.get(newest).targetX = target.pos.x;
  cannonBalls.get(newest).targetY = target.pos.y;
  cannonBalls.get(newest).targetDistance = cannonBalls.get(newest).distanceToXY(cannonBalls.get(newest).targetX, cannonBalls.get(newest).targetY);
  cannonBalls.get(newest).addDefaultCostumes();
  cannonBalls.get(newest).brightnessEffect = -100;
  cannonBalls.get(newest).move(25);
}


void moveCannonBalls() {
  for (int i = 0; i < cannonBalls.size (); i++) {
    boolean removeThis = false;
    cannonBalls.get(i).move(20);
    if (frameCount % 2 == 0) cannonBalls.get(i).nextCostume();
    if (cannonBalls.get(i).distanceToXY(cannonBalls.get(i).targetX, cannonBalls.get(i).targetY) > cannonBalls.get(i).targetDistance/2) cannonBalls.get(i).size++;
    else cannonBalls.get(i).size--;
    if (cannonBalls.get(i).distanceToXY(cannonBalls.get(i).targetX, cannonBalls.get(i).targetY) <= 21) {
      //explode, do damage
      for (int j=0; j<walls.size (); j++) {
        if (cannonBalls.get(i).touchingSprite(walls.get(j))) {
          walls.get(j).nextCostume();
          gameGrid[walls.get(j).gridX][walls.get(j).gridY] = 0;
        }
      }
      for (int j=0; j<enemyShips.size (); j++) {
        if (cannonBalls.get(i).touchingSprite(enemyShips.get(j))) {
          enemyShips.get(j).nextCostume();
          if (enemyShips.get(j).costumeNumber==0) {
            enemyShips.remove(j);
            j--;
          }
        }
      }      
      removeThis = true;
    }

    if (removeThis) {
      cannonBalls.remove(i);
      i--;
    } else cannonBalls.get(i).draw();
  }
}


// the code below is essential for certain Scratching functions. Do not change keyPressed
// or keyReleased - unless you're absolute sure you know what you're doing!
void keyPressed() {
  if (key=='d') debugGameGrid();
  if (key=='c') println(countSurroundedCastles()); 
  if (key==' ') nextGameMode();
  if (key=='z') rotateNewWallLeft();
  if (key=='x') rotateNewWallRight();
  if (stage.askingQuestion) stage.questionKeycheck();
  if (key<256) {
    keyIsDown[key] = true;
  }
  if (key==CODED) {
    switch (keyCode) {
    case UP: 
      arrowIsDown[upArrow]=true; 
      break;
    case DOWN: 
      arrowIsDown[downArrow]=true; 
      break;
    case LEFT: 
      arrowIsDown[leftArrow]=true;  
      break;
    case RIGHT: 
      arrowIsDown[rightArrow]=true; 
      break;
    }
  }
}

// the code below is essential for certain Scratching functions. Do not change keyPressed
// or keyReleased - unless you're absolute sure you know what you're doing!
void keyReleased() {
  if (key<256) {
    keyIsDown[key] = false;
  }
  if (key==CODED) {
    switch (keyCode) {
    case UP: 
      arrowIsDown[upArrow]=false; 
      break;
    case DOWN: 
      arrowIsDown[downArrow]=false; 
      break;
    case LEFT: 
      arrowIsDown[leftArrow]=false;  
      break;
    case RIGHT: 
      arrowIsDown[rightArrow]=false; 
      break;
    }
  }
}

void removeAll(ArrayList list) {
  if (list.size() > 0) do {
    list.remove(0);
  } 
  while (list.size () > 0);
}

void mousePressed() {
  int cursorX = (int)(mouseX/tileSize);
  int cursorY = (int)(mouseY/tileSize);
  if (gamestate=="placingW") {
    if (mouseButton==37) placeNewWall(cursorX, cursorY);
    else rotateNewWallRight();
  } else if (gamestate=="placingC") {
    if (gameGrid[cursorX][cursorY]==2 && gameGrid[cursorX-1][cursorY]==2 && gameGrid[cursorX][cursorY-1]==2 && gameGrid[cursorX-1][cursorY-1]==2) {
      addCannon(cursorX, cursorY);
    }
  } else if (gamestate=="playing") {
    if (cannons.size() > 0) if (cannons.get(currentCannon).firingTimeout < 0) {
      fireAtMouse(cannons.get(currentCannon));
      cannons.get(currentCannon).firingTimeout = 60;
      do { 
        currentCannon++; 
        if (currentCannon+1 > cannons.size()) currentCannon = 0;
      } 
      while (!cannons.get (currentCannon).enabled);
    }
  }
}

void addCannon(int x, int y) {
  boolean spawnOK = true;
  int newest = cannons.size();
  cannons.add(new cannon(this, stage));
  cannons.get(newest).addCostume("images/cannon-64px.png");
  cannons.get(newest).gridX = x;
  cannons.get(newest).gridY = y;
  cannons.get(newest).goToXY(x*(tileSize), y*(tileSize));
  for (int i=0; i<newest; i++) {
    if (cannons.get(newest).touchingSprite(cannons.get(i))) spawnOK = false;
  }
  if (spawnOK) {
    cannonsToPlace--;
  } else {
    cannons.remove(newest);
  }
}

void drawAllCannons() {
  for (int i = 0; i<cannons.size (); i++) {
    cannons.get(i).firingTimeout--;
    cannons.get(i).draw();
  }
}

boolean addCastle(int x, int y) {
  int newest = castles.size();
  castles.add(new castle(this, stage));
  castles.get(newest).addCostume("images/castle-64px.png");
  castles.get(newest).direction = 90;
  castles.get(newest).gridX = x;
  castles.get(newest).gridY = y;
  castles.get(newest).goToXY(x*(tileSize), y*(tileSize));
  boolean spawnOK = true;
  for (int i=0; i<newest; i++) {
    //if (castles.get(newest).touchingSprite(castles.get(i))) spawnOK = false;
    if (castles.get(newest).distanceToSprite(castles.get(i)) < (tileSize*6) ) spawnOK = false;
  }
  if (spawnOK) {
    gameGrid[x][y]=4;
    gameGrid[x-1][y]=4;
    gameGrid[x][y-1]=4;
    gameGrid[x-1][y-1]=4;
  } else {
    castles.remove(newest);
  }
  return spawnOK;
}

void drawAllCastles() {
  for (int i = 0; i<castles.size (); i++) {
    castles.get(i).draw();
  }
}

void surroundCastle(Sprite castle) {
  int wallSize = 8;
  int castleGridX = (int)castle.pos.x/tileSize;
  int castleGridY = (int)castle.pos.y/tileSize;
  for (int x = 0; x<wallSize; x++) {
    gameGrid[castleGridX-(wallSize/2)+x][castleGridY-(wallSize/2)] = 1;
    addWallBlock(castleGridX-(wallSize/2)+x, castleGridY-(wallSize/2));
    gameGrid[castleGridX-(wallSize/2)+x][castleGridY+(wallSize/2)-1] = 1;
    addWallBlock(castleGridX-(wallSize/2)+x, castleGridY+(wallSize/2)-1);
  }
  for (int y = 1; y<wallSize-1; y++) {
    gameGrid[castleGridX-(wallSize/2)][castleGridY-(wallSize/2)+y] = 1;
    addWallBlock(castleGridX-(wallSize/2), castleGridY-(wallSize/2)+y);
    gameGrid[castleGridX+(wallSize/2)-1][castleGridY-(wallSize/2)+y] = 1;
    addWallBlock(castleGridX+(wallSize/2)-1, castleGridY-(wallSize/2)+y);
  }
}

void placeNewWall(int cursorX, int cursorY) {
  if (cursorX < gridSize && cursorY < gridSize) {
    if (addWall(cursorX, cursorY)) {
      pickNewWall();
      checkForInteriors();
    }
  }
}

void drawLabels() {
  textAlign(CENTER);
  textSize(20);
  fill(255);
  if (gamestate=="placingW") text("BUILD NEW WALLS", width/2, 30);
  else if (gamestate=="placingC") text("PLACE "+cannonsToPlace+((cannonsToPlace>1) ? " CANNONS" : " CANNON"), width/2, 30);
  else if (gamestate=="playing") text("WAVE "+level, width/2, 30);
  textSize(30);
  if (gamestate!="playing" && gamestate!="gameover") text((int)(roundTime-stage.timer()), width/2, 70);
  if (gamestate=="gameover") {
    pushStyle();
    textSize(40);
    fill(255);
    text("GAME OVER", width/2, height/2);
    popStyle();
  }
}

int wallCount() {
  int count = 0;
  for (int x = 0; x<gridSize; x++) {
    for (int y = 0; y<gridSize; y++) {
      if (testGrid[x+1][y+1]==1) count++;
    }
  }
  return count;
}

int floorCount() {
  int count = 0;
  for (int x = 0; x<gridSize; x++) {
    for (int y = 0; y<gridSize; y++) {
      if (gameGrid[x][y]==2) count++;
    }
  }
  return count;
}


int fillInFloors() {
  int count = 0;
  for (int y = 0; y<gridSize; y++) {
    for (int x = 0; x<gridSize; x++) {
      if (testGrid[x+1][y+1] == 0) {
        gameGrid[x][y] = 2;
        count++;
      }
    }
  }
  return count;
}

void debugGameGrid() {
  println("play grid");
  for (int y = 0; y<gridSize; y++) {
    for (int x = 0; x<gridSize; x++) {
      print(gameGrid[x][y]);
    }
    println();
  }
}


void debugTestGrid() {
  println("test grid");
  for (int y = 0; y<gridSize+2; y++) {
    for (int x = 0; x<gridSize+2; x++) {
      print(testGrid[x][y]);
    }
    println();
  }
}

void checkForInteriors() {
  copyTestGrid();
  floorFillTestGrid(0, 0);
  fillInFloors();
  if (gamestate=="placingC" && countSurroundedCastles() < 1) gamestate="gameover";
  checkIfCannonsCanFire();
}

void checkIfCannonsCanFire() {
  for (int i=0; i<cannons.size (); i++) {
    boolean canFire = true;
    int x = cannons.get(i).gridX;
    int y = cannons.get(i).gridY;
    if (gameGrid[x-2][y-1]<1) canFire=false;
    if (gameGrid[x-2][y]<1) canFire=false;
    if (gameGrid[x+1][y-1]<1) canFire=false;
    if (gameGrid[x+1][y]<1) canFire=false;
    if (gameGrid[x][y-2]<1) canFire=false;
    if (gameGrid[x-1][y-2]<1) canFire=false;
    if (gameGrid[x][y+1]<1) canFire=false;
    if (gameGrid[x-1][y+1]<1) canFire=false;
    cannons.get(i).enabled = canFire;
  }
}


void clearInteriorsFromTest() {
  for (int y=0; y<gridSize; y++) {
    for (int x=0; x<gridSize; x++) {
      if (gameGrid[x][y]==2) gameGrid[x][y]=0;
    }
  }
}

int countSurroundedCastles() {
  int count = 0;
  for (int i=0; i<castles.size (); i++) {
    boolean surrounded = true;
    int x = castles.get(i).gridX;
    int y = castles.get(i).gridY;
    if (gameGrid[x-2][y-1]<1) surrounded=false;
    if (gameGrid[x-2][y]<1) surrounded=false;
    if (gameGrid[x+1][y-1]<1) surrounded=false;
    if (gameGrid[x+1][y]<1) surrounded=false;
    if (gameGrid[x][y-2]<1) surrounded=false;
    if (gameGrid[x-1][y-2]<1) surrounded=false;
    if (gameGrid[x][y+1]<1) surrounded=false;
    if (gameGrid[x-1][y+1]<1) surrounded=false;
    if (surrounded) count++;
  }
  return count;
}


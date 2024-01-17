//ZType final
import javalib.worldimages.*;
import java.awt.Color;

import javalib.funworld.World;
import javalib.funworld.WorldScene;
import java.util.Random;
import tester.Tester;

// Interface to represent constants in the game
interface IConstant {
  int SCENESIZE = 500;
  int TEXTSIZE = 50;
  double TICKRATE = 1.0;

}

// Represents the ZType game
class ZTypeGame extends World implements IConstant {
  ILoWord words; 
  boolean gameOver; 
  int score; 
  Random rand;

  // Constructor
  ZTypeGame(ILoWord words, boolean gameOver, int score, Random rand) {
    this.words = words;
    this.gameOver = gameOver;
    this.score = score;
    this.rand = rand;
  }

  // Constructor ZType
  ZTypeGame(Random rand) {
    this.rand = rand;
  }
  

  
  
  

  //Draws the game in its current state
  public WorldScene makeScene() {
    //ZTypeGame
    /* TEMPLATE:
  Fields:
  ... this.words ...        -- ILoWords
  ... this.gameOver ...       -- boolean
  ... this.score ...        -- int
  ... this.rand ...        -- random

  Methods:
  ... this.makeScene() ...   -- WordScene
  ... this.lastScene(String msg) ... -- WorldScene
  ... this.ontick()... -- World
  ... this.onKey( ) ... -- Word

  Methods for fields:
  ... this.words.draw(??) ...    -- ??  
  */
    WorldScene background = new WorldScene(SCENESIZE, SCENESIZE);
    WorldImage scoreText = new TextImage("Score:" + this.score, 10, Color.MAGENTA);
    if (gameOver) {
      return lastScene( "Game Over");
    } else {
      return this.words.draw(background).placeImageXY(scoreText, 450 , 450);
    }
  }

  //Draws the final scene when the game is over
  public WorldScene lastScene(String msg) {

    //ZTypeGame
    /* TEMPLATE:
  Fields:
  ... this.words ...        -- ILoWords
  ... this.gameOver ...       -- boolean
  ... this.score ...        -- int
  ... this.rand ...        -- random

  Methods:
  ... this.makeScene() ...   -- WordScene
  ... this.lastScene(String msg) ... -- WorldScene
  ... this.ontick()... -- World
  ... this.onKey( ) ... -- Word

  Methods for fields:
  ... this.words.draw(??) ...    -- ??  
  */
    WorldScene background = new WorldScene(SCENESIZE, SCENESIZE);
    WorldScene endScreen = new WorldScene(SCENESIZE, SCENESIZE);

    WorldImage gameOver = new TextImage(msg, 80, Color.MAGENTA);
    WorldImage scoreText = new TextImage("Score:" + this.score, 80, Color.PINK);

    background.placeImageXY(gameOver, SCENESIZE / 2, SCENESIZE / 2)
      .placeImageXY(scoreText, SCENESIZE / 2 , SCENESIZE / 2 + 50 );
    return endScreen.placeImageXY(gameOver, SCENESIZE / 2, SCENESIZE / 2)
      .placeImageXY(scoreText, SCENESIZE / 2 , SCENESIZE / 2 + 150 );

  }

  //Handles ticking of the clock and updating the world if needed
  public World onTick() {
    //ZTypeGame
    /* TEMPLATE:
  Fields:
  ... this.words ...        -- ILoWords
  ... this.gameOver ...       -- boolean
  ... this.score ...        -- int
  ... this.rand ...        -- random

  Methods:
  ... this.makeScene() ...   -- WordScene
  ... this.lastScene(String msg) ... -- WorldScene
  ... this.ontick()... -- World
  ... this.onKey( ) ... -- Word
  Methods for fields:
  ... this.words.moveAll(??) ...    -- ??
  ... this.words.isWordAtBottom(??) ..-- ??
  ... this.createWord(??) ..-- ??
  */
    if (!this.gameOver) {
      this.words = this.words.moveAll();
      if (this.words.isWordAtBottom()) {
        this.gameOver = true;
      }
      this.words = this.words.addToEnd(this.createWord());
    }
    return this;
  }

  //key handler
  public World onKeyEvent(String key) {
    //ZTypeGame
    /* TEMPLATE:
  Fields:
  ... this.words ...        -- ILoWords
  ... this.gameOver ...       -- boolean
  ... this.score ...        -- int
  ... this.rand ...        -- random

  Methods:
  this.makeScene() ...   -- WordScene
  this.lastScene(String msg) ... -- WorldScene
  this.ontick()... -- World
  this.onKey( ) ... -- Word

  Methods for fields:
  this.words.checkAndReduce(??) ...-- ??
  this.words.countRemoved(??) ..-- ??
  this.createWord(??) ..-- ??
  */

    if (!this.gameOver) {
      // Find the first matching active word
      // Remove the typed string from the active word
      this.words = this.words.checkAndReduce(key);
      this.score += this.words.countRemoved();
      // Update the score
    }
    return this;
  }



  // Generates a random word
  public IWord createWord() {
    //ZTypeGame
    /* TEMPLATE:
   Fields:
  ... this.words ...        -- ILoWords
  ... this.gameOver ...       -- boolean
  ... this.score ...        -- int
  ... this.rand ...        -- random
  Methods:
  ... this.makeScene() ...   -- WordScene
  ... this.lastScene(String msg) ... -- WorldScene
  ... this.ontick()... -- World
  ... this.onKey( ) ... -- Word
  Methods for fields:
  ..none.. 
  */
    String randomString = generateRandomString(rand.nextInt(5) + 1); 
    int x = rand.nextInt(SCENESIZE);
    int y = SCENESIZE;
    return new ActiveWord(randomString, x, y);
  }

  // Generates a random string of the specified length
  public String generateRandomString(int length) {
    //ZTypeGame
    /* TEMPLATE:
  Fields:
  ... this.words ...        -- ILoWords
  ... this.gameOver ...       -- boolean
  ... this.score ...        -- int
  ... this.rand ...        -- random
  Methods:
  ... this.makeScene() ...   -- WordScene
  ... this.lastScene(String msg) ... -- WorldScene
  ... this.ontick()... -- World
  ... this.onKey( ) ... -- Word
  Methods for fields:
  ..none.. 
  */
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < length; i++) {
      char randomChar = (char) (rand.nextInt(26) + 'a'); // Generate a random lowercase character
      sb.append(randomChar);
    }

    return sb.toString();
  }
}


// Represents a list of words
interface ILoWord extends IConstant {
  // draws the world on the scene
  WorldScene draw(WorldScene scene);

  // moves the words down
  ILoWord moveAll();

  //adds the new word to the end of the list
  ILoWord addToEnd(IWord word);

  //checks if the word reached the bottom
  boolean isWordAtBottom();

  //finds the first matching word in the string
  IWord findMatchingWord(String key);

  // replaces the old word with the new world
  ILoWord replace(IWord oldWord, IWord newWord);
  
  //adds to the score
  int countRemoved();
  
  //removes the letters of the word as they are typed
  ILoWord checkAndReduce(String that);
  
}

// Represents an empty list of words
class MtLoWord implements ILoWord, IConstant {
  public WorldScene draw(WorldScene scene) {
    return scene;
  }
    
  //ZTypeGame
  /* TEMPLATE:
  Fields:
  ...none....
  Methods:
  ... this.draw(WordScene scene) ...   -- WordScene
  ... this.moveAll() ... -- ILoWord
  ... this.addToEnd(IWord word)... -- ILoWord
  ... this.isWordAtBottom( ) ... -- boolean
  ... this.findMatchingWord(String key).. -- IWord
  ... this.replace(IWord oldWord, IWord newWord) ... -- boolean
  ... this.countRemoved().. -- int
  ... this.checkAndReduce(String that) ... -- ILoWord
  Methods for fields:
  ..none.. 
  */


  // moves the words down
  public ILoWord moveAll() {
    return this;
  }
  
  
  //adds the new word to the end of the list
  public ILoWord addToEnd(IWord word) {
    return new ConsLoWord(word, this);
  }

  
  //checks if the word reached the bottom
  public boolean isWordAtBottom() {
    return false;
  }
  
  //finds the first matching word in the string
  public IWord findMatchingWord(String key) {
    return null;
  }

  //replaces the old word with the new world
  public ILoWord replace(IWord oldWord, IWord newWord) {
    return this;
  }
  

  // No word to count in the empty list
  public int countRemoved() {
    return 0;
  }

  //removes the letters of the word as they are typed
  public ILoWord checkAndReduce(String that) {
    return this;

  }

  // Represents a non-empty list of words
  class ConsLoWord implements ILoWord, IConstant {
    IWord first;
    ILoWord rest;

    // Constructor
    ConsLoWord(IWord first, ILoWord rest) {
      this.first = first;
      this.rest = rest;
    }
    //ZTypeGame
    /* TEMPLATE:
  Fields:
  ... this.first ...        -- IWord
  ... this.rest ...       -- ILoWord

  Methods:
  ... this.draw(WordScene scene) ...   -- WordScene
  ... this.moveAll() ... -- ILoWord
  ... this.addToEnd(IWord word)... -- ILoWord
  ... this.isWordAtBottom( ) ... -- boolean
  ... this.findMatchingWord(String key).. -- IWord
  ... this.replace(IWord oldWord, IWord newWord) ... -- boolean
  ... this.countRemoved().. -- int
  ... this.checkAndReduce(String that) ... -- ILoWord

  Methods for fields:
  ..none.. 
*/

    //draws the world on the scene
    public WorldScene draw(WorldScene scene) {
      return this.rest.draw(this.first.draw(scene));
    }

    //moves the words down
    public ILoWord moveAll() {
      return new ConsLoWord(this.first.move(), this.rest.moveAll());
    }

    //adds the new word to the end of the list
    public ILoWord addToEnd(IWord word) {
      return new ConsLoWord(this.first, this.rest.addToEnd(word));
    }
  
    //checks if the word reached the bottom
    public boolean isWordAtBottom() {
      return this.first.isAtBottom() || this.rest.isWordAtBottom();
    }



    // Counts the number of removed words
    public int countRemoved() {
      if (this.first.isRemoved()) {
        return 1 + this.rest.countRemoved();
      } else {
        return this.rest.countRemoved();
      }
    }

    // Finds the first matching active word in the list
    public IWord findMatchingWord(String key) {
      if (this.first.isActive() && this.first.matches(key)) {
        return this.first;
      } else {
        return this.rest.findMatchingWord(key);
      }
    }

    // Replaces an old word with a new word in the list
    public ILoWord replace(IWord oldWord, IWord newWord) {
      if (this.first.equals(oldWord)) {
        return new ConsLoWord(newWord, this.rest);
      } else {
        return new ConsLoWord(this.first, this.rest.replace(oldWord, newWord));
      }
    }

    //removes the letters of the word as they are typed
    public ILoWord checkAndReduce(String that) {
      return new ConsLoWord(this.first.removeFirst(that), this.rest.checkAndReduce(that));
    }
  }
}
  


// Represents a word in the game
interface IWord extends IConstant {
  // Draws the word on the given scene
  WorldScene draw(WorldScene scene);

  // Moves the word upwards
  IWord move();

  // Checks if the word has reached the bottom of the screen
  boolean isAtBottom();

  // Checks if the word is active (not yet typed)
  boolean isActive();

  // Gets the string representation of the word
  String getTypedString();

  // Transforms the word into an inactive state
  IWord transformToInactive();
  
  //check if the two strings match
  boolean matches(String key);
  
  
  //check if the word is removed
  boolean isRemoved();
  
  
  //remove the first letter of the word
  IWord removeFirst(String that);
  
  
}

// Represents an active word
class ActiveWord implements IWord, IConstant {
  String text;
  int x;
  int y;

  // Constructor
  ActiveWord(String text, int x, int y) {
    this.text = text;
    this.x = x;
    this.y = y;
  }
  //ZTypeGame
  /* TEMPLATE:
    Fields:
    ... this.text ...     -- String
    ... this.x ...       -- int
    ... this.y ...       -- int

    Methods:
    ... this.draw(WordScene scene) ...   -- WordScene
    ... this.move() ... -- IWord
    ... this.isAtBottom( ) ... -- boolean
    ... this.isActive().. -- boolean
    ... this.getTypedString() ... -- String
    ... this.transformToInactive().. -- IWord
    ... this.matches(String key) ... -- boolean
    ... this.isRemoved() ... -- boolean
    ... this.removeFirst(String that) ... -- IWord

    Methods for fields:
    ..this.scene...

  */
  //Draws the word on the given scene
  public WorldScene draw(WorldScene scene) {
    return scene.placeImageXY(new TextImage(this.text, 
      TEXTSIZE, Color.CYAN), 
      this.x, SCENESIZE - this.y);
  }
  
  // Moves the word upwards

  public IWord move() {
    return new ActiveWord(this.text,  this.x, this.y - 30);
  }

  //Checks if the word has reached the bottom of the screen
  public boolean isAtBottom() {
    return this.y <= 0;
  }

  //Checks if the word is active (not yet typed)
  public boolean isActive() {
    return true;
  }

  //Gets the string representation of the word
  public String getTypedString() {
    return this.text;
  }
  
  
  //Transforms the word into an inactive state
  public IWord transformToInactive() {
    return new InactiveWord(this.text, this.x, SCENESIZE - this.y);
  }
  
  // Checks if the active word matches the given key
  public boolean matches(String key) {
    return this.text.equals(key);
  }
  
  // The active word is not removed
  public boolean isRemoved() {
    return false;
  }
  
  public IWord removeFirst(String that) {
    if (this.text.startsWith(that)) {
      String newWord = this.text.substring(1);
      if (newWord.isEmpty()) {
        return new InactiveWord("", this.x, this.y);
      } else {
        return new ActiveWord(newWord, this.x, this.y);
      }
    } else {
      return this;
    }
  }
}

//check if you should get rid of first letter
//reduce the word and get rid ofthe first letter
//get active untill empty

// Represents an inactive word
class InactiveWord implements IWord, IConstant {
  String text;
  int x;
  int y;

  // Constructor
  InactiveWord(String text, int x, int y) {
    this.text = text;
    this.x = x;
    this.y = y;
  }
  
  //ZTypeGame
  /* TEMPLATE:
    Fields:
    ... this.text ...     -- String
    ... this.x ...       -- int
    ... this.y ...       -- int

    Methods:
    ... this.draw(WordScene scene) ...   -- WordScene
    ... this.move() ... -- IWord
    ... this.isAtBottom( ) ... -- boolean
    ... this.isActive().. -- boolean
    ... this.getTypedString() ... -- String
    ... this.transformToInactive().. -- IWord
    ... this.matches(String key) ... -- boolean
    ... this.isRemoved() ... -- boolean
    ... this.removeFirst(String that) ... -- IWord

    Methods for fields:
    ..this.scene...
    
    */

  //Draws the word on the given scene
  public WorldScene draw(WorldScene scene) {
    return scene.placeImageXY(new TextImage(this.text, 
      TEXTSIZE, Color.BLUE),
      this.x, SCENESIZE - this.y);
  }

  //Moves the word upwards
  public IWord move() {
    return new InactiveWord(this.text, this.x, SCENESIZE - this.y);
  }

  //checks if the words is at the bottom
  public boolean isAtBottom() {
    return false;
  }

  //checks if the word is active
  public boolean isActive() {
    return false;
  }

  //gets the types string
  public String getTypedString() {
    return this.text;
  }

  //transforms an active word to inactive
  public IWord transformToInactive() {
    return this;
  }
  
  // The inactive word does not match any key
  public boolean matches(String key) {
    return false;
  }
  
  // The inactive word is removed
  public boolean isRemoved() {
    return true;
  }
  
  //removes the first letter in the word
  public IWord removeFirst(String that) {
    if (this.text.startsWith(that)) {
      String newWord = this.text.substring(1);
      if (newWord.isEmpty()) {
        return new InactiveWord("", this.x, this.y);
      } else {
        return new ActiveWord(newWord, this.x, this.y);
      }
    } else {
      return this;
    }
  }



}


// to represent tests and examples for ZTypeGame
class Examples implements IConstant {
  IWord activeWord = new ActiveWord("", 0, 0);
  IWord activeWord1 = new ActiveWord("hello", 0, 0);
  IWord newactiveWord1 = new ActiveWord("ello", 0, 0);
  IWord activeWord2 = new ActiveWord("world", 0, 50);
  IWord newactiveWord2 = new ActiveWord("orld", 0, 50);
  IWord activeWord3 = new ActiveWord("java", 0, 100);
  IWord newactiveWord3 = new ActiveWord("ava", 0, 100);
  IWord activeWord4 = new ActiveWord("hello", 0, 500);
  IWord inactiveWord1 = new InactiveWord("java", 0, 100);
  IWord inactiveWord2 = new InactiveWord("java", 0, 0);
  ILoWord emptyList = new MtLoWord();
  ILoWord wordList = new ConsLoWord(activeWord1, 
      new ConsLoWord(this.activeWord2, 
      new ConsLoWord(activeWord3, 
      emptyList)));
  ILoWord wordList1 = new ConsLoWord(activeWord1,
      new ConsLoWord(activeWord2,
      new ConsLoWord(activeWord3,
      new ConsLoWord(activeWord1, emptyList))));
  ILoWord wordList2 = new ConsLoWord(newactiveWord1,
      new ConsLoWord(activeWord2,
      new ConsLoWord(activeWord3,
      new ConsLoWord(newactiveWord1, emptyList))));
    
    
  ILoWord oneWord = new ConsLoWord(activeWord1, emptyList);
    
    
 
    
    

  //calls the ZTypeGame
  void startGame() {
  
    Random rand = new Random();
    ILoWord List = new ConsLoWord(new ActiveWord("hello", 250, 500), new MtLoWord());
    ZTypeGame gameList = new ZTypeGame(List, false, 0, rand);

    gameList.bigBang(SCENESIZE, SCENESIZE, TICKRATE);
  }

  public void testGame(Tester t) {
    Examples examples = new Examples();
    examples.startGame();

  }
  
  
    
  // represents test for the method containsName
  boolean testremoveFirst(Tester t) {
    return t.checkExpect(activeWord1.removeFirst("h"), this.newactiveWord1)
      && t.checkExpect(activeWord2.removeFirst("w"), this.newactiveWord2)
      && t.checkExpect(activeWord3.removeFirst("j"), this.newactiveWord3)
      && t.checkExpect(inactiveWord1.removeFirst("h"), this.inactiveWord1)
      && t.checkExpect(activeWord2.removeFirst("p"), this.activeWord2);
  
  }
  
  
  //represents test for the method moveAll
  boolean testmoveAll(Tester t) {
    return t.checkExpect(wordList.moveAll(), new ConsLoWord(new ActiveWord("hello", 0, -30),
                new ConsLoWord(new ActiveWord("world", 0, 20),
                        new ConsLoWord(new ActiveWord("java", 0, 70),
                                       this.emptyList))))
      && t.checkExpect(emptyList.moveAll(), this.emptyList);
  }

  //represents test for the method is At Bottom
  boolean testisAtBottom(Tester t) {
    return t.checkExpect(activeWord1.isAtBottom(), true)
      && t.checkExpect(activeWord2.isAtBottom(), false)
      && t.checkExpect(activeWord3.isAtBottom(), false)
      && t.checkExpect(activeWord1.isAtBottom(), true)
      && t.checkExpect(inactiveWord2.isAtBottom(), false);

  }
  
  //represents test for the method find Matching word 
  boolean testFindMatchingWord(Tester t) {
    return t.checkExpect(wordList.findMatchingWord("hello"), this.activeWord1)
      && t.checkExpect(emptyList.findMatchingWord(""), null)
      && t.checkExpect(wordList1.findMatchingWord("world"), this.activeWord2)
      && t.checkExpect(wordList1.findMatchingWord("java"), this.activeWord3)
      && t.checkExpect(wordList1.findMatchingWord("hi"), null)
      && t.checkExpect(oneWord.findMatchingWord("hello"), this.activeWord1)
      && t.checkExpect(oneWord.findMatchingWord("hi"), null);
  }

  
  
  
  //represents test for the method for count Removed
  boolean testIsRemoved(Tester t) {
    return t.checkExpect(activeWord1.isRemoved(), false)
      && t.checkExpect(activeWord2.isRemoved(), false)
      && t.checkExpect(activeWord3.isRemoved(), false)
      && t.checkExpect(activeWord4.isRemoved(), false)
      && t.checkExpect(inactiveWord1.isRemoved(), true)
      && t.checkExpect(inactiveWord2.isRemoved(), true);
  }
}
  
 







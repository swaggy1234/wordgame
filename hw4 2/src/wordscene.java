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

  // Draws the game in its current state
  public WorldScene makeScene() {
    WorldScene background = new WorldScene(SCENESIZE, SCENESIZE);
    return this.words.draw(background);
  }

  // Handles ticking of the clock and updating the world if needed
  public World onTick() {
    if (!this.gameOver) {
      this.words = this.words.moveAll();
      if (this.words.isWordAtBottom()) {
        this.gameOver = true;
      }
      this.words = this.words.addToEnd(this.createWord());
    }
    return this;
  }


  public World onKey(String key) {
    if (!this.gameOver) {
      // Find the first matching active word
      IWord matchingWord = this.words.findMatchingWord(key);

      if (matchingWord != null) {
        // Remove the typed string from the active word
        this.words = this.words.checkAndReduce(key);
        // Update the score
        this.score += 1;
      }
    }
    return this;
  }

  // Draws the final scene when the game is over
  public WorldScene lastScene(String msg) {
    WorldScene background = new WorldScene(SCENESIZE, SCENESIZE);
    WorldImage gameOver = new TextImage(msg, 100, Color.RED);
    WorldImage scoreText = new TextImage("Score: " + this.score, 50, Color.BLACK);
    background.placeImageXY(gameOver, SCENESIZE / 2, SCENESIZE / 2)
    .placeImageXY(scoreText, SCENESIZE / 2, SCENESIZE / 2 + 50);
    return background;
  }

  // Generates a random word
  public IWord createWord() {
    Random rand = new Random();
    String randomString = generateRandomString(rand.nextInt(5) + 1); // Generate word with length 1-5
    int x = rand.nextInt(SCENESIZE);
    int y = SCENESIZE;
    return new ActiveWord(randomString, x, y);
  }

  // Generates a random string of the specified length
  public String generateRandomString(int length) {
    Random rand = new Random();
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < length; i++) {
      char randomChar = (char) (rand.nextInt(26) + 'a'); // Generate a random lowercase character
      sb.append(randomChar);
    }

    return sb.toString();
  }
}

// Represents a list of words
interface ILoWord extends IConstant{
  // Draws all the words in the list onto the given background
  WorldScene draw(WorldScene background);

  // Moves all the words in the list
  ILoWord moveAll();

  // Adds a word to the end of the list
  ILoWord addToEnd(IWord word);

  // Checks if any word has reached the bottom of the scene
  boolean isWordAtBottom();

  // Counts the number of removed words
  int countRemoved();

  // Finds the first matching active word in the list
  IWord findMatchingWord(String key);

  // Replaces an old word with a new word in the list
  ILoWord replace(IWord oldWord, IWord newWord);

  //Takes in a string of length one and removes the first letter of IWord if it contains the string
  ILoWord checkAndReduce(String that);
}

// Represents an empty list of words
class MtLoWord implements ILoWord {
  // Draws all the words in the empty list (none)
  public WorldScene draw(WorldScene background) {
    return background;
  }

  // Moves all the words in the empty list (none)
  public ILoWord moveAll() {
    return this;
  }

  // Adds a word to the end of the empty list
  public ILoWord addToEnd(IWord word) {
    return new ConsLoWord(word, this);
  }

  // No word has reached the bottom of the scene
  public boolean isWordAtBottom() {
    return false;
  }

  // No word to count in the empty list
  public int countRemoved() {
    return 0;
  }

  // No matching word in the empty list
  public IWord findMatchingWord(String key) {
    return null;
  }

  // No word to replace in the empty list
  public ILoWord replace(IWord oldWord, IWord newWord) {
    return this;
  }

  public ILoWord checkAndReduce(String that) {
    return this;
  }

}

// Represents a non-empty list of words
class ConsLoWord implements ILoWord {
  IWord first;
  ILoWord rest;

  // Constructor
  ConsLoWord(IWord first, ILoWord rest) {
    this.first = first;
    this.rest = rest;
  }

  // Draws all the words in the list onto the given background
  public WorldScene draw(WorldScene background) {
    return this.rest.draw(this.first.draw(background));
  }

  // Moves all the words in the list
  public ILoWord moveAll() {
    return new ConsLoWord(this.first.move(), this.rest.moveAll());
  }

  // Adds a word to the end of the list
  public ILoWord addToEnd(IWord word) {
    return new ConsLoWord(this.first, this.rest.addToEnd(word));
  }

  // Checks if any word has reached the bottom of the scene
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

  public ILoWord checkAndReduce(String that) {
    return new ConsLoWord(this.first.removeFirst(that), this.rest.checkAndReduce(that));
  }
}

// Represents a word in the game
interface IWord extends IConstant{
  // Draws the word onto the given background
  WorldScene draw(WorldScene background);

  // Moves the word down
  IWord move();

  // Checks if the word has reached the bottom of the scene
  boolean isAtBottom();

  // Checks if the word matches the given key
  boolean matches(String key);

  // Checks if the word is active
  boolean isActive();

  // Checks if the word is removed
  boolean isRemoved();

  // Transforms an active word into an inactive word
  IWord transformToInactive();

  //Removes the first occurrence of the given string in the word
  IWord removeFirst(String that);

}

// Represents an active word
class ActiveWord implements IWord {
  String word;
  int x;
  int y;

  // Constructor
  ActiveWord(String word, int x, int y) {
    this.word = word;
    this.x = x;
    this.y = y;
  }

  // Draws the active word onto the given background
  public WorldScene draw(WorldScene background) {
    return background.placeImageXY(new TextImage(this.word, TEXTSIZE, Color.BLACK), this.x, this.y);
  }

  // Moves the active word down
  public IWord move() {
    return new ActiveWord(this.word, this.x, this.y - 5);
  }

  // Checks if the active word has reached the bottom of the scene
  public boolean isAtBottom() {
    return this.y <= 0;
  }

  // Checks if the active word matches the given key
  public boolean matches(String key) {
    return this.word.equals(key);
  }

  // The active word is active
  public boolean isActive() {
    return true;
  }

  // The active word is not removed
  public boolean isRemoved() {
    return false;
  }

  // Transforms the active word into an inactive word
  public IWord transformToInactive() {
    return new InactiveWord(this.word, this.x, this.y);
  }

  public IWord removeFirst(String that) {
    if (this.word.contains(that)) {
      int index = this.word.indexOf(that);
      String newWord = this.word.substring(0, index) + this.word.substring(index + 1);
      return new ActiveWord(newWord, this.x, this.y);
    } else {
      return this;
    }
  }

}

// Represents an inactive word
class InactiveWord implements IWord {
  String word;
  int x;
  int y;

  // Constructor
  InactiveWord(String word, int x, int y) {
    this.word = word;
    this.x = x;
    this.y = y;
  }

  // Draws the inactive word onto the given background
  public WorldScene draw(WorldScene background) {
    return background.placeImageXY(new TextImage(this.word, TEXTSIZE, Color.GRAY), this.x, this.y);
  }

  // The inactive word does not move
  public IWord move() {
    return this;
  }

  // The inactive word is not at the bottom
  public boolean isAtBottom() {
    return false;
  }

  // The inactive word does not match any key
  public boolean matches(String key) {
    return false;
  }

  // The inactive word is not active
  public boolean isActive() {
    return false;
  }

  // The inactive word is removed
  public boolean isRemoved() {
    return true;
  }

  // The inactive word cannot transform to inactive
  public IWord transformToInactive() {
    return this;
  }

  public IWord removeFirst(String that) {
    if (this.word.contains(that)) {
      int index = this.word.indexOf(that);
      String newWord = this.word.substring(0, index) + this.word.substring(index + 1);
      return new ActiveWord(newWord, this.x, this.y);
    } else {
      return this;
    }
  }

}

// runs the game by creating a world and calling bigBang
class Examples implements IConstant{
  Examples() {}

  boolean testGame(Tester t) {
    Random random = new Random();
    ZTypeGame gameWorld = new ZTypeGame(new MtLoWord(), true, 0, random);
    int sceneSize = ZTypeGame.SCENESIZE;
    return gameWorld.bigBang(sceneSize, sceneSize, 1);
  }
}




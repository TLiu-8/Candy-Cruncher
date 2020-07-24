/**
 * CandyCruncher.java
 * Version 4.0
 * Author: Theo Liu
 * 11/6/2018
 * Plays a candy crush game by itself, candies don't regenerate, game ends when no possible moves
 */

import java.util.Scanner; // import statments
import java.io.File; 

class CandyCruncherFinale{ 
  public static void main(String[] args) throws Exception{ 
    
    System.out.println("Welcome to Candy Cruncher!"); 
     
    String[][] board = BoardReader();                     // initial board taken from file
    int row = Integer.parseInt(board[0][0]); 
    int column = Integer.parseInt(board[1][0]);
    
    int totalScore = 0;                                // initial score
     
    do{ 
      System.out.println("Next switch...");
      for (int l = 2; l < row+2; l++){ 
        for (int k = 0; k < column; k++){                 // print out modified board
         System.out.print(board[l][k]+" "); 
        } 
        System.out.println(); 
      }
        
      board = gamePlayer(board,totalScore);                   // call main function that finds best possible switch and returns board with blanks, and gravity taken into account
        
      if (board[0][0] != "-1"){                                        
       totalScore = Integer.parseInt(board[0][1]); 
       System.out.println("The total score is:"+totalScore); 
      }
        
    }while (board[0][0] != "-1");          // end condition, when game ends board[0][0] is set to -1.
    
    System.out.println("No more possible switches!");
    System.out.println("Your final score is:"+totalScore); 
    System.out.println("Game is Over."); 
  }
  
   /**
   * gamePlayer
   * method takes in letters and score, finds # of valid swaps, determines best swap
   * produces new array with empty spaces from grouping, drops candies down, the returns new array
   * @param String[][] letters to be copied and then modified, totalScore to continiously add scores previously earned
   * @return new array with all changes
   */
  
  public static String[][] gamePlayer(String[][] letters, int totalScore) throws Exception { 
    // INITIALIZING VARIABLES
    // gets the number of rows and columns 
    int rows = Integer.parseInt(letters[0][0]); 
    int columns = Integer.parseInt(letters[1][0]);
  
    // Convert to board to char array by removing first two rows (these rows hold information like the score and # of rows/ columns) for simplicity
    char[][] candiesBoard = new char[rows][columns];
    for (int i = 2; i < rows + 2; i++){
      for (int j = 0; j < columns; j++){
        candiesBoard[i - 2][j] = letters[i][j].charAt(0);
      }
    }
  
    // conditions
    boolean firstTimeCheck = true;
  
    // initial these values here so they can be used in both horizontal and vertical 
    int validSwaps = 0; 
    int totalPoints = 0; 
    
    // store of swapped coordinates
    int[] coordinateSwitchedList = new int[4];
    int[] bestCoordinateSwitchedList = new int[4];
  
    // index of past found letter 
    int[] pastLetter = new int[2];
    // past found letter store,
    String[][] paststore = new String[1][1];
    paststore[0][0] = "!";
  
    // initialzing variables (two types, one general, one is the best case based on storing best case)
    String storeA = " ", storeB = " ", beststoreA=" ", beststoreB=" ";
    int bestAValue = 0, bestBValue = 0, highest = 0;
  
    // creates new board to be returned 
    String[][] newBoard = new String[rows+2][columns];   
    newBoard[0][0] = Integer.toString(rows);
    newBoard[1][0] = Integer.toString(columns);
  
    // SWITCHING AND CHECKING
    
    // horizontal switch 
    for (int i = 0; i < rows; i++){ 
      for (int j = 1; j < columns; j++){
        totalPoints = totalScore;
        char[][] candies = Switcher(candiesBoard, i, j, i, j - 1, rows, columns);    // create candies array which is switched.
        
        int aValue = 1 + GroupingChecker(candies, i, j, rows, columns, pastLetter, paststore);           // first checks the coordinate on the right then on the left.
        storeA = paststore[0][0];
        int bValue = 1 + GroupingChecker(candies, i, j-1, rows, columns, pastLetter, paststore);
        storeB = paststore[0][0];
        
        coordinateSwitchedList[0] = i;              // stores the coordinates in the array.
        coordinateSwitchedList[1] = j;
        coordinateSwitchedList[2] = i;
        coordinateSwitchedList[3] = j-1;
       
        if ( (aValue >= 3)|| (bValue >= 3) ){                         // conditions on whether to add points
          if ( (aValue >= 3) && (bValue >= 3) ){
            totalPoints = totalPoints+ aValue + bValue;
          } else if (aValue >= 3){
            totalPoints = totalPoints + aValue;
          } else if (bValue >= 3){
            totalPoints = totalPoints + bValue;
          }  
          validSwaps++;                           //if you add points, you must add a valid swap.
            
          if (firstTimeCheck == true){                 // store these key variables into an equivalent "best" variable the first time.
          highest = totalPoints;
          beststoreA = storeA;
          beststoreB = storeB;
          bestAValue = aValue;
          bestBValue = bValue;
          for (int k = 0; k < 4; k++){
            bestCoordinateSwitchedList[k] = coordinateSwitchedList[k];
          } 
          for (int k = 2; k < rows+2; k++){ 
            for (int l = 0; l < columns; l++){ 
              newBoard[k][l] = Character.toString(candies[k-2][l]); 
            }
          }
          firstTimeCheck = false;
        } else if (totalPoints > highest){               // second time if another switch has a greater score, reset the variables.
          highest = totalPoints;
          beststoreA = storeA;
          beststoreB = storeB;
          bestAValue = aValue;
          bestBValue = bValue;
          for (int s = 0; s < 4; s++){
            bestCoordinateSwitchedList[s] = coordinateSwitchedList[s];
          }
          for (int k = 2; k < rows+2; k++){ 
            for (int l = 0; l < columns; l++){ 
              newBoard[k][l] = Character.toString(candies[k-2][l]); 
              }
            } 
          }
        }
      }
    }
              
    // vertical switch 
    for (int i = 0; i < columns; i++){
      for (int j = 1; j < rows; j++){
        totalPoints = totalScore;             // resets total points to originally given total score, so that total points does not keep accumulating
        char[][] candies = Switcher(candiesBoard, j, i, j - 1, i, rows, columns);
      
        int aValue = 1 + GroupingChecker(candies, j, i, rows, columns, pastLetter, paststore);              
        storeA = paststore[0][0];
        int bValue = 1 + GroupingChecker(candies, j - 1, i, rows, columns, pastLetter, paststore);
        storeB = paststore[0][0];
      
        coordinateSwitchedList[0] = j;
        coordinateSwitchedList[1] = i;
        coordinateSwitchedList[2] = j-1;
        coordinateSwitchedList[3] = i;
      
        if (aValue >= 3 || bValue >= 3){ 
          if (aValue >= 3 && bValue >= 3){
            validSwaps++;
            totalPoints = totalPoints + aValue + bValue;
          } else if (aValue >= 3){
            validSwaps++;
           totalPoints = totalPoints + aValue;
          } else if (bValue >= 3){
            validSwaps++;
            totalPoints = totalPoints+bValue;
          }
        
          if (firstTimeCheck == true){
            highest = totalPoints;                                       // same process but checking down the up.
            beststoreA = storeA;
            beststoreB = storeB;
            bestAValue = aValue;
            bestBValue = bValue;
            for (int k = 0; k < 4; k++){
              bestCoordinateSwitchedList[k] = coordinateSwitchedList[k];
            }
            for (int k = 2; k < rows+2; k++){ 
              for (int l = 0; l < columns; l++){ 
                newBoard[k][l] = Character.toString(candies[k-2][l]); 
              }
            } 
            firstTimeCheck = false;
          } else if (totalPoints > highest){
            highest = totalPoints;
            beststoreA = storeA;
            beststoreB = storeB;
            bestAValue = aValue;
            bestBValue = bValue;
            for (int s = 0; s < 4; s++){
              bestCoordinateSwitchedList[s] = coordinateSwitchedList[s];
            }
            for (int k = 2; k < rows+2; k++){ 
              for (int l = 0; l < columns; l++){ 
                newBoard[k][l] = Character.toString(candies[k-2][l]); 
              }
            } 
          }
        }
      }
    }
  
    // if there is no  valid swaps then end the game
    if (validSwaps == 0){ 
      newBoard[0][0] = "-1";
      return newBoard;
    }
  
    // if there are valid swaps display swaps, coordinate switching
  
    System.out.println("The number of valid swaps is: " + validSwaps);
    System.out.println("Switching coordinates: ("+ bestCoordinateSwitchedList[0] + "," + bestCoordinateSwitchedList[1] + ") and (" +
                         bestCoordinateSwitchedList[2] + "," + bestCoordinateSwitchedList[3] + ")");
 
    char[][] bestBoard = Switcher(candiesBoard, bestCoordinateSwitchedList[0], bestCoordinateSwitchedList[1],        // create two boards based on stored switched coordinates.
                         bestCoordinateSwitchedList[2], bestCoordinateSwitchedList[3], rows, columns);
   
    char[][] compareBoard = Switcher(candiesBoard,bestCoordinateSwitchedList[0], bestCoordinateSwitchedList[1],              // one will be modified the other will be used as a template
                         bestCoordinateSwitchedList[2] , bestCoordinateSwitchedList[3], rows, columns); 
   
    // displays board that has been switched.
    for (int k = 0; k < rows; k++){ 
     for (int l = 0; l < columns; l++){ 
      System.out.print(bestBoard[k][l]+" ");
     }
     System.out.println();
    }
 
    GroupingChecker(bestBoard, bestCoordinateSwitchedList[0], bestCoordinateSwitchedList[1], rows, columns, pastLetter, paststore);  //switches best board
    GroupingChecker(bestBoard, bestCoordinateSwitchedList[2], bestCoordinateSwitchedList[3], rows, columns, pastLetter, paststore);
   
    // convert '-' to space for valid switch and recover old value for invalid switch 
    for (int l = 0; l < rows; l++){ 
      for (int k = 0; k < columns; k++){  
       
        if(bestBoard[l][k] == '-'){
          if( (bestAValue >= 3) &&  (compareBoard[l][k] == beststoreA.charAt(0)) ){             // compareBoard is used as atemplate, best board is modified.
            bestBoard[l][k] = ' ';
          }else if( (bestBValue >= 3) && (compareBoard[l][k] == beststoreB.charAt(0)) ){
            bestBoard[l][k] = ' ';
          } else {
            if (bestAValue < 3){
              bestBoard[l][k] = beststoreA.charAt(0);           // if one of the values is less than 3 revert to original character.
            } else {
              bestBoard[l][k] = beststoreB.charAt(0);
            }
          } 
        }
      } 
    }
   
   // move down candy to fill the space 
   bestBoard = GapFiller(bestBoard, rows, columns);

   // assign the result newBoard(with String data type) to be ready to return 
   for (int k = 2; k < rows+2; k++){ 
     for (int l = 0; l < columns; l++){ 
       newBoard[k][l] = Character.toString(bestBoard[k-2][l]);
     }
   }
   
   // assign the highest score before return 
   newBoard[0][1] = Integer.toString(highest);
   
   return newBoard;   
  }
  
  /** 
   * BoardReader 
   * uses file to create array that holds row number, column number, 
   * and the letters to be displayed on the screen. 
   * @param null 
   * @return 2d array of letters. 
   */ 
   
  public static String[][] BoardReader() throws Exception{ 
    File myFile = new File("board.txt"); 
    Scanner myFileScanner = new Scanner(myFile); 
    String row, column; 
    row = myFileScanner.next(); 
    column = myFileScanner.next(); 
    String[][] board = new String[Integer.parseInt(row)+2][Integer.parseInt(column)]; //board created, has first two rows storing important data
    board[0][0] = row; 
    board[1][0] = column; 
    while (myFileScanner.hasNext()){ 
      for (int i = 2; i< (Integer.parseInt(row)+2); i++){ 
        String blank = myFileScanner.next(); 
        for (int j = 2; j< (Integer.parseInt(column)+2); j++){ 
          board[i][j-2] = blank.substring(j-2,j-1); 
        } 
      } 
    } 
    myFileScanner.close(); 
    return board; 
  }  
  
  public static char[][] Switcher(char[][] board, int coordRow1, int coordCol1,int coordRow2, int coordCol2,int  rows, int cols){
    char[][] original = new char[rows][cols];
    
    // copy original candies board 
    for (int i = 0; i < rows; i++){ 
      for (int j = 0; j < cols; j++){ 
        original[i][j] = board[i][j]; 
      } 
    }
    
    // switch and return 
    char candy = original[coordRow1][coordCol1]; 
    original[coordRow1][coordCol1] = original[coordRow2][coordCol2]; 
    original[coordRow2][coordCol2] = candy; 
    return original; 
  } 
  
  /**
   * GroupingChecker
   * takes in a coordinate, and checks all possible letters around it, before turning them to lower case.
   * In the GamePlayer method, it will check for lower cases to delete.
   * @param coordinate1, coordinate2, pastLetters to recursively hold the letters that are already lower case, row, column, 
   * count to increase size of pastLetters
   * @return number of letters in a row.
   */
  
  public static int GroupingChecker(char[][] candies,int coordRow, int coordCol, int rows, int cols, int[] storeList, String[][] store){
  
    int a = UpChecker(candies, coordRow, coordCol, rows, cols, storeList, store);
    // when  found match one, we set current one to '-' and move on, 
    // so if there is found one, last found one should be set to '-'
    if (a > 0) // Update last found candy before turn to different direction 
      candies[storeList[0]][storeList[1]] = '-';
    
    int b = LeftChecker(candies, coordRow, coordCol, rows, cols, storeList, store);
    if (b > 0) // Update last found candy before turning to different direction 
        candies[storeList[0]][storeList[1]] = '-';

    int c = DownChecker(candies, coordRow, coordCol, rows, cols, storeList, store);
    if (c > 0) // Update last found candy before turning to different direction 
        candies[storeList[0]][storeList[1]] = '-';
    
    int d = RightChecker(candies, coordRow, coordCol, rows, cols, storeList, store);
    if (d > 0) // Update last found candy before turning to different direction 
        candies[storeList[0]][storeList[1]] = '-';
    
    return a + b + c + d;
  }
  
  /**
   * UpChecker
   * Checks to up based on conditions, may modify candies to a dash, and counts how many are in a row.
   * @param coordRow, coordCol for coordinates,rows and columns for boundaries, storeList to hold index of past checked locations, 
   * and store which holds the particular value of the letter.
   * @return number of letters in a row, if there is a letter to above returns 1 + GroupingChecker.
   */
  
  public static int UpChecker(char[][] candies, int coordRow, int coordCol, int rows, int columns, int[] storeList, String[][] store){
  
    if (coordRow == 0){
      if (candies[coordRow][coordCol] == store[0][0].charAt(0)){
            // when it moves to upper edge, if it is horizontal movement then return 0
            // if is is from below to up, then it means it is last found one and should be '-' 
            if (coordCol == 0 && candies[coordRow][coordCol + 1] == '-')
                return 0;
            else if (coordCol == columns - 1 && candies[coordRow][coordCol - 1] == '-')
                return 0;
            else if (coordCol > 0 && coordCol < columns -1 && 
                    (candies[coordRow][coordCol - 1] == '-' || candies[coordRow][coordCol + 1] == '-'))
                return 0;
            else
                candies[coordRow][coordCol] = '-';
        }
        return 0;
    } else if (store[0][0].charAt(0) == candies[coordRow - 1][coordCol] && candies[coordRow][coordCol] == '-'){
        // if current is found one('-'), then we check next if it is equal found char, 
        // if so then move to next and continue. this happens when finish one direction and starts on different one
        storeList[0] = coordRow - 1;
        storeList[1] = coordCol;

        return 1 + GroupingChecker(candies, coordRow - 1, coordCol, rows, columns, storeList, store);
    } else if (candies[coordRow][coordCol] != candies[coordRow - 1][coordCol]){
        return 0;
    }else if (candies[coordRow][coordCol] == candies[coordRow - 1][coordCol] && candies[coordRow][coordCol] == ' '){         // check for gaps, dashes, and whether not the same
        return 0;
    }else if (candies[coordRow][coordCol] == candies[coordRow - 1][coordCol] && candies[coordRow][coordCol] == '-'){
        return 0;
    }else if (candies[coordRow][coordCol] == candies[coordRow - 1][coordCol]){
        storeList[0] = coordRow - 1;
        storeList[1] = coordCol;

        store[0][0] = Character.toString(candies[coordRow][coordCol]);
        candies[coordRow][coordCol] = '-';

        return 1 + GroupingChecker(candies, coordRow - 1, coordCol, rows, columns, storeList, store);
    }
    return 0;
  }
  
  /**
   * LeftChecker
   * Checks to the left based on conditions, may modify candies to a dash, and counts how many are in a row.
   * @param coordRow, coordCol for coordinates,rows and columns for boundaries, storeList to hold index of past checked locations, 
   * and store which holds the particular value of the letter.
   * @return number of letters in a row, if there is a letter to the left returns 1 + GroupingChecker.
   */
  public static int LeftChecker(char[][] candies, int coordRow, int coordCol, int rows, int columns, int[] storeList, String[][] store){
  
    if (coordCol == 0){
        if (candies[coordRow][coordCol] == store[0][0].charAt(0)){
            // when it moves to left edge, if it is vertical movement then return 0, 
            // if is is from right to left, then it means it is last found one and should be '-' 
          if (coordRow == 0 && candies[coordRow + 1][coordCol] == '-'){
                return 0;
          } else if (coordRow == rows - 1 && candies[coordRow - 1][coordCol] == '-'){
                return 0;
          } else if (coordRow > 0 && coordRow < rows - 1 &&(candies[coordRow - 1][coordCol] == '-' || candies[coordRow + 1][coordCol] == '-')){
                return 0;
          } else{
                candies[coordRow][coordCol] = '-';
          }
        }
        return 0;
    }
    else if (store[0][0].charAt(0) == candies[coordRow][coordCol - 1] && candies[coordRow][coordCol] == '-'){
        // if current is found one('-'), then we check next if it is equal found char, 
        // if so then move to next and continue. this happens when finish one direction and starts on different one
        storeList[0] = coordRow;
        storeList[1] = coordCol - 1;

        return 1 + GroupingChecker(candies, coordRow, coordCol - 1, rows, columns, storeList, store);
    } else if (candies[coordRow][coordCol] != candies[coordRow][coordCol - 1]){
        return 0;
    }else if (candies[coordRow][coordCol] == candies[coordRow][coordCol - 1] && candies[coordRow][coordCol] == ' '){        // check for gaps, dashes, and whether not the same
        return 0;
    }else if (candies[coordRow][coordCol] == candies[coordRow][coordCol - 1] && candies[coordRow][coordCol] == '-'){
        return 0;
    }else if (candies[coordRow][coordCol] == candies[coordRow][coordCol - 1]){
        storeList[0] = coordRow;
        storeList[1] = coordCol - 1;

        store[0][0] = Character.toString(candies[coordRow][coordCol]);
        candies[coordRow][coordCol] = '-';

        return 1 + GroupingChecker(candies, coordRow, coordCol - 1, rows, columns, storeList, store);
    }
    return 0;
   }
 
  
  /**
   * DownChecker
   * Checks to the left based on conditions, may modify candies to a dash, and counts how many are in a row.
   * @param coordRow, coordCol for coordinates,rows and columns for boundaries, storeList to hold index of past checked locations, 
   * and store which holds the particular value of the letter.
   * @return number of letters in a row, if there is a letter below, returns 1 + GroupingChecker.
   */
  
  public static int DownChecker(char[][] candies, int coordRow, int coordCol, int rows, int columns, int[] storeList, String[][] store){
  
    if (coordRow == rows - 1){
      if (candies[coordRow][coordCol] == store[0][0].charAt(0)){
            // when it moves to bottom edge, if it is horizontal movement then return 0, 
            // if is is from upper down to here, then it means it is last found one and should be '-' 
        if (coordCol == 0 && candies[coordRow][coordCol + 1] == '-'){
          return 0;
        } else if (coordCol == columns - 1 && candies[coordRow][coordCol - 1] == '-'){
          return 0;
        } else if (coordCol > 0 && coordCol < columns -1 &&(candies[coordRow][coordCol - 1] == '-' || candies[coordRow][coordCol + 1] == '-')){
          return 0;
        } else {
          candies[coordRow][coordCol] = '-';
        }
      }
      return 0;
    } else if (store[0][0].charAt(0) == candies[coordRow + 1][coordCol] && candies[coordRow][coordCol] == '-'){
        //if current is found one('-'), then we check next if it is equal found char, 
        // if so then move to next and continue. this happens when finish one direction and starts on different one
        storeList[0] = coordRow + 1;
        storeList[1] = coordCol;

        return 1 + GroupingChecker(candies, coordRow + 1, coordCol, rows, columns, storeList, store);
    }else if (candies[coordRow][coordCol] != candies[coordRow + 1][coordCol]){
      return 0;
    }else if (candies[coordRow][coordCol] == candies[coordRow + 1][coordCol] && candies[coordRow][coordCol] == ' '){
        return 0;
    }else if (candies[coordRow][coordCol] == candies[coordRow + 1][coordCol] && candies[coordRow][coordCol] == '-'){       // check for gaps, dashes, and whether not the same
        return 0;
    }else if (candies[coordRow][coordCol] == candies[coordRow + 1][coordCol]){
        storeList[0] = coordRow + 1;
        storeList[1] = coordCol;

        store[0][0] = Character.toString(candies[coordRow][coordCol]);
        candies[coordRow][coordCol] = '-';

        return 1 + GroupingChecker(candies, coordRow + 1, coordCol, rows, columns, storeList, store);
    }
    return 0;
  }
  
  /**
   * RightChecker
   * Checks to the right based on conditions, may modify candies to a dash, and counts how many are in a row.
   * @param coordRow, coordCol for coordinates,rows and columns for boundaries, storeList to hold index of past checked locations, 
   * and store which holds the particular value of the letter.
   * @return number of letters in a row, if there is a letter to the right returns 1 + GroupingChecker.
   */

  public static int RightChecker(char[][] candies, int coordRow, int coordCol, int rows, int columns, int[] storeList, String[][] store){
  
    if (coordCol == columns - 1){
        if (candies[coordRow][coordCol] == store[0][0].charAt(0)){
          // when it moves to right edge, if it is vertical movement then return 0, 
          // if is is from left to right, then it means it is last found one and should be '-' 
          if (coordRow == 0 && candies[coordRow + 1][coordCol] == '-'){
            return 0;
          }else if (coordRow == rows - 1 && candies[coordRow - 1][coordCol] == '-'){
            return 0;
          }else if (coordRow > 0 && coordRow < rows - 1 && (candies[coordRow - 1][coordCol] == '-' || candies[coordRow + 1][coordCol] == '-')){
            return 0;
          }else{
            candies[coordRow][coordCol] = '-';
          }
        }
        return 0;
    }
    else if (store[0][0].charAt(0) == candies[coordRow][coordCol + 1] && candies[coordRow][coordCol] == '-'){
        // if current is found one('-'), then we check next if it is equal found char, 
        // if so then move to next and continue. this happens when finish one direction and starts on different one
        storeList[0] = coordRow;
        storeList[1] = coordCol + 1;

        return 1 + GroupingChecker(candies, coordRow, coordCol + 1, rows, columns, storeList, store);
    }else if (candies[coordRow][coordCol] != candies[coordRow][coordCol + 1]){
        return 0;
    }else if (candies[coordRow][coordCol] == candies[coordRow][coordCol + 1] && candies[coordRow][coordCol] == ' '){  // check for gaps, dashes, and whether not the same
        return 0;
    }else if (candies[coordRow][coordCol] == candies[coordRow][coordCol + 1] && candies[coordRow][coordCol] == '-'){
        return 0;
    }else if (candies[coordRow][coordCol] == candies[coordRow][coordCol + 1]){ 
        storeList[0] = coordRow;
        storeList[1] = coordCol + 1;

        store[0][0] = Character.toString(candies[coordRow][coordCol]);
        candies[coordRow][coordCol] = '-';

        return 1 + GroupingChecker(candies, coordRow, coordCol + 1, rows, columns, storeList, store);
    }
    return 0;
  }
  
  /**
   * GapFiller
   * takes array of letters and if there is a blank the move down the one right above it and then set the one right above
   * it to a blank.
   * @param inital array of letters (candies), row, and column
   * @return 2d array of letters with gaps filled
   */

  public static char[][] GapFiller(char[][] candies, int row, int column){
    for (int i = 0; i < column; i ++){
      for (int j = row-1; j > 0; j--){
        for (int l = 0; l < row; l++){
          if (candies[j][i] == ' '){
            for (int k = j; k > 0; k--){          // checks from bottom up if there is a space, if there is switch the space,
              candies[k][i] = candies[k-1][i];    // and the space right above, then move on to the value above.
              candies[k-1][i] = ' ';
            }
          }
        }
      }
    }
    return candies;
  }
}
// end of program
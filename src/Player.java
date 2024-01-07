class Player{
    //basic information
    String lastName;
    String firstName;
    String levelName;

    //time management
    long startTime;
    long endTime;
    String timeElaspsed;
    
    //quiz management
    int questionSolve = 0;
    int questionSolveTemp = 0;
    int score = 0;
    
    //platformer content
    int playerPosX = 1;
    int playerPosY = 1;

    boolean lose = false; //if true, the main game stops

}
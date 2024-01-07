//self explainable functions/lines are not commented

import extensions.CSVFile;
import extensions.File;

class Main extends Program{

    //const
    final String startEndLine = "--------------------------------------------------------------------------------------";
    final char NEW_LINE = '\n';
    final int TURN_BEFORE_GAME = 5; //number of questions left to solve before a mini game starts

    Difficultie[] difficulties; // list of /Difficultie.java types
    Player player; // initialises the player type stored at /Player.java
    FileNameStorage storage; // /FileNameStorage.java
    Config config;

    void algorithm(){
        clearAllScreen();
        config = new Config();
        displayMainMenu();       
    }

    void displayMainMenu(){
        clearAllScreen();
        getColorFromFile();
        text(config.color[1][0]);
        background(config.color[1][1]);

        String choice;
        drawASCIIFromFile("../ressources/ascii/mainMenu.txt");
        
        //nav main menu
        do{
            choice = readString();
        }while(!equals(choice, "1") && !equals(choice, "2") && !equals(choice, "3"));


        clearAllScreen();
        drawASCIIFromFile("../ressources/ascii/mainMenu"+choice+".txt");
        delay(1000);
        clearAllScreen();

        if(equals(choice, "1")){
            displayGame();
        }else if(equals(choice, "2")){
            displayColorConfigMenu();
        }else{
            displayDebugMenu();
        }
        
    }

    void displayGame() {
        
        //hardness levels management
        if(!fileInFolder("../ressources/", "Difficultes.csv")){
            error("Le fichier Difficultes.csv n'est pas dans le dossier ressources/ !");
        }
        CSVFile csvConfigDifficulties = loadCSV("../ressources/Difficultes.csv");
        createDifficulties(csvConfigDifficulties);

        drawASCIIFromFile("../ressources/ascii/intro.txt");

        //Manage level files
        String[] levelFolders = getAllFilesFromDirectory("../ressources/questions");
        Level levels;

        //Manage players
        println(startEndLine);
        println("Ecrit ton niveau.(Il faut recopier votre niveau correspondant à ce qui est écrit en dessous)");
        print("Il y a : ");
        //lists every available level
        for (int i = 0; i < length(levelFolders); i++){
            print(levelFolders[i]+" ");
        }
        println();

        //get player level
        String playerLevel;
        int playerLevelID = -1;
        do{
            playerLevel = readString();
            playerLevelID = getLevelFileIdFromString(playerLevel, levelFolders);
            if(playerLevelID == -1){
                clearLine();
            }
        }while(playerLevelID == -1);
        player = newPlayer(playerLevel);

        storage = newFileNameStorage();
        levels = newLevel("../ressources/questions/", levelFolders[playerLevelID]);

        if(length(levels.matieres) == 0){
            println("Il n'y a pas de matières pour le niveau "+levels.name);
            delay(3000);
            choiceAfterGame();
        }

        //get player info
        println("Donner votre nom : ");
        player.lastName = readString();
        println("Donner votre prénom : ");
        player.firstName = readString();

        displayAnimation("../ressources/ascii/", new String[]{"introBulle1.txt", "introBulle2.txt"}, 3000);

        //lists every question subject
        println("Ecrit ta matiere.(Il faut recopier le numéro)");
        println("Il y a : ");
        for (int i = 0; i < length(levels.matieres); i++){
            println(i+" - "+levels.matieres[i].name);
        }
        //Gives the player a choice: either chooses a random subject, either all the questions are random
        println((length(levels.matieres))+" - Choix Aleatore "+NEW_LINE+ (length(levels.matieres) + 1)+" - Tous Aleatore ");

        String matiereChoice;
        boolean ok = false;
        do{
            clearLine();
            matiereChoice = readString();
            if(length(matiereChoice) == 1){
                if(stringToInt(matiereChoice) >= 0 && stringToInt(matiereChoice) <= (length(levels.matieres) + 1)){
                    ok = true;
                }
            }else{
                println("Valeur incorrecte il faut un chiffre");
            }
            
        }while(!ok);
        
        if(equals(matiereChoice, length(levels.matieres) + "")){
            matiereChoice =  (int)(random() * (length(levels.matieres) - 1)) +"";
        }

        player.startTime = getTime(); //starts a timer

        //game starts
        while(!player.lose){

            //if 0 questions left, launches a platformer 
            if(player.questionSolveTemp == TURN_BEFORE_GAME){
                startPlatformer();
                player.questionSolveTemp = 0;
            }

            //Manage if is random choice
            int choiceMatiere = 0;
            if(equals(matiereChoice, (length(levels.matieres) + 1)+"")){
                choiceMatiere = (int)(random() * length(storage.questionFile));
            }else{
                choiceMatiere = stringToInt(matiereChoice);
            }
            
            //Display random question
            if(length(levels.matieres[choiceMatiere].questions) != 0){
                int choiceQuestion = (int)(random() * length(levels.matieres[choiceMatiere].questions));

                //Display informations
                displayDifficultieLine(levels.matieres[choiceMatiere].questions[choiceQuestion]);
                displayScoreLine();
                displayQuestion(levels.matieres[choiceMatiere].questions[choiceQuestion], levels.matieres[choiceMatiere].name);
            }else{
                println("Il n'y a pas de question pour le matière "+levels.matieres[choiceMatiere].name);
                delay(3000);
                choiceAfterGame();
            }
        }

    }


    void displayColorConfigMenu(){
        String choice;
        
        drawASCIIFromFile("../ressources/ascii/colorsMenu.txt");

        do{
            choice = readString();
            if(equals(choice, "R")){
                displayMainMenu();
            }
        }while(!equals(choice, "1") && !equals(choice, "2"));

        clearAllScreen();
        drawASCIIFromFile("../ressources/ascii/colorsMenu"+choice+".txt");
        delay(1000);
        clearAllScreen();
        drawASCIIFromFile("../ressources/ascii/colorMenu.txt");

        do{
            choice = readString();
            if(equals(choice, "R")){
                displayMainMenu();
            }
        }while(!equals(choice, "1") && !equals(choice, "2") && !equals(choice, "3") 
            && !equals(choice, "4") && !equals(choice, "5") && !equals(choice, "6") && 
            !equals(choice, "7") && !equals(choice, "8"));
        
        if(equals(choice, "1")){
            config.color[1][0] = config.allColor[stringToInt(choice) - 1];
        }else{
            config.color[1][1] = config.allColor[stringToInt(choice) - 1];
        }

        saveCSV(config.color, "../ressources/config/color.csv");
        displayMainMenu();
    }

    void displayDebugMenu(){
        clearAllScreen();
        println(startEndLine);
        println("Merci de rentrer le code secret ou R pour revenir au menu");
        String code;
        do {
            code = readString();
            if(equals(code, "R")){
                displayMainMenu();
            }else{
                clearLine();
            }
        }while(!equals(code, "4445425547"));

        clearAllScreen();

        println("Liste des Fichiers Ressources :");
        displayAllFile("../ressources", 0);
        readString();
        displayMainMenu();
    }
    
//////////////////////////////////////////////PLATFORMER/////////////////////////////////////////////////////////////////////////////////////////
    void startPlatformer(){
        CSVFile mapFile = getRandomMap("../ressources/config/platformer/maps/");
        String[][] map = loadFromCSVFile(mapFile);

        for(int lig = 0; lig < length(map, 1); lig++){
            for(int col = 0; col < length(map, 2); col++){
                if(equals(map[lig][col], "Y")){
                    player.playerPosX = col;
                    player.playerPosY = lig;
                }
            }
            println();
        }

        clearAllScreen();
        fall(map);
        displayPlatformerMap(map);

        while(!isWin(map)){
            delay(200);
            println("Chosis l'action G(Gauche), D(Droite) ou S(Saut) : ");
            String choiceMove; 

            do{
                choiceMove = readString();
                if(!equals(choiceMove, "D") && !equals(choiceMove, "G") && !equals(choiceMove, "S")){
                    clearLine();
                }
            }while(!equals(choiceMove, "D") && !equals(choiceMove, "G") && !equals(choiceMove, "S"));

            if(equals(choiceMove, "S")){
                println("Chosis l'action G ou D : ");
                do{
                    choiceMove = readString();
                    if(!equals(choiceMove, "D") && !equals(choiceMove, "G")){
                        clearLine();
                    }
                }while(!equals(choiceMove, "D") && !equals(choiceMove, "G"));

                if(equals(choiceMove, "G")){
                    if(canMove(map, player.playerPosY - 1, player.playerPosX) && canMove(map, player.playerPosY - 2, player.playerPosX) &&
                        canMove(map, player.playerPosY - 3, player.playerPosX) && canMove(map, player.playerPosY - 3, player.playerPosX - 1) && canMove(map, player.playerPosY - 3, player.playerPosX - 2)){
                            jump(map, 3, 2, false);
                    }
                }
                if(equals(choiceMove, "D")){
                    if(canMove(map, player.playerPosY - 1, player.playerPosX) && canMove(map, player.playerPosY - 2, player.playerPosX) &&
                        canMove(map, player.playerPosY - 3, player.playerPosX) && canMove(map, player.playerPosY - 3, player.playerPosX + 1) && canMove(map, player.playerPosY - 3, player.playerPosX + 2)){
                            jump(map, 3, 2, true);
                    }
                }

            }

            if(equals(choiceMove, "D") && canMove(map,player.playerPosY,player.playerPosX + 1)){
                moveRight(map);
                fall(map);
                clearAllScreen();
                displayPlatformerMap(map);
            }

            if(equals(choiceMove, "G") && canMove(map,player.playerPosY,player.playerPosX - 1)){
                moveLeft(map);
                fall(map);
                clearAllScreen();
                displayPlatformerMap(map);
            }
        }
    }

    CSVFile getRandomMap(String path){
        String[] file = getAllFilesFromDirectory(path);
        CSVFile result = null;
        int choice = (int)(random() * length(getAllFilesFromDirectory(path)));
        if(verifyExtension(file[choice], ".csv")){
            result = loadCSV(path+file[choice]);
        }else{
            error(file[choice]+" n'est pas un fichier csv !");
        }

        return result;
    }

    void displayPlatformerMap(String[][] map){
        for(int lig = 0; lig < length(map, 1); lig++){
            for(int col = 0; col < length(map, 2); col++){
                print(map[lig][col]);
            }
            println();
        }
    }

    boolean canMove(String[][] map, int y, int x){
        boolean result = true;
        if(equals(map[y][x],"X") || equals(map[y][x], "F") || equals(map[y][x], "|")){
            result = false;
        }
        return result;
    }

    void moveUp(String[][] map){
        map[player.playerPosY][player.playerPosX] = " ";
        map[player.playerPosY - 1][player.playerPosX] = "Y";
        player.playerPosY = player.playerPosY - 1;
    }

    void moveDown(String[][] map){
        map[player.playerPosY][player.playerPosX] = " ";
        map[player.playerPosY + 1][player.playerPosX] = "Y";
        player.playerPosY = player.playerPosY + 1;
    }

    void moveLeft(String[][] map){
        map[player.playerPosY][player.playerPosX] = " ";
        map[player.playerPosY][player.playerPosX - 1] = "Y";
        player.playerPosX = player.playerPosX - 1;
    }

    void moveRight(String[][] map){
        map[player.playerPosY][player.playerPosX] = " ";
        map[player.playerPosY][player.playerPosX + 1] = "Y";
        player.playerPosX = player.playerPosX + 1;
    }

    void jump(String[][] map, int height, int x, boolean isRight) {
        for (int Hight = 0; Hight < height; Hight = Hight + 1) {
            clearAllScreen();
            moveUp(map);
            displayPlatformerMap(map);
            delay(500);
        }

        for (int idx = 0; idx < x; idx = idx + 1) {
            clearAllScreen();
            if(isRight){
                moveRight(map);
            }else{
                moveLeft(map);
            }
            
            displayPlatformerMap(map);
            delay(500);
        }
                
        clearAllScreen();
        fall(map);
        displayPlatformerMap(map);
        delay(500);
    }

    void fall(String[][] map) {
        while(canMove(map, player.playerPosY + 1, player.playerPosX)){
            moveDown(map);
            clearAllScreen();
            displayPlatformerMap(map);
            delay(1000);
        }
    }

    boolean isWin(String[][] map){
        return equals(map[player.playerPosY + 1][player.playerPosX],"F");
    }

    
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*/////////////////////////////////
    //      Difficulties             //
    /////////////////////////////////*/
    void createDifficulties(CSVFile csv) {
        difficulties = new Difficultie[rowCount(csv)];
        for (int line=0; line < rowCount(csv); line++) {
            difficulties[line] = newDifficultie(getCell(csv, line, 0), stringToInt(getCell(csv, line, 1)));
        }
    }
    
    Difficultie newDifficultie(String name, int scoreGive){
        Difficultie D = new Difficultie();
        D.name = name;
        D.scoreGive = scoreGive;

        return D;
    }

    //checks a given difficulties's existence and returns its ID
    int getDifficultieFromString(String difficultiesInString){
        int result = 0;
        boolean find = false;
        int idx = 0;
        while(idx < length(difficulties) && !find){
            if(equals(difficulties[idx].name, difficultiesInString)){
                result = idx;
            }
            idx++;
        } 

        return result;
    }

    /*/////////////////////////////////
    //      Player                   //
    /////////////////////////////////*/
    //create a new player
    Player newPlayer(String levelName){
        Player P = new Player();
        P.endTime = -1;
        P.levelName = levelName;
        return P;
    }

    /*/////////////////////////////////
    //      Level                    //
    /////////////////////////////////*/
    //creates a level and gets all question csv files related
    Level newLevel(String path, String folderName) {
        Level L = new Level();
        L.name = folderName;

        String[] matiereFolders = getAllFilesFromDirectory(path+folderName);
        int matiereFoldersNB = length(matiereFolders);
        L.matieres = new Matiere[matiereFoldersNB];

        for (int matiereID = 0; matiereID < length(L.matieres); matiereID = matiereID + 1) {
            L.matieres[matiereID] = newMatiere(path+folderName, substring(matiereFolders[matiereID], 0, length(matiereFolders[matiereID]) - 4));   
        }

        return L;
    }

    //checks a given level's existence and returns its ID
    int getLevelFileIdFromString(String levelInString, String[] levelFolders){
        int result = -1;
        boolean find = false;
        int idx = 0;
        while(idx < length(levelFolders) && !find){
            if(equals(levelFolders[idx], levelInString)){
                result = idx;
            }
            idx++;
        } 
        return result;
    } 

    /*/////////////////////////////////
    //      Matiere                  //
    /////////////////////////////////*/
    Matiere newMatiere(String path, String matiereFolders) {
        Matiere M = new Matiere();
        M.name = matiereFolders;

        if(!fileInFolder(path+"/", matiereFolders+".csv")){
            error("Le fichier "+matiereFolders+".csv n'est pas dans le dossier "+path+"/ !");
        }
        CSVFile csvQuestions = loadCSV(path+"/"+matiereFolders+".csv");

        M.questions = new Question[rowCount(csvQuestions)];

        for (int questionId = 0; questionId < length(M.questions); questionId = questionId + 1) {
            M.questions[questionId] = newQuestion(
                getCell(csvQuestions, questionId, 0), 
                getCell(csvQuestions, questionId, 1), 
                getCell(csvQuestions, questionId, 2), 
                getCell(csvQuestions, questionId, 3), 
                getCell(csvQuestions, questionId, 4), 
                getCell(csvQuestions, questionId, 5), 
                getCell(csvQuestions, questionId, 6)
            );
        }

        return M;
    }

    /*/////////////////////////////////
    //      Question                 //
    /////////////////////////////////*/
    Question newQuestion(String question, String q1, String q2, String q3, String q4, String solution, String difficultieString) {
        Question q = new Question();
        q.question = question;

        q.answer[0] = q1;
        q.answer[1] = q2;
        q.answer[2] = q3;
        q.answer[3] = q4;
        q.difficultie = difficulties[getDifficultieFromString(difficultieString)];
        q.solution = solution;

        return q;
    }  

    /*/////////////////////////////////
    //      FileNameStorage          //
    /////////////////////////////////*/    
    FileNameStorage newFileNameStorage(){
        FileNameStorage FNS = new FileNameStorage();
        FNS.asciiFile = getAllFilesFromDirectory("../ressources/ascii/");
        FNS.scoreboardFile = getAllFilesFromDirectory("../ressources/scoreboard/");
        FNS.questionFile = getAllFilesFromDirectory("../ressources/questions/"+player.levelName);
        return FNS;
    }

    void displayDifficultieLine(Question questionAsk){
        String difficultieNb = questionAsk.difficultie.name+"";
        String difficultieStart = " Level : ";
        println(difficultieStart + difficultieNb  + " ");
        println(startEndLine);

    }

    void displayScoreLine(){
        String scoreNb = player.score+"";
        String scoreStart = " Score : ";
        println(scoreStart + scoreNb);
        println(startEndLine);
    }

    void displayQuestion(Question questionAsk, String matiereChose){
        clearAllScreen();
        drawASCIIFromFile("../ressources/ascii/intro.txt");

        String questionStart = " Question : ";

        println("Matières : "+matiereChose);
        println(questionStart  + questionAsk.question);

        String temp = questionAsk.answer[stringToInt(questionAsk.solution)];
        int rnd = (int)(random() * 3);
        questionAsk.answer[stringToInt(questionAsk.solution)] = questionAsk.answer[rnd];
        questionAsk.answer[rnd] = temp; 
        questionAsk.solution = rnd + "";

        String reponse;
        println(" Reponses : ");
        println(" 0-"+questionAsk.answer[0]+"   "+"1-"+questionAsk.answer[1]);
        println(" 2-"+questionAsk.answer[2]+"   "+"3-"+questionAsk.answer[3]);
        do{
            reponse = readString();
        }while(!equals(reponse, "0") && !equals(reponse, "1") && !equals(reponse, "2") && !equals(reponse, "3") && !equals(reponse, "50415353"));

        displayAnimation("../ressources/ascii/", new String[]{"frame1.txt", "frame2.txt", "frame3.txt",}, 1000);

        if(equals(reponse, questionAsk.solution) || equals(reponse, "50415353")){
            player.score = player.score + questionAsk.difficultie.scoreGive;
            player.questionSolve++;
            player.questionSolveTemp++;
            displayScoreLine();
            displayframeWin();
        }else{
            player.lose = true;
            displayframeLose(questionAsk);
            readString();//use to temporise
            loseGame();
            readString();//use to temporise
            choiceAfterGame();
        }

    }

    void displayframeWin(){
        delay(1000);
        drawASCIIFromFile("../ressources/ascii/frameWin.txt");
        println(startEndLine);
        delay(1000);
    }

    void displayframeLose(Question questionLose){
        println(startEndLine);
        drawASCIIFromFile("../ressources/ascii/frameLose.txt");
        displayScoreLine();   
        println("La solution à la question "+questionLose.question+" était la numéro "+questionLose.solution+" soit la réponse : "+questionLose.answer[stringToInt(questionLose.solution)]);
        println("Tu as répondu a "+player.questionSolve+" questions");
    }

    void loseGame(){
        getTimeBetween();
        if(fileInScoreboardFolder(player.levelName+".csv")){
            CSVFile scoreFile = loadCSV("../ressources/scoreboard/"+player.levelName+".csv");
            String[][] scoreboard = loadFromCSVFile(scoreFile);

            sortArray(scoreboard, 2);

            if(stringToInt(scoreboard[length(scoreboard, 1) - 1][2]) < player.score){
                scoreboard[length(scoreboard, 1) - 1][0] = player.firstName;
                scoreboard[length(scoreboard, 1) - 1][1] = player.lastName;
                scoreboard[length(scoreboard, 1) - 1][2] = player.score+"";
                scoreboard[length(scoreboard, 1) - 1][3] = player.timeElaspsed;
                scoreboard[length(scoreboard, 1) - 1][4] = player.questionSolve + "";
            }else{
                if(stringToInt(scoreboard[length(scoreboard, 1) - 1][2]) == player.score && stringToInt(scoreboard[length(scoreboard, 1) - 1][4]) < player.questionSolve){
                    scoreboard[length(scoreboard, 1) - 1][0] = player.firstName;
                    scoreboard[length(scoreboard, 1) - 1][1] = player.lastName;
                    scoreboard[length(scoreboard, 1) - 1][2] = player.score+"";
                    scoreboard[length(scoreboard, 1) - 1][3] = player.timeElaspsed;
                    scoreboard[length(scoreboard, 1) - 1][4] = player.questionSolve + "";
                }
            }

            sortArray(scoreboard, 2);

            //displayScoreboard
            println(startEndLine);
            println("    Nom - Prenom - Score - Temps - Nombre de questions réussites");
            for (int y = 0; y < length(scoreboard, 1); y = y + 1) {
                print((y+1) + " - ");
                for (int x = 0; x < length(scoreboard, 2); x = x + 1) {
                    print(scoreboard[y][x]+" - ");
                }
                println();
            }
            println(startEndLine);
            saveCSV(scoreboard, "../ressources/scoreboard/"+player.levelName+".csv");
             
        }
    }

    void choiceAfterGame(){
        String choice;
        clearAllScreen();
        drawASCIIFromFile("../ressources/ascii/afterMenu.txt");
        
        do{
            choice = readString();
        }while(!equals(choice, "1") && !equals(choice, "2"));

        clearAllScreen();
        drawASCIIFromFile("../ressources/ascii/afterMenu"+choice+".txt");
        delay(1000);
        clearAllScreen();

        if(equals(choice, "2")){
            displayGame();
        }else{
            displayMainMenu();
        }
        
    }

    void getTimeBetween(){
        player.endTime = getTime();
        long timeMs = player.endTime - player.startTime;
        long timeS = timeMs / 1000;
        if(timeMs < 1000){
            player.timeElaspsed = timeMs + " ms";
        }else if(timeS <= 60){
            player.timeElaspsed = timeS + " secondes";
        }else if(timeS >= 60){
            player.timeElaspsed = timeS / 60 + " minutes "+ timeS% 60 + " secondes";
        }
    }
/////////////////////////////////////////////////////////TOOLS//////////////////////////////////////////////////////////////////////////////////////////
    void displayAnimation(String path, String[] frameName, int delayToWait){
        for(int frameIdx = 0; frameIdx < length(frameName); frameIdx++){
            clearAllScreen();
            
            if(!fileInAsciiFolder(frameName[frameIdx])){
                error("Le fichier "+frameName[frameIdx]+" n'est pas dans le dossier "+path+" !");
            }
            drawASCIIFromFile(path+frameName[frameIdx]);
            delay(delayToWait);
        }
    }

    void drawASCIIFromFile(String path){
        File f = newFile(path);
        while (ready(f)) {
            String currentLine = readLine(f);
            println(currentLine);
        } 
    }

    String[][] loadFromCSVFile(CSVFile csv) {
        String[][] result =  new String[rowCount(csv)][columnCount(csv)];
        for (int line=0; line < rowCount(csv); line++) {
            for (int column=0; column < columnCount(csv, line); column++) {
                result[line][column] = getCell(csv, line, column);
            }
        }

        return result;
    }

    //sorts the stats shown in the scoreboard
    void sortArray(String[][] tab, int colID) {
        boolean permutation = true;
    
        while (permutation) {
            permutation = false;
            String[] temp = new String[1];
    
            for (int i = length(tab, 1) - 1; i > 0; i--) {
                int n1 = stringToInt(tab[i][colID]);
                int n2 = stringToInt(tab[i - 1][colID]);

                if(n1 > n2){
                    temp = tab[i];
                    tab[i] = tab[i - 1];
                    tab[i - 1] = temp;
                    permutation = true;
                }

            }
        }
    }

    boolean verifyExtension(String fileName, String extensionName){
        int fileNameLength = length(fileName);
        int extensionNameLength = length(extensionName);

        if (fileNameLength < extensionNameLength) {
            return false;  
        }

        return equals(substring(fileName, fileNameLength - extensionNameLength, length(fileName)), extensionName);
    }

    //checks if a file exists in a given folder
    boolean fileInFolder(String path, String fileName){
        boolean result = false;
        String[] file = getAllFilesFromDirectory(path);
        int idx = 0;
        while(idx < length(file) && !result){
            if(equals(file[idx], fileName)){
                result = true;
            }
            idx++;
        }
        return result;
    }

    //checks if a file exists in the ascii folder
    boolean fileInAsciiFolder(String fileName){
        boolean result = false;
        int idx = 0;
        while(idx < length(storage.asciiFile) && !result){
            if(equals(storage.asciiFile[idx], fileName)){
                result = true;
            }
            idx++;
        }
        return result;
    }

    //checks if a file exists in the scoreboard folder
    boolean fileInScoreboardFolder(String fileName){
        boolean result = false;
        int idx = 0;
        while(idx < length(storage.scoreboardFile) && !result){
            if(equals(storage.scoreboardFile[idx], fileName)){
                result = true;
            }
            idx++;
        }
        return result;
    }

    void getColorFromFile(){
        if(fileInFolder("../ressources/config/", "color.csv")){
            CSVFile colorConfig = loadCSV("../ressources/config/color.csv");
            config.color = loadFromCSVFile(colorConfig);
        }
    }
    
    //prints a CSV file in a readable format
    void printCSV(CSVFile csv, String space) {
        for (int line=0; line < rowCount(csv); line++) {
            print(space+"   |");
            for (int column=0; column < columnCount(csv, line); column++) {
                print(getCell(csv, line, column)+"|");
            }
            println();
        }
    }

    void displayAllFile(String path, int deep) {
        String[] fichiersEtDossiers = getAllFilesFromDirectory(path);

        if (fichiersEtDossiers != null) {
            int nombreElements = length(fichiersEtDossiers);
            for (int i = 0; i < nombreElements; i++) {
                String fichierOuDossier = fichiersEtDossiers[i];
                String space = "";
                for (int spaceId = 0; spaceId < deep; spaceId = spaceId + 1) {
                    space = space + "  ";
                }
                if (verifyExtension(fichierOuDossier, ".csv") || verifyExtension(fichierOuDossier, ".txt") || verifyExtension(fichierOuDossier, ".md")) {
                    println(space+"  | Fichier : " + fichierOuDossier);
                    if(verifyExtension(fichierOuDossier, ".csv")){
                        CSVFile csvFile = loadCSV(path+"/"+fichierOuDossier);
                        printCSV(csvFile, space);
                    }
                } else {
                    println(space+" -- Dossier : " + fichierOuDossier);
                    displayAllFile(path+"/"+fichierOuDossier, deep + 1);
                }
            }
        }
    }

    void clearAllScreen(){
        print("\033[H\033[2J");
    }

    ///////////////////////////////////////////////TEST////////////////////////////////////////////////////////////////////////////////////
    void testVerifyExtension(){
        assertTrue(verifyExtension("../ressources/ascii/afterMenu.txt", ".txt"));
        assertFalse(verifyExtension("../ressources/ascii/afterMenu.txt", ".csv"));
    }

    void testGetDifficultieFromString(){
        difficulties = new Difficultie[2];
        difficulties[0] = newDifficultie("hard", 15);
        difficulties[1] = newDifficultie("easy", 1);
        assertEquals(0, getDifficultieFromString("hard"));

    }

    void testCanMove(){
        String[][] map = {{"X","X","X","X","X","X","X"},
                          {"|"," "," "," "," ","F","|"},
                          {"X","X","X","X","X","X","X"}};
        assertTrue(canMove(map, 1, 1));
        assertFalse(canMove(map, 1, 0));
        assertFalse(canMove(map, 2, 0));
        assertFalse(canMove(map, 1, 5));
    }

    void testIsWin(){
        String[][] map = {{"X","X","X","X","X"," ","X"},
                          {"|"," "," "," "," ","F","|"},
                          {"X","X","X","X","X","X","X"}};
        player = newPlayer("cp");
        player.playerPosX = 5;

        player.playerPosY = 0;
        assertTrue(isWin(map));

        player.playerPosY = 1;
        assertFalse(isWin(map));
    }

    void testFileInFolder(){
        assertTrue(fileInFolder("../ressources/ascii/", "afterMenu.txt"));
        assertFalse(fileInFolder("../ressources/scoreboard/", "null.jpg"));
    }

    void testGetLevelFileIdFromString(){
        String[] levelFolders = {"cp","ce1","cm1"};
        assertEquals(0, getLevelFileIdFromString("cp", levelFolders));
        assertEquals(-1, getLevelFileIdFromString("jaaj", levelFolders));
    }
}
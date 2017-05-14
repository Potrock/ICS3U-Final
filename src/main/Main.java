/*
TO DO:
- Collision against other tanks
- Settings page for tweaking sensitivity??
- Turning corners
- High score
- Fix main menu (Start button isn't chopped off + add high scores button)
 */

package main;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class Main extends Application {
    private static Stage primaryStage;
    private static AnchorPane map1p, map2p, map3p;
    private static AnchorPane endingPagep, roundinputp, leaderboardp;
    private static Scene map1, map2, map3, mainmenu, roundinput, endingPage, leaderboard;
    private static Tank player1, player2;
    private static ArrayList<Element> bullets = new ArrayList<>();
    private static ArrayList<Rectangle> walls = new ArrayList<>();
    private static int count = 0, currentMap = 0, roundCount = 0;
    static int totalRounds = 1;
    private static boolean w, a, s, d, up, down, left, right, m, q;
    private static boolean gameStarted, readyToShoot1 = false, readyToShoot2 = false, notTouching1 = true, notTouching2 = true, collisionup1 = false, collisionup2 = false;
    private static long player1Score = 0, player2Score = 0, timebullet = 0;
    private static Label labelplayer1score = new Label("Player 1: " + player1Score), labelplayer2score = new Label("Player 2: " + player2Score);
    private static int reload1, reload2;
    private static Rectangle recentwall, recentwall2, dummyrectangle, dummyrectangle2, dummyrectangle3;
    private TableView<Score> table = new TableView<Score>();
    private static final ObservableList<Score> data = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) throws Exception {
        AnchorPane stage = FXMLLoader.load(getClass().getResource("sample.fxml")); //Gets the main menu from the FXML file
        mainmenu = new Scene(stage, 400, 400); //Initializes Main Menu Scene
        primaryStage.setScene(mainmenu); //Sets the scene
        primaryStage.show(); //Shows the screen
        primaryStage.setResizable(false); //Disables resizing the screen
        Main.primaryStage = primaryStage;

        roundinputp = FXMLLoader.load(getClass().getResource("roundinput.fxml"));
        roundinput = new Scene(roundinputp, 600, 400);

        leaderboardp = FXMLLoader.load(getClass().getResource("leaderboard.fxml"));
        leaderboard = new Scene(leaderboardp, 600, 400);

        endingPagep = FXMLLoader.load(getClass().getResource("endingPage.fxml"));
        endingPage = new Scene(endingPagep, 600,400);

        leaderboardp.getChildren().add(table);

        TableColumn player1scores = new TableColumn("Player 1");
        player1scores.setMinWidth(100);
        player1scores.setCellValueFactory(new PropertyValueFactory<Score, SimpleLongProperty>("player1Score"));

        TableColumn player2scores = new TableColumn("Player 2");
        player2scores.setMinWidth(100);
        player2scores.setCellValueFactory(new PropertyValueFactory<Score, SimpleLongProperty>("player2Score"));

        table.getColumns().addAll(player1scores, player2scores);
        table.setItems(data);

        map1p = FXMLLoader.load(getClass().getResource("map.fxml")); //Just setup for the matches
        map2p = FXMLLoader.load(getClass().getResource("map.fxml"));
        map3p = FXMLLoader.load(getClass().getResource("map.fxml"));
        map1 = new Scene(map1p, 600, 400); //Initializes Map1
        map2 = new Scene(map2p, 600, 400);
        map3 = new Scene(map3p, 600, 400);

        /*
        Creates an animation that will run the handle method x times per second (60fps masterrace)
         */
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;
            @Override
            public void handle(long now) {
                if (now - lastUpdate >=8_000_000) {
                    onUpdate();
                    lastUpdate = now;
                }
            }
        };
        timer.start();

    }



    /*
    This method is just to make adding objects to the game easier, just saves some typing since most of the objects made in this game are Elements, not native JavaFX Nodes.
    */
    private static void addToGame(Element element, double x, double y, Pane map) {
        element.getView().setTranslateX(x);
        element.getView().setTranslateY(y);
        map.getChildren().add(element.getView());
    }

    public static void submitScore() {
        try {
            FileReader reader = new FileReader("src/main/scores.json");
            JSONArray scoreData = new JSONArray();
            JSONParser parser = new JSONParser();
            scoreData = (JSONArray) parser.parse(reader);
            reader.close();

            FileWriter writer = new FileWriter("src/main/scores.json");
            JSONObject currentScore = new JSONObject();
            currentScore.put("Player 1", player1Score);
            currentScore.put("Player 2", player2Score);
            scoreData.add(currentScore);
            writer.write(scoreData.toJSONString());

            for(Object record : scoreData) {
                long player1Score = (long) ((JSONObject)record).get("Player 1");
                long player2Score = (long) ((JSONObject)record).get("Player 2");
                data.add(new Score(player1Score,player2Score));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*
    Sets the game state for the match, also has all the prep for that arena (Making the walls, spawning the tanks, etc.)
    Right now, you can't set a gameState back to one that has already been created but if it's really an issue and we want more than just Best 2/3 matches we can make a workaround.
     */
    static void setGameState(gameState gameState) throws IOException {
        if (gameState.equals(Main.gameState.LEADERBOARD)) {
            primaryStage.setScene(leaderboard);
            map1p.getChildren().clear();
            map2p.getChildren().clear();
            map3p.getChildren().clear();
            roundinputp.getChildren().clear();
        } else if (roundCount < totalRounds) {
            switch (gameState) {
                case MAIN:
                    map1p.getChildren().clear();
                    map2p.getChildren().clear();
                    map3p.getChildren().clear();
                    endingPagep.getChildren().clear();
                    roundinputp.getChildren().clear();
                    primaryStage.setScene(mainmenu); //Sets the scene to the main menu, declarations already happened in the start method.
                    break;
                case ROUNDINPUT:
                    primaryStage.setScene(roundinput);
                    break;
                case MAP1:
                    readyToShoot1 = false;
                    readyToShoot2 = false;
                    reload1 = 0;
                    reload2 = 0;
                    map1p.getChildren().clear();
                    map2p.getChildren().clear();
                    map3p.getChildren().clear();
                    currentMap = 1; //Tells us that it's on Map 1.
                    primaryStage.setScene(map1); //Sets the scene to map1
                    player1 = new Tank();
                    player2 = new Tank(); //Creates both tanks
                    addToGame(player1, 30, 285, map1p);
                    addToGame(player2, 525, 255, map1p);
                    gameStarted = true; //Sets game to have started
                    createWall(10, 400, 0, 0, map1p); //These createWall methods all create the layout of the map.
                    createWall(10, 400, 590, 0, map1p);
                    createWall(600, 10, 0, 0, map1p);
                    createWall(600, 10, 0, 390, map1p);
                    createWall(90, 10, 10, 65, map1p);
                    createWall(90, 10, 10, 125, map1p);
                    createWall(150, 10, 10, 190, map1p);
                    createWall(10, 75, 160, 190, map1p);
                    createWall(90, 10, 10, 255, map1p);
                    createWall(90, 10, 10, 320, map1p);
                    createWall(10, 70, 160, 0, map1p);
                    createWall(10, 75, 160, 335, map1p);
                    createWall(10, 75, 250, 325, map1p);
                    createWall(250, 10, 220, 65, map1p);
                    createWall(10, 130, 340, 70, map1p);
                    createWall(10, 70, 250, 125, map1p);
                    createWall(230, 10, 250, 190, map1p);
                    createWall(10, 75, 480, 190, map1p);
                    createWall(125, 10, 475, 125, map1p);
                    createWall(140, 10, 250, 255, map1p);
                    createWall(10, 75, 390, 255, map1p);
                    createWall(210, 10, 390, 330, map1p);
                    Rectangle dummyrectangle = new Rectangle(100000, 1, 1000, 1000); //Creates a rectangle outside of pain that recentwall is assigned to when the bullet dies.
                    map1p.getChildren().add(dummyrectangle);

                    labelplayer1score.setTranslateX(0);
                    labelplayer1score.setTranslateY(390);
                    labelplayer1score.setFont(new Font("Arial", 10));
                    labelplayer1score.setTextFill(Color.WHITE);
                    map1p.getChildren().add(labelplayer1score);
                    labelplayer2score.setTranslateX(548);
                    labelplayer2score.setTranslateY(390);
                    labelplayer2score.setFont(new Font("Arial", 10));
                    labelplayer2score.setTextFill(Color.WHITE);
                    map1p.getChildren().add(labelplayer2score);

                    createListeners(map1p);
                    roundCount++;
                    break;
                case MAP2:
                /*
                See explanation for Map 1
                 */
                    readyToShoot1 = false;
                    readyToShoot2 = false;
                    reload1 = 0;
                    reload2 = 0;
                    map1p.getChildren().clear();
                    map2p.getChildren().clear();
                    map3p.getChildren().clear();
                    currentMap = 2;
                    createWall(10, 400, 0, 0, map2p);
                    createWall(10, 400, 590, 0, map2p);
                    createWall(600, 10, 0, 0, map2p);
                    createWall(600, 10, 0, 390, map2p);
                    createWall(90, 10, 0, 165, map2p);
                    createWall(10, 90, 80, 0, map2p);
                    createWall(10, 90, 80, 235, map2p);
                    createWall(10, 160, 160, 80, map2p);
                    createWall(10, 100, 160, 325, map2p);
                    createWall(80, 10, 160, 315, map2p);
                    createWall(10, 90, 240, 235, map2p);
                    createWall(160, 10, 240, 80, map2p);
                    createWall(80, 10, 240, 165, map2p);
                    createWall(10, 165, 315, 85, map2p);
                    createWall(85, 10, 325, 240, map2p);
                    createWall(10, 100, 315, 315, map2p);
                    createWall(10, 100, 390, 315, map2p);
                    createWall(10, 90, 500, 0, map2p);
                    createWall(110, 10, 400, 165, map2p);
                    createWall(130, 10, 490, 240, map2p);
                    createWall(10, 80, 490, 240, map2p);
                    Rectangle dummyrectangle2 = new Rectangle(10000, 1, 1000, 1000); //Creates a rectangle outside of pain that recentwall is assigned to when the bullet dies.
                    map2p.getChildren().add(dummyrectangle2);


                    labelplayer1score.setTranslateX(0);
                    labelplayer1score.setTranslateY(390);
                    labelplayer1score.setFont(new Font("Arial", 10));
                    labelplayer1score.setTextFill(Color.WHITE);
                    map2p.getChildren().add(labelplayer1score);
                    labelplayer2score.setTranslateX(548);
                    labelplayer2score.setTranslateY(390);
                    labelplayer2score.setFont(new Font("Arial", 10));
                    labelplayer2score.setTextFill(Color.WHITE);
                    map2p.getChildren().add(labelplayer2score);

                    primaryStage.setScene(map2);
                    player1 = new Tank();
                    player2 = new Tank();
                    addToGame(player1, 115, 30, map2p);
                    addToGame(player2, 455, 30, map2p);
                    gameStarted = true;
                    createListeners(map2p);
                    roundCount++;
                    break;
                case MAP3:
                /*
                See explanation for Map 1
                 */
                    readyToShoot1 = false;
                    readyToShoot2 = false;
                    reload1 = 0;
                    reload2 = 0;
                    map1p.getChildren().clear();
                    map2p.getChildren().clear();
                    map3p.getChildren().clear();
                    currentMap = 3;
                    createWall(10, 400, 0, 0, map3p);
                    createWall(10, 400, 590, 0, map3p);
                    createWall(600, 10, 0, 0, map3p);
                    createWall(600, 10, 0, 390, map3p);
                    createWall(90, 10, 0, 100, map3p);
                    createWall(90, 10, 0, 225, map3p);
                    createWall(90, 10, 0, 310, map3p);
                    createWall(10, 105, 90, 60, map3p);
                    createWall(95, 10, 90, 160, map3p);
                    createWall(10, 105, 180, 0, map3p);
                    createWall(90, 10, 180, 225, map3p);
                    createWall(420, 10, 180, 310, map3p);
                    createWall(10, 90, 180, 310, map3p);
                    createWall(90, 10, 180, 60, map3p);
                    createWall(10, 100, 270, 60, map3p);
                    createWall(95, 10, 270, 100, map3p);
                    createWall(10, 70, 355, 165, map3p);
                    createWall(60, 10, 355, 225, map3p);
                    createWall(10, 90, 415, 225, map3p);
                    createWall(10, 125, 455, 0, map3p);
                    createWall(60, 10, 455, 125, map3p);
                    createWall(10, 110, 505, 125, map3p);
                    Rectangle dummyrectangle3 = new Rectangle(100000, 1, 1000, 1000); //Creates a rectangle outside of pain that recentwall is assigned to when the bullet dies.
                    map3p.getChildren().add(dummyrectangle3);

                    labelplayer1score.setTranslateX(0);
                    labelplayer1score.setTranslateY(390);
                    labelplayer1score.setFont(new Font("Arial", 10));
                    labelplayer1score.setTextFill(Color.WHITE);
                    map3p.getChildren().add(labelplayer1score);
                    labelplayer2score.setTranslateX(548);
                    labelplayer2score.setTranslateY(390);
                    labelplayer2score.setFont(new Font("Arial", 10));
                    labelplayer2score.setTextFill(Color.WHITE);
                    map3p.getChildren().add(labelplayer2score);

                    player1 = new Tank();
                    player2 = new Tank();
                    addToGame(player1, 120, 115, map3p);
                    addToGame(player2, 315, 60, map3p);
                    gameStarted = true;
                    primaryStage.setScene(map3);
                    createListeners(map3p);
                    roundCount++;
                    break;

            }
        } else {
            map1p.getChildren().clear();
            map2p.getChildren().clear();
            map3p.getChildren().clear();
            if (primaryStage.getScene() != leaderboard) {
                primaryStage.setScene(endingPage);
            }
        }
    }

    private static void createListeners(AnchorPane map) {
        map.getScene().setOnKeyPressed(e -> { //Creates listener for key presses and sets boolean values for usage in the KeyCheck method
            if (e.getCode() == KeyCode.LEFT) {
                left = true;
            }
            if (e.getCode() == KeyCode.RIGHT) {
                right = true;
            }
            if (e.getCode() == KeyCode.M) {
                m = true;
            }
            if (e.getCode() == KeyCode.UP) {
                up = true;
            }
            if (e.getCode() == KeyCode.DOWN) {
                down = true;
            }
            if (e.getCode() == KeyCode.W) {
                w = true;
            }
            if (e.getCode() == KeyCode.S) {
                s = true;
            }
            if (e.getCode() == KeyCode.A) {
                a = true;
            }
            if (e.getCode() == KeyCode.D) {
                d = true;
            }
            if (e.getCode() == KeyCode.Q) {
                q = true;
            }
        });

        map.getScene().setOnKeyReleased(e -> { //Creates listener for key releases and sets boolean values for usage in the KeyCheck method.
            if (e.getCode() == KeyCode.LEFT) {
                left = false;
            }
            if (e.getCode() == KeyCode.RIGHT) {
                right = false;
            }
            if (e.getCode() == KeyCode.M) {
                m = false;
                readyToShoot1 = true;
            }
            if (e.getCode() == KeyCode.UP) {
                up = false;
                collisionup1 = false;
            }
            if (e.getCode() == KeyCode.DOWN) {
                down = false;
            }
            if (e.getCode() == KeyCode.W) {
                w = false;
                collisionup2 = false;
            }
            if (e.getCode() == KeyCode.S) {
                s = false;
            }
            if (e.getCode() == KeyCode.A) {
                a = false;
            }
            if (e.getCode() == KeyCode.D) {
                d = false;
            }
            if (e.getCode() == KeyCode.Q) {
                q = false;
                readyToShoot2 = true;
            }
        });

    }

    private static void createWall(int width, int height, int x, int y, Pane map) { //Takes input for the width,e height, coordinates and which Parent the wall should be in
        Rectangle rect = new Rectangle(x, y, width, height); //Creates the rectangle with a meaningless name.
        walls.add(rect); //Adds the rectangle to the arraylist of rectangles
        map.getChildren().add(rect); //Adds it to the parent that is being used.
    }

    /*
    Takes input for which players collision is being checked
    For every wall in the array list of walls (all walls in the map), it checks if the player in question is touching that wall.
    If it's hitting a wall, it pushes them backwards.
     */
    private static void colDetect(Tank player, int playerNumber) {
        if (playerNumber == 1) {
            for (Rectangle wall : walls) {
                if (player.isHitting(wall) && collisionup1) {
                    player.updateLocation(-1.75);
                } else if (player.isHitting(wall) && !collisionup1) {
                    player.updateLocation(1.75);
                }
            }
        }
        if (playerNumber == 2) {
            for (Rectangle wall : walls) {
                if (player.isHitting(wall) && collisionup2) {
                    player.updateLocation(-1.75);
                } else if (player.isHitting(wall) && !collisionup2) {
                    player.updateLocation(1.75);
                }
            }
        }
    }

    /*
    Doesnt take input because bullets interact with everything and the bullet arraylist is global.
    For every bullet that has been shot, check if it's hit each wall, and if it's hit a wall:
    Check if the wall is vertical (Width of 10), if it is, invert the X velocity of the bullet
    Check fi the wall is horizontal (width of 10), if it is, invert the Y velocity of the bullet
     */

    private static void bulletCol() {
        for (Element bullet : bullets) {
            for (Rectangle wall : walls) {
                if (bullet.isHitting(wall)){
//                if (bullet.isHitting(wall) && recentwall != wall) {
                    if (wall.getY() - bullet.getView().getTranslateY() < wall.getX() - bullet.getView().getTranslateX()) {
                        bullet.setVelocity(new Point2D(bullet.getVelocity().getX() * -1, bullet.getVelocity().getY()));
                    } else {
                        bullet.setVelocity(new Point2D(bullet.getVelocity().getX(), bullet.getVelocity().getY() * -1));
                    }
//                    recentwall = wall;
                }

            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    /*
    Adds a bullet to the game (called when M or Q is being pressed)
    Adds the bullet to the arraylist of bullets (global)
    Adds it to the game
    Sets the count to 0 to stop people from being killed by their own bullet
     */
    private void addBullet(Element element, double x, double y, Pane map) {
        bullets.add(element);
        addToGame(element, x, y, map);
        count = 0;
    }


    /*
    Called when somebody dies
    Depending on the map being played it will go to a different task in the switch statement.
    Clears the walls array list to stop people from being blocked by non-existant walls
    Clears the bullet array list just because
     */
    private void resetMatch() {
        switch (currentMap) {
            case 1:
                try {
                    walls.clear();
                    bullets.clear();
                    setGameState(gameState.MAP2);
                    reload1 = 0;
                    reload2 = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    walls.clear();
                    bullets.clear();
                    setGameState(gameState.MAP3);
                    reload1 = 0;
                    reload2 = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                try {
                    walls.clear();
                    bullets.clear();
                    setGameState(gameState.MAP1);
                    reload1 = 0;
                    reload2 = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /*
    Checks if the game is started to prevent NullPointerException errors
    Checks collision detection for each loop iteration
    Checks if a player is dead and resets the match if that happened
    All the cases in the switch statement do the same thing, just for different maps.
    Starts by checking key presses/releases
    If a bullet was shot 20 iterations ago, check kill detection. Otherwise your own bullet will kill you.
    Check bullet collision
    Updates the location of every bullet, and checks if they should be removed/killed
    Finally, increases the counter count of bullets.
     */
    private void onUpdate() {
        if (gameStarted) {
            if (reload1 > 0)
                reload1--;
            if (reload2 > 0)
                reload2--;
            if (player1.dead() || player2.dead())
                resetMatch();

            switch (currentMap) {
                case 1:
                    keyCheck(map1p);
                    bulletCol();
                    if (count > 20)
                        killDetect(map1p);
                    for (Element bullet : bullets) {
                        bullet.counter++;
                        bullet.updateLocation(1);
                        if (bullet.getCounter() > 300 || bullet.dead()) {
                            map1p.getChildren().remove(bullet.getView());
                            bullet.setStatus(false);
//                            recentwall = dummyrectangle;
                        }
                    }
                    for (int i = 0; i < bullets.size() ; i++) {
                        if (bullets.get(i).dead())
                            bullets.remove(i);

                    }
                    count++;
                    labelplayer1score.setText("Player 1: " + player1Score);
                    labelplayer2score.setText("Player 2: " + player2Score);
                    break;
                case 2:
                    keyCheck(map2p);
                    bulletCol();
                    if (count > 20)
                        killDetect(map2p);
                    for (Element bullet : bullets) {
                        bullet.counter++;
                        bullet.updateLocation(1);
                        if (bullet.getCounter() > 300 || bullet.dead()) {
                            map2p.getChildren().remove(bullet.getView());
                            bullet.setStatus(false);
//                            recentwall = dummyrectangle2;
                        }
                    }
                    for (int i = 0; i < bullets.size() ; i++) {
                        if (bullets.get(i).dead())
                            bullets.remove(i);

                    }
                    count++;
                    labelplayer1score.setText("Player 1: " + player1Score);
                    labelplayer2score.setText("Player 2: " + player2Score);
                    break;
                case 3:
                    keyCheck(map3p);
                    bulletCol();
                    if (count > 20)
                        killDetect(map3p);
                    for (Element bullet : bullets) {
                        bullet.counter++;
                        bullet.updateLocation(1);
                        if (bullet.getCounter() > 300 || bullet.dead()) {
                            map3p.getChildren().remove(bullet.getView());
                            bullet.setStatus(false);
//                            recentwall = dummyrectangle3;
                        }
                    }
                    for (int i = 0; i < bullets.size() ; i++) {
                        if (bullets.get(i).dead())
                            bullets.remove(i);

                    }
                    count++;
                    labelplayer1score.setText("Player 1: " + player1Score);
                    labelplayer2score.setText("Player 2: " + player2Score);
                    break;
            }
        }
    }

    /*
    Checks the key presses and releases for both player1 and player2, for a more in-depth explanation look at the inline comments.
     */
    private void keyCheck(Pane map) {
        if (left) { //If left is true it runs the left turn logic
            for (Rectangle wall : walls) { //For every wall in the array list, check if the player is touching one and prevent turning if it is.
                if (player1.isHitting(wall)) {
                    notTouching1 = false;
                    player1.rotateRight();
                }
            }
            if (notTouching1) {
                player1.rotateLeft(); //Only turn if it's not touching the wall
            }
            notTouching1 = true;
        }
        if (right) {
            for (Rectangle wall : walls) {
                if (player1.isHitting(wall)) {
                    notTouching1 = false;
                    player1.rotateLeft();
                }
            }
            if (notTouching1) {
                player1.rotateRight();
            }
            notTouching1 = true;
        }

        if (up) {
            player1.updateLocation(1.75); //Updates the players location (Constant is for changing direction/speed)
            for (Rectangle wall : walls) {
                if (player1.isHitting(wall)) {
                    collisionup1 = true;
                    colDetect(player1, 1);
                }
            }
        }

        if (down) { //Same logic as up, but if it's going into a wall make it go forward instead of back.
            player1.updateLocation(-1.75);
            for (Rectangle wall : walls) {
                if (player1.isHitting(wall)) {
                    collisionup1 = false;
                    colDetect(player1, 1);
                }
            }
        }

        if (w)
            player2.updateLocation(1.75); //Below this repeats the logic above for the other player
        for (Rectangle wall : walls) {
            if (player2.isHitting(wall)) {
                collisionup2 = true;
                colDetect(player2, 2);
            }
        }

        if (s) {
            player2.updateLocation(-1.75);
            for (Rectangle wall : walls) {
                if (player2.isHitting(wall)) {
                    collisionup2 = false;
                    colDetect(player2, 2);
                }
            }
        }

        if (a) {
            for (Rectangle wall : walls) { //For every wall in the array list, check if the player is touching one and prevent turning if it is.
                if (player2.isHitting(wall)) {
                    notTouching2 = false;
                    player2.rotateRight();
                }
            }
            if (notTouching2) {
                player2.rotateLeft(); //Only turn if it's not touching the wall
            }
            notTouching2 = true;
        }

        if (d) {
            for (Rectangle wall : walls) {
                if (player2.isHitting(wall)) {
                    notTouching2 = false;
                    player2.rotateLeft();
                }
            }
            if (notTouching2) {
                player2.rotateRight();
            }
            notTouching2 = true;
        }

        if (m && readyToShoot1 && reload1 == 0) { //Checks if M is true and the player is allowed to shoot again
            if (player1.alive()) { //Checks if the player is actually alive
                Bullet bullet = new Bullet(); //Creates a new bullet
                bullet.setVelocity(player1.getVelocity().normalize().multiply(3)); //Sets the velocity to 3x that of the player who shot it
                addBullet(bullet, player1.getView().getTranslateX(), player1.getView().getTranslateY(), map); //Adds the bullet
                reload1 = 300;
            }
        }

        if (q && readyToShoot2 && reload2 == 0) {
            if (player2.alive()) {
                Bullet bullet = new Bullet();
                bullet.setVelocity(player2.getVelocity().normalize().multiply(3));
                addBullet(bullet, player2.getView().getTranslateX(), player2.getView().getTranslateY(), map);
                reload2 = 300;
            }
        }
    }

    /*
    For every bullet in the game, check if it's hitting a player.
    If it's hitting a player, kill the player and remove it from the scene.
    Also, remove the bullet.
     */
    private void killDetect(AnchorPane map) {
        for (Element bullet : bullets) {
            if (bullet.isHitting(player1.getView())) {
                player1.setStatus(false);
                map.getChildren().remove(player1.getView());
                System.out.println("player 1 dead");
                map.getChildren().remove(bullet.getView());
                player2Score++;
            }
            if (bullet.isHitting(player2.getView())) {
                player2.setStatus(false);
                map.getChildren().remove(player2.getView());
                map.getChildren().remove(bullet.getView());
                System.out.println("player 2 dead");
                player1Score++;
            }
        }
    }

    /*
    Enums for different game states.
     */
    public enum gameState {
        MAIN,
        ROUNDINPUT,
        MAP1,
        MAP2,
        LEADERBOARD, MAP3
    }

    /*
    Tank class that inherits methods from the Element class
    Makes a 25x25 blue square as a tank.
     */
    static class Tank extends Element {
        Tank() {
            super(new Rectangle(25,25,Color.BLUE));
            this.setTexture(new ImagePattern(new Image("file:tankimage.png")));

        }
    }

    /*
    Bullet class that inherits methods from the Element class.
    Makes a small black circle as a bullet.
     */
    static class Bullet extends Element {
        Bullet() {
            super(new Circle(5, 5, 5, Color.BLACK));
        }
    }


    public static class Score {
        private final SimpleLongProperty player1score = new SimpleLongProperty(-1);
        private final SimpleLongProperty player2score = new SimpleLongProperty(-1);

        public Score() {
            this(-1,-1);
        }

        public Score(long player1score, long player2score) {
            this.setPlayer1Score(player1score);
            this.setPlayer2Score(player2score);
        }

        public long getPlayer1Score() {
            return player1score.get();
        }
        public long getPlayer2Score() {
            return player2score.get();
        }

        private void setPlayer1Score(long n) {
            player1score.set(n);
        }

        private void setPlayer2Score(long n) {
            player2score.set(n);
        }
    }
}
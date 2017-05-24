package main;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application {
    private static Stage primaryStage;
    private static AnchorPane map1p, map2p, map3p;
    private static AnchorPane endingPagep;
    private static AnchorPane roundinputp;
    private static Scene map1, map2, map3, mainmenu, roundinput, endingPage, leaderboard;
    private static Tank player1, player2;
    private static ArrayList<Element> bullets = new ArrayList<>();
    private static ArrayList<Element> bullets2 = new ArrayList<>();
    private static ArrayList<Rectangle> walls = new ArrayList<>();
    private static int currentMap = 0, roundCount = 0;
    static int totalRounds = 1;
    private static boolean w, a, s, d, up, down, left, right, m, q;
    private static boolean gameStarted, notTouching1 = true, notTouching2 = true, collisionup1 = false, collisionup2 = false, tankup1 = false, tankup2 = false, bullethitwall1, bullethitwall2;
    private static long player1Score = 0, player2Score = 0;
    private static Label labelplayer1score = new Label("Player 1: " + player1Score), labelplayer2score = new Label("Player 2: " + player2Score);
    private static int reload1, reload2, autoshootdelay1, autoshootdelay2;
    private TableView<Score> table = new TableView<>();
    private static final ObservableList<Score> data = FXCollections.observableArrayList();
    private static int Wall1, Wall2, intwall, intwall2;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Gets the main menu from the FXML file
        AnchorPane stage = FXMLLoader.load(getClass().getResource("sample.fxml"));
        //Initializes Main Menu Scene
        mainmenu = new Scene(stage, 400, 400);
        //Sets the scene
        primaryStage.setScene(mainmenu);
        //Shows the screen
        primaryStage.show();
        //Disables resizing the screen
        primaryStage.setResizable(false);
        Main.primaryStage = primaryStage;

        roundinputp = FXMLLoader.load(getClass().getResource("roundinput.fxml"));
        roundinput = new Scene(roundinputp, 600, 400);

        AnchorPane leaderboardp = FXMLLoader.load(getClass().getResource("leaderboard.fxml"));
        leaderboard = new Scene(leaderboardp, 200, 400);

        endingPagep = FXMLLoader.load(getClass().getResource("endingPage.fxml"));
        endingPage = new Scene(endingPagep, 600,400);

        leaderboardp.getChildren().add(table);

        TableColumn player1scores = new TableColumn("Player 1");
        player1scores.setMinWidth(100);
        player1scores.setCellValueFactory(new PropertyValueFactory<Score, SimpleLongProperty>("player1score"));

        TableColumn player2scores = new TableColumn("Player 2");
        player2scores.setMinWidth(100);
        player2scores.setCellValueFactory(new PropertyValueFactory<Score, SimpleLongProperty>("player2score"));

        table.getColumns().addAll(player1scores, player2scores);
        table.setItems(data);

        map1p = FXMLLoader.load(getClass().getResource("map.fxml")); //Just setup for the matches
        map2p = FXMLLoader.load(getClass().getResource("map.fxml"));
        map3p = FXMLLoader.load(getClass().getResource("map.fxml"));
        map1 = new Scene(map1p, 600, 400); //Initializes Map1
        map2 = new Scene(map2p, 600, 400);
        map3 = new Scene(map3p, 600, 400);

        /*
        Creates an animation that will run the handle method at 60 frames per second.
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
    This method is just to make adding objects to the game easier.
    It saves some typing since most of the objects made in this game are Elements, not native JavaFX Nodes.
    */
    private static void addToGame(Element element, double x, double y, Pane map) {
        element.getView().setTranslateX(x);
        element.getView().setTranslateY(y);
        map.getChildren().add(element.getView());
    }

    /*
    This method creates the leaderboard.
     */
    public static void submitScore() {
        try {
            FileReader reader = new FileReader("src/main/scores.json");
            JSONArray scoreData;
            JSONParser parser = new JSONParser();
            scoreData = (JSONArray) parser.parse(reader);
            reader.close();

            FileWriter writer = new FileWriter("src/main/scores.json");
            JSONObject currentScore = new JSONObject();
            currentScore.put("Player1", player1Score);
            currentScore.put("Player2", player2Score);
            scoreData.add(currentScore);
            writer.write(scoreData.toJSONString());
            writer.flush();
            writer.close();

            for(Object record : scoreData) {
                long player1Score = (long) ((JSONObject)record).get("Player1");
                long player2Score = (long) ((JSONObject)record).get("Player2");
                data.add(new Score(player1Score,player2Score));
            }
            setGameState(gameState.LEADERBOARD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*
    Sets the game state for the match, also has all the prep for that arena (making the walls, spawning the tanks, score counters).
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
                    //Sets the scene to the main menu, declarations already happened in the start method.
                    primaryStage.setScene(mainmenu);
                    break;
                case ROUNDINPUT:
                    primaryStage.setScene(roundinput);
                    break;
                case MAP1:
                    reload1 = 0;
                    reload2 = 0;
                    autoshootdelay1 = 5;
                    autoshootdelay2 = 5;
                    map1p.getChildren().clear();
                    map2p.getChildren().clear();
                    map3p.getChildren().clear();
                    //Tells us that it's on Map 1.
                    currentMap = 1;
                    //Sets the scene to map1
                    primaryStage.setScene(map1);
                    //Creates both tanks
                    player1 = new Tank();
                    player2 = new Tank();
                    addToGame(player1, 30, 285, map1p);
                    addToGame(player2, 525, 255, map1p);
                    //Sets game to have started
                    gameStarted = true;
                    //These createWall methods all create the layout of the map.
                    createWall(10, 400, 0, 0, map1p);
                    createWall(10, 400, 590, 0, map1p);
                    createWall(600, 10, 0, 0, map1p);
                    createWall(600, 10, 0, 390, map1p);
                    createWall(90, 10, 10, 65, map1p);
                    createWall(90, 10, 10, 125, map1p);
                    createWall(150, 10, 10, 190, map1p);
                    createWall(10, 75, 160, 160, map1p);
                    createWall(90, 10, 10, 255, map1p);
                    createWall(90, 10, 10, 320, map1p);
                    createWall(10, 70, 160, 0, map1p);
                    createWall(10, 75, 160, 335, map1p);
                    createWall(10, 75, 250, 325, map1p);
                    createWall(250, 10, 220, 65, map1p);
                    createWall(10, 125, 340, 70, map1p);
                    createWall(10, 125, 250, 125, map1p);
                    createWall(230, 10, 255, 190, map1p);
                    createWall(10, 125, 480, 150, map1p);
                    createWall(10, 75, 390, 255, map1p);
                    createWall(300, 10, 335, 330, map1p);

                    //Makes the player score counters.
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
                    reload1 = 0;
                    reload2 = 0;
                    autoshootdelay1 = 5;
                    autoshootdelay2 = 5;
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
                    createWall(10, 160, 160, 80, map2p);
                    createWall(10, 100, 160, 325, map2p);
                    createWall(150, 10, 90, 315, map2p);
                    createWall(160, 10, 240, 80, map2p);
                    createWall(80, 10, 240, 165, map2p);
                    createWall(10, 250, 315, 85, map2p);
                    createWall(85, 10, 325, 240, map2p);
                    createWall(10, 100, 390, 315, map2p);
                    createWall(10, 90, 500, 0, map2p);
                    createWall(110, 10, 400, 165, map2p);
                    createWall(130, 10, 495, 270, map2p);
                    createWall(10, 100, 490, 230, map2p);


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
                    reload1 = 0;
                    reload2 = 0;
                    autoshootdelay1 = 5;
                    autoshootdelay2 = 5;
                    map1p.getChildren().clear();
                    map2p.getChildren().clear();
                    map3p.getChildren().clear();
                    currentMap = 3;
                    createWall(10, 400, 0, 0, map3p);
                    createWall(10, 400, 590, 0, map3p);
                    createWall(600, 10, 0, 0, map3p);
                    createWall(600, 10, 0, 390, map3p);
                    createWall(90, 10, 0, 225, map3p);
                    createWall(90, 10, 0, 310, map3p);
                    createWall(10, 105, 90, 60, map3p);
                    createWall(190, 10, 0, 160, map3p);
                    createWall(10, 105, 180, 0, map3p);
                    createWall(90, 10, 185, 225, map3p);
                    createWall(420, 10, 180, 310, map3p);
                    createWall(10, 90, 250, 315, map3p);
                    createWall(180, 10, 185, 60, map3p);
                    createWall(10, 100, 270, 65, map3p);
                    createWall(10, 150, 355, 165, map3p);
                    createWall(60, 10, 360, 225, map3p);
                    createWall(60, 10, 455, 125, map3p);
                    createWall(10, 225, 510, 0, map3p);

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
                    addToGame(player2, 315, 80, map3p);
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
        //Creates listener for key presses and sets boolean values for usage in the KeyCheck method
        map.getScene().setOnKeyPressed(e -> {
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

        //Creates listener for key releases and sets boolean values for usage in the KeyCheck method.
        map.getScene().setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                left = false;
            }
            if (e.getCode() == KeyCode.RIGHT) {
                right = false;
            }
            if (e.getCode() == KeyCode.M) {
                m = false;
            }
            if (e.getCode() == KeyCode.UP) {
                up = false;
                collisionup1 = false;
                tankup1 = false;
            }
            if (e.getCode() == KeyCode.DOWN) {
                down = false;
            }
            if (e.getCode() == KeyCode.W) {
                w = false;
                collisionup2 = false;
                tankup2 = false;
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
            }
        });

    }

    /*
    The method that creates walls for the map. Each wall is made up of 4 different rectangles (top, right, bottom, and left).
     */
    private static void createWall (int width, int height, int x, int y, Pane map) {
        Rectangle left, top, right, bottom;
        if (width > height) {
            top = new Rectangle(x + 1, y, width - 1, height / 2);
            right = new Rectangle(x + width, y, 1, height);
            bottom = new Rectangle(x + 1, y + height / 2, width - 1, height / 2);
            left = new Rectangle(x, y, 1, height);

        } else {
            top = new Rectangle(x, y, width,1);
            right = new Rectangle(x+width/2, y+1, width/2, height-3);
            bottom = new Rectangle(x, y+height-2, width, 2);
            left = new Rectangle(x, y+1, width/2, height-3);
        }

        walls.add(top);
        walls.add(right);
        walls.add(bottom);
        walls.add(left);
        map.getChildren().addAll(top, right, bottom, left);

    }

    /*
    Takes input for which players collision is being checked
    For every wall in the array list of walls (all walls in the map), it checks if the player in question is touching that wall.
    If it's hitting a wall, it pushes them away.
     */
    private static void colDetect(Tank player, int playerNumber, Rectangle wall) {
        if (playerNumber == 1) {
            if (player.isHitting(wall) && collisionup1) {
                player.updateLocation(-1.75);
            } else if (player.isHitting(wall) && !collisionup1) {
                player.updateLocation(1.75);
            }

        }

        if (playerNumber == 2) {
            if (player.isHitting(wall) && collisionup2) {
                player.updateLocation(-1.75);
            } else if (player.isHitting(wall) && !collisionup2) {
                player.updateLocation(1.75);
            }
        }
    }

    /*
    Collision detection for a tank against another tank. Uses same structure as collision detection for walls.
     */
    private static void tankDetect(Tank player, int playerNumber) {
        if (playerNumber == 1) {
            if (player.isHitting(player2.getView()) && tankup1) {
                player.updateLocation(-2);
            } else if (player.isHitting(player2.getView()) && !tankup1) {
                player.updateLocation(2);
            }
        }

        if (playerNumber == 2) {
            if (player.isHitting(player1.getView()) && tankup2) {
                player.updateLocation(-2);
            } else if (player.isHitting(player1.getView()) && !tankup2) {
                player.updateLocation(2);
            }
        }
    }

    /*
    Doesn't take input because bullets interact with everything and the bullets and bullets2 arraylist is global.
    For every bullet that has been shot, check if it's hit each wall, and if it's hit a wall:
    - determine if the wall is vertical or horizontal, if it is the same wall that it just hit last time, and if it is a wall it can hit according to physics.
    (ie a bullet travelling downwards cannot hit the bottom wall of a rectangle)
    - Reverse the X velocity if it is a vertical wall, and reverse the Y velocity if it is a horizontal wall.

     */

    private static void bulletCol() {
        Wall1 = walls.size() + 1;
        Wall2 = walls.size() + 1;

        int nonhittableX, nonhittableY;
        for (Element bullet : bullets) {

            nonhittableX = bullet.getVelocity().getX() > 0 ? 1:3;
            nonhittableY = bullet.getVelocity().getY() > 0 ? 2:0;

            for (int i = 0; i < walls.size(); i++) {
                Rectangle wall = walls.get(i);
                if (bullet.isHitting(wall)) {
                    if (wall.getWidth() < wall.getHeight() && i != intwall && nonhittableX != i%4 && nonhittableY != i%4) {
                        bullet.setVelocity(new Point2D(bullet.getVelocity().getX() * -1, bullet.getVelocity().getY()));
                        intwall = i;
                        Wall1 = i/4;
                    }
                    else if (wall.getWidth() > wall.getHeight() && i != intwall && nonhittableX != i%4 && nonhittableY != i%4) {
                        bullet.setVelocity(new Point2D(bullet.getVelocity().getX(), bullet.getVelocity().getY() * -1));
                        Wall1 = i/4;
                        intwall = i;
                    }
                    bullethitwall1 = true;
                }
            }
        }
        for (Element bullet2 : bullets2) {

            nonhittableX = bullet2.getVelocity().getX() > 0 ? 1:3;
            nonhittableY = bullet2.getVelocity().getY() > 0 ? 2:0;

            for (int i = 0; i < walls.size(); i++) {
                Rectangle wall = walls.get(i);
                if (bullet2.isHitting(wall)) {
                    if (wall.getWidth() < wall.getHeight() && Wall2 != i/4 && nonhittableX != i%4 && nonhittableY != i%4) {
                        bullet2.setVelocity(new Point2D(bullet2.getVelocity().getX() * -1, bullet2.getVelocity().getY()));
                        intwall2 = i;
                        Wall2 = i/4;
                    }
                    else if (wall.getWidth() > wall.getHeight() && Wall2 != i/4 && nonhittableX != i%4 && nonhittableY != i%4) {
                        bullet2.setVelocity(new Point2D(bullet2.getVelocity().getX(), bullet2.getVelocity().getY() * -1));
                        intwall2 = i;
                        Wall2 = i/4;
                    }
                    bullethitwall2 = true;
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    /*
    Adds a bullet to the game (called when M or Q is being pressed)
    Adds the bullet to the arraylist of bullets or bullets2 (global)
    Adds it to the game
     */
    private void addBullet(Element element, double x, double y, Pane map, Element player) {
        if (player == player1) {
            bullets.add(element);
            bullethitwall1 = false;
        }

        else if (player == player2) {
            bullets2.add(element);
            bullethitwall2 = false;
        }
            addToGame(element, x, y, map);
    }


    /*
    Called when somebody dies
    Depending on the map being played it will go to a different task in the switch statement.
    Clears the walls array list to stop people from being blocked by non-existent walls
    Clears the bullet array list
    Resets the reload and intwall variables.
     */
    private void resetMatch() {
        switch (currentMap) {
            case 1:
                try {
                    walls.clear();
                    bullets.clear();
                    bullets2.clear();
                    setGameState(gameState.MAP2);
                    reload1 = 0;
                    reload2 = 0;
                    intwall = 0;
                    intwall2 = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    walls.clear();
                    bullets.clear();
                    bullets2.clear();
                    setGameState(gameState.MAP3);
                    reload1 = 0;
                    reload2 = 0;
                    intwall = 0;
                    intwall2 = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                try {
                    walls.clear();
                    bullets.clear();
                    bullets2.clear();
                    setGameState(gameState.MAP1);
                    reload1 = 0;
                    reload2 = 0;
                    intwall = 0;
                    intwall2 = 0;
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

            if (autoshootdelay1 > 0)
                autoshootdelay1--;
            if (autoshootdelay2 > 0)
                autoshootdelay2--;

            switch (currentMap) {
                case 1:
                    keyCheck(map1p);
                    bulletCol();
                    killDetect(map1p);
                    for (Element bullet : bullets) {
                        bullet.counter++;
                        bullet.updateLocation(1);
                        if (bullet.getCounter() > 300 || bullet.dead()) {
                            map1p.getChildren().remove(bullet.getView());
                            bullet.setStatus(false);
                            //sets intwall to a negative value so bullets don't go through walls
                            intwall = -5;
                        }
                    }

                    for (int i = 0; i < bullets.size() ; i++) {
                        if (bullets.get(i).dead())
                            bullets.remove(i);

                    }

                    for (Element bullet2 : bullets2) {
                        bullet2.counter++;
                        bullet2.updateLocation(1);
                        if (bullet2.getCounter() > 300 || bullet2.dead()) {
                            map1p.getChildren().remove(bullet2.getView());
                            bullet2.setStatus(false);
                            intwall = -5;
                        }
                    }

                    for (int i = 0; i < bullets2.size() ; i++) {
                        if (bullets2.get(i).dead()) {
                            bullets2.remove(i);
                        }
                    }
                    labelplayer1score.setText("Player 1: " + player1Score);
                    labelplayer2score.setText("Player 2: " + player2Score);
                    break;
                case 2:
                    keyCheck(map2p);
                    bulletCol();
                    killDetect(map2p);
                    for (Element bullet : bullets) {
                        bullet.counter++;
                        bullet.updateLocation(1);
                        if (bullet.getCounter() > 300 || bullet.dead()) {
                            map2p.getChildren().remove(bullet.getView());
                            bullet.setStatus(false);
                            intwall = -5;
                        }
                    }
                    for (int i = 0; i < bullets.size() ; i++) {
                        if (bullets.get(i).dead())
                            bullets.remove(i);
                    }

                    for (Element bullet2 : bullets2) {
                        bullet2.counter++;
                        bullet2.updateLocation(1);
                        if (bullet2.getCounter() > 300 || bullet2.dead()) {
                            map2p.getChildren().remove(bullet2.getView());
                            bullet2.setStatus(false);
                            intwall = -5;
                        }
                    }
                    for (int i = 0; i < bullets2.size() ; i++) {
                        if (bullets2.get(i).dead())
                            bullets2.remove(i);

                    }

                    labelplayer1score.setText("Player 1: " + player1Score);
                    labelplayer2score.setText("Player 2: " + player2Score);
                    break;
                case 3:
                    keyCheck(map3p);
                    bulletCol();
                    killDetect(map3p);
                    for (Element bullet : bullets) {
                        bullet.counter++;
                        bullet.updateLocation(1);
                        if (bullet.getCounter() > 300 || bullet.dead()) {
                            map3p.getChildren().remove(bullet.getView());
                            bullet.setStatus(false);
                            intwall = -5;
                        }
                    }
                    for (int i = 0; i < bullets.size() ; i++) {
                        if (bullets.get(i).dead())
                            bullets.remove(i);

                    }

                    for (Element bullet2 : bullets2) {
                        bullet2.counter++;
                        bullet2.updateLocation(1);
                        if (bullet2.getCounter() > 300 || bullet2.dead()) {
                            map3p.getChildren().remove(bullet2.getView());
                            bullet2.setStatus(false);
                            intwall = -5;
                        }
                    }
                    for (int i = 0; i < bullets2.size() ; i++) {
                        if (bullets2.get(i).dead())
                            bullets2.remove(i);

                    }

                    labelplayer1score.setText("Player 1: " + player1Score);
                    labelplayer2score.setText("Player 2: " + player2Score);
                    break;
            }
        }
    }

    /*
    Checks the key presses and releases for both player1 and player2, determines if a player shoots/moves.
     */
    private void keyCheck(Pane map) {
        //If left is true it runs the left turn logic
        if (left) {
            //For every wall in the array list, check if the player is touching one and prevent turning if it is.
            for (Rectangle wall : walls) {
                    if (player1.isHitting(wall)) {
                        notTouching1 = false;
                        player1.rotateRight();
                    }
                }

                if (player1.isHitting(player2.getView())) {
                    player1.rotateRight();
                    notTouching1 = false;
                }

                //Only turn if it's not touching the wall
                if (notTouching1) {
                    player1.rotateLeft();
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

            if (player1.isHitting(player2.getView())) {
                player1.rotateLeft();
                notTouching1 = false;
            }

            //Only turn if it's not touching the wall
            if (notTouching1) {
                player1.rotateRight();
            }
            notTouching1 = true;
        }


        if (up) {
            player1.updateLocation(1.75);

            if (player1.isHitting(player2.getView())) {
                tankup1 = true;
                tankDetect(player1, 1);
            }

            for (Rectangle wall : walls) {
                if (player1.isHitting(wall)) {
                    collisionup1 = true;
                    colDetect(player1, 1, wall);
                }
            }
        }

        if (down) {
            player1.updateLocation(-1.75);

            if (player1.isHitting(player2.getView())) {
                tankup1 = false;
                tankDetect(player1, 1);
            }

            for (Rectangle wall : walls) {
                if (player1.isHitting(wall)) {
                    collisionup1 = false;
                    colDetect(player1, 1, wall);
                }
            }
        }

        //Below this repeats the logic above for player 2
        if (w) {
            player2.updateLocation(1.75);

            if (player1.isHitting(player2.getView())) {
                tankup2 = true;
                tankDetect(player2, 2);
            }

            for (Rectangle wall : walls) {
                if (player2.isHitting(wall)) {
                    collisionup2 = true;
                    colDetect(player2, 2, wall);
                }
            }
        }

        if (s) {
            player2.updateLocation(-1.75);

            if (player1.isHitting(player2.getView())) {
                tankup2 = false;
                tankDetect(player2, 2);
            }

            for (Rectangle wall : walls) {
                if (player2.isHitting(wall)) {
                    collisionup2 = false;
                    colDetect(player2, 2, wall);
                }
            }
        }

        if (a) {
            for (Rectangle wall : walls) {
                if (player2.isHitting(wall)) {
                    notTouching2 = false;
                    player2.rotateRight();
                }
            }

            if (player1.isHitting(player2.getView())) {
                player2.rotateRight();
                notTouching2 = false;
            }

            if (notTouching2) {
                player2.rotateLeft();
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

            if (player1.isHitting(player2.getView())) {
                player2.rotateLeft();
                notTouching2 = false;
            }

            if (notTouching2) {
                player2.rotateRight();
            }
            notTouching2 = true;
        }

        //Checks if M is true and if the gun has reloaded.
        // (autoshootdelay is to prevent a player from shooting automatically at the start of a round)
        if (m && reload1 == 0 && autoshootdelay1 == 0) {
            //Checks if the player is actually alive
            if (player1.alive()) {
                //Creates a new bullet
                Bullet bullet = new Bullet();
                //Sets the velocity to 3x that of the player who shot it
                bullet.setVelocity(player1.getVelocity().normalize().multiply(3));
                //Adds the bullet
                addBullet(bullet, player1.getView().getTranslateX(), player1.getView().getTranslateY(), map, player1);
                reload1 = 300;
            }
        }

        //Shooting logic for player 2, same as player 1 above.
        if (q && reload2 == 0 && autoshootdelay2 == 0) {
            if (player2.alive()) {
                Bullet bullet2 = new Bullet();
                bullet2.setVelocity(player2.getVelocity().normalize().multiply(3));
                addBullet(bullet2, player2.getView().getTranslateX(), player2.getView().getTranslateY(), map, player2);
                reload2 = 300;
            }
        }
    }

    /*
    For every bullet in the game, check if it's hitting a player.
    If it's hitting a player, kill the player and remove it from the scene.
    Also, remove the bullet and update the score.
     */
    private void killDetect(AnchorPane map) {
        for (Element bullet : bullets) {
            if (bullet.isHitting(player1.getView())&& bullethitwall1 == true) {
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

        for (Element bullet2 : bullets2) {
            if (bullet2.isHitting(player1.getView())) {
                player1.setStatus(false);
                map.getChildren().remove(player1.getView());
                System.out.println("player 1 dead");
                map.getChildren().remove(bullet2.getView());
                player2Score++;
            }
            if (bullet2.isHitting(player2.getView()) && bullethitwall2 == true) {
                player2.setStatus(false);
                map.getChildren().remove(player2.getView());
                map.getChildren().remove(bullet2.getView());
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
        }
    }

    /*
    Separate bullet classes for player 1 and 2 that inherits methods from the Element class.
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

        Score(long player1score, long player2score) {
            this.setPlayer1score(player1score);
            this.setPlayer2score(player2score);
        }

        public long getPlayer1score() {
            return player1score.get();
        }

        public long getPlayer2score() {
            return player2score.get();
        }

        public void setPlayer1score(long n) {
            player1score.set(n);
        }

        public void setPlayer2score(long n) {
            player2score.set(n);
        }
    }
}
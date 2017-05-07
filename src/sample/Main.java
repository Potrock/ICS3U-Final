/*
TO DO:
- Collision against other tanks
- Tank not disappearing if it doesn't move and dies
- Swerving problem still needs to be tweaked
- Turning corners
- High score screen
- Paragraphs about game (1 per person)
- Fix main menu (Start button isn't chopped off)
- Multiple round option (so no error after map 3)
- Bullets hitting top of a wall
 */

package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application {
    private static AnchorPane map1p;
    private static AnchorPane map2p;
    private static AnchorPane map3p;
    private static Scene map1, map2, map3, mainmenu;
    private static Tank player1;
    private static Tank player2;
    private static ArrayList<Element> bullets = new ArrayList<>();
    private static ArrayList<Rectangle> walls = new ArrayList<>();
    private static Stage primaryStage;
    private static int count = 0, currentMap = 0;
    private static boolean w, a, s, d, up, down, left, right, m, q;
    private static boolean gameStarted;
    private static boolean readyToShoot = false;
    private static boolean notTouching = true;
    private static int player1Score = 0;
    private static int player2Score = 0;
    private static int score2 = 0, score1 = 0;
    private static Label labelplayer1score = new Label("Player 1: " + player1Score);
    private static Label labelplayer2score = new Label("Player 2: " + player2Score);
    private static boolean collisionup = false;



    /*
    This method is just to make adding objects to the game easier, just saves some typing since most of the objects made in this game are Elements, not native JavaFX Nodes.
    */
    private static void addToGame(Element element, double x, double y, Pane map) {
        element.getView().setTranslateX(x);
        element.getView().setTranslateY(y);
        map.getChildren().add(element.getView());
    }


    /*
    Sets the game state for the match, also has all the prep for that arena (Making the walls, spawning the tanks, etc.)
    Right now, you can't set a gameState back to one that has already been created but if it's really an issue and we want more than just Best 2/3 matches we can make a workaround.

     */
    static void setGameState(gameState gameState) throws IOException {
        switch (gameState) {
            case MAIN:
                primaryStage.setScene(mainmenu); //Sets the scene to the main menu, declarations already happened in the start method.
                break;
            case MAP1:
                currentMap = 1; //Tells us that it's on Map 1..
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
                createWall(10, 75, 160, 0, map1p);
                createWall(10, 75, 160, 325, map1p);
                createWall(10, 75, 250, 325, map1p);
                createWall(300, 10, 170, 65, map1p);
                createWall(10, 130, 340, 65, map1p);
                createWall(10, 75, 250, 125, map1p);
                createWall(230, 10, 250, 190, map1p);
                createWall(10, 75, 480, 190, map1p);
                createWall(125, 10, 475, 125, map1p);
                createWall(140, 10, 250, 255, map1p);
                createWall(10, 75, 390, 255, map1p);
                createWall(210, 10, 390, 330, map1p);


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

                map1p.getScene().setOnKeyPressed(e -> { //Creates listener for key presses and sets boolean values for usage in the KeyCheck method
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

                map1p.getScene().setOnKeyReleased(e -> { //Creates listener for key releases and sets boolean values for usage in the KeyCheck method.
                    if (e.getCode() == KeyCode.LEFT) {
                        left = false;
                    }
                    if (e.getCode() == KeyCode.RIGHT) {
                        right = false;
                    }
                    if (e.getCode() == KeyCode.M) {
                        m = false;
                        readyToShoot = true;
                    }
                    if (e.getCode() == KeyCode.UP) {
                        up = false;
                    }
                    if (e.getCode() == KeyCode.DOWN) {
                        down = false;
                    }
                    if (e.getCode() == KeyCode.W) {
                        w = false;
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
                        readyToShoot = true;
                    }
                });
                break;
            case MAP2:
                /*
                See explanation for Map 1
                 */
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
                createWall(90, 10, 160, 315, map2p);
                createWall(10, 90, 240, 235, map2p);
                createWall(160, 10, 240, 80, map2p);
                createWall(80, 10, 240, 165, map2p);
                createWall(10, 160, 315, 80, map2p);
                createWall(85, 10, 315, 240, map2p);
                createWall(10, 100, 315, 315, map2p);
                createWall(10, 100, 390, 315, map2p);
                createWall(10, 100, 315, 315, map2p);
                createWall(10, 90, 500, 0, map2p);
                createWall(110, 10, 400, 165, map2p);
                createWall(130, 10, 490, 240, map2p);
                createWall(10, 80, 490, 240, map2p);

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
                map2p.getScene().setOnKeyPressed(e -> {
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

                map2p.getScene().setOnKeyReleased(e -> {
                    if (e.getCode() == KeyCode.LEFT) {
                        left = false;
                    }
                    if (e.getCode() == KeyCode.RIGHT) {
                        right = false;
                    }
                    if (e.getCode() == KeyCode.M) {
                        m = false;
                        readyToShoot = true;
                    }
                    if (e.getCode() == KeyCode.UP) {
                        up = false;
                    }
                    if (e.getCode() == KeyCode.DOWN) {
                        down = false;
                    }
                    if (e.getCode() == KeyCode.W) {
                        w = false;
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
                break;
            case MAP3:
                /*
                See explanation for Map 1
                 */
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
                map3.setOnKeyPressed(e -> {
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

                map3.setOnKeyReleased(e -> {
                    if (e.getCode() == KeyCode.LEFT) {
                        left = false;
                    }
                    if (e.getCode() == KeyCode.RIGHT) {
                        right = false;
                    }
                    if (e.getCode() == KeyCode.M) {
                        m = false;
                        readyToShoot = true;
                    }
                    if (e.getCode() == KeyCode.UP) {
                        up = false;
                    }
                    if (e.getCode() == KeyCode.DOWN) {
                        down = false;
                    }
                    if (e.getCode() == KeyCode.W) {
                        w = false;
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
                break;
        }
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
    private static void colDetect(Tank player) {
        for (Rectangle wall : walls) {
            if (player.isHitting(wall) && collisionup) {
                player.updateLocation(-1.75);
            }
            else if (player.isHitting(wall) && !collisionup)
                player.updateLocation(1.75);
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
                if (bullet.isHitting(wall)) {
                    if (wall.getWidth() == 10) {
                        bullet.setVelocity(new Point2D(bullet.getVelocity().getX() * -1, bullet.getVelocity().getY()));
                    }
                    if (wall.getHeight() == 10) {
                        bullet.setVelocity(new Point2D(bullet.getVelocity().getX(), bullet.getVelocity().getY() * -1));
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        AnchorPane stage = FXMLLoader.load(getClass().getResource("sample.fxml")); //Gets the main menu from the FXML file
        mainmenu = new Scene(stage, 400, 400); //Initializes Main Menu Scene
        primaryStage.setScene(mainmenu); //Sets the scene
        primaryStage.show(); //Shows the screen
        primaryStage.setResizable(false); //Disables resizing the screen
        Main.primaryStage = primaryStage;
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
            @Override
            public void handle(long now) {
                onUpdate();
            }
        };
        timer.start();

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
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    walls.clear();
                    bullets.clear();
                    setGameState(gameState.MAP3);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                try {
                    walls.clear();
                    bullets.clear();
                    setGameState(gameState.MAP1);
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
            colDetect(player1);
            colDetect(player2);
            if (player1.dead())
                resetMatch();
            if (player2.dead())
                resetMatch();
            switch (currentMap) {
                case 1:
                    keyCheck(map1p);
                    if (count > 20)
                        killDetect(map1p);
                    bulletCol();
                    for (Element bullet : bullets) {
                        bullet.counter++;
                        bullet.updateLocation(1);
                        if (bullet.getCounter() > 300 || bullet.dead()) {
                            map1p.getChildren().remove(bullet.getView());
                            bullet.setStatus(false);
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
                    if (count > 20)
                        killDetect(map2p);
                    bulletCol();
                    for (Element bullet : bullets) {
                        bullet.counter++;
                        bullet.updateLocation(1);
                        if (bullet.getCounter() > 300 || bullet.dead()) {
                            map2p.getChildren().remove(bullet.getView());
                            bullet.setStatus(false);
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
                    if (count > 20)
                        killDetect(map3p);
                    bulletCol();
                    for (Element bullet : bullets) {
                        bullet.counter++;
                        bullet.updateLocation(1);
                        if (bullet.getCounter() > 300 || bullet.dead()) {
                            map3p.getChildren().remove(bullet.getView());
                            bullet.setStatus(false);
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
                    notTouching = false;
                    player1.rotateRight();
                }
            }
            if (notTouching) {
                    player1.rotateLeft(); //Only turn if it's not touching the wall
            }
            notTouching = true;
        }
        if (right) {
            for (Rectangle wall : walls) {
                if (player1.isHitting(wall)) {
                    notTouching = false;
                    player1.rotateLeft();
                }
            }
            if (notTouching) {
                player1.rotateRight();
            }
            notTouching = true;
        }

        if (up) {
            player1.updateLocation(1.75); //Updates the players location (Constant is for changing direction/speed)
            for (Rectangle wall : walls) {
                if (player1.isHitting(wall)) {
                    collisionup = true;
                    colDetect(player1);
                }
            }
        }

        if (down) { //Same logic as up, but if it's going into a wall make it go forward instead of back.
            player1.updateLocation(-1.75);
            for (Rectangle wall : walls) {
                if (player1.isHitting(wall)) {
                    collisionup = false;
                    colDetect(player1);
                }
            }
        }

        if (w)
            player2.updateLocation(1.75); //Below this repeats the logic above for the other player
        for (Rectangle wall : walls) {
            if (player2.isHitting(wall)) {
                collisionup = true;
                colDetect(player2);
            }
        }

        if (s) {
            player2.updateLocation(-1.75);
            for (Rectangle wall : walls) {
                if (player2.isHitting(wall)) {
                    collisionup = false;
                    colDetect(player2);
                }
            }
        }
        if (a) {
            for (Rectangle wall : walls) {
                if (player2.isHitting(wall))
                    notTouching = false;
            }
            if (notTouching) {
                player2.rotateLeft();
            }

        }
        if (d) {
            for (Rectangle wall : walls) {
                if (player2.isHitting(wall))
                    notTouching = false;
            }
            if (notTouching) {
                player2.rotateRight();
            }
            notTouching = true;
        }

        if (m && readyToShoot) { //Checks if M is true and the player is allowed to shoot again
            if (player1.alive()) { //Checks if the player is actually alive
                Bullet bullet = new Bullet(); //Creates a new bullet
                bullet.setVelocity(player1.getVelocity().normalize().multiply(3)); //Sets the velocity to 3x that of the player who shot it
                addBullet(bullet, player1.getView().getTranslateX(), player1.getView().getTranslateY(), map); //Adds the bullet
                readyToShoot = false; //Sets the player to not be ready to shoot.
            }
        }

        if (q && readyToShoot) {
            if (player2.alive()) {
                Bullet bullet = new Bullet();
                bullet.setVelocity(player2.getVelocity().normalize().multiply(3));
                addBullet(bullet, player2.getView().getTranslateX(), player2.getView().getTranslateY(), map);
                readyToShoot = false;
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
        MAP1,
        MAP2,
        MAP3
    }

    /*
    Tank class that inherits methods from the Element class
    Makes a 25x25 blue square as a tank.
     */
    static class Tank extends Element {
        Tank() {
            super(new Rectangle(25, 25, Color.BLUE));
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
}


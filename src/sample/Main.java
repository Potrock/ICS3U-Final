package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;


public class Main extends Application {

    public static AnchorPane stage, map1p, map2p, map3p;
    public static Scene mainmenu;
    public static Scene map1, map2, map3;
    public static Tank player1;
    public static Tank player2;
    static ArrayList<Element> bullets = new ArrayList<>();
    static ArrayList<Rectangle> walls = new ArrayList<>();
    static Stage primaryStage;
    static int count = 0, currentMap = 0;
    static boolean w,a,s,d,up,down,left,right,m,q;
    static boolean gameStarted;

    private static void addToGame(Element element, double x, double y, Pane map) {
        element.getView().setTranslateX(x);
        element.getView().setTranslateY(y);
        map.getChildren().add(element.getView());
    }

    public static void setGameState(gameState gameState) throws IOException {
        switch (gameState) {
            case MAIN:
                primaryStage.setScene(map1);
                break;
            case MAP1:
                currentMap = 1;
                map1 = new Scene(map1p, 600, 400);
                primaryStage.setScene(map1);
                player1 = new Tank();
                player2 = new Tank();
                addToGame(player1, 30, 285, map1p);
                addToGame(player2,525,255, map1p);
                gameStarted = true;
                createWall(10,400,0,0, map1p);
                createWall(10,400,590,0, map1p);
                createWall(600,10,0,0, map1p);
                createWall(600,10,0, 390, map1p);
                createWall(90,10,10,65, map1p);
                createWall(90,10,10,125, map1p);
                createWall(150,10,10,190, map1p);
                createWall(10,75,160,190, map1p);
                createWall(90,10,10,255, map1p);
                createWall(90,10,10,320, map1p);
                createWall(10,75,160,0, map1p);
                createWall(10,75,160,325, map1p);
                createWall(10,75,250,325, map1p);
                createWall(300,10,170,65, map1p);
                createWall(10,130,340,65, map1p);
                createWall(10,75,250,125, map1p);
                createWall(230,10,250,190, map1p);
                createWall(10,75,480,190, map1p);
                createWall(125,10,475,125, map1p);
                createWall(140,10,250,255, map1p);
                createWall(10,75,390,255, map1p);
                createWall(210,10,390,330, map1p);
                map1p.getScene().setOnKeyPressed(e -> {
                    if (e.getCode() == KeyCode.LEFT) {
                        left = true;
                    } else
                        left = false;
                    if (e.getCode() == KeyCode.RIGHT) {
                        right = true;
                    } else
                        right = false;
                    if (e.getCode() == KeyCode.M) {
                        m = true;
                    } else
                        m = false;
                    if (e.getCode() == KeyCode.UP) {
                        up = true;
                    } else
                        up = false;
                    if (e.getCode() == KeyCode.DOWN) {
                        down = true;
                    } else
                        down = false;
                    if (e.getCode() == KeyCode.W) {
                        w = true;
                    } else
                        w = false;
                    s = e.getCode() == KeyCode.S;
                    if (e.getCode() == KeyCode.A) {
                        a = true;
                    } else
                        a = false;
                    if (e.getCode() == KeyCode.D) {
                        d = true;
                    } else
                        d  = false;
                    if (e.getCode() == KeyCode.Q) {
                        q = true;
                    } else
                        q = false;
                });
                break;
            case MAP2:
                currentMap = 2;
                createWall(10,400,0,0, map2p);
                createWall(10,400,590,0, map2p);
                createWall(600,10,0,0, map2p);
                createWall(600,10,0, 390, map2p);
                createWall(90,10,0, 165, map2p);
                createWall(10,90,80, 0, map2p);
                createWall(10,90,80, 235, map2p);
                createWall(10,160,160, 80, map2p);
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
                map2 = new Scene(map2p, 600, 400);
                primaryStage.setScene(map2);
                player1 = new Tank();
                player2 = new Tank();
                addToGame(player1, 115, 30, map2p);
                addToGame(player2,455,30, map2p);
                gameStarted = true;
                map2p.getScene().setOnKeyPressed(e -> {
                    if (e.getCode() == KeyCode.LEFT) {
                        left = true;
                    } else
                        left = false;
                    if (e.getCode() == KeyCode.RIGHT) {
                        right = true;
                    } else
                        right = false;
                    if (e.getCode() == KeyCode.M) {
                        m = true;
                    } else
                        m = false;
                    if (e.getCode() == KeyCode.UP) {
                        up = true;
                    } else
                        up = false;
                    if (e.getCode() == KeyCode.DOWN) {
                        down = true;
                    } else
                        down = false;
                    if (e.getCode() == KeyCode.W) {
                        w = true;
                    } else
                        w = false;
                    if (e.getCode() == KeyCode.S) {
                        s = true;
                    } else
                        s = false;
                    if (e.getCode() == KeyCode.A) {
                        a = true;
                    } else
                        a = false;
                    if (e.getCode() == KeyCode.D) {
                        d = true;
                    } else
                        d  = false;
                    if (e.getCode() == KeyCode.Q) {
                        q = true;
                    } else
                        q = false;
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
                currentMap = 3;
                createWall(10,400,0,0, map3p);
                createWall(10,400,590,0, map3p);
                createWall(600,10,0,0, map3p);
                createWall(600,10,0, 390, map3p);
                createWall(90,10,0,100, map3p);
                createWall(90,10,0,225, map3p);
                createWall(90,10,0,310, map3p);
                createWall(10,105,90,60, map3p);
                createWall(95,10,90,160, map3p);
                createWall(10,105,180,0, map3p);
                createWall(90,10,180,225, map3p);
                createWall(420,10,180,310, map3p);
                createWall(10,90,180,310, map3p);
                createWall(90,10,180,60, map3p);
                createWall(10,100,270,60, map3p);
                createWall(95,10,270,100, map3p);
                createWall(10,70,355,165, map3p);
                createWall(60,10,355,225, map3p);
                createWall(10,90,415,225, map3p);
                createWall(10,125,455,0, map3p);
                createWall(10,125,455,0, map3p);
                createWall(60,10,455,125, map3p);
                createWall(10,110,505,125, map3p);
                map3 = new Scene(map3p, 600, 400);
                player1 = new Tank();
                player2 = new Tank();
                addToGame(player1, 120, 115, map3p);
                addToGame(player2,315,60, map3p);
                gameStarted = true;
                primaryStage.setScene(map3);
                map3.setOnKeyPressed(e -> {
                    if (e.getCode() == KeyCode.LEFT) {
                        left = true;
                    } else
                        left = false;
                    if (e.getCode() == KeyCode.RIGHT) {
                        right = true;
                    } else
                        right = false;
                    if (e.getCode() == KeyCode.M) {
                        m = true;
                    } else
                        m = false;
                    if (e.getCode() == KeyCode.UP) {
                        up = true;
                    } else
                        up = false;
                    if (e.getCode() == KeyCode.DOWN) {
                        down = true;
                    } else
                        down = false;
                    if (e.getCode() == KeyCode.W) {
                        w = true;
                    } else
                        w = false;
                    if (e.getCode() == KeyCode.S) {
                        s = true;
                    } else
                        s = false;
                    if (e.getCode() == KeyCode.A) {
                        a = true;
                    } else
                        a = false;
                    if (e.getCode() == KeyCode.D) {
                        d = true;
                    } else
                        d  = false;
                    if (e.getCode() == KeyCode.Q) {
                        q = true;
                    } else
                        q = false;
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

    public static void createWall(int width, int height, int x, int y, Pane map) {
        Rectangle rect = new Rectangle(x, y, width, height);
        walls.add(rect);
        map.getChildren().add(rect);
    }

    public static void colDetect(Tank player) {
        for (Rectangle wall : walls) {
            if (player.isHitting(wall)) {
                player.updateLocation(-1.75);
                }
            }
        }

    public static void bulletCol(Pane map) {
        for (Element bullet : bullets) {
            for (Rectangle wall : walls) {
                if (bullet.isHitting(wall)) {
                    bullet.setVelocity(bullet.getVelocity().add(bullet.getVelocity().getX() * -2, bullet.getVelocity().getY() * -2));
//                    map.getChildren().remove(bullet.getView());
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = FXMLLoader.load(getClass().getResource("sample.fxml"));
        mainmenu = new Scene(stage, 400, 400);
        primaryStage.setScene(mainmenu);
        primaryStage.show();
        primaryStage.setResizable(false);
        this.primaryStage = primaryStage;
        map1p = FXMLLoader.load(getClass().getResource("map1.fxml"));
        map2p = FXMLLoader.load(getClass().getResource("map1.fxml"));
        map3p = FXMLLoader.load(getClass().getResource("map1.fxml"));


        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onUpdate();
            }
        };
        timer.start();

    }

    private void addBullet(Element element, double x, double y, Pane map) {
        bullets.add(element);
        addToGame(element, x, y, map);
        count = 0;
    }

    public void onUpdate() {
        if (gameStarted) {
            System.out.println("Game is started");
            for (Element bullet : bullets) {
                bullet.updateLocation(1);
                if (bullet.dead())
                    map1p.getChildren().remove(bullet);
            }
            colDetect(player1);
            colDetect(player2);

            switch (currentMap) {
                case 1:
                    keyCheck(map1p);
                    if (count > 10)
                        killDetect(map1p);
                    bulletCol(map1p);
                    break;
                case 2:
                    keyCheck(map2p);
                    if (count > 10)
                        killDetect(map2p);
                    bulletCol(map2p);
                    break;
//                case 3: keyCheck(map3p); break;
            }

        }
    }
    public void keyCheck(Pane map) {
        if (left) {
            player1.rotateLeft();
        }
        if (right) {
            player1.rotateRight();
        }
        if (m) {
            if (player1.alive()) {
                Bullet bullet = new Bullet();
                bullet.setVelocity(player1.getVelocity().normalize().multiply(5));
                addBullet(bullet, player1.getView().getTranslateX(), player1.getView().getTranslateY(), map);
            }
        }
        if (up) {
            player1.updateLocation(1.75);
        }
        if (down) {
            player1.updateLocation(-1.75);
            for (Rectangle wall : walls) {
                if (player1.isHitting(wall)) {
                    player1.updateLocation(1.75);
                }
            }
        }
        if (w)
        player2.updateLocation(1.75);
        if (s) {
            player2.updateLocation(-1.75);
            for (Rectangle wall : walls) {
                if (player2.isHitting(wall)) {
                    player2.updateLocation(1.75);
                }
            }
        }
        if (a)
        player2.rotateLeft();
        if (d)
        player2.rotateRight();
        if (q) {
            if (player2.alive()) {
                Bullet bullet = new Bullet();
                bullet.setVelocity(player2.getVelocity().normalize().multiply(5));
                addBullet(bullet, player2.getView().getTranslateX(), player2.getView().getTranslateY(), map);
                count = 0;
            }
        }
    }

    public void killDetect(Pane map) {
        for (Element bullet : bullets) {
            if (bullet.isHitting(player1.getView())) {
                player1.setStatus(false);
                map.getChildren().remove(player1.getView());
                System.out.println("player 1 dead");
        }
            if (bullet.isHitting(player2.getView())) {
                player2.setStatus(false);
                map.getChildren().remove(player2.getView());
                System.out.println("player 2 dead");
            }
        }
    }

    public enum gameState {
        MAIN,
        MAP1,
        MAP2,
        MAP3
    }

    static class Tank extends Element {
        Tank() {
            super(new Rectangle(25,25,Color.BLUE));
        }

    }

    static class Bullet extends Element {
        Bullet() {
            super(new Circle(5, 5, 5, Color.BLACK));
        }
    }
}


package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;


public class Main extends Application {

    public static Rectangle rect;
    public static AnchorPane stage, map1p, map2p, map3p;
    public static Scene mainmenu;
    public static Scene map1, map2, map3;
    public static Tank player1;
    public static Tank player2;
    static ArrayList<Element> bullets = new ArrayList<>();
    static ArrayList<Rectangle> walls = new ArrayList<>();
    static Stage primaryStage;
    static int count = 0;
    static boolean w,a,s,d,up,down,left,right,m,q;

    private static void addToGame(Element element, double x, double y) {
        element.getView().setTranslateX(x);
        element.getView().setTranslateY(y);
        map1p.getChildren().add(element.getView());
    }

    public static void setGameState(gameState gameState) throws IOException {
        switch (gameState) {
            case MAIN:
                primaryStage.setScene(map1);
                break;
            case MAP1:
                primaryStage.setScene(map1);
                player1 = new Tank();
                player2 = new Tank();
                addToGame(player1, 150, 100);
                addToGame(player2,150,150);
                createWall(10,400,0,0);
                createWall(10,400,590,0);
                createWall(600,10,0,0);
                createWall(600,10,0, 390);
                createWall(90,10,10,65);
                createWall(90,10,10,125);
                createWall(150,10,10,190);
                createWall(10,75,160,190);
                createWall(90,10,10,255);
                createWall(90,10,10,320);
                createWall(10,75,160,0);
                createWall(10,75,160,325);
                createWall(10,75,250,325);
                createWall(300,10,170,65);
                createWall(10,130,340,65);
                createWall(10,75,250,125);
                createWall(230,10,250,190);
                createWall(10,75,480,190);
                createWall(125,10,475,125);
                createWall(140,10,250,255);
                createWall(10,75,390,255);
                createWall(210,10,390,330);
                break;
            case MAP2:
                primaryStage.setScene(map2);
                break;
            case MAP3:
                break;
        }
    }

    public static void createWall(int width, int height, int x, int y) {
        Rectangle rect = new Rectangle(x, y, width, height);
        walls.add(rect);
        map1p.getChildren().add(rect);
    }

    public static void colDetect(Tank player) {
        for (Rectangle wall : walls) {
            if (player.isHitting(wall)) {
                player.updateLocation(-3.5);
                }
            }
        }

    public static void bulletCol() {
        for (Element bullet : bullets) {
            for (Rectangle wall : walls) {
                if (bullet.isHitting(wall)) {
                    map1p.getChildren().remove(bullet.getView());
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
        map1 = new Scene(map1p, 600, 400);

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


        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onUpdate();
            }
        };
        timer.start();

    }

    private void addBullet(Element element, double x, double y) {
        bullets.add(element);
        addToGame(element, x, y);
    }

    public void onUpdate() {
        for (Element bullet : bullets) {
            bullet.updateLocation(1);
            if (bullet.dead())
                map1p.getChildren().remove(bullet);
        }
        colDetect(player1);
        colDetect(player2);
        bulletCol();
        System.out.println(player1.getView().getRotate());
        if (player1.getRotate() == -360 || player1.getRotate() == 360)
            player1.getView().setRotate(0);
        count++;
        if (count > 10)
            killDetect();

        keyCheck();

    }
    public void keyCheck() {
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
                addBullet(bullet, player1.getView().getTranslateX(), player1.getView().getTranslateY());
                count = 0;
            }
        }
        if (up) {
            player1.updateLocation(3.5);
        }
        if (down) {
            player1.updateLocation(-3.5);
            for (Rectangle wall : walls) {
                if (player1.isHitting(wall)) {
                    player1.updateLocation(3.5);
                }
            }
        }
        if (w)
        player2.updateLocation(3.5);
        if (s) {
            player2.updateLocation(-3.5);
            for (Rectangle wall : walls) {
                if (player2.isHitting(wall)) {
                    player2.updateLocation(3.5);
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
                addBullet(bullet, player2.getView().getTranslateX(), player2.getView().getTranslateY());
                count = 0;
            }
        }
    }

    public void killDetect() {
        for (Element bullet : bullets) {
            if (bullet.isHitting(player1.getView())) {
                player1.setStatus(false);
                map1p.getChildren().remove(player1.getView());
                System.out.println("player 1 dead");
        }
            if (bullet.isHitting(player2.getView())) {
                player2.setStatus(false);
                map1p.getChildren().remove(player2.getView());
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


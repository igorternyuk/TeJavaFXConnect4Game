/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tejavafxconnect4;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author igor
 * Last edited 26.09.2017
 */
public class TeJavaFXConnect4 extends Application {
    private enum GameState{
      PLAY,
      RED_WON,
      YELLOW_WON
    };
    private static final String TITLE_OF_PROGRAM = "TeConnect4";
    static final int TILE_SIZE = 80;
    private static final int BOARD_SIZE_X = 7;
    private static final int BOARD_SIZE_Y = 6;
    private static final int WIN_CHAIN = 4;
    private static final int MARGIN = 5;
    private static final int WINDOW_WIDTH = TILE_SIZE / 2 + BOARD_SIZE_X *
            (TILE_SIZE + MARGIN);
    private static final int WINDOW_HEIGHT = TILE_SIZE / 2 + BOARD_SIZE_Y * 
            (TILE_SIZE + MARGIN);
    boolean isRedToMove = true;
    private final Disc[][] board = new Disc[BOARD_SIZE_Y][BOARD_SIZE_X];
    private final Group gBoard = new Group();
    private final Group gRectangles = new Group();
    private final Group gDiscs = new Group();
    private final TranslateTransition anim = new TranslateTransition(
            Duration.seconds(0.5));
    private GameState gameState = GameState.PLAY;
    
    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(createContent());
        scene.setOnKeyReleased(e -> {
            if(e.getCode() == KeyCode.N){
                prepareNewGame();
            }
        });
        stage.setTitle(TITLE_OF_PROGRAM);
        stage.setScene(scene);
        stage.show();
    }

    private Parent createContent(){
        Pane root = new Pane();
        root.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        createBoard();
        createRectangles();
        root.getChildren().addAll(gBoard, gRectangles, gDiscs);
        return root;
    }
     
    private Shape createBoard() {
        Shape shape = new Rectangle((BOARD_SIZE_X + 1) * TILE_SIZE,
        (BOARD_SIZE_Y + 1) * TILE_SIZE);
        for (int y = 0; y < BOARD_SIZE_Y; y++) {
            for (int x = 0; x < BOARD_SIZE_X; x++) {
                Circle circle = new Circle(TILE_SIZE / 2);
                circle.setCenterX(TILE_SIZE / 2);
                circle.setCenterY(TILE_SIZE / 2);
                circle.setTranslateX(TILE_SIZE / 4 + x * (TILE_SIZE + 5));
                circle.setTranslateY(TILE_SIZE / 4 + y * (TILE_SIZE + 5));
                shape = Shape.subtract(shape, circle);
            }
        }
        //Adds some lighting
        Light.Distant light = new Light.Distant();
        light.setAzimuth(45.0);
        light.setElevation(30.0);
        Lighting lighting = new Lighting(light);
        lighting.setSurfaceScale(5.0);
        shape.setFill(Color.BLUE);
        shape.setEffect(lighting);
        gBoard.getChildren().add(shape);
        return shape;
    }
    
    private List<Rectangle> createRectangles(){
        List<Rectangle> list = new ArrayList<>();
        for (int x = 0; x < BOARD_SIZE_X; ++x) {
            Rectangle rect = new Rectangle(TILE_SIZE, WINDOW_HEIGHT);
            rect.setTranslateX(TILE_SIZE / 4 + x * (TILE_SIZE + 5));
            rect.setFill(Color.TRANSPARENT);
            rect.setOnMouseEntered(e -> {
                rect.setFill(Color.rgb(200,200,50,0.3));
            });
            rect.setOnMouseExited(e -> {
                rect.setFill(Color.TRANSPARENT);
            });
            final int col = x;
            rect.setOnMouseClicked(e -> {
                if(anim.getStatus() == TranslateTransition.Status.STOPPED){
                    if(placeDisc(col)) {
                        if (gameState == GameState.PLAY) {
                            isRedToMove = !isRedToMove;
                        } else {
                            gameOver();
                        }
                    }
                }
            });
            list.add(rect);
            gRectangles.getChildren().add(rect);
        }
        return list;
    }
    
    private void checkWinner(int clickX, int clickY){
        System.out.println("Check for winners x = " + String.valueOf(clickX) +
                " y = " + String.valueOf(clickY));
        System.out.println("isRedTurn = " + isRedToMove);
        //Horizontal line
        List<Point2D> horizontal = IntStream.rangeClosed(clickX - WIN_CHAIN + 1,
                clickX + WIN_CHAIN - 1).mapToObj(c -> {
            return new Point2D(c, clickY);
        }).collect(Collectors.toList());
        //Vertical line
        List<Point2D> vertical = IntStream.rangeClosed(clickY - WIN_CHAIN + 1,
                clickY + WIN_CHAIN - 1).mapToObj(c -> {
                    return new Point2D(clickX, c); 
        }).collect(Collectors.toList());
        //Main diagonal of the board matrix
        Point2D topLeft = new Point2D(clickX - WIN_CHAIN + 1,
                clickY - WIN_CHAIN + 1);
        List<Point2D> diagonal1 = IntStream.rangeClosed(0, WIN_CHAIN + 2)
                .mapToObj(i -> {
                    return topLeft.add(i, i);
        }).collect(Collectors.toList());
        //Secondary diagonal of the board matrix
        Point2D bottomLeft = new Point2D(clickX - WIN_CHAIN + 1,
                clickY + WIN_CHAIN - 1);
        List<Point2D> diagonal2 = IntStream.rangeClosed(0, WIN_CHAIN + 2)
                .mapToObj(i -> {
                    return bottomLeft.add(i, -i);
        }).collect(Collectors.toList());
        boolean isWin = checkRange(horizontal) || checkRange(vertical) ||
                checkRange(diagonal1) || checkRange(diagonal2);
        if(isWin){
            gameState = isRedToMove ? GameState.RED_WON : GameState.YELLOW_WON;
        }
    }
    
    private void gameOver(){
        if(gameState != GameState.PLAY){
            String info = gameState == GameState.RED_WON ? "RED WON" :
                    "YELLOW WON";
            Alert alert = new Alert(AlertType.CONFIRMATION, info +
                    "\nPlay again?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                prepareNewGame();
            }
            else {
                Platform.exit();
                System.exit(0);
            }
        }
    }
    
    private void prepareNewGame(){
        for (int y = 0; y < BOARD_SIZE_Y; ++y) {
            for(int x = 0; x < BOARD_SIZE_X; ++x){
                if(getDisc(x, y).isPresent()){
                    gDiscs.getChildren().remove(board[y][x]);
                    board[y][x] = null;
                }
            }            
        }        
        gameState = GameState.PLAY;
    }

    private boolean checkRange(List<Point2D> line){
        int maxInARow = 0;
        for(Point2D p: line){
            int row = (int)p.getY();
            int col = (int)p.getX();
            Disc disc = getDisc(col, row).orElse(new Disc(!isRedToMove));
            if(isRedToMove == disc.isRed()){
                ++maxInARow;
                if(maxInARow == WIN_CHAIN){
                    return true;
                }
            }
            else {
                maxInARow = 0;
            }
        }
        return false;
    }
       
    private Optional<Disc> getDisc(int x, int y){
        if(x < 0 || x >= BOARD_SIZE_X || y < 0 || y >= BOARD_SIZE_Y){
            return Optional.empty();
        }
        return Optional.ofNullable(board[y][x]);
    }
    
    private boolean placeDisc(int col){
        for(int y = BOARD_SIZE_Y - 1; y >= 0; --y){
             if(!getDisc(col, y).isPresent()){
                Disc disc = new Disc(isRedToMove);
                board[y][col] = disc;
                gDiscs.getChildren().add(disc);
                disc.setTranslateX(TILE_SIZE / 4 + col * (TILE_SIZE + MARGIN));
                disc.setTranslateY(TILE_SIZE / 4);  
                anim.setNode(disc);
                anim.setToY(TILE_SIZE / 4 + y * (TILE_SIZE + MARGIN));
                /*anim.setOnFinished(e -> {

                });*/
                anim.play();
                checkWinner(col, y);
                return true;
             }
         }              
        return false;
    }
        
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }


    
}

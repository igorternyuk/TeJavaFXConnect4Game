/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tejavafxconnect4;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author igor
 */
public class Disc extends Circle{

    public boolean isRed() {
        return isRed;
    }
    private static final int RADIUS = TeJavaFXConnect4.TILE_SIZE / 2;
    private final boolean isRed;
    public Disc(boolean isRed){
        super(RADIUS, isRed ? Color.RED : Color.YELLOW);
        this.isRed = isRed;
        this.setCenterX(RADIUS);
        this.setCenterY(RADIUS);
    }
}

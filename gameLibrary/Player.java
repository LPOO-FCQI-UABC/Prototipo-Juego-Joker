package gameLibrary;

import gameLibrary.Board;

import java.awt.event.KeyEvent;

public abstract class Player extends Element{

    // keep track of the player's score
    private int score;
    public Player(int x, int y, String imagePlayer) {
        // load the assets
        super(x,y,imagePlayer);
        // initialize the state
        // initialize the state
        score = 0;
    }


    public abstract void keyPressed(KeyEvent e);

    public void tick() {
        // this gets called once every tick, before the repainting process happens.
        // so we can do anything needed in here to update the state of the player.

        // prevent the player from moving off the edge of the board sideways
        if (pos.x < 0) {
            pos.x = 0;
        } else if (pos.x >= Board.columns) {
            pos.x = Board.columns - 1;
        }
        // prevent the player from moving off the edge of the board vertically
        if (pos.y < 0) {
            pos.y = 0;
        } else if (pos.y >= Board.rows) {
            pos.y = Board.rows - 1;
        }
    }

    public String getScore() {
        return String.valueOf(score);
    }

    public void addScore(int amount) {
        score += amount;
    }


}

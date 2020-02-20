package Game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class Controller extends KeyAdapter {
    private Model model;
    private View view;

    public View getView() {
        return view;
    }

    private final static int WINNING_TILE = 2048;
    public Tile[][] getGameTiles () {
        return model.getGameTiles();
    }


    public int getScore () {
        return model.score;
    }

    public Controller(Model model) {
        this.model = model;
        this.view = new View(this);
    }

    public void resetGame() {
        model.score=0;
        model.maxTile=0;
        view.isGameLost=false;
        view.isGameWon=false;
        model.resetGameTiles();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            resetGame();
        } else if(!model.canMove()) {
            view.isGameLost = true;
        }
        try {
            if( !view.isGameLost && !view.isGameWon ) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_LEFT: model.left();
                    case KeyEvent.VK_RIGHT: model.right();
                    case KeyEvent.VK_UP: model.up();
                    case KeyEvent.VK_DOWN: model.down();
                    case KeyEvent.VK_Z: model.rollback();
                    case KeyEvent.VK_R: model.randomMove();
                    case KeyEvent.VK_A: model.autoMove();
                }
            }

            if(model.maxTile==WINNING_TILE)
                view.isGameWon=true;
            view.repaint();
        } catch (Exception ignored) {
        }

    }
}
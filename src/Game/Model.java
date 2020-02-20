package Game;

import java.util.*;

public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
    protected int score;
    protected int maxTile;
    private Stack<Tile[][]> previousStates = new Stack<Tile[][]>();
    private Stack<Integer> previousScores = new Stack<Integer>();
    private boolean isSaveNeeded = true;



    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public Model() {
        resetGameTiles();
    }

    private void addTile() {
        ArrayList<Tile> emptyTiles = (ArrayList<Tile>) getEmptyTiles();
        if(emptyTiles.size()!=0)
            emptyTiles.get((int) (Math.random() * emptyTiles.size())).value = Math.random () < 0.9 ? 2 : 4;
    }

    private void saveState(Tile[][] tiles) {
        Tile[][] fieldToSave = new Tile[tiles.length][tiles[0].length];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                fieldToSave[i][j] = new Tile(tiles[i][j].value);
            }
        }
        previousStates.push(fieldToSave);
        int scoreToSave = score;
        previousScores.push(scoreToSave);
        isSaveNeeded = false;
    }

    public void rollback() {
        if (!previousStates.isEmpty()) {
            gameTiles = (Tile[][]) previousStates.pop();
        }
        if (!previousScores.isEmpty()) {
            score = (int)previousScores.pop();
        }
    }

    private List<Tile> getEmptyTiles (){
        ArrayList<Tile> emptyTiles = new ArrayList<>();
        for(int i=0;i<FIELD_WIDTH;i++) {
            for (int j=0; j<FIELD_WIDTH;j++) {
                if(gameTiles[i][j].value==0) {
                    emptyTiles.add(gameTiles[i][j]);
                }
            }
        }
        return emptyTiles;
    }

    public void resetGameTiles(){
        for(int i=0;i<FIELD_WIDTH;i++) {
            for (int j=0; j<FIELD_WIDTH;j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    private boolean consolidateTiles(Tile[] tiles) {
        int count = 0;
        int[] valuesBefore = new int[tiles.length];

        for(int i=0; i<tiles.length; i++)
            valuesBefore[i]=tiles[i].value;

        for (int i = 0; i < tiles.length; i++)
            if (tiles[i].value != 0)
                tiles[count++].value = tiles[i].value;

        while (count < tiles.length)
            tiles[count++].value = 0;

        for(int i=0;i<tiles.length;i++)
            if(valuesBefore[i]!=tiles[i].value)
                return true;
        return false;
    }

    private boolean mergeTiles(Tile[] tiles) {
        boolean changed = false;
        for(int i=0;i<FIELD_WIDTH;i++) {
            if( i < FIELD_WIDTH - 1 && tiles[i].value == tiles[i + 1].value && tiles[i].value!=0) {
                tiles[i].value = 2 * tiles[i].value;
                tiles[i + 1].value = 0;
                consolidateTiles(tiles);
                score += tiles[i].value;
                changed = true;

                if( tiles[i].value > maxTile )
                    maxTile = tiles[i].value;
            }
        }
        return changed;
    }

    public void randomMove() {
        int n = ((int) (Math.random () * 100)) % 4;
        switch (n) {
            case 0: left();
            case 1: right();
            case 2: up();
            case 3:
                down();
                break;
        }
    }
    public void left() {
        if (isSaveNeeded)
            saveState(this.gameTiles);
        boolean isChanged = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (consolidateTiles(gameTiles[i]) | mergeTiles(gameTiles[i]))
                isChanged = true;
        }
        isSaveNeeded = true;
        if (isChanged) addTile();
    }

    public void right() {
        saveState(gameTiles);
        rotate90CW();
        rotate90CW();
        left();
        rotate90CW();
        rotate90CW();
    }

    public void up() {
        saveState(gameTiles);
        rotate90CW();
        rotate90CW();
        rotate90CW();
        left();
        rotate90CW();
    }

    public void down() {
        saveState(gameTiles);
        rotate90CW();
        left();
        rotate90CW();
        rotate90CW();
        rotate90CW();
    }

    public void rotate90CW(){
        int[][] temp = new int[FIELD_WIDTH][FIELD_WIDTH];

        for(int i=0; i<FIELD_WIDTH;i++) {
            for(int j=0;j<FIELD_WIDTH;j++) {
                temp[i][j] = gameTiles[i][j].value;
            }
        }

        int i1=0;
        int y1=0;

        for(int i=0;i<FIELD_WIDTH;i++) {
            for (int j = FIELD_WIDTH-1; j >= 0; j--) {
                gameTiles[i1][y1++].value = temp[j][i];
            }
            i1++;
            y1=0;
        }

    }

    public boolean canMove() // hard coding, todo
    {
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[i].length; j++) {
                if (j != gameTiles[i].length - 1) {
                    if (gameTiles[i][j].value == gameTiles[i][j + 1].value) {
                        return true;
                    }
                    if (gameTiles[i][j].value == 0 && gameTiles[i][j + 1].value != 0) {
                        return true;
                    }
                }

                if (i != gameTiles.length - 1) {
                    if (gameTiles[i][j].value == gameTiles[i + 1][j].value) {
                        return true;
                    }

                    if (gameTiles[i][j].value == 0 && gameTiles[i + 1][j].value != 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hasBoardChanged() {
        return getTileWeights(gameTiles) != getTileWeights(previousStates.peek());
    }

    private int getTileWeights(Tile[][] tiles) {
        int result = 0;
        for (int i = 0; i < FIELD_WIDTH; i++)
            for (int j = 0; j < FIELD_WIDTH; j++)
                result += tiles[i][j].value;
        return result;
    }

    private int getNumOfEmptyTiles(Tile[][] tiles) {
        int result = 0;
        for (int i = 0; i < FIELD_WIDTH; i++)
            for (int j = 0; j < FIELD_WIDTH; j++)
                if (tiles[i][j].value == 0)
                    result++;
        return result;
    }

    public MoveFitness getMoveFitness(Move move) {
        move.move();
        MoveFitness moveFitness;
        if (!hasBoardChanged())
            moveFitness = new MoveFitness(-1, 0, move);
        else
            moveFitness = new MoveFitness(getNumOfEmptyTiles(gameTiles), score, move);
        rollback();
        return moveFitness;
    }
    public void autoMove(){
        PriorityQueue<MoveFitness> queue = new PriorityQueue<>(4,Collections.reverseOrder());
        queue.add(getMoveFitness(this::left));
        queue.add(getMoveFitness(this::right));
        queue.add(getMoveFitness(this::up));
        queue.add(getMoveFitness(this::down));

        queue.peek().getMove().move();
    }
}

package projlab;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class GameEngine {
    private List<Worker> workers;
    private Timer timer;
    private List<Tile> tiles;

    public GameEngine(int gameTimeSeconds, List<Tile> tiles, List<Worker> workers;) {
        this.workers = workers;
        this.timer = new Timer(gameTimeSeconds);
        this.tiles = tiles;
    }

    private int getVictor(){
        int max = 0;
        for(Worker worker: workers){
            if(worker.getPoints() > max){
                max = worker.getPoints();
            }
        }
        return max;
    }

    private boolean allBoxesLocked(){
        boolean allLocked = true;
        for(Tile tile: tiles){
            allLocked = allLocked | tile.isLocked();
        }
        return allLocked;
    }

    private void startGame(){
        for(Worker w: workers){
            w.setController(this);
        }
        playGame();
    }

    private void pauseGame(){
        timer.togglePaused();
    }

    private void playGame(){
        while (timer.getTime() != 0){
            String[] inputs = System.console().readLine().split(" "); //pl. W W

            for (int i = 0; i < inputs.length; i++) {
                Direction chosen;
                if ("W".equals(inputs[i])) {
                    chosen = Direction.UP;

                } else if ("D".equals(inputs[i])) {
                    chosen = Direction.RIGHT;

                } else if ("S".equals(inputs[i])) {
                    chosen = Direction.DOWN;

                } else if ("A".equals(inputs[i])) {
                    chosen = Direction.LEFT;

                } else {
                    chosen = Direction.UP;

                }
                workers.get(i).move(chosen);
            }
            if(allBoxesLocked()){
                endGame();
            }
            timer.tick();
        }
        endGame();
    }

    private void endGame(){
        System.out.println("Game over!");
        System.out.print("Winner: Player ");
        System.out.print(getVictor());
        System.out.println();
    }

    public void kill(Worker worker){
        System.out.print("projlab.Worker dead!");
        endGame();
    }

    public static String[] readLines(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();
        return lines.toArray(new String[lines.size()]);
    }

    public static GameEngine loadGame(String path) throws IOException {
        String [] lines = readLines(path);

        int mapLength = lines.length;

        ArrayList<Tile> tiles  = new ArrayList<Tile>();
        ArrayList<Worker> workers= new ArrayList<Worker>();

        for (int i = 0; i<lines.length; i++){
            for (int j = 0; j < lines[i].length(); j++){
                if (lines[i].charAt(j) == 'W'){
                    tiles.add(new Wall());
                } else if (lines[i].charAt(j) == 'P') {
                    Tile t = new Tile();
                    Worker w = new Worker();
                    t.setOccupiedBy(w);
                    tiles.add(t);
                    objects.add(w);
                } else if (lines[i].charAt(j) == 'T') {
                    tiles.add(new Tile());
                } else if (lines[i].charAt(j) == 'B'){
                    Tile t = new Tile();
                    t.setOccupiedBy(new Box());
                    tiles.add(t);
                }
            }
        }

        

        return new GameEngine(tiles, workers);
    }

    public static void main(String[] args){
        try {
            loadMap("/Users/its_behind_you/IdeaProjects/untitled1/out/production/untitled1/com/company/map.txt");
        } catch (Exception e){
            System.out.println(e.getStackTrace());
        }
        //GameEngine engine = new GameEngine(90, map);
        //engine.startGame();
    }
}
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameEngine {
    private List<Worker> workers;
    private Timer timer;
    private List<Tile> tiles;

    public GameEngine(int gameTimeSeconds, List<Tile> tiles) {
        this.workers = new ArrayList<Worker>();
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
        workers.add(new Worker(this));
        workers.add(new Worker(this));
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
        System.out.print("Worker dead!");
        endGame();
    }

    public static List<Tile> loadMap(String Path){
        return new ArrayList<Tile>();
    }

    public static void main(){
        List<Tile> map = loadMap("~foo");
        GameEngine engine = new GameEngine(90, map);
        engine.startGame();
    }

}

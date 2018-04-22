package projlab;

import projlab.SyntaxError;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class GameEngine {
    Map map;
    private Timer timer = null;

    public GameEngine() throws SyntaxError, IOException {
        timer = setTimer();
        map = chooseMap();
        startGame();
    }

    private Timer setTimer(){
        Scanner s = new Scanner(System.in);
        boolean controllable = false;
        System.out.print("TIMER OFF / ON> ");
        String response = s.nextLine();
        if ("OFF".contains(response.toLowerCase()) || response.equals(""))
            controllable = true;

        if (controllable) {
            return new Timer(10000, true);
        }
        else {
            System.out.print("ENTER GAME TIME> ");
            response = s.nextLine();
            while (!response.matches("\\d+")) {
                System.out.print("ENTER A NUMBER> ");
                response = s.nextLine();
            }
            return new Timer(Integer.parseInt(response), false);
        }

    }

    private Map chooseMap() throws SyntaxError, IOException {
        File[] maps = new File("maps/").listFiles();

        for (int i = 0; i < maps.length; i++){
            String filename = maps[i].getName().split(Pattern.quote("."))[0];
            System.out.println(String.format("[%d] %s", i, filename));
        }
        System.out.print("CHOOSE MAP> ");

        String chosen = new Scanner(System.in).nextLine();
        int chosenNum = Integer.parseInt(chosen);

        return new Map(maps[chosenNum].getAbsolutePath());
    }

    private void startGame(){

        for(Worker w: map.workers){
            w.setController(this);
        }

        for (Box b : map.boxes){
            b.setController(this);
        }

        playGame();
    }

    public void pauseGame(){
        timer.togglePaused();
    }

    private void playGame(){
        map.printMap();
        Scanner s  = new Scanner(System.in);
        String[] inputs = new String[map.workers.size()];
        while(timer.tick()) {

            if(timer.isPaused())
                continue;

            for (int i = 0; i < map.workers.size(); i++){
                System.out.print(map.workers.get(i).getName()+">");
                String input = s.nextLine();

                while (!Worker.CONTROLS.containsKey(input.toLowerCase())){
                    System.out.print("INPUT NOT DEFINED (PRESS ENTER OR G TO PASS)> ");
                    input = s.nextLine();
                }

                inputs[i] = input;
            }

            for (int i = 0; i < map.workers.size(); i++)
                map.workers.get(i).getInput(inputs[i]);

            if (allBoxesLocked() || map.boxes.size() < 1 || map.workers.size() < 2)
                break;

            map.printMap();
        }

        endGame();
    }

    private void endGame(){
        timer.stop();
        Worker winner = getWinner();
        if (winner == null){
            System.out.println("IT'S A TIE!");
        }
        else {
            System.out.println("GAME OVER!");
            System.out.print("WINNER: ");
            System.out.print(winner.getName());
            System.out.println();
        }
    }

    private boolean allBoxesLocked(){
        for(Box box : map.boxes) {
            if (box.isLocked())
                return true;
        }
        return false;
    }

    private Worker getWinner(){
        Worker winner = map.workers.get(0);
        for(Worker worker: map.workers){
            if(worker.getPoints() > winner.getPoints()){
                winner = worker;
            }
            else if (worker.getPoints() == winner.getPoints()){
                return null;
            }
        }
        return winner;
    }

    public void removeWorker(Worker worker){
        map.workers.remove(worker);
    }

    public void removeBox(Box box) {
        map.boxes.remove(box);
    }

    public static void main(String[] args) throws SyntaxError, IOException {
        GameEngine ge = new GameEngine();

    }
}

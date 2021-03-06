package projlab;

import java.util.HashMap;

public class Tile {
    protected HashMap<Direction, Tile> neighbors;
    protected GameObject occupiedBy;

    public Tile(){
        neighbors = new HashMap<>();
        occupiedBy = null;
    }

    public void leave(GameObject go){
        this.occupiedBy = null;
    }

    public void enter(Worker worker, Direction direction){
        if (occupiedBy != null){
            occupiedBy.push(direction);
        }
        worker.setTile(this);
        setOccupiedBy(worker);
    }

    public void enter(Box box, Direction direction){
        if (occupiedBy != null){
            occupiedBy.push(direction);
        }
        box.tile = this;
        this.occupiedBy = box;
    }

    public boolean canBeEnteredBy(Worker worker, Direction goingIn){
        if(occupiedBy != null){
            if (occupiedBy.canBeOverPoweredBy(worker, goingIn)) {
                return occupiedBy.canEnter(getNeighborInDirection(goingIn), goingIn);
            }
            return false;
        }
        return true;
    }

    public boolean canBeEnteredBy(Box box, Direction goingIn){
        if(occupiedBy != null){
            if (occupiedBy.canBeOverPoweredBy(box, goingIn)) {
                return true;
            }
            return false;
        }
        return true;
    }

    public void setNeighborInDirection(Direction direction, Tile tile) {
        this.neighbors.put(direction, tile);
    }

    public Tile getNeighborInDirection(Direction direction){
        return neighbors.get(direction);
    }

    public void setOccupiedBy(GameObject occupiedBy) {
        this.occupiedBy = occupiedBy;
    }

    public String toString() {
        return occupiedBy == null ? "T" : occupiedBy.toString();
    }
}

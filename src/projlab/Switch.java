package projlab;

public class Switch extends Tile {
    private Hole controlling;

    public Switch() {
        controlling = null;
    }


    @Override
    public void enter(Box box, Direction direction) {
        super.enter(box, direction);
        //Changes controlling tile's
        controlling.setClosed(!controlling.isClosed());
    }

    public void setControlling(Hole controlling) {
        this.controlling = controlling;
    }

    @Override
    public void leave(Box box, Direction direction) {
        super.leave(box);
        //Changes controlling tile's
        controlling.setClosed(!controlling.isClosed());
    }
    
    public void leave(Worker worker, Direction direction) {
        super.leave(worker);
    }

    @Override
    public String toString() {
        return occupiedBy == null ? "S" : occupiedBy.toString();
    }
}

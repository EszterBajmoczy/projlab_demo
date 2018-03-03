package projlab;

public class Switch extends Tile {
    private Hole controlling;

    public Switch() {
        controlling = null;
    }

    public void toggle(){
        flipSwitch();
        System.out.println("Switch toggled!");
    }

    @Override
    public void enter(Box box, Direction direction) {
        super.enter(box, direction);
        toggle();
    }

    public void setControlling(Hole controlling) {
        this.controlling = controlling;
    }

    @Override
    public void leave(GameObject go) {
        super.leave(go);
        toggle();
    }

    @Override
    public String toString() {
        return occupiedBy == null ? "S" : occupiedBy.toString();
    }
}

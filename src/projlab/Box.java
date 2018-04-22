package projlab;

import projlab.SyntaxError;
import projlab.NotImplementedException;

import java.util.ArrayList;
import java.util.Random;

/**
* A doboz viselkedését implementáló osztály.  A GameObject leszármazottja.
*/
public class Box extends GameObject {
    //Megadja, hogy a dobozt el lehet-e mozdítani a helyéről
    private boolean locked = false;
    //A doboz súlya
    int weight = new Random().nextInt(30) + 20;
    
    /**
    * Az osztály egy paraméterű konstruktora
    * @param t Az a csempe, amelyiken a doboz a játék kezdetekor tartozkodik
    */
    public Box(Tile t) { this.tile = t; }

    /**
    * Az osztály kettő paraméterű konstruktora
    * @param t      Az a csempe, amelyiken a doboz a játék kezdetekor tartozkodik
    * @param weight A csempe súlya    
    */
    public Box(Tile t, int weight) {
        this.tile = t;
        this.weight = weight;
    }
    
    /**
    * Egy adott <osztályt> olvas be a térképfájlból, és
    * visszatér az <osztály> megfelelően inicializált példányával.
    * @param tokens         A térképfile tokenek-re szétszedve
    * @param t              Az a csempe, amelyiken a doboz a játék kezdetekor tartozkodik
    * @throws SyntaxError   Ha nem a térképleíró nyelv szabályainak megfelelő struktúrát
                            talál
    */
    public static Box fromString(ArrayList<String> tokens, Tile t) throws SyntaxError {
        int weight = -1;
        while (! tokens.get(0).equals("}")){
            if (tokens.remove(0).equals(":weight"))
                weight = Map.parse_number(tokens);
        }

        tokens.remove(0);

        if (weight > 0)
            return new Box(t, weight);

        return new Box(t);
    }

    /**
    * Az ősosztálytól örökölt metódus.
    */
    @Override
    public void move(Direction d)  { }

    /**
    * Megadja, hogy ez a doboz adott erővel nyomva eltolható-e a megadott irányban 
    * lévő mezőre. Ha van mező az adott irányban, akkor a visszatérési érték megegyezik 
    * azzal, hogy a szomszédos mező be tudja-e a fogani ezt a dobozt (egyébként false).
    * @param t      Az a mező, amire tolni szeretnék
    * @param d      Az irány, amibe tolni szeretnék
    * @param force  Az erő, amivel tolni szeretnék
    * @return       Igaz vagy hamis értékkel tér vissza attól függően, hogy eltolható-e a doboz
    */
    @Override
    public boolean canEnter(Tile t, Direction d, double force) {
        if (t == null){
            return false;
        }
        return t.canBeEnteredBy(this, d, force);
    }

    /**
    * A doboz eltolásakor fellépő viselkedést implementálja. Mivel itt már le van ellenőrizve, 
    * hogy beléphet-e a egadott irányban lévő mezőre, itt csupán elhagyja a régi mezőt, belép 
    * ez újra, majd végül leellenőrzni, hogy nincs-e bezárva.
    * @param d  Az irány, amerre a dobozt eltolják
    */
    @Override
    public void push(Direction d) {
        tile.leave(this);
        tile.getNeighborInDirection(d).enter(this, d);
        checkLocked();
    }

    /**
    * Megadja, hogy egy adott erővel tolt mésik doboz el tudja-e tolni ezt a dobozt. Ha tolóerő nagyobb, 
    * mint ennek a doboznak a súlya, akkor a visszatérési érték megegyezik azzal, hogy a szomszédos mezőre 
    * be tud-e lépni ez a doboz a saját súlyával csökkentett nyomóerővel tolva (egyébként false).
    * @param b      A másik doboz, ami arrébb akarja tolni
    * @param d      Az irány, amerre tolni akarja
    * @param force  Az erő, amivel tolni akarja
    * @return       Igazat vagy hamisat ad vissza, annak megfelelően, hogy a doboz eltudja-e tolni
    */
    @Override
    public boolean canBeOverPoweredBy(Box b, Direction d, double force) {
        if (force < weight)
            return false;
        return canEnter(tile.getNeighborInDirection(d), d, force - weight);
    }

    /**
    * Megadja, hogy egy adott erővel toló Worker el tudja-e tolni ezt a dobozt. Ha tolóerő nagyobb, 
    * mint ennek a doboznak a súlya, akkor a visszatérési érték megegyezik azzal, hogy a szomszédos 
    * mezőre be tud-e lépni ez a doboz a saját súlyával csökkentett nyomóerővel tolva (egyébként false).
    * @param w      A munkás, aki el akarja tolni
    * @param d      Az irány, amerre tolni akarja
    * @param force  Az erő, amivel tolni akarja
    * @return       Igazat vagy hamisat ad vissza, annak megfelelően, hogy a eltudja-e tolni
    */
    @Override
    public boolean canBeOverPoweredBy(Worker w, Direction d, double force) {
        if (force < weight)
            return false;
        return canEnter(tile.getNeighborInDirection(d), d, force - weight);
    }

    /**
    * A controller-en keresztül eltávolítja a játékból ezt a dobozt.
    */
    @Override
    public void removeFromGame() {
        controller.removeBox(this);
    }

    /*
    * Ellenőrzi, hogy a doboznak van-e két olyan egymás melletti szomszédja, ahova nem lehetséges 
    * betolni a dobozt (tehát egy munkás nem számít). Ha van, a locked flag-et igazra állítja.
    *
    * Teztesetek:
    *
    *   FAL                 FAL
    *   +-+                 +-+
    *FAL| | MEZŐ      MEZŐ  | | MEZŐ
    *   +-+                 +-+
    *    MEZŐ               FAL
    *
    *   !OK                 OK
    */
    private void checkLocked() {
        if (tile == null)
            return;
        boolean northIsOpen = canEnter(tile.getNeighborInDirection(Direction.UP), Direction.UP, 10000);
        boolean southIsOpen = canEnter(tile.getNeighborInDirection(Direction.DOWN), Direction.DOWN, 10000);
        boolean eastIsOpen = canEnter(tile.getNeighborInDirection(Direction.RIGHT), Direction.RIGHT, 10000);
        boolean westIsOpen = canEnter(tile.getNeighborInDirection(Direction.LEFT), Direction.LEFT, 10000);
        locked = ! ( (northIsOpen && southIsOpen) || (westIsOpen && eastIsOpen) );


    }

    /**
    * A locked getter-e.
    */
    public boolean isLocked() {
        return locked;
    }


    /**
    * Visszatér a doboz jelkódjával (“B”).
    * @return A doboz jelkódja
    */
    @Override
    public String toString() {
        return "B ";
    }
}

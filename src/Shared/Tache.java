package Shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tache implements Serializable {

    public List<Calcul> tache;

    public Tache() {
        this.tache = new ArrayList<>();
    }
}

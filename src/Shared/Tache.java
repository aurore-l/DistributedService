package Shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un tache
 */
public class Tache implements Serializable {

    public List<Calcul> tache;

    public Tache() {
        this.tache = new ArrayList<>();
    }
}

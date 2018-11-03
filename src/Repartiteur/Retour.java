package Repartiteur;

import Shared.ServeurDeCalculInterface;
import Shared.Tache;

public class Retour {

    private ServerDeCalculAugmente serveurDeCalcul = null;
    private Tache tache = null;
    private int codeRetour;

    public Retour(ServerDeCalculAugmente serveurDeCalcul, Tache tache, int codeRetour) {
        this.serveurDeCalcul = serveurDeCalcul;
        this.tache = tache;
        this.codeRetour = codeRetour;
    }

    public ServerDeCalculAugmente getServeurDeCalcul() {
        return serveurDeCalcul;
    }

    public Tache getTache() {
        return tache;
    }

    public int getCodeRetour() {
        return codeRetour;
    }
}

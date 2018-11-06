package Repartiteur;

import Shared.Tache;

/**
 * Classe représentant le retour d'un thread (Callable) en mode sécurisé
 */
class Retour {

    /**
     * Informations sur le serveur de calcul associé au thread
     */
    private ServerDeCalculAugmente serveurDeCalcul;
    /**
     * Tache associée au thread
     */
    private Tache tache;
    /**
     * Code retour du thread
     */
    private int codeRetour;

    Retour(ServerDeCalculAugmente serveurDeCalcul, Tache tache, int codeRetour) {
        this.serveurDeCalcul = serveurDeCalcul;
        this.tache = tache;
        this.codeRetour = codeRetour;
    }

    ServerDeCalculAugmente getServeurDeCalcul() {
        return serveurDeCalcul;
    }

    Tache getTache() {
        return tache;
    }

    int getCodeRetour() {
        return codeRetour;
    }
}

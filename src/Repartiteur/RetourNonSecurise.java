package Repartiteur;

import Shared.Calcul;
import Shared.Tache;

/**
 * Classe représentant le retour d'un thread (Callable) en mode non sécurisé
 */
class RetourNonSecurise {

    /**
     * Informations sur le premier serveur de calcul associé au thread
     */
    private ServerDeCalculAugmente serveurDeCalcul1;
    /**
     * Informations sur le deuxième serveur de calcul associé au thread
     */
    private ServerDeCalculAugmente serveurDeCalcul2 ;
    /**
     * Tache contenant les calculs à recalculer, c'est-à-dire ceux dont les deux résultats obtenus n'étaient pas les mêmes
     */
    private Tache tacheARecalculer;
    /**
     * Tache contenant les calculs calculés, c'est-à-dire ceux dont les deux résultats obtenus étaient les mêmes
     */
    private Tache tacheCalculee;
    /**
     * Code retour du sous-thread associé au premier serveur de calcul
     */
    private int codeRetour1;
    /**
     * Code retour du sous-thread associé au deuxième serveur de calcul
     */
    private int codeRetour2;

    RetourNonSecurise(ServerDeCalculAugmente serveurDeCalcul1, ServerDeCalculAugmente serveurDeCalcul2) {
        this.serveurDeCalcul1 = serveurDeCalcul1;
        this.serveurDeCalcul2 = serveurDeCalcul2;
        tacheARecalculer = new Tache();
        tacheCalculee = new Tache();
    }

    ServerDeCalculAugmente getServeurDeCalcul1() {
        return serveurDeCalcul1;
    }

    ServerDeCalculAugmente getServeurDeCalcul2() {
        return serveurDeCalcul2;
    }

    int getCodeRetour1() {
        return codeRetour1;
    }

    int getCodeRetour2() {
        return codeRetour2;
    }

    void setCodeRetour1(int codeRetour1) {
        this.codeRetour1 = codeRetour1;
    }

    void setCodeRetour2(int codeRetour2) {
        this.codeRetour2 = codeRetour2;
    }

    void addCalculReussi(Calcul calcul) {
        tacheCalculee.tache.add(calcul);
    }

    void addCalculARecalculer(Calcul calcul) {
        tacheARecalculer.tache.add(calcul);
    }

    Tache getTacheARecalculer() {
        return tacheARecalculer;
    }

    Tache getTacheCalculee() {
        return tacheCalculee;
    }
}

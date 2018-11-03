package Repartiteur;

import Shared.Calcul;
import Shared.ServerDeCalculAugmente;
import Shared.Tache;

public class RetourSecurise {

    private ServerDeCalculAugmente serveurDeCalcul1;
    private ServerDeCalculAugmente serveurDeCalcul2 ;
    private Tache tacheARecalculer;
    private Tache tacheCalculee;
    private int codeRetour1;
    private int codeRetour2;

    public RetourSecurise(ServerDeCalculAugmente serveurDeCalcul1, ServerDeCalculAugmente serveurDeCalcul2) {
        this.serveurDeCalcul1 = serveurDeCalcul1;
        this.serveurDeCalcul2 = serveurDeCalcul2;
        tacheARecalculer = new Tache();
        tacheCalculee = new Tache();
    }

    public ServerDeCalculAugmente getServeurDeCalcul1() {
        return serveurDeCalcul1;
    }

    public ServerDeCalculAugmente getServeurDeCalcul2() {
        return serveurDeCalcul2;
    }

    public int getCodeRetour1() {
        return codeRetour1;
    }

    public int getCodeRetour2() {
        return codeRetour2;
    }

    public void setCodeRetour1(int codeRetour1) {
        this.codeRetour1 = codeRetour1;
    }

    public void setCodeRetour2(int codeRetour2) {
        this.codeRetour2 = codeRetour2;
    }

    public void addCalculReussi(Calcul calcul) {
        tacheCalculee.tache.add(calcul);
    }

    public void addCalculARecalculer(Calcul calcul) {
        tacheARecalculer.tache.add(calcul);
    }

    public Tache getTacheARecalculer() {
        return tacheARecalculer;
    }

    public Tache getTacheCalculee() {
        return tacheCalculee;
    }
}

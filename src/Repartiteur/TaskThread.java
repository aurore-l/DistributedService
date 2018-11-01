package Repartiteur;

import Shared.Calcul;
import Shared.ServeurDeCalculInterface;
import Shared.Tache;
import org.apache.commons.lang3.tuple.Pair;

import java.rmi.RemoteException;
import java.util.List;

public class TaskThread extends Thread {

    private ServeurDeCalculInterface serveurDeCalcul = null;
    private Tache tache = null;
    private List<Calcul> listeDeCalcul = null;
    private List<Calcul> listeResultatCalcul = null;
    private List<ServeurDeCalculInterface> listeServeursDeCalcul = null;

    public TaskThread(ServeurDeCalculInterface serveurDeCalcul, Tache tache, List<Calcul> listeDeCalcul, List<Calcul> listeResultatCalcul, List<ServeurDeCalculInterface> listeServeursDeCalcul) {
        this.serveurDeCalcul = serveurDeCalcul;
        this.tache = tache;
        this.listeDeCalcul = listeDeCalcul;
        this.listeResultatCalcul = listeResultatCalcul;
        this.listeServeursDeCalcul = listeServeursDeCalcul;
    }

    @Override
    public void run() {
        Pair<Boolean, Tache> retour = null;
        try {
            retour = serveurDeCalcul.recevoirTache(tache);
        } catch (RemoteException e) {
            e.printStackTrace();
            annulerTache();
            supprimerServeur();
        }
        if (retour != null) {
            if (retour.getKey()) {
                completerResultat(retour.getValue());
                rendreServeurDisponible();
            } else {
                annulerTache();
                rendreServeurDisponible();
            }
        }
    }



    private void supprimerServeur() {
        for (ServeurDeCalculInterface serveurDeCalculInterface : listeServeursDeCalcul) {
            if (serveurDeCalculInterface == serveurDeCalcul) {
                listeServeursDeCalcul.remove(serveurDeCalcul);
            }
        }
    }


    private void completerResultat(Tache tache) {
        listeResultatCalcul.addAll(tache.tache);
    }

    private void annulerTache() {
        listeDeCalcul.addAll(tache.tache);
    }


    private void rendreServeurDisponible() {
        listeServeursDeCalcul.add(serveurDeCalcul);
    }

}

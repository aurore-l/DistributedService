package Repartiteur;

import Shared.Tache;
import org.apache.commons.lang3.tuple.Pair;

import java.rmi.RemoteException;
import java.util.concurrent.Callable;

/**
 * Classe chargée de lancer le calcul sur le serveur calcul en mode sécurisé
 */
public class TacheCallable implements Callable<Retour> {

    /**
     * Serveur de calcul à contacter
     */
    private ServerDeCalculAugmente serveurDeCalcul;
    /**
     * Tache à envoyer
     */
    private Tache tache;

    TacheCallable(ServerDeCalculAugmente serveurDeCalcul, Tache tache) {
        this.serveurDeCalcul = null;
        this.serveurDeCalcul = serveurDeCalcul;
        this.tache = tache;
            }


    /**
     * Envoi la tache au serveur et crée un Retour
     * @see Retour
     * @return Retour
     * @throws Exception
     */
    @Override
    public Retour call() throws Exception {
        Pair<Integer, Tache> retour;
        try {
            retour = serveurDeCalcul.getServeurDeCalculInterface().recevoirTache(tache);
        } catch (RemoteException e) {
            return new Retour(serveurDeCalcul,tache,3); //RemoteException
        }
        if (retour != null) {
            return new Retour(serveurDeCalcul,retour.getValue(),retour.getKey());
        }  else {
            return new Retour(serveurDeCalcul,tache,3);
        }
    }
}

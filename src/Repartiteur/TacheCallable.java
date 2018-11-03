package Repartiteur;

import Shared.Tache;
import org.apache.commons.lang3.tuple.Pair;

import java.rmi.RemoteException;
import java.util.concurrent.Callable;

public class TacheCallable implements Callable<Retour> {

    private ServerDeCalculAugmente serveurDeCalcul = null;
    private Tache tache = null;

    TacheCallable(ServerDeCalculAugmente serveurDeCalcul, Tache tache) {
        this.serveurDeCalcul = serveurDeCalcul;
        this.tache = tache;
            }


    @Override
    public Retour call() throws Exception {
        Pair<Integer, Tache> retour = null;
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

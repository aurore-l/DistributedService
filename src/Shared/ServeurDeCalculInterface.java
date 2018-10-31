package Shared;

import org.apache.commons.lang3.tuple.Pair;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServeurDeCalculInterface extends Remote {

    Pair<Boolean,Tache> recevoirTache(Tache tache) throws RemoteException;
    boolean ouvrirSession(String identifiant, String motDePasse)  throws RemoteException;
}

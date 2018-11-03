package Shared;

import org.apache.commons.lang3.tuple.Pair;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServeurDeCalculInterface extends Remote {

    Pair<Integer,Tache> recevoirTache(Tache tache) throws RemoteException;
    boolean ouvrirSession(String identifiant, String motDePasse)  throws RemoteException;
    String getNombreCalculRecu() throws RemoteException;
    int recupererCapacite() throws  RemoteException;

}

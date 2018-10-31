package Shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.apache.commons.collections4.MultiValuedMap;

public interface ServeurDeNomInterface extends Remote {
    MultiValuedMap<String, String> getServeurDeCalculMap() throws RemoteException;
    boolean initiationServeurDeCalcul(String hostname, String name) throws RemoteException;
    boolean connecterRepartiteur(String identifiant, String motDePasse) throws  RemoteException;
    boolean verifierRepartiteur(RepartiteurIdentite repartiteurIdentite) throws RemoteException;

}

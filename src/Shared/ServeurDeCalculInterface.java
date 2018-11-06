package Shared;

import org.apache.commons.lang3.tuple.Pair;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface d'un serveur de calcul
 */
public interface ServeurDeCalculInterface extends Remote {

    /**
     * Reçoit une tache du répartiteur
     * @param tache Tache reçue à calculer
     * @return Pair de code retour et de la tache complétée ou non
     * @throws RemoteException
     */
    Pair<Integer,Tache> recevoirTache(Tache tache) throws RemoteException;

    /**
     * Ouvre une session entre un répartiteur et le serveur de calcul
     * @param identifiant Identifiant du répartiteur
     * @param motDePasse Mot de passe du répartiteur
     * @return true si la session est ouverte, false sinon
     * @throws RemoteException
     */
    boolean ouvrirSession(String identifiant, String motDePasse)  throws RemoteException;

    /**
     * Vérifie si un répartiteur est connecté
     * @return true si le répartiteur actuel est bien le répartieur connecté, false sinon
     */
    int recupererCapacite() throws  RemoteException;

}

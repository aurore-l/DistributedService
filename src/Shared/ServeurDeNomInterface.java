package Shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.apache.commons.collections4.MultiValuedMap;

/**
 * Interface d'un serveur de nom
 */
public interface ServeurDeNomInterface extends Remote {

    /**
     * Renvoie les informations sur les serveur de calculs connectée
     * @return Map contenant les informations : ip:port et nom dans le rmiregistry
     * @throws RemoteException
     */
    MultiValuedMap<String, String> getServeurDeCalculMap() throws RemoteException;

    /**
     * Initiation de la connexion entre le serveur de nom et un serveur de calcul. Permettre d'enregristrer les informations sur le serveur de calcul
     * @param hostname ip:port du serveur de calcul
     * @param name nom dans le rmiregistry du serveur de calcul
     * @return true
     * @throws RemoteException
     */
    boolean initiationServeurDeCalcul(String hostname, String name) throws RemoteException;

    /**
     * Initiation de la connexion entre le serveur de nom et le répariteur
     * @param identifiant Identifiant du répartiteur
     * @param motDePasse Mot du passe du répartiteur
     * @return true si aucun répartiteur n'était connecté avant et que l'identifiant et le mot de passe sont non null, false sinon
     * @throws RemoteException
     */
    boolean connecterRepartiteur(String identifiant, String motDePasse) throws  RemoteException;

    /**
     * Vérifie si le répartieur passé en paramètre correspond au répartiteur connecté
     * @param repartiteurIdentite informations sur le répartieur à vérifier
     * @return true si les répartiteurs sont les mêmes, false sinon
     * @throws RemoteException
     */
    boolean verifierRepartiteur(RepartiteurIdentite repartiteurIdentite) throws RemoteException;

}

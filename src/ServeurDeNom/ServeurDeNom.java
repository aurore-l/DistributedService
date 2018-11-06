package ServeurDeNom;

import Shared.RepartiteurIdentite;
import Shared.ServeurDeNomInterface;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;

import static java.rmi.server.RemoteServer.getClientHost;

/**
 * Classe représentant le serveur de nom
 */
public class ServeurDeNom implements ServeurDeNomInterface {

    /**
     * Map contenant les informations sur les serveurs de calculs connectée : ip:port et nom dans le rmiregistry
     */
    private MultiValuedMap<String, String> serveurDeCalculMap;
    /**
     * Informations sur le répartiteur connecté
     */
    private RepartiteurIdentite repartiteurIdentite = null;


    public static void main(String[] args) {
        ServeurDeNom serveurDeNom = new ServeurDeNom();
        serveurDeNom.run(args[0]);
    }


    private ServeurDeNom() {
        super();
        serveurDeCalculMap = new ArrayListValuedHashMap<>();

    }

    private void run(String port) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            ServeurDeNomInterface stub = (ServeurDeNomInterface) UnicastRemoteObject
                    .exportObject(this, Integer.parseInt(port));

            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(port));
            registry.rebind("serveurDeNom", stub);
            System.out.println("Server ready.");
        } catch (ConnectException e) {
            System.err
                    .println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
            System.err.println();
            System.err.println("Erreur: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    /**
     * Initiation de la connexion entre le serveur de nom et un serveur de calcul. Permettre d'enregristrer les informations sur le serveur de calcul
     * @param hostname ip:port du serveur de calcul
     * @param name nom dans le rmiregistry du serveur de calcul
     * @return true
     * @throws RemoteException
     */
    @Override
    public boolean initiationServeurDeCalcul(String hostname, String name) throws RemoteException {
        serveurDeCalculMap.put(hostname, name);
        return true;
    }

    /**
     * Initiation de la connexion entre le serveur de nom et le répariteur
     * @param identifiant Identifiant du répartiteur
     * @param motDePasse Mot du passe du répartiteur
     * @return true si aucun répartiteur n'était connecté avant et que l'identifiant et le mot de passe sont non null, false sinon
     * @throws RemoteException
     */
    @Override
    public boolean connecterRepartiteur(String identifiant, String motDePasse) throws RemoteException {
        if (repartiteurIdentite == null) {
            if (identifiant != null && motDePasse != null) {
                try {
                    repartiteurIdentite = new RepartiteurIdentite(getClientHost(), identifiant, motDePasse);
                    return true;
                } catch (ServerNotActiveException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Vérifie si le répartieur passé en paramètre correspond au répartiteur connecté
     * @param repartiteurIdentite informations sur le répartieur à vérifier
     * @return true si les répartiteurs sont les mêmes, false sinon
     * @throws RemoteException
     */
    @Override
    public boolean verifierRepartiteur(RepartiteurIdentite repartiteurIdentite) throws RemoteException {
        return this.repartiteurIdentite != null && this.repartiteurIdentite.compareTo(repartiteurIdentite) == 0;
    }

    /**
     * Renvoie les informations sur les serveur de calculs connectée
     * @return Map contenant les informations : ip:port et nom dans le rmiregistry
     * @throws RemoteException
     */
    @Override
    public MultiValuedMap<String, String> getServeurDeCalculMap() throws RemoteException {
        return serveurDeCalculMap;
    }


}

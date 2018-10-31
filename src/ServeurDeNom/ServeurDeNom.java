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

public class ServeurDeNom implements ServeurDeNomInterface {

    private MultiValuedMap<String, String> serveurDeCalculMap = null;
    private RepartiteurIdentite repartiteurIdentite = null;


    public static void main(String[] args) {
        ServeurDeNom serveurDeNom = new ServeurDeNom();
        serveurDeNom.run();
    }


    private ServeurDeNom() {
        super();
        serveurDeCalculMap = new ArrayListValuedHashMap<>();

    }

    private void run() {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            ServeurDeNomInterface stub = (ServeurDeNomInterface) UnicastRemoteObject
                    .exportObject(this, 0);

            //Registry registry = LocateRegistry.getRegistry();
            //registry.rebind("serveurDeNom", stub);
            Registry registry = LocateRegistry.createRegistry(1099);
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

    @Override
    public boolean initiationServeurDeCalcul(String hostname, String name) throws RemoteException {
        serveurDeCalculMap.put(hostname, name);
        return true;
    }

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

    @Override
    public boolean verifierRepartiteur(RepartiteurIdentite repartiteurIdentite) throws RemoteException {
        return this.repartiteurIdentite != null && this.repartiteurIdentite.compareTo(repartiteurIdentite) == 0;
    }

    @Override
    public MultiValuedMap<String, String> getServeurDeCalculMap() throws RemoteException {
        return serveurDeCalculMap;
    }


}

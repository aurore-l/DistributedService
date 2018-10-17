package ServeurDeNom;

import Shared.ServeurDeNomInterface;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.tuple.Pair;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServeurDeNom implements ServeurDeNomInterface {

    private MultiValuedMap<String, String> serveurDeCalculMap = null;


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

            Registry registry = LocateRegistry.getRegistry();
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
    public MultiValuedMap<String, String> getServeurDeCalculMap() throws RemoteException {
        return serveurDeCalculMap;
    }


}

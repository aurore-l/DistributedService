package Repartiteur;

import Shared.RepartiteurInterface;
import Shared.ServeurDeCalculInterface;
import Shared.ServeurDeNomInterface;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.tuple.Pair;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Repartiteur implements RepartiteurInterface {

    private ServeurDeNomInterface serveurDeNomInterface = null;
    private List<ServeurDeCalculInterface> serveurDeCalculInterfaceList = null;


    public static void main(String[] args) {
        Repartiteur repartiteur = new Repartiteur();
        repartiteur.run();
    }


    private Repartiteur() {
        super();
        serveurDeNomInterface = loadServeurDeNomStub("127.0.0.1");

        serveurDeCalculInterfaceList = loadAllServeursDeCalculStub();

    }


    private ServeurDeNomInterface loadServeurDeNomStub(String hostname) {
        ServeurDeNomInterface stub = null;

        try {
            Registry registry = LocateRegistry.getRegistry(hostname);
            stub = (ServeurDeNomInterface) registry.lookup("serveurDeNom");
        } catch (NotBoundException e) {
            System.out.println("Erreur: Le nom '" + e.getMessage()
                    + "' n'est pas défini dans le registre.");
        } catch (RemoteException e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        return stub;
    }

    private List<ServeurDeCalculInterface> loadAllServeursDeCalculStub() {
        List<ServeurDeCalculInterface> list = new ArrayList<>();
        try {
            MultiValuedMap<String, String> serveurDeCalculInfosMap = serveurDeNomInterface.getServeurDeCalculMap();
            for (Map.Entry<String, String> entry : serveurDeCalculInfosMap.entries() ) {
                ServeurDeCalculInterface serveurDeCalcul = loadServeurDeCalculStub(entry.getKey(), entry.getValue());
                list.add(serveurDeCalcul);
            }
            return list;
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }

    }

    private ServeurDeCalculInterface loadServeurDeCalculStub(String hostname, String name) {
        ServeurDeCalculInterface stub = null;

        try {
            Registry registry = LocateRegistry.getRegistry(hostname);
            stub = (ServeurDeCalculInterface) registry.lookup(name);
        } catch (NotBoundException e) {
            System.out.println("Erreur: Le nom '" + e.getMessage()
                    + "' n'est pas défini dans le registre.");
        } catch (RemoteException e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        return stub;
    }


    private void run() {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            RepartiteurInterface stub = (RepartiteurInterface) UnicastRemoteObject
                    .exportObject(this, 0);

            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("repartiteur", stub);
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
}

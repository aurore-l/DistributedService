package Repartiteur;

import Shared.*;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
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
    private String fichier;
    private List<Calcul> listeDeCalcul;


    public static void main(String[] args) {
        Repartiteur repartiteur = new Repartiteur(args[0]);
        repartiteur.run();
    }


    private Repartiteur(String fichier) {
        super();
        serveurDeNomInterface = loadServeurDeNomStub("127.0.0.1");
        try {
            if (!serveurDeNomInterface.connecterRepartiteur("test","test")) {
                System.err.println("Un répartiteur est déjà connecté au serveur de nom");
                System.exit(-1);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        serveurDeCalculInterfaceList = loadAllServeursDeCalculStub();

        this.fichier = fichier;

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
                if (serveurDeCalcul.ouvrirSession("test","test")) {
                    list.add(serveurDeCalcul);
                }
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
        lirefichier();
        Tache tache = new Tache();
        tache.tache = listeDeCalcul;
        Pair<Boolean, Tache> retour = null;
        for (ServeurDeCalculInterface serverDeCalculInterface : serveurDeCalculInterfaceList) {
            try {
                retour = serverDeCalculInterface.recevoirTache(tache);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        int resultat = calculerResultat(retour.getRight());
        System.out.println("Le résultat du calcul est : " + resultat);

    }


    private void lirefichier() {
        listeDeCalcul = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(fichier);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] splitedLine = line.split(" ");
                String operation = splitedLine[0];
                int operande = Integer.parseInt(splitedLine[1]);
                if (operation.equals("pell")) {
                    listeDeCalcul.add(new Calcul(Op.PELL, operande));
                } else if (operation.equals("prime")) {
                    listeDeCalcul.add(new Calcul(Op.PRIME, operande));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private int calculerResultat(Tache tache) {
        int somme = 0;
        for (Calcul calcul : tache.tache) {
            System.out.println(calcul);
            somme += calcul.getResult();
            somme %= 4000;
        }
        return somme;
    }


}

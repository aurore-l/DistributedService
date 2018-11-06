package ServeurDeCalcul;

import Shared.*;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ThreadLocalRandom;

import static java.rmi.server.RemoteServer.getClientHost;

/**
 * Classe représentat un serveur de calcul
 */
public class ServeurDeCalcul implements ServeurDeCalculInterface {

    private ServeurDeNomInterface serveurDeNomInterface;
    /**
     * Capacité du serveur de calcul
     */
    private long capacite;
    /**
     * Taux de réponse incorrectes envoyées
     */
    private int tauxDeReponsesIncorrectes;
    /**
     * Informations sur le répartiteur associé
     */
    private RepartiteurIdentite repartiteurIdentite = null;

    public static void main(String[] args){
       ServeurDeCalcul serveurDeCalcul = new ServeurDeCalcul(args[2], args[3], args[4], args[5]); //args[2] = ip serveur de nom, args[3] = port serveur de nom, args[4] = capacite, args[5] = taux de réponse erronée
       serveurDeCalcul.run(args[0], args[1]); //args[0] = nom dans rmiregistry, args[1] = port d'écoute

    }

    private ServeurDeCalcul(String ipServeurDeNom, String portServeurDeNom, String capacite, String tauxDeReponsesIncorrectes) {
        super();
        serveurDeNomInterface = loadServeurDeNomStub(ipServeurDeNom, portServeurDeNom);
        this.capacite = Long.valueOf(capacite);
        this.tauxDeReponsesIncorrectes = Integer.valueOf(tauxDeReponsesIncorrectes);


    }

    private void run(String name, String port) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            ServeurDeCalculInterface stub = (ServeurDeCalculInterface) UnicastRemoteObject
                    .exportObject(this, Integer.parseInt(port));

            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(port));
            registry.rebind(name, stub);
            System.out.println("ServerDeCalcul ready.");


            try {
                String ip = System.getProperty("java.rmi.server.hostname");
                serveurDeNomInterface.initiationServeurDeCalcul(ip+":"+port,name);
            } catch (RemoteException e) {
                e.printStackTrace();
            }


        } catch (ConnectException e) {
            System.err
                    .println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
            System.err.println();
            System.err.println("Erreur: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }



    private ServeurDeNomInterface loadServeurDeNomStub(String hostname, String port) {
        ServeurDeNomInterface stub = null;

        try {
            Registry registry = LocateRegistry.getRegistry(hostname, Integer.parseInt(port));
            stub = (ServeurDeNomInterface) registry.lookup("serveurDeNom");
        } catch (NotBoundException e) {
            System.out.println("Erreur: Le nom '" + e.getMessage()
                    + "' n'est pas défini dans le registre.");
        } catch (RemoteException e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        return stub;
    }

    /**
     * Vérifie si la tache est acceptée en fonction de la capacité et du nombre de calcul recu
     * @param nombreDeCalculs nombre de calculs reçus
     * @return true si la tache est acceptée, false sinon
     */
    private boolean accepteTache(long nombreDeCalculs) {
        if (nombreDeCalculs <= capacite) {
            return true;
        }
        long taux = ((nombreDeCalculs - capacite)*100)/(4 * capacite);
        if (taux>=100) {
            return false;
        } else if (taux==0){
            return true;
        } else {
            int randomNum = ThreadLocalRandom.current().nextInt(0, 101);
            return randomNum > taux;
        }
    }

    /**
     * Indique si le serveur de calcul doit renvoyer une mauvaise reponse
     * @return true si le serveur doit renvoyer une mauvaise réponse, false sinon
     */
    private boolean envoieMauvaiseReponse() {
        int randomNum = ThreadLocalRandom.current().nextInt(0, 101);
        return randomNum < tauxDeReponsesIncorrectes;

    }


    /**
     * Calcule les calculs d'une tache
     * @param tache Tache contenant les calculs avec leur résultats
     */
    private void calculer(Tache tache) {
        for (Calcul calcul : tache.tache) {
            if (envoieMauvaiseReponse()) {
                calcul.setResultat( ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE-4000));
            } else {
                if (calcul.getOperation() == Op.PELL) {
                    calcul.setResultat(Operations.pell(calcul.getOperande()));
                } else if (calcul.getOperation() == Op.PRIME) {
                    calcul.setResultat(Operations.prime(calcul.getOperande()));
                }
            }
        }
    }

    /**
     * Reçoit une tache du répartiteur
     * @param tache Tache reçue à calculer
     * @return Pair de code retour et de la tache complétée ou non
     * @throws RemoteException
     */
    @Override
    public Pair<Integer, Tache> recevoirTache(Tache tache) throws RemoteException {
        if (repartiteurConnecte()) {
            boolean accepteTache = accepteTache(tache.tache.size());
            if (!accepteTache) {
                return new MutablePair<>(2, tache); //tache non acceptée
            } else {
                calculer(tache);
                return new MutablePair<>(0, tache);
            }
        } return new MutablePair<>(1, tache); //repartiteur non connu
    }

    /**
     * Ouvre une session entre un répartiteur et le serveur de calcul
     * @param identifiant Identifiant du répartiteur
     * @param motDePasse Mot de passe du répartiteur
     * @return true si la session est ouverte, false sinon
     * @throws RemoteException
     */
    @Override
    public boolean ouvrirSession(String identifiant, String motDePasse) throws RemoteException {
        if (!repartiteurConnecte()) {
            if (identifiant != null && motDePasse != null) {
                try {
                    boolean resultat = serveurDeNomInterface.verifierRepartiteur(new RepartiteurIdentite(getClientHost(), identifiant, motDePasse));
                    if (resultat) {
                        repartiteurIdentite = new RepartiteurIdentite(getClientHost(), identifiant, motDePasse);
                        return true;
                    } else {
                        return false;
                    }
                } catch (ServerNotActiveException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Vérifie si un répartiteur est connecté
     * @return true si le répartiteur actuel est bien le répartieur connecté, false sinon
     */
    private boolean repartiteurConnecte() {
        try {
            return repartiteurIdentite != null && repartiteurIdentite.getIp().equals(getClientHost());
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Renvoie la capacité du serveur de calcul
     * @return capacité
     * @throws RemoteException
     */
    @Override
    public int recupererCapacite() throws RemoteException {
        return (int) capacite;
    }
}

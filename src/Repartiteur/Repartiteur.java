package Repartiteur;

import Shared.*;
import org.apache.commons.collections4.MultiValuedMap;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * Classe du répartiteur qui se charge de répartir les calculs entre les différents serveurs de calculs
 */
public class Repartiteur implements Remote {

    private ServeurDeNomInterface serveurDeNomInterface;
    /**
     * Queue contenant les serveurs de calculs disponibles
     */
    private LinkedBlockingQueue<ServerDeCalculAugmente> serveurDeCalculInterfaceQueue;
    /**
     * Emplacement du fichier d'opérations
     */
    private String fichier;
    /**
     * Queue contenant les calculs devant être calculés
     */
    private LinkedBlockingQueue<Calcul> listeDeCalcul;
    /**
     * Liste contenant les calcul dont le résultat à été calculé
     */
    private List<Calcul> listeResultatCalcul;
    /**
     * Constante multiplicative a associer à la capacité d'un serveur pour calculer le nombre de tache à lui envoyer.
     */
    private final double FACTEUR_TAUX_DE_REFUS = 1.35; //1.35 correspond à 15% de taux de refus
    private boolean modeNonSecurise = false;


    public static void main(String[] args) {
        Repartiteur repartiteur = new Repartiteur(args[0],args[1], args[2]);
        repartiteur.run();
    }


    private Repartiteur(String fichier, String ipServeurDeNom, String portServeurDeNom) {
        super();
        listeResultatCalcul = new ArrayList<>();
        serveurDeNomInterface = loadServeurDeNomStub(ipServeurDeNom,portServeurDeNom);
        try {
            if (!serveurDeNomInterface.connecterRepartiteur("test","test")) {
                System.err.println("Un répartiteur est déjà connecté au serveur de nom");
                System.exit(-1);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        serveurDeCalculInterfaceQueue = loadAllServeursDeCalculStub();

        this.fichier = fichier;

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

    private LinkedBlockingQueue<ServerDeCalculAugmente> loadAllServeursDeCalculStub() {
        LinkedBlockingQueue<ServerDeCalculAugmente> queue = new LinkedBlockingQueue<>();
        try {
            MultiValuedMap<String, String> serveurDeCalculInfosMap = serveurDeNomInterface.getServeurDeCalculMap();
            for (Map.Entry<String, String> entry : serveurDeCalculInfosMap.entries() ) {
                ServeurDeCalculInterface serveurDeCalcul = loadServeurDeCalculStub(entry.getKey(), entry.getValue());
                try {
                    if (serveurDeCalcul.ouvrirSession("test", "test")) {
                        queue.add(new ServerDeCalculAugmente(serveurDeCalcul, entry.getKey(), entry.getValue(), serveurDeCalcul.recupererCapacite()));
                    }
                } catch (ConnectException e) {
                    System.err.println("Impossible de se connecter au serveur " + entry.getKey() + "  " + entry.getValue());
                }
            }
            return queue;
        } catch (RemoteException e) {
            e.printStackTrace();
            return queue;
        }

    }

    private ServeurDeCalculInterface loadServeurDeCalculStub(String hostname, String name) {
        ServeurDeCalculInterface stub = null;
        String ip = hostname.split(":")[0];
        String port = hostname.split(":")[1];

        try {
            Registry registry = LocateRegistry.getRegistry(ip, Integer.parseInt(port));
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
        Scanner reader = new Scanner(System.in);
        System.out.println("Entrez 0 pour mode sécurisé, entrez 1 pour mode non sécurisé : ");
        int n = reader.nextInt();
        reader.close();
        if (n==1) {
            modeNonSecurise = true;
        }

        verifierDisponibiliteServeurDeCalcul();

        lirefichier();

        if (!modeNonSecurise) {
            calculRepartiModeSecurise();
        } else {
            calculRepartiModeNonSecurise();
        }

        int resultat = calculerResultat();
        System.out.println("Le résultat du calcul est : " + resultat);

        System.exit(0);
    }

    /**
     * Méthode pour obtenir les résultats en mode non sécurisé
     */
    private void calculRepartiModeNonSecurise() {
        int compteurDeCallable = 0; //Nombre de thread en cours
        ExecutorService CallablePool = Executors.newFixedThreadPool(serveurDeCalculInterfaceQueue.size());
        CompletionService<RetourNonSecurise> service = new ExecutorCompletionService<RetourNonSecurise>(CallablePool);
        long startProcessingTime = System.currentTimeMillis();
        while (serveurDeCalculInterfaceQueue.size()>1 && !listeDeCalcul.isEmpty() ) {
            ServerDeCalculAugmente serveurDeCalculCourant1 = serveurDeCalculInterfaceQueue.poll();
            ServerDeCalculAugmente serveurDeCalculCourant2 = serveurDeCalculInterfaceQueue.poll();
            Tache tacheCourante = new Tache();
            for (int i = 0; i<Math.min(serveurDeCalculCourant1.getCapaciteDeCalcul(), serveurDeCalculCourant2.getCapaciteDeCalcul())* FACTEUR_TAUX_DE_REFUS && !listeDeCalcul.isEmpty(); i++) {
                tacheCourante.tache.add(listeDeCalcul.poll());
            }
            service.submit(new TacheCallableNonSecurise(serveurDeCalculCourant1,serveurDeCalculCourant2,tacheCourante));
            compteurDeCallable++;
        }

        while (compteurDeCallable > 0) {
            try {
                Future<RetourNonSecurise> future = service.take();
                try {
                    RetourNonSecurise retour = future.get();
                    listeDeCalcul.addAll(retour.getTacheARecalculer().tache);
                    listeResultatCalcul.addAll(retour.getTacheCalculee().tache);
                    if (retour.getCodeRetour1() == 0) {
                        //System.out.println(retour.getServeurDeCalcul1().getIp() + "   "+  retour.getServeurDeCalcul1().getNom()+ "    Retour ok du serveur                           "+(System.currentTimeMillis()-startProcessingTime));
                        serveurDeCalculInterfaceQueue.add(retour.getServeurDeCalcul1());
                    } else if (retour.getCodeRetour1() == 1) {
                        //System.out.println(retour.getServeurDeCalcul1().getIp() + "   "+  retour.getServeurDeCalcul1().getNom()+ "    Serveur ne connait pas le répartiteur          "+(System.currentTimeMillis()-startProcessingTime));
                    } else if (retour.getCodeRetour1() == 2 ) {
                        //System.out.println(retour.getServeurDeCalcul1().getIp() + "   "+  retour.getServeurDeCalcul1().getNom()+ "    Tache non acceptée                             "+(System.currentTimeMillis()-startProcessingTime));
                        serveurDeCalculInterfaceQueue.add(retour.getServeurDeCalcul1());
                    } else if (retour.getCodeRetour1() == 3) {
                        //System.out.println(retour.getServeurDeCalcul1().getIp() + "   "+  retour.getServeurDeCalcul1().getNom()+ "    Remote Exception                               "+(System.currentTimeMillis()-startProcessingTime));
                    }

                    if (retour.getCodeRetour2() == 0) {
                        //System.out.println(retour.getServeurDeCalcul2().getIp() + "   "+  retour.getServeurDeCalcul2().getNom()+ "    Retour ok du serveur                           "+(System.currentTimeMillis()-startProcessingTime));
                        serveurDeCalculInterfaceQueue.add(retour.getServeurDeCalcul2());
                    } else if (retour.getCodeRetour2() == 1) {
                        //System.out.println(retour.getServeurDeCalcul2().getIp() + "   "+  retour.getServeurDeCalcul2().getNom()+ "    Serveur ne connait pas le répartiteur          "+(System.currentTimeMillis()-startProcessingTime));
                    } else if (retour.getCodeRetour2() == 2 ) {
                        //System.out.println(retour.getServeurDeCalcul2().getIp() + "   "+  retour.getServeurDeCalcul2().getNom()+ "    Tache non acceptée                             "+(System.currentTimeMillis()-startProcessingTime));
                        serveurDeCalculInterfaceQueue.add(retour.getServeurDeCalcul2());
                    } else if (retour.getCodeRetour2() == 3) {
                        //System.out.println(retour.getServeurDeCalcul2().getIp() + "   "+  retour.getServeurDeCalcul2().getNom()+ "    Remote Exception                               "+(System.currentTimeMillis()-startProcessingTime));
                    }

                    compteurDeCallable--;


                    if (serveurDeCalculInterfaceQueue.size()>1 && !listeDeCalcul.isEmpty()) {
                        ServerDeCalculAugmente serveurDeCalculCourant1 = serveurDeCalculInterfaceQueue.poll();
                        ServerDeCalculAugmente serveurDeCalculCourant2 = serveurDeCalculInterfaceQueue.poll();
                        Tache tacheCourante = new Tache();
                        for (int i = 0; i<Math.min(serveurDeCalculCourant1.getCapaciteDeCalcul(), serveurDeCalculCourant2.getCapaciteDeCalcul())* FACTEUR_TAUX_DE_REFUS && !listeDeCalcul.isEmpty(); i++) {
                            tacheCourante.tache.add(listeDeCalcul.poll());
                        }
                        service.submit(new TacheCallableNonSecurise(serveurDeCalculCourant1,serveurDeCalculCourant2,tacheCourante));
                        compteurDeCallable++;
                    }

                    if (compteurDeCallable == 0 && !listeDeCalcul.isEmpty()) {
                        System.err.println("Plus de serveurs disponibles pour finir le calcul sécurisé");
                        System.exit(-2);
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        System.out.println("Temps d'exécution = " + (System.currentTimeMillis() - startProcessingTime));
    }

    /**
     * Méthode pour obtenir les résultat en mode sécurisé
     */
    private void calculRepartiModeSecurise() {
        int compteurDeCallable = 0; //Nombre de threads en cours
        ExecutorService CallablePool = Executors.newFixedThreadPool(serveurDeCalculInterfaceQueue.size());
        CompletionService<Retour> service = new ExecutorCompletionService<Retour>(CallablePool);
        long startProcessingTime = System.currentTimeMillis();
        while (!serveurDeCalculInterfaceQueue.isEmpty() && !listeDeCalcul.isEmpty()) {
            ServerDeCalculAugmente serveurDeCalculCourant = serveurDeCalculInterfaceQueue.poll();
            Tache tacheCourante = new Tache();
            for (int i = 0; i< FACTEUR_TAUX_DE_REFUS *serveurDeCalculCourant.getCapaciteDeCalcul() && !listeDeCalcul.isEmpty(); i++) {
                tacheCourante.tache.add(listeDeCalcul.poll());
            }
            service.submit(new TacheCallable(serveurDeCalculCourant,tacheCourante));
            compteurDeCallable++;
        }

        while (compteurDeCallable > 0) {
            try {
                Future<Retour> future = service.take();
                try {
                    Retour retour = future.get();
                    if (retour.getCodeRetour() == 0) {
                        //System.out.println(retour.getServeurDeCalcul().getIp() + "   "+  retour.getServeurDeCalcul().getNom()+ "    Retour ok du serveur                           "+(System.currentTimeMillis()-startProcessingTime));
                        listeResultatCalcul.addAll(retour.getTache().tache);
                        compteurDeCallable--;
                        serveurDeCalculInterfaceQueue.add(retour.getServeurDeCalcul());
                    } else if (retour.getCodeRetour() == 1) {
                        //System.out.println(retour.getServeurDeCalcul().getIp() + "   "+  retour.getServeurDeCalcul().getNom()+ "    Serveur ne connait pas le répartiteur          "+(System.currentTimeMillis()-startProcessingTime));
                        listeDeCalcul.addAll(retour.getTache().tache);
                        compteurDeCallable--;
                    } else if (retour.getCodeRetour() == 2 ) {
                        //System.out.println(retour.getServeurDeCalcul().getIp() + "   "+  retour.getServeurDeCalcul().getNom()+ "    Tache non acceptée                             "+(System.currentTimeMillis()-startProcessingTime));
                        listeDeCalcul.addAll(retour.getTache().tache);
                        compteurDeCallable--;
                        serveurDeCalculInterfaceQueue.add(retour.getServeurDeCalcul());
                    } else if (retour.getCodeRetour() == 3) {
                        //System.out.println(retour.getServeurDeCalcul().getIp() + "   "+  retour.getServeurDeCalcul().getNom()+ "    Remote Exception                               "+(System.currentTimeMillis()-startProcessingTime));
                        listeDeCalcul.addAll(retour.getTache().tache);
                        compteurDeCallable--;
                    }

                    if (!serveurDeCalculInterfaceQueue.isEmpty() && !listeDeCalcul.isEmpty()) {
                        ServerDeCalculAugmente serveurDeCalculCourant = serveurDeCalculInterfaceQueue.poll();
                        Tache tacheCourante = new Tache();
                        for (int i = 0; i< FACTEUR_TAUX_DE_REFUS *serveurDeCalculCourant.getCapaciteDeCalcul() && !listeDeCalcul.isEmpty(); i++) {
                            tacheCourante.tache.add(listeDeCalcul.poll());
                        }
                        service.submit(new TacheCallable(serveurDeCalculCourant,tacheCourante));
                        compteurDeCallable++;
                    }

                    if (compteurDeCallable == 0 && !listeDeCalcul.isEmpty()) {
                        System.err.println("Plus de serveurs disponibles pour finir le calcul");
                        System.exit(-2);
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        System.out.println("Temps d'exécution = " + (System.currentTimeMillis() - startProcessingTime));
    }

    /**
     * Vérifie si des serveurs de calculs sont disponibles pour le calcul. Si plus de serveurs sont disponibles, l'exécution s'arrete
     */
    private void verifierDisponibiliteServeurDeCalcul() {
        if (serveurDeCalculInterfaceQueue.isEmpty()) {
            System.err.println("Pas de serveurs disponibles pour le calcul");
            System.exit(-2);
        }
    }

    /**
     * Remplit la liste des calculs à effectuer à partir du fichier d'opérations
     */
    private void lirefichier() {
        listeDeCalcul = new LinkedBlockingQueue<>();
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

    /**
     * Calcul le résultat final
     * @return Somme finale
     */
    private int calculerResultat() {
        int somme = 0;
        for (Calcul calcul : listeResultatCalcul) {
            somme += calcul.getResultat();
            somme %= 4000;
        }
        return somme;
    }


}

package Repartiteur;

import Shared.*;
import org.apache.commons.collections4.MultiValuedMap;

import java.io.*;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

public class Repartiteur implements RepartiteurInterface {

    private ServeurDeNomInterface serveurDeNomInterface = null;
    private LinkedBlockingQueue<ServerDeCalculAugmente> serveurDeCalculInterfaceQueue = null;
    private String fichier;
    private LinkedBlockingQueue<Calcul> listeDeCalcul;
    private List<Calcul> listeResultatCalcul;
    private final int NUMBER_OF_CALCUL_IN_TACHE = 5;


    public static void main(String[] args) {
        Repartiteur repartiteur = new Repartiteur(args[0]);
        repartiteur.run();
    }


    private Repartiteur(String fichier) {
        super();
        listeResultatCalcul = new ArrayList<>();
        serveurDeNomInterface = loadServeurDeNomStub("127.0.0.1");
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
        if (serveurDeCalculInterfaceQueue.isEmpty()) {
            System.err.println("Pas de serveurs disponibles pour le calcul");
            System.exit(-2);
        }
        lirefichier();
        int compteurDeCallable = 0;
        ExecutorService CallablePool = Executors.newFixedThreadPool(serveurDeCalculInterfaceQueue.size());
        CompletionService<Retour> service = new ExecutorCompletionService<Retour>(CallablePool);
        long startProcessingTime = System.currentTimeMillis();
        while (!serveurDeCalculInterfaceQueue.isEmpty() && !listeDeCalcul.isEmpty()) {
            ServerDeCalculAugmente serveurDeCalculCourant = serveurDeCalculInterfaceQueue.poll(); //TODO get capacite
            Tache tacheCourante = new Tache();
            for (int i = 0; i<NUMBER_OF_CALCUL_IN_TACHE && !listeDeCalcul.isEmpty(); i++) {
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
                        System.out.println(retour.getServeurDeCalcul().getIp() + "   "+  retour.getServeurDeCalcul().getNom()+ "    Retour ok du serveur                           "+(System.currentTimeMillis()-startProcessingTime));
                        listeResultatCalcul.addAll(retour.getTache().tache);
                        compteurDeCallable--;
                        serveurDeCalculInterfaceQueue.add(retour.getServeurDeCalcul());
                    } else if (retour.getCodeRetour() == 1) {
                        System.out.println(retour.getServeurDeCalcul().getIp() + "   "+  retour.getServeurDeCalcul().getNom()+ "    Serveur ne connait pas le répartiteur          "+(System.currentTimeMillis()-startProcessingTime));
                        listeDeCalcul.addAll(retour.getTache().tache);
                        compteurDeCallable--;
                    } else if (retour.getCodeRetour() == 2 ) {
                        System.out.println(retour.getServeurDeCalcul().getIp() + "   "+  retour.getServeurDeCalcul().getNom()+ "    Tache non acceptée                             "+(System.currentTimeMillis()-startProcessingTime));
                        listeDeCalcul.addAll(retour.getTache().tache);
                        compteurDeCallable--;
                        serveurDeCalculInterfaceQueue.add(retour.getServeurDeCalcul());
                    } else if (retour.getCodeRetour() == 3) {
                        System.out.println(retour.getServeurDeCalcul().getIp() + "   "+  retour.getServeurDeCalcul().getNom()+ "    Remote Exception                               "+(System.currentTimeMillis()-startProcessingTime));
                        listeDeCalcul.addAll(retour.getTache().tache);
                        compteurDeCallable--;
                    }

                    if (!serveurDeCalculInterfaceQueue.isEmpty() && !listeDeCalcul.isEmpty()) {
                        ServerDeCalculAugmente serveurDeCalculCourant = serveurDeCalculInterfaceQueue.poll(); //TODO get capacite
                        Tache tacheCourante = new Tache();
                        for (int i = 0; i<NUMBER_OF_CALCUL_IN_TACHE && !listeDeCalcul.isEmpty(); i++) {
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

        System.out.println("Time = " + (System.currentTimeMillis() - startProcessingTime));

        int resultat = calculerResultat();
         System.out.println("Le résultat du calcul est : " + resultat);

        for (ServerDeCalculAugmente server: serveurDeCalculInterfaceQueue) {
            try {
                System.out.println(server.getServeurDeCalculInterface().getNombreCalculRecu());
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }








//        Tache tache = new Tache();
//        tache.tache = listeDeCalcul;
//        Pair<Boolean, Tache> retour = null;
//        for (ServeurDeCalculInterface serverDeCalculInterface : serveurDeCalculInterfaceQueue) {
//            try {
//                retour = serverDeCalculInterface.recevoirTache(tache);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        int resultat = calculerResultat(retour.getRight());
//        System.out.println("Le résultat du calcul est : " + resultat);

    }


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

    private int calculerResultat() {
        int somme = 0;
        for (Calcul calcul : listeResultatCalcul) {
            //System.out.println(calcul);
            somme += calcul.getResult();
            somme %= 4000;
        }
        return somme;
    }


}

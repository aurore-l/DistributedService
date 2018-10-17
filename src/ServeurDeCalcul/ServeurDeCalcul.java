package ServeurDeCalcul;

import Shared.RepartiteurInterface;
import Shared.ServeurDeCalculInterface;
import Shared.ServeurDeNomInterface;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ThreadLocalRandom;

public class ServeurDeCalcul implements ServeurDeCalculInterface {

    private ServeurDeNomInterface serveurDeNomInterface = null;
    private long taille = 0;
    private int maliciousness = 0;

    public static void main(String[] args){
       ServeurDeCalcul serveurDeCalcul = new ServeurDeCalcul(args[1], args[2]); //args[1] = taille des tâches, args[2] = taux de réponse erronée
       serveurDeCalcul.run(args[0]); //args[0] = nom dans rmiregistry

    }

    private ServeurDeCalcul(String taille, String maliciousness) {
        super();
        serveurDeNomInterface = loadServeurDeNomStub("127.0.0.1");
        this.taille = Long.valueOf(taille);
        this.maliciousness = Integer.valueOf(maliciousness);


    }

    private void run(String name) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            ServeurDeNomInterface stub = (ServeurDeNomInterface) UnicastRemoteObject
                    .exportObject(this, 0);

            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("FileServer ready.");


            try {
                InetAddress inetAddress = InetAddress.getLocalHost();
                String ip = inetAddress.getHostAddress();
                serveurDeNomInterface.initiationServeurDeCalcul(ip,name);
            } catch (UnknownHostException e) {
                e.printStackTrace();
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

    private boolean acceptRequestedTask(long numberOfTasks) {
        long taux = ((numberOfTasks - taille)*100)/(4 * taille);
        if (taux>=100) {
            return false;
        } else if (taux==0){
            return true;
        } else {
            int randomNum = ThreadLocalRandom.current().nextInt(0, 101);
            return randomNum > taille;
        }
    }

    private boolean willBeMalicious() {
        int randomNum = ThreadLocalRandom.current().nextInt(0, 101);
        return randomNum <= maliciousness;

    }


}

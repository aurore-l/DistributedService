package Repartiteur;

import Shared.Calcul;
import Shared.ServerDeCalculAugmente;
import Shared.Tache;
import org.apache.commons.lang3.tuple.Pair;

import java.rmi.RemoteException;
import java.util.concurrent.*;

public class TacheCallableSecurise implements Callable<RetourSecurise> {

    private ServerDeCalculAugmente serveurDeCalcul1 = null;
    private ServerDeCalculAugmente serveurDeCalcul2 = null;
    private Tache tache = null;

    public TacheCallableSecurise(ServerDeCalculAugmente serveurDeCalcul1, ServerDeCalculAugmente serveurDeCalcul2, Tache tache) {
        this.serveurDeCalcul1 = serveurDeCalcul1;
        this.serveurDeCalcul2 = serveurDeCalcul2;
        this.tache = tache;
    }

    @Override
    public RetourSecurise call() throws Exception {
        ExecutorService CallablePool = Executors.newFixedThreadPool(2);
        CompletionService<Retour> service = new ExecutorCompletionService<Retour>(CallablePool);
        service.submit(new TacheCallable(serveurDeCalcul1,tache));
        service.submit(new TacheCallable(serveurDeCalcul2,tache));

        Retour retour1 = service.take().get();
        Retour retour2 = service.take().get();

        RetourSecurise retourSecurise = new RetourSecurise(serveurDeCalcul1,serveurDeCalcul2);
        retourSecurise.setCodeRetour1(retour1.getCodeRetour());
        retourSecurise.setCodeRetour2(retour2.getCodeRetour());

        if (retour1.getCodeRetour() == 0 && retour2.getCodeRetour()==0) {
            for (int i =0; i<tache.tache.size(); i++) {
                if (retour1.getTache().tache.get(i).getResult().equals(retour2.getTache().tache.get(i).getResult())) {
                    retourSecurise.addCalculReussi(retour1.getTache().tache.get(i));
                } else {
                    retourSecurise.addCalculARecalculer(tache.tache.get(i));
                }
            }
        } else {
            for (Calcul calcul : tache.tache) {
                retourSecurise.addCalculARecalculer(calcul);
            }
        }
        return retourSecurise;
    }
}

package Repartiteur;

import Shared.Calcul;
import Shared.Tache;

import java.util.concurrent.*;

/**
 * Classe chargée de lancer le calcul sur le serveur calcul en mode sécurisé
 */
public class TacheCallableNonSecurise implements Callable<RetourNonSecurise> {

    /**
     * Premier serveur de calcul à contacter
     */
    private ServerDeCalculAugmente serveurDeCalcul1;
    /**
     * Deuxième serveur de calcul à contacter
     */
    private ServerDeCalculAugmente serveurDeCalcul2;
    /**
     * Tache à envoyer
     */
    private Tache tache;

    TacheCallableNonSecurise(ServerDeCalculAugmente serveurDeCalcul1, ServerDeCalculAugmente serveurDeCalcul2, Tache tache) {
        this.serveurDeCalcul1 = serveurDeCalcul1;
        this.serveurDeCalcul2 = null;
        this.serveurDeCalcul2 = serveurDeCalcul2;
        this.tache = tache;
    }

    /**
     * Crée un TacheCallable pour chacun des deux serveurs calculs et crée un RetourNonSecurise à partir des Retour
     * @see TacheCallable
     * @see  RetourNonSecurise
     * @see Retour
     * @return RetourNonSecurise
     * @throws Exception
     */
    @Override
    public RetourNonSecurise call() throws Exception {
        ExecutorService CallablePool = Executors.newFixedThreadPool(2);
        CompletionService<Retour> service = new ExecutorCompletionService<Retour>(CallablePool);
        service.submit(new TacheCallable(serveurDeCalcul1,tache));
        service.submit(new TacheCallable(serveurDeCalcul2,tache));

        Retour premierRetourRecu = service.take().get();
        Retour deuxiemeRetourRecu = service.take().get();

        Retour retour1;
        Retour retour2;

        if (premierRetourRecu.getServeurDeCalcul().equals(serveurDeCalcul1)) {
            retour1 = premierRetourRecu;
            retour2 = deuxiemeRetourRecu;
        } else {
            retour1 = deuxiemeRetourRecu;
            retour2 = premierRetourRecu;
        }

        RetourNonSecurise retourNonSecurise = new RetourNonSecurise(serveurDeCalcul1,serveurDeCalcul2);
        retourNonSecurise.setCodeRetour1(retour1.getCodeRetour());
        retourNonSecurise.setCodeRetour2(retour2.getCodeRetour());

        if (retour1.getCodeRetour() == 0 && retour2.getCodeRetour()==0) {
            for (int i =0; i<tache.tache.size(); i++) {
                if (retour1.getTache().tache.get(i).getResultat().equals(retour2.getTache().tache.get(i).getResultat())) {
                    retourNonSecurise.addCalculReussi(retour1.getTache().tache.get(i));
                } else {
                    retourNonSecurise.addCalculARecalculer(tache.tache.get(i));
                }
            }
        } else {
            for (Calcul calcul : tache.tache) {
                retourNonSecurise.addCalculARecalculer(calcul);
            }
        }
        return retourNonSecurise;
    }
}

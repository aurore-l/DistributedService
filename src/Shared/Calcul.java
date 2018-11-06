package Shared;

import java.io.Serializable;

/**
 * Classe représentant un calcul
 */
public class Calcul implements Serializable {

    private Op operation;
    private int operande;
    private Integer resultat;

    public Calcul(Op operation, int operande) {
        this.operation = operation;
        this.operande = operande;
        resultat = null;
    }

    public void setResultat(int resultat){
        this.resultat = resultat;
    }

    public int getOperande() {
        return operande;
    }

    public Integer getResultat() {
        return resultat;
    }

    public Op getOperation() {
        return operation;
    }



    @Override
    public String toString() {
        return "Calcul{" +
                "operation=" + operation +
                ", operande=" + operande +
                ", resultat=" + resultat +
                '}';
    }
}

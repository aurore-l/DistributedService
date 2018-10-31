package Shared;

import java.io.Serializable;

public class Calcul implements Serializable {

    private Op operation;
    private int operande;
    private Integer result;

    public Calcul(Op operation, int operande) {
        this.operation = operation;
        this.operande = operande;
        result = null;
    }

    public void setResult(int result){
        this.result = result;
    }

    public int getOperande() {
        return operande;
    }

    public Integer getResult() {
        return result;
    }

    public Op getOperation() {
        return operation;
    }

    @Override
    public String toString() {
        return "Calcul{" +
                "operation=" + operation +
                ", operande=" + operande +
                ", result=" + result +
                '}';
    }
}

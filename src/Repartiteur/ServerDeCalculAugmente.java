package Repartiteur;

import Shared.ServeurDeCalculInterface;

public class ServerDeCalculAugmente {

    private ServeurDeCalculInterface serveurDeCalculInterface = null;
    private String ip = null;
    private String nom = null;
    private int capaciteDeCalcul = 0;

    public ServerDeCalculAugmente(ServeurDeCalculInterface serveurDeCalculInterface, String ip, String nom, int capaciteDeCalcul) {
        this.serveurDeCalculInterface = serveurDeCalculInterface;
        this.ip = ip;
        this.nom = nom;
        this.capaciteDeCalcul = capaciteDeCalcul;
    }

    public ServeurDeCalculInterface getServeurDeCalculInterface() {
        return serveurDeCalculInterface;
    }

    public String getNom() {
        return nom;
    }

    public int getCapaciteDeCalcul() {
        return capaciteDeCalcul;
    }

    public String getIp() {
        return ip;
    }

    @Override
    public String toString() {
        return "ServerDeCalculAugmente{" +

                ", ip='" + ip + '\'' +
                ", nom='" + nom + '\'' +
                ", capaciteDeCalcul=" + capaciteDeCalcul +
                '}';
    }
}


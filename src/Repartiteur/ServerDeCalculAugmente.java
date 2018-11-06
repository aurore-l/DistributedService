package Repartiteur;


import Shared.ServeurDeCalculInterface;

import java.io.Serializable;

/**
 * Classe permettant de stocker plusieurs informations à propos d'un serveur de calcul
 */
public class ServerDeCalculAugmente implements Comparable<ServerDeCalculAugmente>, Serializable {

    /**
     * Stub du serveur de calcul
     */
    private ServeurDeCalculInterface serveurDeCalculInterface;
    /**
     * Ip:port du serveur de calcul
     */
    private String ip;
    /**
     * Nom du serveur de calcul dans son rmiregistry
     */
    private String nom;
    /**
     * Capacité du serveur de calcul
     */
    private int capaciteDeCalcul;

    ServerDeCalculAugmente(ServeurDeCalculInterface serveurDeCalculInterface, String ip, String nom, int capaciteDeCalcul) {
        this.serveurDeCalculInterface = serveurDeCalculInterface;
        this.ip = ip;
        this.nom = nom;
        this.capaciteDeCalcul = capaciteDeCalcul;
    }

    ServeurDeCalculInterface getServeurDeCalculInterface() {
        return serveurDeCalculInterface;
    }

    String getNom() {
        return nom;
    }

    int getCapaciteDeCalcul() {
        return capaciteDeCalcul;
    }

    String getIp() {
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

    @Override
    public int compareTo(ServerDeCalculAugmente server) {
        if (server == null) return 1;
        if (ip.equals(server.ip) && nom.equals(server.nom)) {
            return 0;
        }
        else {
            return 1;
        }
    }
}


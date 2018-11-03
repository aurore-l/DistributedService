package Shared;


import java.io.Serializable;

public class ServerDeCalculAugmente implements Comparable<ServerDeCalculAugmente>, Serializable {

    private ServeurDeCalculInterface serveurDeCalculInterface;
    private String ip;
    private String nom;
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


package Shared;

import java.io.Serializable;

/**
 * Classe permettant de stocker plusieurs informations à propos d'un répartiteur
 */
public class RepartiteurIdentite implements Comparable<RepartiteurIdentite>, Serializable {


    private String ip;
    private String identifiant;
    private String motDePasse;

    public RepartiteurIdentite(String ip, String identifiant, String motDePasse) {
        this.ip = ip;
        this.identifiant = identifiant;
        this.motDePasse = motDePasse;
    }


    @Override
    public int compareTo(RepartiteurIdentite ri) {
        if (ri == null) return 1;
        if (ip.equals(ri.ip) && identifiant.equals(ri.identifiant) && motDePasse.equals(ri.motDePasse)) {
            return 0;
        }
        else {
            return 1;
        }
    }

    public String getIp() {
        return ip;
    }
}

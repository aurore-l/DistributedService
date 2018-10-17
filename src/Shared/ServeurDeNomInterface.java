package Shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.tuple.Pair;

public interface ServeurDeNomInterface extends Remote {
    MultiValuedMap<String, String> getServeurDeCalculMap() throws RemoteException;
    boolean initiationServeurDeCalcul(String hostname, String name) throws RemoteException;

}

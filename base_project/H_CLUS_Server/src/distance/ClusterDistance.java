package distance;

import data.Data;
import clustering.Cluster;
import data.InvalidSizeException;

/**
 * Classe che rappresenta l'interfaccia che permette di implementare il metodo
 * per il calcolo della distanza tra due cluster secondo metodi distinti.
 */
public interface ClusterDistance {

  /**
   * Calcola la distanza tra due cluster.
   * 
   * @param c1 primo cluster
   * @param c2 secondo cluster
   * @param d  dataset su cui calcolare la distanza
   * @return distanza tra i due cluster
   * @throws InvalidSizeException se i cluster hanno dimensioni diverse
   */
  Double distance(Cluster c1, Cluster c2, Data d) throws InvalidSizeException;
}
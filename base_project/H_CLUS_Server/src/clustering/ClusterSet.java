package clustering;

import data.Data;
import data.InvalidSizeException;
import distance.ClusterDistance;
import java.io.Serializable;

/**
 * Classe che rappresenta un insieme di cluster.
 * Implementa l'interfaccia Iterable per poter iterare sui cluster.
 * Implementa l'interfaccia Serializable per poter serializzare l'oggetto.
 */
class ClusterSet implements Serializable {

  private Cluster C[];
  private Integer lastClusterIndex = 0; // indica la posizione dell'ultimo cluster in C

  /**
   * Costruttore della classe ClusterSet
   * 
   * @param k numero di cluster da inserire nel ClusterSet
   */
  ClusterSet(Integer k) {
    C = new Cluster[k];
  }

  /**
   * Aggiunge un cluster all'interno del clusterset
   * 
   * @param c cluster da aggiungere
   * @throws InvalidSizeException se il clusterset e' pieno
   */
  void add(Cluster c) throws InvalidSizeException {
    try {
      for (Integer j = 0; j < lastClusterIndex; j++)
        if (c == get(j)) // to avoid duplicates
          return;

      C[lastClusterIndex] = c;
      lastClusterIndex++;
    } catch (IndexOutOfBoundsException e) {
      throw new InvalidSizeException();
    }
  }

  /**
   * Restituisce il cluster in posizione i.
   * 
   * @param i indice del cluster da restituire
   * @return cluster in posizione i
   */
  private Cluster get(Integer i) {
    return C[i];
  }

  /**
   * Unisce i due cluster piu' vicini presenti all'interno del ClusterSet.
   * 
   * @param distance istanza di una classe che implementa l'interfaccia
   *                 ClusterDistance
   * @param data     dataset degli esempi
   * @return nuovo ClusterSet ottenuto dalla fusione dei due cluster piu' vicini
   * @throws InvalidSizeException se si calcola la distanza tra due esempi di
   *                              dimensione diversa
   */
  ClusterSet mergeClosestClusters(ClusterDistance distance, Data data)
      throws InvalidSizeException {
    Integer firstClosestCluster = -1; // Indice del primo cluster piu' vicino
    Integer secondClosestCluster = -1; // Indice del secondo cluster piu' vicino
    Double min = Double.MAX_VALUE;

    // Cerca i due cluster piu' vicini
    for (Integer i = 0; i < this.lastClusterIndex; i++) {
      for (Integer j = i + 1; j < this.lastClusterIndex; j++) {
        if (distance.distance(this.get(i), this.get(j), data) < min) {
          min = distance.distance(this.get(i), this.get(j), data);
          firstClosestCluster = i;
          secondClosestCluster = j;
        }
      }
    }

    // Crea un nuovo ClusterSet con un cluster in meno,
    // poiché due cluster saranno fusi
    ClusterSet C2 = new ClusterSet(this.lastClusterIndex - 1);

    // Aggiungi i cluster al nuovo insieme, unendo i due piu' vicini
    for (Integer i = 0; i < this.lastClusterIndex; i++) {
      if (i != firstClosestCluster && i != secondClosestCluster) {
        C2.add(this.get(i));
      } else if (i == firstClosestCluster) {
        C2.add(this.get(firstClosestCluster).mergeCluster(this.get(secondClosestCluster)));
      }
    }

    return C2;
  }

  /**
   * Restituisce la stringa che rappresenta gli indici degli esempi presenti nei
   * cluster del ClusterSet
   * 
   * @return stringa degli indici degli esempi nei cluster
   */
  public String toString() {
    String str = "";
    for (Integer i = 0; i < C.length; i++) {
      if (get(i) != null)
        str += "cluster" + i + ":" + get(i) + "\n";
    }
    return str;

  }

  /**
   * Restituisce la stringa contentene gli esempi del dataset presenti nei cluster
   * del ClusterSet
   * 
   * @param data dataset degli esempi
   * @return stringa contenente gli esempi del dataset presenti nei cluster del
   *         ClusterSet
   */
  public String toString(Data data) {
    String str = "";
    for (Integer i = 0; i < C.length; i++) {
      if (get(i) != null)
        str += "cluster" + i + ":" + get(i).toString(data) + "\n";
    }
    return str;
  }
}
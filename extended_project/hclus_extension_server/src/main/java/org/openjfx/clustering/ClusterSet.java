package org.openjfx.clustering;

import java.util.Iterator;
import java.io.Serializable;
import org.openjfx.data.Data;
import org.openjfx.distance.ClusterDistance;
import java.util.NoSuchElementException;
import org.openjfx.data.InvalidSizeException;

/**
 * Classe che rappresenta un insieme di cluster.
 * Implementa l'interfaccia Iterable per poter iterare sui cluster.
 * Implementa l'interfaccia Serializable per poter serializzare l'oggetto.
 */
class ClusterSet implements Serializable, Iterable<Cluster> {

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
  ClusterSet mergeClosestClusters(ClusterDistance distance, Data data) throws InvalidSizeException {
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
   * @throws IndexOutOfBoundsException se si cerca di accedere a un indice non
   *                                   valido
   */
  public String toString(Data data) throws IndexOutOfBoundsException {
    String str = "";
    for (Integer i = 0; i < C.length; i++) {
      if (get(i) != null)
        str += "cluster" + i + ":" + get(i).toString(data) + "\n";
    }
    return str;
  }

  /**
   * Restituisce un iteratore sui cluster del ClusterSet
   * 
   * @return iteratore sui cluster del ClusterSet
   */
  @Override
  public Iterator<Cluster> iterator() {
    return new ClusterIterator();
  }

  /**
   * Classe interna che implementa l'interfaccia Iterator per iterare sui cluster
   * del ClusterSet
   * Implementa i metodi hasNext() e next() dell'interfaccia Iterator
   */
  private class ClusterIterator implements Iterator<Cluster> {
    private Integer currentIndex = 0;

    /**
     * Verifica se ci sono ancora elementi non nulli su cui iterare
     * 
     * @return true se ci sono ancora elementi non nulli da iterare, false
     *         altrimenti
     */
    @Override
    public boolean hasNext() {
      // Verifica se ci sono ancora elementi non nulli da iterare
      while (currentIndex < C.length && C[currentIndex] == null) {
        currentIndex++;
      }
      return currentIndex < C.length;
    }

    /**
     * Restituisce il prossimo cluster del ClusterSet
     * 
     * @return prossimo cluster del ClusterSet
     */
    @Override
    public Cluster next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      return C[currentIndex++];
    }
  }
}

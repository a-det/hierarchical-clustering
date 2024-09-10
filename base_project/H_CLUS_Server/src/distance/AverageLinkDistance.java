package distance;

import data.Data;
import clustering.Cluster;
import data.InvalidSizeException;

/**
 * Classe che implementa il calcolo della distanza tra due cluster utilizzando
 * il metodo della average-link distance.
 * Implementa l'interfaccia ClusterDistance.
 */
public class AverageLinkDistance implements ClusterDistance {

  /**
   * Calcola la distanza tra due cluster utilizzando il metodo della average-link
   * distance.
   * 
   * @param c1 il primo cluster su cui calcolare la distanza
   * @param c2 il secondo cluster su cui calcolare la distanza
   * @param d  dataset degli esempi contenuti nei cluster tra cui calcolare la
   *           distanza
   * @return la distanza tra i due cluster
   * @throws InvalidSizeException se si calcola la distanza tra esempi di
   *                              dimensione diversa
   */
  public Double distance(Cluster c1, Cluster c2, Data d) throws InvalidSizeException {
    Double distance = 0.00;
    for (Integer i : c1) {
      for (Integer j : c2) {
        distance += d.getExample(i).distance(d.getExample(j));
      }
  }
    return distance / (c1.getSize() * c2.getSize());
  }
}
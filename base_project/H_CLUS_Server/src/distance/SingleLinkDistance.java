package distance;

import data.Data;
import clustering.Cluster;
import data.InvalidSizeException;

/**
 * Classe che implementa il calcolo della distanza tra due cluster utilizzando
 * il metodo della single-link distance.
 * Implementa l'interfaccia ClusterDistance.
 */
public class SingleLinkDistance implements ClusterDistance  {
  
  /**
   * Calcola la distanza tra due cluster utilizzando il metodo della
   * single-link distance
   * 
   * @param c1 primo cluster tra cui calcolare la distanza
   * @param c2 secondo cluster tra cui calcolare la distanza
   * @param d  dataset degli esempi contenuti nei cluster tra cui calcolare la
   *           distanza
   * @return distanza tra i due cluster
   * @throws InvalidSizeException se si prova a calcolare la distanza tra esempi
   *                              di dimensione diversa
   */
  public Double distance(Cluster c1, Cluster c2, Data d) throws InvalidSizeException {
      Double min = Double.MAX_VALUE;
      for (Integer i : c1) {
        for (Integer j : c2) {
          Double distance = d.getExample(i).distance(d.getExample(j));
          if (distance < min)
            min = distance;
        }
      }
      return min;
    }
  }
package org.openjfx.clustering;

import org.openjfx.data.Data;
import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;
import java.io.Serializable;

/**
 * Classe che rappresenta un cluster di esempi.
 * Implementa l'interfaccia Iterable per poter iterare sugli esempi contenuti
 * nel cluster.
 * Implementa l'interfaccia Cloneable per poter creare una copia profonda di un
 * cluster.
 * Implementa l'interfaccia Serializable per poter serializzare un cluster.
 */
public class Cluster implements Iterable<Integer>, Cloneable, Serializable {
  
  /**
   * Set di interi che rappresenta gli indici degli esempi contenuti nel cluster.
   */
  private Set<Integer> clusteredData = new TreeSet<>();

  /**
   * Restituisce un iteratore per scorrere gli esempi contenuti nel cluster.
   * 
   * @return un iteratore per scorrere gli esempi contenuti nel cluster
   */
  public Iterator<Integer> iterator() {
    return clusteredData.iterator();
  }

  /**
   * Aggiunge un nuovo esempio al cluster sotto forma di indice che l'esempio ha
   * nel dataset
   * 
   * @param id indice dell'esempio da aggiungere al cluster
   */
  void addData(Integer id) {
    clusteredData.add(id);
  }

  /**
   * Restituisce il numero di esempi contenuti nel cluster.
   * 
   * @return il numero di esempi contenuti nel cluster
   */
  public Integer getSize() {
    return clusteredData.size();
  }

  /**
   * Restituisce una deep-copy del cluster su cui e' richiamato.
   * 
   * @return deep-copy del cluster
   */
  public Cluster clone() {
    Cluster copyC = new Cluster();
    for (Integer i : clusteredData)
      copyC.addData(i);
    return copyC;
  }

  /**
   * Crea un nuovo cluster dalla funzione del cluster su cui e' richiamato e
   * quello passato come parametro.
   * 
   * @param c cluster da unire al cluster su cui il metodo e' richiamato
   * @return nuovo cluster ottenuto dalla fusione dei due cluster
   */
  Cluster mergeCluster(Cluster c) {
    Cluster newC = new Cluster();
    for (Integer i : clusteredData)
      newC.addData(i);
    for (Integer i : c.clusteredData)
      newC.addData(i);
    return newC;
  }

  /**
   * Restituisce la stringa contenente gli indici degli esempi del dataset
   * presenti all'interno del cluster su cui e' richiamato.
   * 
   * @return stringa degli indici degli esempi del dataset contenuti
   * all'interno del cluster
   */
  public String toString() {
    String str = "";
    Integer i = 0;
    for (Integer d : clusteredData) {
      str += d + (i < clusteredData.size() - 1 ? ";" : "");
      i++;
    }
    return str;
  }

  /**
   * Restituisce la stringa contenente gli esempi del dataset presenti all'interno
   * del cluster su cui e' richiamato.
   * 
   * @param data dataset degli esempi
   * @return stringa degli esempi del dataset contenuti all'interno del cluster
   */
  public String toString(Data data) {
    String str = "";
    for (Integer d : clusteredData)
      str += "<" + data.getExample(d) + ">";
    return str;
  }

}

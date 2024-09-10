package data;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

/**
 * La classe Example rappresenta un singolo esempio di quelli sui quali si
 * andra' ad eseguire il clustering agglomerativo.
 * Implementa l'interfaccia Iterable per poter iterare su tutti i valori
 * numerici contenuti
 * nell'esempio.
 */
public class Example implements Iterable<Double> {

  private List<Double> example;

  /**
   * Restituisce un iteratore per scorrere tutti i valori numerici
   * contenuti nell'esempio.
   * 
   * @return iteratore per scorrere tutti i valori numerici contenuti
   *         nell'esempio.
   */
  public Iterator<Double> iterator() {
    return example.iterator();
  }

  /**
   * Costruttore di default della classe Example.
   */
  public Example() {
    example = new LinkedList<>();
  }

  /**
   * Inserisce i valori all'interno di example.
   * 
   * @param v valore numerico da inserire all'interno dell'esempio
   */
  public void add(Double v) {
    example.add(v);
  }

  /**
   * Restituisce uno dei valori numerici conservati all'interno di un
   * esempio.
   * 
   * @param index indice del valore numerico dell'esempio da restituire
   * @return valore numerico dell'esempio conservato in posizione index
   */
  private Double get(Integer index) {
    return example.get(index);
  }

  /**
   * Restituisce la distanza euclidea tra due esempi, quello sul quale
   * il metodo e' richiamato e quello passato come parametro.
   * 
   * @param newE secondo esempio con cui calcolare la distanza euclidea
   * @return distanza euclidea tra i due esempi
   * @throws InvalidSizeException se si prova a calcolare la distanza su esempi di
   *                              dimensione diversa
   */
  public Double distance(Example newE) throws InvalidSizeException {
    try {
      Double euclide = 0.0;
      Integer index = 0;
      for (Double ex : example) {
        euclide = euclide + (ex - newE.get(index)) * (ex - newE.get(index));
        index++;
      }
      return euclide;
    } catch (IndexOutOfBoundsException e) {
      throw new InvalidSizeException();
    }
  }

  /**
   * Metodo stampa la stringa dell'esempio.
   * 
   * @return stringa dell'esempio
   */
  public String toString() {
    String s = "[";

    Integer i = 0;
    for (Double d : example) {
      s += d + (i < example.size() - 1 ? "," : "]");
      i++;
    }
    return s;
  }
}
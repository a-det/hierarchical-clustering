package data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import database.TableData;
import database.DbAccess;
import database.EmptySetException;
import database.MissingNumberException;
import database.DatabaseConnectionException;
import java.util.Iterator;

/**
 * La classe Data rappresenta il dataset di esempi letti da una tabella del
 * database
 * e sui quali si andra' ad eseguire il clustering agglomerativo.
 * Implementa l'interfaccia Iterable per poter iterare su tutti gli esempi
 * presenti nel dataset
 */
public class Data implements Iterable<Example> {

  // set di esempi modellato per mezzo del container List
  private List<Example> data = new ArrayList<>();

  /**
   * Costruttore della classe Data
   * 
   * @param tableName nome della tabella del database da cui leggere i dati
   * @param db        oggetto di tipo DbAccess per la connessione al database
   * @throws NoDataException             se la tabella e' vuota, non esiste o
   *                                     contiene attributi non numerici
   * @throws DatabaseConnectionException se si verifica un errore nella
   *                                     connessione al database
   */
  public Data(String tableName, DbAccess db) throws NoDataException, DatabaseConnectionException  {
    try {
      TableData table = new TableData(db);
      data = table.getDistinctTransazioni(tableName);
    } catch (SQLException e) {
      throw new NoDataException("Tabella non trovata.");
    } catch (EmptySetException e) {
      throw new NoDataException("Tabella vuota.");
    } catch (MissingNumberException e) {
      throw new NoDataException("Errore: presenza di attributi non numerici in tabella.");
    }
  }

  /**
   * Restituisce un iteratore per scorrere tutti gli esempi presenti nel dataset.
   * 
   * @return iteratore per scorrere tutti gli esempi presenti nel dataset
   */
  public Iterator<Example> iterator() {
    return data.iterator();
  }

  /**
   * Restituisce il numero di esempi presenti nel dataset.
   * 
   * @return numero di esempi presenti nel dataset
   */
  public Integer getNumberOfExample() {
    return data.size();
  }

  /**
   * Restituisce l'i-esimo esempio presente nel dataset.
   * 
   * @param exampleIndex indice dell'esempio da restituire
   * @return esempio i-esimo presente nel dataset
   */
  public Example getExample(Integer exampleIndex) {
    return data.get(exampleIndex);
  }

  /*private List<List<Double>> distance() throws InvalidSizeException {

    // lista per memorizzare la matrice triangolare superiore delle distanze tra esempi
    List<List<Double>> distance = new ArrayList<>();

    // inizializzazione della lista delle distanze con valori di default
    for (Integer i = 0; i < getNumberOfExample(); i++) {
      List<Double> innerList = new ArrayList<>();
      for (Integer j = 0; j < getNumberOfExample(); j++) {
        innerList.add(0.0);
      }
      distance.add(innerList);
    }

    // iteratore per la lista di esempi
    Iterator<Example> it = data.iterator();
    Integer iListExt = 0;

    while (it.hasNext()) { // primo esempio utilizzato per il calcolo della distanza
      Example mainExample = it.next();

      // secondo iteratore utilizzato per ottenere il secondo esempio per il calcolo della distanza
      Iterator<Example> it2 = data.iterator();
      Integer iListInt = 0;
      // scorrimento della lista di esempi fino all'esempio principale (per ottenere una matrice triangolare superiore)
      while (iListInt <= iListExt) {
        it2.next();
        iListInt++;
      }

      // calcolo effettivo della distanza tra l'esempio principale e i successivi
      while (it2.hasNext()) {
        Example secondaryExample = it2.next();
        Double distanceTemp = mainExample.distance(secondaryExample);
        distance.get(iListExt).set(iListInt, distanceTemp);
        iListInt++;
      }

      iListExt++;
    }
    return distance;
  }*/

  /**
   * Restituisce una stringa contenente la rappresentazione testuale del dataset.
   * 
   * @return stringa che rappresenta il dataset
   */
  public String toString() {
    String s = "";
    Integer index = 0;
    for (Example e : data) {
      s += index + ":" + e.toString() + "\n";
      index++;
    }
    return s;
  }
}
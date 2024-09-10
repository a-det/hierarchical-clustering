package org.openjfx.data;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.sql.SQLException;
import org.openjfx.database.DbAccess;
import org.openjfx.database.TableData;
import org.openjfx.database.EmptySetException;
import org.openjfx.database.MissingNumberException;
import org.openjfx.database.DatabaseConnectionException;

/**
 * La classe Data rappresenta il dataset di esempi letti da una tabella del
 * database
 * e sui quali si andra' ad eseguire il clustering agglomerativo.
 * Implementa l'interfaccia Iterable per poter iterare su tutti gli esempi
 * presenti
 * nel dataset
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
  public Data(String tableName, DbAccess db) throws NoDataException, DatabaseConnectionException {
    try {
      TableData table = new TableData(db);
      data = table.getDistinctTransazioni(tableName);
    } catch (SQLException e) {
      throw new NoDataException("Tabella non trovata.");
    } catch (EmptySetException e) {
      throw new NoDataException("Tabella vuota.");
    } catch (MissingNumberException e) {
      throw new NoDataException("Presenza di attributi non numerici in tabella.");
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
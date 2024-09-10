package org.openjfx.database;

import java.sql.SQLException;
import java.util.List;
import org.openjfx.data.Example;

import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Classe che rappresenta i dati contenuti all'interno
 * di una tabella del database.
 */
public class TableData {

  private DbAccess db;

  /**
   * Costruttore della classe TableData.
   * 
   * @param db oggetto di tipo DbAccess che rappresenta la connessione al
   *           database.
   */
  public TableData(DbAccess db) {
    this.db = db;
  }

  /**
   * Restituisce la lista degli esempi contenuti all'interno della tabella
   * consultata del database.
   * 
   * @param table nome della tabella del database da consultare
   * @return List lista degli esempi ricavati dal database.
   * @throws SQLException                nel caso in cui si verifichi un errore
   *                                     nella query formulata al database
   * @throws EmptySetException           nel caso in cui la tabella non contenga
   *                                     alcuna transazione
   * @throws MissingNumberException      nel caso in cui all'interno della tabella
   *                                     siano presenti dei valori non numerici
   * @throws DatabaseConnectionException nel caso in cui si verifichi un errore di
   *                                     comunicazione con il database
   */
  public List<Example> getDistinctTransazioni(String table)
      throws SQLException, EmptySetException, MissingNumberException, DatabaseConnectionException {

    TableSchema tb = new TableSchema(db, table);
    List<Example> examples = new ArrayList<>();
    Statement s = db.getConnection().createStatement();
    ResultSet r = s.executeQuery("SELECT * FROM " + table + ";");

    while (r.next()) {
      Example e = new Example();

      for (Integer i = 0; i < tb.getNumberOfAttributes(); i++) {
        if (!tb.getColumn(i).isNumber()) {
          throw new MissingNumberException();
        } else {
          e.add(r.getDouble(tb.getColumn(i).getColumnName()));
        }
      }
      examples.add(e);
    }

    if (examples.isEmpty()) {
      throw new EmptySetException();
    }

    r.close();
    s.close();
    return examples;
  }

}

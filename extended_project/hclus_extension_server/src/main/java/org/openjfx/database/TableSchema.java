package org.openjfx.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Classe che rappresenta lo schema della tabella del database da consulare.
 */
public class TableSchema {
  private DbAccess db;

  /**
   * Classe che rappresenta un attributo della tabella del database.
   */
  class Column {
    private String name;
    private String type;

    /**
     * Costruttore della classe interna Column.
     * 
     * @param name nome dell'attributo della tabella
     * @param type tipo dell'attributo della tabella
     */
    private Column(String name, String type) {
      this.name = name;
      this.type = type;
    }

    /**
     * Restituisce il nome dell'attributo.
     * 
     * @return name nome dell'attributo
     */
    String getColumnName() {
      return name;
    }

    /**
     * Restituisce true se l'attributo e' numerico.
     * 
     * @return true se l'attributo e' numerico, false altrimenti
     */
    boolean isNumber() {
      return type.equals("number");
    }

    /**
     * Stampa la stinga che rappresenta l'attributo.
     * 
     * @return name stringa da stampare
     */
    public String toString() {
      return name + ":" + type;
    }
  }

  private List<Column> tableSchema = new ArrayList<Column>();

  /**
   * Costruttore della classe TableSchema.
   * 
   * @param db        oggetto DbAccess per la connessione al database
   * @param tableName nome della tabella del database
   * @throws SQLException                nel caso in cui si verifichi un errore di
   *                                     lettura della tabella
   * @throws DatabaseConnectionException nel caso in cui si verifichi un errore
   *                                     nella comunicazione con il database
   */
  TableSchema(DbAccess db, String tableName) throws SQLException, DatabaseConnectionException {
    this.db = db;
    HashMap<String, String> mapSQL_JAVATypes = new HashMap<String, String>();
    // http://java.sun.com/j2se/1.3/docs/guide/jdbc/getstart/mapping.html
    mapSQL_JAVATypes.put("CHAR", "string");
    mapSQL_JAVATypes.put("VARCHAR", "string");
    mapSQL_JAVATypes.put("LONGVARCHAR", "string");
    mapSQL_JAVATypes.put("BIT", "string");
    mapSQL_JAVATypes.put("SHORT", "number");
    mapSQL_JAVATypes.put("INT", "number");
    mapSQL_JAVATypes.put("LONG", "number");
    mapSQL_JAVATypes.put("FLOAT", "number");
    mapSQL_JAVATypes.put("DOUBLE", "number");

    Connection con = this.db.getConnection();
    DatabaseMetaData meta = con.getMetaData();
    ResultSet res = meta.getColumns(null, null, tableName, null);
    con.close();
    while (res.next()) {

      if (mapSQL_JAVATypes.containsKey(res.getString("TYPE_NAME")))
        tableSchema.add(new Column(
            res.getString("COLUMN_NAME"),
            mapSQL_JAVATypes.get(res.getString("TYPE_NAME"))));
    }
    res.close();
  }

  /**
   * Restituisce il numero di attributi della tabella.
   * 
   * @return numero di attributi della tabella
   */
  Integer getNumberOfAttributes() {
    return tableSchema.size();
  }

  /**
   * Restituisce l'indice dell'attributo con nome specificato.
   * 
   * @param attributo attributo di tipo colonna all'indice passato
   */
  Column getColumn(Integer index) {
    return tableSchema.get(index);
  }

}

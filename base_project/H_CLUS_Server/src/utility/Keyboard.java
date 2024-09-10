package utility;

//********************************************************************
//  Keyboard.java       Author: Lewis and Loftus
//
//  Facilitates keyboard input by abstracting details about input
//  parsing, conversions, and exception handling.
//********************************************************************

import java.io.*;
import java.util.*;

/**
 * Classe che facilita la lettura degli input da tastiera astranedo i dettagli
 * relativi al parsing, conversioni e gestione delle eccezioni.
 */
public class Keyboard {
  // ************* Error Handling Section **************************

  private static boolean printErrors = true;

  private static Integer errorCount = 0;

  /**
   * Restituisce il conteggio degli errori attuale.
   * 
   * @return errorCount il conteggio degli errori attuale
   */
  public static Integer getErrorCount() {
    return errorCount;
  }

  /**
   * Resetta il conteggio degli errori attuale.
   * 
   * @param count il conteggio degli errori attuale
   */
  public static void resetErrorCount(Integer count) {
    errorCount = 0;
  }

  /**
   * Restituisce un booleano che indica se gli errori di input
   * vengono attualmente stampati sull'output standard.
   * 
   * @return printErrors un booleano che indica se gli errori di input
   *         vengono attualmente stampati sull'output standard
   */
  public static boolean getPrintErrors() {
    return printErrors;
  }

  /**
   * Imposta un booleano che indica se gli errori di input
   * vengono attualmente stampati sull'output standard.
   * 
   * @param flag un booleano che indica se gli errori di input
   *             vengono attualmente stampati sull'output standard
   */
  public static void setPrintErrors(boolean flag) {
    printErrors = flag;
  }

  /**
   * Incrementa il conteggio degli errori e stampa il messaggio di errore
   * se appropriato.
   * 
   * @param str il messaggio di errore
   */
  private static void error(String str) {
    errorCount++;
    if (printErrors)
      System.out.println(str);
  }

  // ************* Tokenized Input Stream Section ******************

  private static String current_token = null;

  private static StringTokenizer reader;

  private static BufferedReader in = new BufferedReader(
      new InputStreamReader(System.in));

  /**
   * Restituisce il prossimo token di input
   * 
   * @return token il prossimo token di input
   */
  private static String getNextToken() {
    return getNextToken(true);
  }

  /**
   * Restituisce il prossimo token di input
   * 
   * @param skip un booleano che indica se saltare i delimitatori
   * 
   * @return token il prossimo token di input
   */
  private static String getNextToken(boolean skip) {
    String token;

    if (current_token == null)
      token = getNextInputToken(skip);
    else {
      token = current_token;
      current_token = null;
    }

    return token;
  }

  /**
   * Restituisce il prossimo token di input, che potrebbe derivare dalla linea
   * di testo corrente o da una successiva. Il parametro determina se vengono
   * utilizzate le linee successive.
   * 
   * @param skip un booleano che indica se saltare i delimitatori
   * @return token il prossimo token di input
   */
  private static String getNextInputToken(boolean skip) {
    final String delimiters = " \t\n\r\f";
    String token = null;

    try {
      if (reader == null)
        reader = new StringTokenizer(in.readLine(), delimiters, true);

      while (token == null || ((delimiters.indexOf(token) >= 0) && skip)) {
        while (!reader.hasMoreTokens())
          reader = new StringTokenizer(in.readLine(), delimiters,
              true);

        token = reader.nextToken();
      }
    } catch (Exception exception) {
      token = null;
    }

    return token;
  }

  /**
   * Restituisce true se non ci sono piu' token da leggere sulla linea di input
   * corrente.
   * 
   * @return un booleano che indica se non ci sono piu' token da leggere sulla
   */
  public static boolean endOfLine() {
    return !reader.hasMoreTokens();
  }

  // ************* Reading Section *********************************
  /**
   * Restituisce una stringa letta da standard input.
   * 
   * @return str la stringa letta da standard input
   */
  public static String readString() {
    String str;

    try {
      str = getNextToken(false);
      while (!endOfLine()) {
        str = str + getNextToken(false);
      }
    } catch (Exception exception) {
      error("Error reading String data, null value returned.");
      str = null;
    }
    return str;
  }

  /**
   * Restituisce una parola letta da standard input delimitata da spazi.
   * 
   * @return token la parola letta da standard input
   */
  public static String readWord() {
    String token;
    try {
      token = getNextToken();
    } catch (Exception exception) {
      error("Error reading String data, null value returned.");
      token = null;
    }
    return token;
  }

  /**
   * Ritorna un booleano letto da standard input.
   * 
   * @return bool il booleano letto da standard input
   */
  public static boolean readBoolean() {
    String token = getNextToken();
    boolean bool;
    try {
      if (token.toLowerCase().equals("true"))
        bool = true;
      else if (token.toLowerCase().equals("false"))
        bool = false;
      else {
        error("Error reading boolean data, false value returned.");
        bool = false;
      }
    } catch (Exception exception) {
      error("Error reading boolean data, false value returned.");
      bool = false;
    }
    return bool;
  }

  /**
   * Restituisce un carattere letto da standard input.
   * 
   * @return value il carattere letto da standard input
   */
  public static char readChar() {
    String token = getNextToken(false);
    char value;
    try {
      if (token.length() > 1) {
        current_token = token.substring(1, token.length());
      } else
        current_token = null;
      value = token.charAt(0);
    } catch (Exception exception) {
      error("Error reading char data, MIN_VALUE value returned.");
      value = Character.MIN_VALUE;
    }

    return value;
  }

  /**
   * Restituisce un intero letto da standard input.
   * 
   * @return value l'intero letto da standard input
   */
  public static Integer readInt() {
    String token = getNextToken();
    Integer value;
    try {
      value = Integer.parseInt(token);
    } catch (Exception exception) {
      error("Error reading Integer data, MIN_VALUE value returned.");
      value = Integer.MIN_VALUE;
    }
    return value;
  }

  /**
   * Restituisce un long letto da standard input.
   * 
   * @return value il long letto da standard input
   */
  public static long readLong() {
    String token = getNextToken();
    long value;
    try {
      value = Long.parseLong(token);
    } catch (Exception exception) {
      error("Error reading long data, MIN_VALUE value returned.");
      value = Long.MIN_VALUE;
    }
    return value;
  }

  /**
   * Restituisce un float letto da standard input.
   * 
   * @return value il float letto da standard input
   */
  public static float readFloat() {
    String token = getNextToken();
    float value;
    try { // Float.parseFloat(token)
      value = Float.parseFloat(token);
    } catch (Exception exception) {
      error("Error reading float data, NaN value returned.");
      value = Float.NaN;
    }
    return value;
  }


  /**
   * Restituisce un Double letto da standard input.
   * @return value il Double letto da standard input
   */
  public static Double readDouble() {
    String token = getNextToken();
    Double value;
    try {
      value = Double.parseDouble(token);
    } catch (Exception exception) {
      error("Error reading Double data, NaN value returned.");
      value = Double.NaN;
    }
    return value;
  }
}

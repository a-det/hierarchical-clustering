package org.openjfx.conversion;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Classe utile per la conversione del dendrogramma in formato JSON,
 * utilizzato per la visualizzazione grafica
 */
public class JsonDendrogram implements Serializable {

  /**
   * Rappresentazione del dendrogramma in formato JSON
   */
  private String json;
  /**
   * Profondita' del dendrogramma
   */
  private Integer depth;

  /**
   * Costruttore della classe JsonDendrogram
   * 
   * @param dendrogram Il dendrogramma da convertire in JSON
   * @param depth      La profondita' del dendrogramma
   */
  public JsonDendrogram(Map<String, Object> dendrogram, Integer depth) {
    this.json = toJson(dendrogram);
    this.depth = depth;
  }

  /**
   * Costruisce il json che racchiude la rappresentazione del dendrogramma
   * precedentemente computato e racchiuso in minedDendrogram
   * 
   * @param minedDendrogram dendrogramma minato dall'algoritmo di clustering
   * @return json che rappresenta il dendrogramma
   */
  public static Map<String, Object> buildDendrogram(Map<Integer, Map<String, String>> minedDendrogram) {
    return buildTree(minedDendrogram, getMaxKey(minedDendrogram),
        minedDendrogram.get(getMaxKey(minedDendrogram)).get("cluster0"));
  }

  /**
   * Metodo ricorsivo per l'effettiva costruzione del json che codifica il
   * dendrogramma.
   * 
   * @param minedDendrogram dendrogramma minato dall'algoritmo di clustering
   * @param level           livello corrente del dendrogramma
   * @param cluster         cluster corrente
   */
  private static Map<String, Object> buildTree(
      Map<Integer, Map<String, String>> minedDendrogram, Integer level, String cluster) {

    Map<String, Object> node = new HashMap<>();
    node.put("a", "(" + cluster + ")");
    List<Map<String, Object>> children = new ArrayList<>();

    // Caso Base: se il cluster e' un elemento individuale, lo restituisce come nodo
    // singolo con una lista di figli vuota.
    // Tuttavia, se il livello corrente non e' 0, aggiunge questo nodo per mantenere
    // le foglie alla stessa profondita'.
    if (!cluster.contains(";")) {
      if (level > 0) {
        Map<String, Object> childNode = buildTree(minedDendrogram, level - 1, cluster);
        children.add(childNode);
      }
      node.put("b", children);
      return node;
    }

    // Trova il livello precedente ed i rispettivi cluster
    Map<String, String> prevLevel = minedDendrogram.get(level - 1);

    for (String childCluster : prevLevel.values()) {
      // un cluster, per essere considerato figlio di un altro cluster, deve essere
      // sottoinsieme stretto del primo, e quindi ogni cluster figlio puo' contenere
      // solo ed
      // esclusivamente esempi contenuti anche nel cluster genitore
      if (isSubset(cluster, childCluster)) {
        children.add(buildTree(minedDendrogram, level - 1, childCluster));
      }
    }

    node.put("b", children);
    return node;
  }

  /**
   * Verifica se un cluster e' sottoinsieme di un altro cluster
   * 
   * @param cluster      cluster gentiore
   * @param childCluster cluster figlio
   * @return true se childCluster e' sottoinsieme stretto di cluster, false
   *         altrimenti
   */
  private static boolean isSubset(String cluster, String childCluster) {
    Set<String> clusterSet = new HashSet<>(Arrays.asList(cluster.split(";")));
    Set<String> childClusterSet = new HashSet<>(Arrays.asList(childCluster.split(";")));
    return clusterSet.containsAll(childClusterSet);
  }

  /**
   * Restituisce la massima chiave del dendrogramma minato secondo il loro ordine
   * alfanumerico crecente
   * 
   * @param minedDendrogram dendrogramma minato dall'algoritmo di clustering
   * @return la chiave massima del dendrogramma minato
   */
  private static Integer getMaxKey(Map<Integer, Map<String, String>> minedDendrogram) {
    return Collections.max(minedDendrogram.keySet());
  }

  /**
   * Salva il dendrogramma in formato JSON su file
   * 
   * @param filePath percorso del file in cui salvare in dendrogramma in formato
   *                 json
   * @throws InvalidPathException   se il percorso del file fornito non e' valido
   * @throws ReadWriteFileException se si verifica un errore durante la scrittura
   *                                del file
   */
  public void saveJsonDendrogram(String filePath) throws InvalidPathException, ReadWriteFileException {
    try {
      if (filePath.contains("..")) {
        throw new InvalidPathException();
      }
      ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath));
      out.writeObject(this);
      out.close();
    } catch (IOException e) {
      throw new ReadWriteFileException();
    }
  }

  /**
   * Carica un dendrogramma in formato Json da un file
   * 
   * @param filePath percorso del file dal quale caricare il dendrogramma in
   *                 formato json
   * @return dendrogramma in formato json caricato da file
   * @throws InvalidPathException   se il percorso del file fornito non e' valido
   * @throws ReadWriteFileException se si verifica un errore durante la lettura
   *                                del
   *                                file
   */
  public static JsonDendrogram loadJsonDendrogram(String filePath) throws InvalidPathException, ReadWriteFileException {
    try {
      if (filePath.contains("..")) {
        throw new InvalidPathException();
      }
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath));
      JsonDendrogram c = (JsonDendrogram) in.readObject();
      in.close();
      return c;

    } catch (ClassNotFoundException e) {
      throw new ReadWriteFileException("Errore: riferimento a classi non trovate");
    } catch (IOException e) {
      throw new ReadWriteFileException("Errore: impossibile leggere il file");
    }

  }

  /**
   * Restituisce la stringa rappresentante il dendrogramma in formato json
   * 
   * @return stringa rappresentante il dendrogramma in formato json
   */
  public String getJson() {
    return json;
  }

  /**
   * Restituisce la profondita' del dendrogramma
   * 
   * @return profondita' del dendrogramma
   */
  public Integer getDepth() {
    return depth;
  }

  /**
   * Converte un oggetto JsonDendrogram in formato JSON
   * 
   * @param jsonDendrogram da convertire in formato JSON
   * @return dendrogramma in formato JSON
   */
  public static String toJson(Map<String, Object> jsonDendrogram) {
    return new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(jsonDendrogram);
  }

}
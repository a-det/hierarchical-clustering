package org.openjfx.clustering;

import java.util.Map;
import java.util.TreeMap;
import java.util.Comparator;
import java.io.Serializable;
import org.openjfx.data.Data;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.openjfx.distance.ClusterDistance;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import org.openjfx.data.InvalidSizeException;

/**
 * Classe HierarchicalClusterMiner che implementa l'algoritmo di costruzione del
 * dendrogramma
 * Implementa l'interfaccia Serializable per poter salvare e caricare i
 * dendrogrammi
 */
public class HierarchicalClusterMiner implements Serializable {

  /**
   * Dendrogramma costruito.
   */
  private Dendrogram dendrogram;

  /**
   * Costruttore della classe HierarchicalClusterMiner
   * 
   * @param depth profondita' del dendrogramma
   */
  public HierarchicalClusterMiner(Integer depth) {
    dendrogram = new Dendrogram(depth);
  }

  /**
   * Controlla che la profondita' richiesta per il dendrogramma sia valida
   * La profondita', per essere ritenuta valida, deve essere compresa tra 0 e il
   * numero di esempi presenti nel dataset
   * 
   * @param depth     profondita' del dendrogramma
   * @param nExamples numero di esempi presenti nel dataset
   * @throws InvalidDepthException se la profondita' richiesta per il dendrogramma
   *                               non e' valida
   */
  public static void checkDepth(Integer depth, Integer nExamples) throws InvalidDepthException {
    if (depth < 0 || depth > nExamples) {
      throw new InvalidDepthException();
    }
  }

  /**
   * Costruisce il dendrogramma a partire dal dataset e dalla distanza specificata
   * 
   * @param data     dataset degli esempi su cui costruire il dendrogramma
   * @param distance tipo di distanza da utilizzare per calcolare la similarita' tra gli
   *                 esempi
   * @return container che associa ad ogni livello del dendrogramma i cluster
   *         presenti a quel livello
   * @throws InvalidSizeException se si calcola la distanza tra due esempi di
   *                             dimensioni diverse
   */
  public Map<Integer, Map<String, String>> mine(Data data, ClusterDistance distance) throws InvalidSizeException {

    // Comparator per ordinare i cluster in base al numero nell'etichetta 
    // (ad esempio "cluster1", "cluster2")
    Comparator<String> comparator = (s1, s2) -> {
      return Integer.compare(
          Integer.parseInt(s1.substring(7)), // Estrai il numero dal primo cluster
          Integer.parseInt(s2.substring(7))); // Estrai il numero dal secondo cluster
    };

    // Map che associa ogni livello del dendrogramma ai cluster in quel livello
    Map<Integer, Map<String, String>> minedDendrogram = new TreeMap<>();

    // Inizializza un nuovo ClusterSet con il numero di esempi nel dataset
    ClusterSet C = new ClusterSet(data.getNumberOfExample());
    String str = "cluster";

    // Livello 0 del dendrogramma: ogni esempio e' un cluster singolo
    minedDendrogram.put(0, new TreeMap<String, String>(comparator));
    for (Integer i = 0; i < data.getNumberOfExample(); i++) {
      Cluster c = new Cluster();
      c.addData(i);
      C.add(c); 
      minedDendrogram.get(0).put(str + i, c.toString()); 
    }
    dendrogram.setClusterSet(C, 0); // Imposta il ClusterSet corrente nel dendrogramma al livello 0

    // Loop per costruire i livelli successivi del dendrogramma
    for (Integer level = 1; level < dendrogram.getDepth(); level++) {
      minedDendrogram.put(level, new TreeMap<String, String>(comparator));
      C = dendrogram.getClusterSet(level - 1).mergeClosestClusters(distance, data);
      dendrogram.setClusterSet(C, level);

      // Ottiene i cluster uniti come stringhe e li divide in base al separatore ":"
      String[] clusters = C.toString().split("\n");
      for (String cluster : clusters) {
        minedDendrogram.get(level).put(cluster.split(":")[0], cluster.split(":")[1]);
      }
    }
    return minedDendrogram;
  }

  /**
   * Carica un dendrogramma da file
   * 
   * @param filename nome del file da cui caricare il dendrogramma
   * @return dendrogramma caricato
   * @throws IOException            se si verifica un errore durante la lettura
   *                                del file
   * @throws ClassNotFoundException se la classe del dendrogramma non e' stata
   *                                trovata
   * @throws FileNotFoundException  se non si puo' accedere al file
   */
  public static HierarchicalClusterMiner loadHierachicalClusterMiner(String filename)
      throws IOException, ClassNotFoundException, FileNotFoundException {
    ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
    HierarchicalClusterMiner hcm = (HierarchicalClusterMiner) in.readObject();
    in.close();
    return hcm;
  }

  /**
   * Salva il dendrogramma su file
   * 
   * @param filename nome del file su cui salvare il dendrogramma
   * @throws FileNotFoundException se non si puo' creare o accedere al file
   * @throws IOException           se si verifica un errore durante la scrittura
   *                               del file
   */
  public void save (String filename) throws FileNotFoundException, IOException {
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
    out.writeObject(this);
    out.close();
  }

  /**
   * Restituisce una stringa del dendrogramma
   * 
   * @return stringa del dendrogramma
   */
  public String toString() {
    return dendrogram.toString();
  }

  /**
   * Restituisce una stringa che riporta una rappresentazione testuale del
   * dendrogramma, completa degli esempi presenti nei clusterSet di ogni suo
   * livello
   * 
   * @param data dataset degli esempi presenti nel dendrogramma
   * @return stringa che rappresenta il dendrogramma, completo degli esempi
   *         presenti
   */
  public String toString(Data data) {
    return dendrogram.toString(data);
  }
}

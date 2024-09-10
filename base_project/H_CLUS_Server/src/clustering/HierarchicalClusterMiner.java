package clustering;

import data.Data;
import java.io.Serializable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import distance.ClusterDistance;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import data.InvalidSizeException;

/**
 * Classe HierarchicalClusterMiner che implementa l'algoritmo di costruzione del
 * dendrogramma
 * Implementa l'interfaccia Serializable per poter salvare e caricare i
 * dendrogrammi
 */
public class HierarchicalClusterMiner implements Serializable {

  /**
   * Dendrogramma costruito
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
    if (depth <= 0 || depth > nExamples) {
      throw new InvalidDepthException();
    }
  }

  /**
   * Costruisce il dendrogramma a partire dal dataset e dalla distanza specificata
   * 
   * @param data     dataset degli esempi su cui costruire il dendrogramma
   * @param distance tipo di distanza da utilizzare per calcolare la similarita'
   *                 tra gli
   *                 esempi
   * @throws InvalidSizeException se si calcola la distanza tra esempi di
   *                              dimensione diversa
   */
  public void mine(Data data, ClusterDistance distance) throws InvalidSizeException {
    ClusterSet C = new ClusterSet(data.getNumberOfExample());
    for (Integer i = 0; i < data.getNumberOfExample(); i++) {
      Cluster c = new Cluster();
      c.addData(i);
      C.add(c);
    }
    dendrogram.setClusterSet(C, 0);
    for (Integer level = 1; level < dendrogram.getDepth(); level++) {
      C = dendrogram.getClusterSet(level - 1).mergeClosestClusters(distance, data);
      dendrogram.setClusterSet(C, level);
    }
  }

  /**
   * Carica un dendrogramma da file
   * 
   * @param fileName nome del file da cui caricare il dendrogramma
   * @return dendrogramma caricato
   * @throws IOException            se si verifica un errore durante la lettura
   *                                del file
   * @throws ClassNotFoundException se la classe del dendrogramma non e' stata
   *                                trovata
   * @throws FileNotFoundException  se non si puo' accedere al file
   * @throws InvalidPathException   se il path del file non è valido
   */
  public static HierarchicalClusterMiner loadHierarchicalClusterMiner(String fileName)
      throws IOException, ClassNotFoundException, FileNotFoundException, InvalidPathException {
    if (fileName.contains("..")) {
      throw new InvalidPathException();
    }
    ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
    HierarchicalClusterMiner hcm = (HierarchicalClusterMiner) in.readObject();
    in.close();
    return hcm;
  }

  /**
   * Salva il dendrogramma su file
   * 
   * @param fileName nome del file su cui salvare il dendrogramma
   * @throws FileNotFoundException se non si puo' creare o accedere al file
   * @throws IOException           se si verifica un errore durante la scrittura
   *                               del file
   * @throws InvalidPathException  se il path del file non è valido
   */
  public void save(String fileName) throws FileNotFoundException, IOException, InvalidPathException {
    if (fileName.contains("..")) {
      throw new InvalidPathException();
    }
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapelites;

import upo.jml.prediction.classification.fss.core.FSObjectiveFunction;
import upo.jml.prediction.classification.fss.core.FSSolution;

import java.util.Random;
/**
 *
 * @author Brenda
 * @author Miguel
 */
public class MapElites {

  private Map mapa;
  private Random random;
  private FSObjectiveFunction objectiveFunction;
  private int numFeatures;

  public MapElites(FSObjectiveFunction objectiveFunction, int numFeatures) {
    this.objectiveFunction = objectiveFunction;
    this.numFeatures = numFeatures;
  }

  /**
   * Método principal del algoritmo Map Elites
   */
  public FSSolution execute(int numCell) throws Exception {

    //parametros del algoritmo
    Double cantBitsFijos = (Math.log10(numCell) / Math.log10(2));
    Integer iteraciones = 5000000;

    //Creamos las celdas
    Map mapa = new Map(numCell, numFeatures, cantBitsFijos.intValue());

    //Cargamos los bits fijos en las celdas
    mapa.loadCell();

    this.mapa = mapa;

    //TODO: Como determinar el número de genomas iniciales?
    Integer numberGenomes = mapa.getCells().length * 3;

    for (int i = 0; i < iteraciones; i++) {

      //Change de seed in each iteration
      this.random = new Random(System.currentTimeMillis());

      if (i < numberGenomes) {

        //cargamos el mapa con una cantidad de genomas iniciales;
        this.generateRandomSolution();
      } else {

        //seleccionamos una celda aleatoriamente
        Cell selectedCell = this.selectionRandom();

        //realizamos la mutación del vector de features
        int[] child = this.variationRandom(selectedCell);


        //ejecutamos el clasificador para obtener el fitness de la solución
        double fitness = this.objectiveFunction
                .evaluate(convertIndexArray(child));

        //almacenamos en el mapa si corresponde
        cellMapping(child, fitness);
      }
    }


    // Recorre el mapa y retorna el mejor resultado de todas las celdas
    double bestValue = 0.0;
    int[] bestSubset = null;
    for (Cell c : this.mapa.getCells()) {
      if (c.getValue() > bestValue) {
        bestValue = c.getValue();
        bestSubset = c.getFeatures();
      }
    }

    FSSolution bestSolution = new FSSolution(this.numFeatures);
    bestSolution.setSubset(convertIndexArray(bestSubset));
    bestSolution.setValue(bestValue);

    return bestSolution;

  }


  /**
   * Convierte el vector de 1 y 0 a un array con los indices de los features
   * que se van a usar en esta solución
   * @param child
   * @return
   */
  private int[] convertIndexArray(int[] child) {
    int contFeature = 0;
    for(int feature: child){
      if(feature == 1){
        contFeature++;
      }
    }
    int childWithIndex[] = new int[contFeature];
    int j=0;
    for(int i = 0; i <  child.length; i++){
      if (child[i] == 1) {
        childWithIndex[j] = i;
        j++;
      }
    }
    return childWithIndex;
  }

  /**
   * Selecciona una celda al azar para realizar la mutación.
   */
  private Cell selectionRandom() {
    Integer cellId = random.nextInt(mapa.getCells().length);
    return mapa.getCells()[cellId];
  }

  /**
   * Genera una solución randomica, calcula el fitness y guarda en el mapa.
   */
  private void generateRandomSolution() throws Exception {
    //creamos un vector vacío
    int[] features = new int[mapa.getNumberFeatures()];

    //completamos con 0 o 1 aleatoriamente.
    for (int i = 0; i < mapa.getNumberFeatures(); i++) {
      features[i] = random.nextInt(2);
    }
    //ejecutamos el clasificador para calcular el fitness
    double fitness = this.objectiveFunction.evaluate(features);

    //almacenamos en el mapa si corresponde
    cellMapping(features, fitness);

  }

  /**
   * Reliza el mapeo de la solución a una celda del mapa.
   */
  private void cellMapping(int[] features, double fitness) {

    //obtenemos el key para buscar en el mapa
    String key = getKey(features);

    //buscamos la celda que concuerda con el key
    Integer cellId = getMappingCell(key);

    //si la celda esta vacía o el fitness calculado es mayor, almacenamos en la celda
    if (mapa.getCells()[cellId].isEmpty() ||
            (!mapa.getCells()[cellId].isEmpty() && mapa.getCells()[cellId].getValue() < fitness)) {
      mapa.getCells()[cellId].setValue(fitness);
      mapa.getCells()[cellId].setFeatures(features);
      mapa.getCells()[cellId].setEmpty(false);
    }
  }

  //TODO: Como realizar la mutación?

  /**
   * Obtiene la celda que coincide con el key de la solución.
   */
  private int getMappingCell(String key) {
    int cellId = 0;
    for (Integer i = 0; i < mapa.getCells().length; i++) {
      if (mapa.getCells()[i].getKey().equals(key)) {
        cellId = i;
        break;
      }
    }
    return cellId;
  }

  /**
   * Calcula el key para una nueva solución.
   */
  private String getKey(int[] features) {
    String key = "";
    for (int j = 0; j < mapa.getNumberBitsKey(); j++) {
      key += features[j];
    }
    return key;
  }

  /**
   * Realiza la mutación del la celda seleccionada.
   */
  private int[] variationRandom(Cell selectedCell) {
    int[] features = new int[selectedCell.getFeatures().length];
    for (int i = 0; i < selectedCell.getFeatures().length; i++) {
      features[i] = selectedCell.getFeatures()[i];
    }
    int featureId = random.nextInt(mapa.getNumberFeatures());
    if (features[featureId] == 0) {
      features[featureId] = 1;
    } else {
      features[featureId] = 0;
    }
    return features;
  }

}

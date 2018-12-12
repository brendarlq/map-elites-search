/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
/**
 * CMIM.java Copyright (C) 2008-2012 Miguel Garcia Torres
 */
package py.com.fpuna.mapelites;

import java.util.Random;
import upo.jml.data.dataset.ToyDatasets;
import upo.jml.prediction.classification.fss.algorithms.fast.mapelite.Cell;
import upo.jml.prediction.classification.fss.algorithms.fast.mapelite.Map;
import upo.jml.prediction.classification.fss.core.FSObjectiveFunction;
import upo.jml.prediction.classification.fss.core.FSSearchAlgorithm;
import upo.jml.prediction.classification.fss.core.FSSolution;
import upo.jml.prediction.classification.fss.evaluators.CfsEvaluator;

/**
 * * Computes conditional mutual information maximisation algorithm from "Fast Binary Feature
 * Selection with Conditional Mutual Information" by F. Fleuret (2004)
 *
 * @author Miguel Garcia Torres (mgarciat[at]upo[dot]es)
 */
public class MapEliteSearch implements FSSearchAlgorithm {

  protected FSObjectiveFunction objectiveFunction = null;
  protected int numFeatures = -1;
  private Map mapa;
  private Random random;

  Integer numCell = 16;
  Double cantBitsFijos = (Math.log10(numCell) / Math.log10(2));
  Integer iteraciones = 5000000;

  public MapEliteSearch() {
    this(0);
  }

  public MapEliteSearch(int nfeatures) {
    this.numFeatures = nfeatures;
  }

  public static void testCorral() throws Exception {
    int[][] data = ToyDatasets.corral;
    int[] labels = ToyDatasets.corral_labels;

    int k = 5;
    FSObjectiveFunction fsobj = new CfsEvaluator(data, labels);

    MapEliteSearch algorithm = new MapEliteSearch(data[0].length);
    algorithm.setObjectiveFunction(fsobj);
    FSSolution solution = algorithm.search();
    System.out.println("best solution found: " + solution);
  }

  public static void testIris() throws Exception {

    int[][] data = ToyDatasets.iris_discrete;
    int[] labels = ToyDatasets.iris_labels;

    FSObjectiveFunction fsobj = new CfsEvaluator(data, labels);
    fsobj.buildEvaluator();

    MapEliteSearch algorithm = new MapEliteSearch(data[0].length);
    algorithm.setObjectiveFunction(fsobj);
    FSSolution solution = algorithm.search();
    System.out.println("solution: " + solution);
  }

  public static void main(String[] args) throws Exception {
    MapEliteSearch.testIris();
  }

  @Override
  public FSSolution search() throws Exception {
    if (this.numFeatures <= 0) {
      this.numFeatures = this.objectiveFunction.numberOfFeatures();
    }
    FSSolution bestSolution = null;

    //Creamos las celdas
    Map mapa = new Map(numCell, numFeatures, cantBitsFijos.intValue());

    //Cargamos los bits fijos en las celdas
    mapa.loadCell();

    Map map = execute(mapa, iteraciones);

    double m = 0.0;
    int[] bestSubset = null;
    for (Cell c : map.getCells()) {
      if (c.getAccuracy() > m) {
        m = c.getAccuracy();
        bestSubset = c.getFeatures();
      }
    }

    bestSolution = new FSSolution(this.numFeatures);
    bestSolution.setSubset(bestSubset);
    bestSolution.setValue(m);
    return bestSolution;
  }

  //TODO: Hacer random por celda?

  /**
   * Método principal del algoritmo Map Elites
   */
  public Map execute(Map mapa, int iteraciones) throws Exception {

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
        //TODO: Que espera recibir el método evaluate de la función objetivo?
        double fitness = this.objectiveFunction
            .evaluate(child); //double fitness = executeClassifier(child);

        //almacenamos en el mapa si corresponde
        cellMapping(child, fitness);
      }
    }

    return this.mapa;

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
        (!mapa.getCells()[cellId].isEmpty() && mapa.getCells()[cellId].getAccuracy() < fitness)) {
      mapa.getCells()[cellId].setAccuracy(fitness);
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

  @Override
  public FSObjectiveFunction getObjectiveFunction() throws Exception {
    return this.objectiveFunction;
  }

  @Override
  public void setObjectiveFunction(FSObjectiveFunction objectiveFunction) {
    this.objectiveFunction = objectiveFunction;
  }

  @Override
  public MapEliteSearch clone() {
    MapEliteSearch gfs = new MapEliteSearch(0);
    return gfs;
  }
}

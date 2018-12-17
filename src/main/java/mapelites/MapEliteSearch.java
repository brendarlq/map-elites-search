package mapelites;/*
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

import upo.jml.data.dataset.ToyDatasets;
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

  public MapEliteSearch() {
    this(0);
  }

  public MapEliteSearch(int nfeatures) {
    this.numFeatures = nfeatures;
  }

  @Override
  public FSSolution search() throws Exception {
    if (this.numFeatures <= 0) {
      this.numFeatures = this.objectiveFunction.numberOfFeatures();
    }
    FSSolution bestSolution = null;

    MapElites mapElites = new MapElites(this.objectiveFunction, this.numFeatures);
    // Parametro del algoritmo, determinar el número de celdas del mapa
    // Debe ser un número que tenga base dos.
    int numCell = 16;

    bestSolution = mapElites.execute(numCell);

    return bestSolution;
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

  public static void testCorral() throws Exception {
    int[][] data = ToyDatasets.corral;
    int[] labels = ToyDatasets.corral_labels;

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

}

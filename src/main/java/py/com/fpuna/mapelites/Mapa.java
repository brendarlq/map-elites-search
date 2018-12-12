package py.com.fpuna.mapelites;

/**
 * A group of cells.
 */
public class Mapa {

  private Cell[] cells;
  private int numberFeatures;
  private int numberBitsKey;

  /**
   * Crea el mapa de celdas con celdas que contienen vectores del tamaño de la cantidad de
   * features.
   */
  public Mapa(int numCell, int numFeatures, int numBitsKey) {
    this.numberFeatures = numFeatures;
    this.numberBitsKey = numBitsKey;
    this.cells = new Cell[numCell];
    for (int i = 0; i < numCell; i++) {
      this.cells[i] = new Cell(numFeatures);
    }
  }

  /**
   * Inicializa las celdas cargando ya los bits necesarios para identificar la celda.
   */
  public void loadCell() {

    for (int i = 0; i < this.cells.length; i++) {
      for (int j = 0; j < this.numberBitsKey; j++) {
        int val = this.cells.length * j + i;
        int ret = (1 & (val >>> j));
        cells[i].setFeature(j, ret); //cells[i].features[j] = ret;
      }
      cells[i].setEmpty(true);
      cells[i].setKey(this.numberBitsKey);
    }
  }

  /**
   * Imprime el mapa de celdas
   */
  public void printMap() {
    for (int i = 0; i < cells.length; i++) {
      System.out.print("\nCell " + i + ": ");
      for (int j = 0; j < numberFeatures; j++) {
        System.out.print(cells[i].getFeature(j) + " ");
      }
      System.out.print("\t    Accuracy:  " + cells[i].getAccuracy());
    }
  }

  /**
   * Gets map
   *
   * @return value of map
   */
  public Cell[] getCells() {
    return cells;
  }

  /**
   * Set map
   */
  public void setCells(Cell[] cells) {
    this.cells = cells;
  }

  /**
   * Gets numberFeatures
   *
   * @return value of numberFeatures
   */
  public Integer getNumberFeatures() {
    return numberFeatures;
  }

  /**
   * Set numberFeatures
   */
  public void setNumberFeatures(Integer numberFeatures) {
    this.numberFeatures = numberFeatures;
  }

  /**
   * Gets numberBitsKey
   *
   * @return value of numberBitsKey
   */
  public Integer getNumberBitsKey() {
    return numberBitsKey;
  }

  /**
   * Set numberBitsKey
   */
  public void setNumberBitsKey(Integer numberBitsKey) {
    this.numberBitsKey = numberBitsKey;
  }
}

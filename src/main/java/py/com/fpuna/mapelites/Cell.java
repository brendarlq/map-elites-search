package py.com.fpuna.mapelites;

import java.util.Arrays;

/**
 * @author Brenda
 * @author Miguel
 */
public class Cell {

  private int[] features;
  private double accuracy;
  private boolean empty;
  private String key;

  public Cell(int numFeatures) {
    this.features = new int[numFeatures];
    Arrays.fill(this.features, 0);
  }

  /**
   * Gets features
   *
   * @return value of features
   */
  public int[] getFeatures() {
    return features;
  }

  /**
   * Set features
   */
  public void setFeatures(int[] features) {
    this.features = features;
  }

  public void setFeature(int j, int feat) {
    this.features[j] = feat;
  }

  public int getFeature(int j) {
    return this.features[j];
  }

  /**
   * Gets accuracy
   *
   * @return value of accuracy
   */
  public double getAccuracy() {
    return this.accuracy;
  }

  /**
   * Set accuracy
   */
  public void setAccuracy(double accuracy) {
    this.accuracy = accuracy;
  }

  /**
   * Gets empty
   *
   * @return value of empty
   */
  public boolean isEmpty() {
    return this.empty;
  }

  /**
   * Set empty
   */
  public void setEmpty(boolean empty) {
    this.empty = empty;
  }

  /**
   * Gets key
   *
   * @return value of key
   */
  public String getKey() {
    return key;
  }

  /**
   * Create the key for the cell with the number of bits
   */
  public void setKey(Integer numBits) {
    String key = "";
    for (int i = 0; i < numBits; i++) {
      key += features[i];
    }
    this.key = key;
  }
}

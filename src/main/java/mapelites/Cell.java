/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mapelites;

import java.util.Arrays;

/**
 *
 * @author Brenda
 * @author Miguel
 */
public class Cell {
    private int [] features;
    private double value;
    private boolean empty;
    private String key;

    public Cell(int numFeatures){
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
     *
     * @param features
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
     * Gets value
     *
     * @return value of value
     */
    public double getValue() {
        return value;
    }

    /**
     * Set value
     *
     * @param value
     */
    public void setValue(double value) {
        this.value = value;
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
     *
     * @param empty
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
     *
     * @param numBits
     */
    public void setKey(Integer numBits) {
        String key = "";
        for(int i= 0; i < numBits; i++){
            key += features[i];
        }
        this.key = key;
    }
}

package main.java;

/**
 * POJO Container class for training/test data.
 */
public class Image {
    public final String imageCategory;
    public final int imageID;
    public int label;
    public double[] data;
    public Image(int label, double[] data, String imageCategory,int imageID) {
        this.label = label;
        this.data = data;
        this.imageCategory=imageCategory;
        this.imageID = imageID;
    }

}

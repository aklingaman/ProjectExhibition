/**
 * POJO Container class for training/test data.
 */
public class Image {
    public int label;
    public double[] data;
    public Image(int label, double[] data) {
        this.label = label;
        this.data = data;
    }

}

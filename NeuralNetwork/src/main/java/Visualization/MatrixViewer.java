package main.java.Visualization;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MatrixViewer {
    public static Logger LOG = LogManager.getLogger();
    private MatrixViewerPixelMode pixelMode;
    private int scaleFactor;

    public enum MatrixViewerPixelMode {
        GREYSCALE,GREENBYRED
    }

    public MatrixViewer(MatrixViewerPixelMode pixelMode) {
        if(pixelMode==null) {
            throw new IllegalArgumentException("Must provide a pixelMode to be able to view the pixels.");
        }
        this.pixelMode = pixelMode;
        scaleFactor=1;
    }
    public void setScaleFactor(int scaleFactor){
        this.scaleFactor = scaleFactor;
    }


    /**
     * Takes in a matrix with decimals ranging from 0-1, with 0 indicating white, 1 indicating black and creates a view of it.
     * One thing os note is that there is a translation that happens because the matrix uses the top left as its 0,0
     * but image uses the bottom left as its 0,0
     *
     *
     * @param matrix
     */
    public BufferedImage viewMatrix(double[][] matrix) {
        int height = matrix.length;
        int width = matrix[0].length;
        BufferedImage image = new BufferedImage(height,width,BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i<matrix.length; i++) {
            for(int j = 0; j<matrix[i].length; j++) {
                image.setRGB(j,i,findPixelColor(matrix[i][j])); //translation done as i,j maps onto j,i ( flip along y=x line )
            }
        }
        if(scaleFactor!=1) {
            //rescale
            BufferedImage newImage = new BufferedImage(matrix.length*scaleFactor,matrix[0].length*scaleFactor,BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = newImage.createGraphics();
            g2d.drawImage(image.getScaledInstance(height*scaleFactor,width*scaleFactor,Image.SCALE_SMOOTH), 0, 0, null);
            g2d.dispose();
            image=newImage;
        }
        return image;
    }

    /**
     * Arbitrates between the value of a pixel that is stored, and the way it should be represented as a color
     *
     *
     * @param pixelValue
     * @return
     */
    public int findPixelColor(double pixelValue){
        switch(pixelMode){
            case GREYSCALE ->  {
                int rgbVal = (int)((1.0-pixelValue)*255);
                return rgbVal<<16 | rgbVal<<8 | rgbVal;
            }
            case GREENBYRED -> {
                int rgbVal = (int)((1.0-pixelValue)*255);
                return rgbVal<<16 | (255-rgbVal)<<8 | 0;
            }
        }

        throw new RuntimeException("Error assigning a color value to pixel because "+pixelMode+" does not have an assigned way of doing that.");
    }



}
package myLibs.datasets.mnist;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to store mnist image data.
 * This class store the image as a 2D int array and the label
 */
public class BinaryImage {

    private int height;

    private int width;

    private int[][] image;

    private int label;

    public BinaryImage(final int[][] image, final int label) {
        if (image.length > 0) {
            this.image = image;

            height = image.length;

            width = image[0].length;
            this.label = label;
        }
    }

    public void displayImage(final int heightFactor, final int widthFactor) {
        System.out.println();

        System.out.println("Image is " + height + "x" + width);
        System.out.println("<< " + label + " >>");
        for (int i = 0; i < width * widthFactor + 2; i++) {
            System.out.print("_");
        }
        System.out.println();

        for (int i = 0; i < height * heightFactor; i++) {
            System.out.print("|");
            for (int j = 0; j < width * widthFactor; j++) {
                final int pixel = image[i / heightFactor][j / widthFactor] & 0xFF;
                if (pixel == 0) {
                    System.out.print(" ");
                } else if (pixel >= 128) {
                    System.out.print("*");
                } else if (pixel >= 56) {
                    System.out.print("-");
                } else {
                    System.out.print(".");
                }
            }
            System.out.println("|");
        }

        for (int i = 0; i < width * widthFactor + 2; i++) {
            System.out.print("_");
        }
        System.out.println();
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    public int[][] getImage() {
        return image;
    }

    public double[] getImage1D() {
        final double[] img1D = new double[height * width];

        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                img1D[i+(j*image[i].length)] = image[i][j]& 0xFF;
            }
        }

        return img1D;
    }

    public void setImage(final int[][] image) {
        this.image = image;
    }

    public int getLabel() {
        return label;
    }

    /**
     * Return a 10 value double array with the label'th value set to 1 and all the others set to 0.
     * For example, if label = 4 :
     * return 0 0 0 0 1 0 0 0 0 0
     * @return
     */
    public double[] getLabel1D() {
        final double[] label1D = new double[10];

        for (int i = 0; i < label1D.length; i++) {
            if (i == label) {
                label1D[i] = 1;
            } else {
                label1D[i] = 0;
            }
        }
        return label1D;
    }

    public void setLabel(final int label) {
        this.label = label;
    }

    public List<Integer> getImageAsList() {
        final List<Integer> list = new ArrayList<Integer>();
        for (final int[] line : image) {
            for (final int i : line) {
                list.add(i);
            }
        }
        return list;
    }
}
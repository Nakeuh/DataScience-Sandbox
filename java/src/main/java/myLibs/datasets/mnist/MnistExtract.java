package myLibs.datasets.mnist;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.javatuples.Pair;

// File format : http://yann.lecun.com/exdb/mnist/
public class MnistExtract {

    /**
     * Extract mnist data
     * @param filepathImage
     * @param filepathLabels
     * @return
     */
    public static List<BinaryImage> extract(final String filepathImage, final String filepathLabels) {
        final List<BinaryImage> list = new ArrayList<BinaryImage>();
        byte[] bytesImages;
        byte[] bytesLabels;
        try {

            bytesImages = Files.readAllBytes(Paths.get(filepathImage));
            bytesLabels = Files.readAllBytes(Paths.get(filepathLabels));

            byte[] buffer = new byte[1024];
            ByteBuffer b ;

            ///// Extract Header Image
            buffer = Arrays.copyOfRange(bytesImages, 0,4);
            b = ByteBuffer.wrap(buffer);
            int magicNumber = b.getInt();
            System.out.println("Magic Number Image:" + magicNumber);

            buffer = Arrays.copyOfRange(bytesImages, 4,8);
            b = ByteBuffer.wrap(buffer);
            int nbItem =b.getInt();
            System.out.println("Number of image :" + nbItem);

            buffer = Arrays.copyOfRange(bytesImages, 8,12);
            b = ByteBuffer.wrap(buffer);
            final int imageHeight = b.getInt();
            System.out.println("Number of rows :" + imageHeight);

            buffer = Arrays.copyOfRange(bytesImages, 12,16);
            b = ByteBuffer.wrap(buffer);
            final int imageWidth = b.getInt();
            System.out.println("Number of columns :" + imageWidth);

            ///// Extract Header Label
            buffer = Arrays.copyOfRange(bytesLabels, 0,4);
            b = ByteBuffer.wrap(buffer);
            magicNumber = b.getInt();
            System.out.println("Magic Number Label:" + magicNumber);

            buffer = Arrays.copyOfRange(bytesLabels, 4,8);
            b = ByteBuffer.wrap(buffer);
            nbItem =b.getInt();
            System.out.println("Number of Label :" + nbItem);


            for (int k = 0; k < nbItem ; k++) {
                final int[][] data = new int[imageWidth][imageHeight];
                int label;

                for (int j = 0; j < imageHeight; j++) {
                    for (int i = 0; i < imageWidth; i++) {
                        data[i][j] = bytesImages[16 + k * imageWidth*imageHeight + i * imageWidth + j];
                    }
                }
                label = bytesLabels[8+k];
                list.add(new BinaryImage(data,label));
            }
            System.out.println(nbItem + " images extracted.");
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Extract mnist data in list of pair <image, label>, where image and label are both stored in 1d int arrays
     * @param filepathImage
     * @param filepathLabels
     * @return
     */
    public static List<Pair<double[], double[]>>extractInPair(final String filepathImage, final String filepathLabels) {
        final List<Pair<double[], double[]>> list = new ArrayList<Pair<double[], double[]>>();
        for(final BinaryImage img:extract(filepathImage,filepathLabels)){
            list.add(new Pair<double[], double[]>(img.getImage1D(),img.getLabel1D()));
        }

        return list;
    }

}
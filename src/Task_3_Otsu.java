package project;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class Task_3_Otsu implements PlugInFilter {
    int NUM_INTENSITY_LEVELS = 256;



    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_8G;
    }

    @Override
    public void run(ImageProcessor imageProcessor) {
        int threshold = otsuGetThreshold(imageProcessor);
        ByteProcessor thresholdedIp = otsuSegementation(imageProcessor, threshold);
        ImagePlus thresholdedImage = new ImagePlus("Thresholded Image", thresholdedIp);
        thresholdedImage.show();
        System.out.println("Threshold: " + threshold);

    }

    public ByteProcessor otsuSegementation(ImageProcessor ip, int threshold){
        Task_1_Threshold Threshold = new Task_1_Threshold();
        ByteProcessor img =Threshold.correctIllumination(ip);
        return Threshold.threshold(img, threshold);



    }

    public double[] getHistogram(ImageProcessor in) {
        double[] histogram = new double[NUM_INTENSITY_LEVELS];
        int totalPixels = in.getWidth() * in.getHeight();

        for (int y = 0; y < in.getHeight(); y++) {
            for (int x = 0; x < in.getWidth(); x++) {
                int pixel = in.get(x, y);
                histogram[pixel]++;
            }
        }
        for (int i = 0; i < histogram.length; i++) {
            histogram[i] = histogram[i] / totalPixels;
        }
        return histogram;
    }

    public double[] getP1(double[] histogram){
        double[] p1 = new double[NUM_INTENSITY_LEVELS];
        p1[0] = histogram[0];
        for (int i = 1; i < NUM_INTENSITY_LEVELS; i++) {
            p1[i] = p1[i - 1] + histogram[i];
        }
        return p1;


    }
    public double[] getP2(double[] P1){
        double[] p2 = new double[NUM_INTENSITY_LEVELS];
        for (int i = 0; i < NUM_INTENSITY_LEVELS; i++) {
            p2[i] = 1 - P1[i];
        }
        return p2;
    }
    public double[] getMu1(double[] histogram, double[] P1){
        double [] Mu1 = new double[NUM_INTENSITY_LEVELS];
        for (int i = 0; i < NUM_INTENSITY_LEVELS; i++) {
            double sum = 0;
            for (int j = 0; j <= i; j++) {
                sum += (j+1) * histogram[j];
            }
            Mu1[i] = sum / P1[i];
        }
        return Mu1;

    }
    public double[] getMu2(double[] histogram, double[] P2){
        double [] Mu2 = new double[NUM_INTENSITY_LEVELS];
        for (int i = 0; i < NUM_INTENSITY_LEVELS; i++) {
            double sum = 0;
            for (int j = i+1; j < NUM_INTENSITY_LEVELS; j++) {
                sum += (j+1) * histogram[j];
            }
            Mu2[i] = sum / P2[i];
        }
        return Mu2;
    }


    public double[] getSigmas(double[] histogram, double[] P1, double[] P2, double[] mu1, double[] mu2) {
        double [] Sigmas = new double[histogram.length];
        for (int i=0; i< histogram.length; i++){
            Sigmas[i] = P1[i] * P2[i] * Math.pow(mu1[i] - mu2[i], 2);
        }
        return Sigmas;
    }

    public int getMaximum(double[] sigmas){
        int maxIndex = 0;
        double max = sigmas[0];
        for (int i = 1; i < NUM_INTENSITY_LEVELS; i++) {
            if (sigmas[i] > max) {
                max = sigmas[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    public int otsuGetThreshold(ImageProcessor in) {
        double[] histogram = getHistogram(in);
        double[] P1 = getP1(histogram);
        double[] P2 = getP2(P1);
        double[] mu1 = getMu1(histogram, P1);
        double[] mu2 = getMu2(histogram, P2);
        double[] sigmas = getSigmas(histogram, P1, P2, mu1, mu2);
        return getMaximum(sigmas);

    }

}

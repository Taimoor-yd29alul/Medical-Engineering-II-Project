package project;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class Task_1_Threshold implements PlugInFilter {
    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_8G;
    }



    @Override
    public void run(ImageProcessor ip) {

        GenericDialog gd = new GenericDialog("Thresholding");
        gd.addNumericField("Threshold value:", 128, 0);
        gd.addCheckbox("Correct uneven illumination", false);
        gd.showDialog();

        //check if the dialog was canceled
        if (gd.wasCanceled())
            return;

        //get user choices  - remove everything from here to check installation
        int threshold = (int) gd.getNextNumber();
        boolean correct = gd.getNextBoolean();

        //correct illumination if selected
        ImageProcessor ipCopy;
        if (correct) {
            ipCopy = correctIllumination(ip);
        } else {
            ipCopy = ip;
        }

        // threshold the image
        ByteProcessor thresholdedIp = threshold(ipCopy, threshold);
        ImagePlus thresholdedImage = new ImagePlus("Thresholded Image", thresholdedIp);
        thresholdedImage.show();


    }

    public ByteProcessor threshold (ImageProcessor ip,int threshold) {
        int height=ip.getHeight();
        int width=ip.getWidth();
        ByteProcessor threshold_image= new ByteProcessor(width,height);
        for (int i =0; i <height; i++ ) {
            for (int j = 0; j < width; j++) {
                int pixel=ip.getPixel(i,j);
                if (pixel>threshold){
                    threshold_image.set(i,j,255);
                }
                else {threshold_image.set(i,j,0);}

            }
        }

        return  threshold_image;
    }

    public ByteProcessor correctIllumination ( ImageProcessor ip ){

        FloatProcessor image= ip.convertToFloatProcessor();
        FloatProcessor gaussian_image= image.duplicate().convertToFloatProcessor();
        gaussian_image.blurGaussian(75.0);
        int height=ip.getHeight();
        int width=ip.getWidth();
        FloatProcessor corrected_image= new FloatProcessor(width,height);
        for (int i =0; i <height; i++ ) {
            for (int j = 0; j < width; j++) {
                float num=image.getPixelValue(i,j);
                float den=gaussian_image.getPixelValue(i,j);
                corrected_image.putPixelValue(i,j,num/den);

            }
        }


        return corrected_image.convertToByteProcessor();





    }



}

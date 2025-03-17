package project;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.IJ;

public class Task_2_EvaluateSegmentation implements PlugInFilter {
    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_8G;
    }

    @Override
    public void run(ImageProcessor imageProcessor) {
        IJ.showMessage("Evaluation", "Please select the reference image");
        ImagePlus referenceImage = IJ.openImage();
        if (referenceImage == null){
            throw new IllegalArgumentException("Reference image was not loaded successfully. Please select a valid image.");
        }
        else {
            ImageProcessor reference = referenceImage.getProcessor();
            EvaluationResult result = evaluateSegmentation(imageProcessor, reference);
            if (result == null){
                throw new IllegalArgumentException("The dimensions of the segmentation and reference images do not match.");
            }
            else {
                IJ.showMessage("Evaluation", "Specificity: " + result.getSpecificity() + "\nSensitivity: " + result.getSensitivity());
            }

        }




    }

    private EvaluationResult evaluateSegmentation ( ImageProcessor segmentation , ImageProcessor reference ){
        int seg_width = segmentation.getWidth();
        int seg_height = segmentation.getHeight();
        int ref_height = reference.getHeight();
        int ref_width = reference.getWidth();
        if ( seg_width != ref_width || seg_height != ref_height ){
            return null;
        }
        else
        {
            double TP = 0;
            double TN = 0;
            double FP = 0;
            double FN = 0;
            for ( int y = 0; y < ref_height; y++ ){
                for ( int x = 0; x < ref_width; x++ ){
                    int seg_pixel = segmentation.getPixel(x, y);
                    int ref_pixel = reference.getPixel(x, y);
                    if ( seg_pixel == 255 && ref_pixel == 255 ){
                        TP=TP+1;

                    }
                    else if ( seg_pixel == 0 && ref_pixel == 0 ){
                        TN=TN+1;
                    }
                    else if ( seg_pixel == 255 && ref_pixel == 0 ){
                        FP=FP+1;
                    }
                    else if ( seg_pixel == 0 && ref_pixel == 255 ){
                        FN=FN+1;

                    }
                }
            }

            return new EvaluationResult(TN / ( TN + FP ), TP / ( TP + FN ));

        }
    }
}

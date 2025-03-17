package project;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class Task_5_CannyEdgeDetection implements PlugInFilter {

    @Override
    public void run(ImageProcessor imageProcessor) {
        Task_4_Filters filter=new Task_4_Filters();
        int[][] SobelX = {{1,0,-1},{2,0,-2},{1,0,-1}};
        int[][] SobelY = {{1,2,1},{0,0,0},{-1,-2,-1}};
        GenericDialog gd = new GenericDialog("Canny Edge Detection");
        gd.addNumericField("Sigma", 1, 0);
        gd.addNumericField("Upper Threshold", 80, 0);
        gd.addNumericField("Lower Threshold", 20, 0);
        gd.showDialog();
        if (gd.wasCanceled()){
            return;
        }
        int sigma = (int)gd.getNextNumber();
        int upper = (int)gd.getNextNumber();
        int lower = (int)gd.getNextNumber();

        FloatProcessor Image = imageProcessor.convertToFloatProcessor();
        Image.blurGaussian(sigma);
        FloatProcessor filter_image_X=filter.applyFilter(Image, SobelX);
        FloatProcessor filter_image_Y=filter.applyFilter(Image, SobelY);
        FloatProcessor Gradient = filter.getGradient(filter_image_X,filter_image_Y);
        ByteProcessor Dir = getDir(filter_image_X,filter_image_Y);
        FloatProcessor NonMax = nonMaxSuppress(Gradient,Dir);
        ByteProcessor Edge = hysteresisThreshold(NonMax,upper,lower);
        ImagePlus Result = new ImagePlus("Canny Edge Detection",Edge);
        Result.show();


    }


    public ByteProcessor getDir (FloatProcessor X_Deriv, FloatProcessor Y_Deriv){

        ByteProcessor output=new ByteProcessor(X_Deriv.getWidth(),X_Deriv.getHeight());
        int [] angles={0,45,90,135};
        int width=X_Deriv.getWidth();
        int height=X_Deriv.getHeight();
        for (int i =0; i<width;i++){
            for (int j=0;j<height;j++){
                float x=X_Deriv.get(i,j);
                float y=Y_Deriv.get(i,j);
                double angle=Math.toDegrees(Math.atan2(-y,x));
                if (angle<0){
                    angle+=180;
                }
                int min_index=0;
                double min_diff=180;
                for (int k=0;k<angles.length;k++){
                    double diff=Math.abs(angle-angles[k]);
                    if (diff<min_diff){
                        min_diff=diff;
                        min_index=k;
                    }
                }
                output.set(i,j,angles[min_index]);
            }
        }
        return output;
    }

    public FloatProcessor nonMaxSuppress(FloatProcessor Grad, ByteProcessor Dir) {
        FloatProcessor result = new FloatProcessor(Grad.getWidth(), Grad.getHeight());
        int width = Grad.getWidth();
        int height = Grad.getHeight();
        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                float angle = Dir.get(i, j);
                float gradient = Grad.get(i, j);
                float pixel1 = 0;
                float pixel2 = 0;
                if (angle == 0) {
                    pixel1 = Grad.get(i - 1, j);
                    pixel2 = Grad.get(i + 1, j);
                } else if (angle == 45) {
                    pixel1 = Grad.get(i - 1, j - 1);
                    pixel2 = Grad.get(i + 1, j + 1);
                } else if (angle == 90) {
                    pixel1 = Grad.get(i, j - 1);
                    pixel2 = Grad.get(i, j + 1);
                } else if (angle == 135) {
                    pixel1 = Grad.get(i - 1, j + 1);
                    pixel2 = Grad.get(i + 1, j - 1);
                }
                if (gradient >= pixel1 && gradient >= pixel2) {
                    result.setf(i, j, gradient);
                } else {
                    result.setf(i, j, 0);
                }
            }
        }
        return result;
    }

    public ByteProcessor hysteresisThreshold (FloatProcessor In, int upper, int lower){
        float tHigh = ((float)In.getMax()*upper)/100f;
        float tLow = ((float)In.getMax()*lower)/100f;
        ByteProcessor output = new ByteProcessor(In.getWidth(), In.getHeight());
        int width = In.getWidth();
        int height = In.getHeight();
        for (int i=0;i<width;i++) {
            for (int j = 0; j < height; j++) {
                if (In.getf(i, j) >= tHigh) {
                    output.set(i, j, 255);
                }

            }
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int x = 0; x < In.getWidth(); x++) {
                for (int y = 0; y < In.getHeight(); y++) {
                    if (In.getPixelValue(x, y) >= tLow && hasNeighbours(output, x, y) && output.getPixel(x,y)==0) {
                        output.set(x, y, 255);
                        changed = true;
                    }
                }
            }
        }

        return output;

    }

    public boolean hasNeighbours(ByteProcessor BP, int x, int y ){
        int count = (BP.getPixel(x+1,y)+BP.getPixel(x-1,y)+BP.getPixel(x,y+1)+BP.getPixel(x,y-1)+BP.getPixel(x+1,y+1)+
                BP.getPixel(x-1,y-1)+BP.getPixel(x-1,y+1)+BP.getPixel(x+1,y-1));
        count/=255;
        return (count>0) ;
    }




    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_8G;
    }
}

package project;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class Task_4_Filters implements PlugInFilter {

    protected int[][] SobelX = {{1,0,-1},{2,0,-2},{1,0,-1}};
    protected int[][] SobelY = {{1,2,1},{0,0,0},{-1,-2,-1}};

    protected int[][] ScharrX = {{47,0,-47},{162,0,-162},{47,0,-47}};
    protected int[][] ScharrY = {{47,162,47},{0,0,0},{-47,-162,-47}};

    protected int[][] PrewittX = {{1,0,-1},{1,0,-1},{1,0,-1}};
    protected int[][] PrewittY = {{1,1,1},{0,0,0},{-1,-1,-1}};

    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_8G;
    }

    @Override
    public void run(ImageProcessor imageProcessor){
        GenericDialog gd = new GenericDialog("Choose Filter");
        String[] Filters = {"Sobel","Scharr","Prewitt"};
        gd.addChoice("Filter",Filters,"Scharr");
        gd.showDialog();
        if (gd.wasCanceled()){
            return;
        }
        int index = gd.getNextChoiceIndex();
        FloatProcessor In = imageProcessor.convertToFloatProcessor();
        FloatProcessor filter_image_X =null;
        FloatProcessor filter_image_Y =null;
        if (index==0) {
            filter_image_X=applyFilter(In, SobelX);
            filter_image_Y=applyFilter(In, SobelY);
        }
        else if (index==1){
            filter_image_X=applyFilter(In, ScharrX);
            filter_image_Y=applyFilter(In, ScharrY);
        }
        else if (index==2){
            filter_image_X=applyFilter(In, PrewittX);
            filter_image_Y=applyFilter(In, PrewittY);

        }
        FloatProcessor Gradient = getGradient(filter_image_X,filter_image_Y);
        ImagePlus Result = new ImagePlus(Filters[index],Gradient);
        Result.show();
    }

    public FloatProcessor applyFilter (FloatProcessor In, int[][] kernel){
        int width = In.getWidth();
        int height = In.getHeight();
        FloatProcessor Filter_image = new FloatProcessor(width, height);
        int kw = kernel[0].length;
        int kh = kernel.length;
        for (int i=0;i<width-kw;i++){
            for (int j=0;j<height-kh;j++){
                float sum = 0;
                for (int k=0;k<kw;k++){
                    for (int l=0;l<kh;l++){
                        sum += In.getf(i+k,j+l)*kernel[k][l];
                    }
                }
                Filter_image.setf(i,j,sum);
            }
        }
        return Filter_image;
    }
    public FloatProcessor getGradient (FloatProcessor In_X, FloatProcessor In_Y) {
        int X_width = In_X.getWidth();
        int X_height = In_X.getHeight();
        int Y_width = In_Y.getWidth();
        int Y_height = In_Y.getHeight();
        if (X_width != Y_width || X_height != Y_height) {
            throw new IllegalArgumentException("The dimensions of the two images are not the same");
        }
        FloatProcessor Gradient = new FloatProcessor(X_width, X_height);
        for (int i = 0; i < X_width; i++) {
            for (int j = 0; j < X_height; j++) {
                float X = In_X.getf(i, j);
                float Y = In_Y.getf(i, j);
                Gradient.setf(i, j, (float) Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2)));
            }
        }

        return Gradient;
    }
}

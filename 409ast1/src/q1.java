import java.awt.Color;
import java.awt.image.*;
import java.io.*;
import java.util.Random;

import javax.imageio.*;

public class q1 {

    // The image constructed
    public static BufferedImage img;

    // Image dimensions; you can modify these for bigger/smaller images
    public static int width = 1920;
    public static int height = 1080;
    public static Random rnd;

    public static int minRadiusFrac = 4;	//Min radius is 1/minRadiusFrac of maxRadius
    public static int r;
    public static int c;
    public static boolean multithreaded;

    public static void main(String[] args) {
    	rnd = new Random();
        try {
            if (args.length<3)
                throw new Exception("Missing arguments, only "+args.length+" were specified!");
            // arg 0 is the max radius
            r = Integer.parseInt(args[0]);
            // arg 1 is count
            c = Integer.parseInt(args[1]);
            // arg 2 is a boolean
            multithreaded = Boolean.parseBoolean(args[2]);

            // create an image and initialize it to all 0's
            img = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
            for (int i=0;i<width;i++) {
                for (int j=0;j<height;j++) {
                    img.setRGB(i,j,0xffffffff);
                }
            }

            // YOU NEED TO ADD CODE HERE AT LEAST!
            
            Thread t1 = new Thread(new Painter());
            Thread t2 = (!multithreaded) ? null : new Thread(new Painter());
            long time1 = System.currentTimeMillis();

            t1.start();
            if(t2 != null) t2.start();
            t1.join(0);
            System.out.println("Total time1: "+(System.currentTimeMillis()-time1+"ms"));
            if(t2 != null) t2.join();
            System.out.println("Total time2: "+(System.currentTimeMillis()-time1+"ms"));
            
            
            // Write out the image
            File outputfile = new File("outputimage.png");
            ImageIO.write(img, "png", outputfile);

        } catch (Exception e) {
            System.out.println("ERROR " +e);
            e.printStackTrace();
        }
        
       
    }
    
    public static class Painter implements Runnable{

		@Override
		public void run() {
			for(int i=0; i<(multithreaded ? c/2 : c); i++) {
				int radius = rnd.nextInt(r-r/minRadiusFrac)+r/minRadiusFrac;
		    	int color = (rnd.nextInt() & 0x00FFFFFF)+0xFF000000;
		    	int xp = rnd.nextInt(width);
		    	int yp = rnd.nextInt(height);
		    	
		    	for(int xc=-radius; xc<radius; xc++) {
		    		for(int yc = (int) -Math.sqrt(Math.pow(radius,2)-Math.pow(xc, 2)); yc < (int) Math.sqrt(Math.pow(radius,2)-Math.pow(xc, 2)); yc++) {
		    			try {
		    				img.setRGB(((xp+xc)%width+width) % width,((yp+yc)%height + height)% height,color);	//Weird syntax to make modulo of negatives actually wrap around
		    			}catch(java.lang.ArrayIndexOutOfBoundsException e) {
		    				System.out.println("Before: "+(xp+xc)+" ,  "+(yp+yc));
		    				System.out.println("After: "+(xp+xc)%width+" ,  "+(yp+yc)%height);
		    				return;
		    			}
		    		}
		    	}
			}
		}
    	
    }
    
    /*
    public static void drawCircle() {
    	
    	int radius = rnd.nextInt(r-r/minRadiusFrac)+r/minRadiusFrac;
    	int color = (rnd.nextInt() & 0x00FFFFFF)+0xFF000000;
    	int xp = rnd.nextInt(width);
    	int yp = rnd.nextInt(height);
    	
    	for(int xc=-radius; xc<radius; xc++) {
    		for(int yc = (int) -Math.sqrt(Math.pow(radius,2)-Math.pow(xc, 2)); yc < (int) Math.sqrt(Math.pow(radius,2)-Math.pow(xc, 2)); yc++) {
    			try {
    				img.setRGB(((xp+xc)%width+width) % width,((yp+yc)%height + height)% height,color);	//Weird syntax to make modulo of negatives actually wrap around
    			}catch(java.lang.ArrayIndexOutOfBoundsException e) {
    				System.out.println("Before: "+(xp+xc)+" ,  "+(yp+yc));
    				System.out.println("After: "+(xp+xc)%width+" ,  "+(yp+yc)%height);
    				return;
    			}
    		}
    	}
    }*/
    
}

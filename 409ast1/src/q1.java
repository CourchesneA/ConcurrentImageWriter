import java.awt.Color;
import java.awt.image.*;
import java.io.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.*;

public class q1 {

    // The image constructed
    public static BufferedImage img;

    // Image dimensions; you can modify these for bigger/smaller images
    public static int width = 1920;
    public static int height = 1080;
    public static Random rnd = new Random();

    public static int minRadiusFrac = 4;	//Min radius is 1/minRadiusFrac of maxRadius
    public static int r;
    public static int c;
    public static boolean multithreaded;
    public static int[] data = new int[width*height];
    public static AtomicInteger counter = new AtomicInteger();
    public volatile static CircleInfo[] otherCircle = new CircleInfo[2];	//info about the circle that the other is writing


    public static void main(String[] args) {
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
            //We dont need this anymore:
            /*
            for (int i=0;i<width;i++) {
                for (int j=0;j<height;j++) {
                    img.setRGB(i,j,0xffffffff);
                }
            }*/
            java.util.Arrays.fill(data, 0xffffffff);

            // YOU NEED TO ADD CODE HERE AT LEAST!
            
            //The threads will write to the static data[], which will after be written to the image
            Thread t1 = new Thread(new PainterConcurrent().init(0));
            Thread t2 = (!multithreaded) ? null : new Thread(new PainterConcurrent().init(1));
            long time1 = System.currentTimeMillis();

            t1.start();
            if(t2 != null) t2.start();
            t1.join(0);
            //System.out.println("Total time1: "+(System.currentTimeMillis()-time1+"ms"));
            if(t2 != null) t2.join();
            System.out.println("Total time2: "+(System.currentTimeMillis()-time1+"ms"));
            
            
	    	try {
		    	img.setRGB(0, 0, width,height, data, 0, width);

	    	}catch(ArrayIndexOutOfBoundsException e) {
	    		e.printStackTrace();
	    	}
            // Write out the image
            File outputfile = new File("outputimage.png");
            ImageIO.write(img, "png", outputfile);

        } catch (Exception e) {
            System.out.println("ERROR " +e);
            e.printStackTrace();
        }
        
       
    }
    
    public static class PainterConcurrent implements Runnable{
    	int id;
		@Override
		public void run() {
			
			while(counter.getAndIncrement()<c) {
				int radius = rnd.nextInt(r-r/minRadiusFrac)+r/minRadiusFrac;
		    	int color = (rnd.nextInt() & 0x00FFFFFF)+0xFF000000;
		    	int xp = rnd.nextInt(width);
		    	int yp = rnd.nextInt(height);
		    	
		    	CircleInfo ci = new CircleInfo(xp, yp, radius);
		    	while(!canWrite(ci)) {
		    		//System.out.println("ID "+id);
		    		Thread.yield();
		    	}
		    	//System.out.println("out");
		    	
		    	for(int xc=-radius; xc<radius; xc++) {
		    		for(int yc = (int) -Math.sqrt(Math.pow(radius,2)-Math.pow(xc, 2)); yc < (int) Math.sqrt(Math.pow(radius,2)-Math.pow(xc, 2)); yc++) {
		    			try {
		    				//img.setRGB(((xp+xc)%width+width) % width,((yp+yc)%height + height)% height,color);	//Weird syntax to make modulo of negatives actually wrap around
		    				data[((yp+yc)%height + height)% height*width+((xp+xc)%width+width) % width] = color;
		    			}catch(java.lang.ArrayIndexOutOfBoundsException e) {
		    				e.printStackTrace();
		    				return;
		    			}
		    		}
		    	}
			}
			//Synchronously set our ID to null in the array
			canWrite(null);
			
		}
		
		/**
		 * Check if we can write this circle safely, and if we can save the info that we are writing this circle
		 * @param ci
		 * @return true if we can write the circle without concurrency issues
		 */
		boolean canWrite(CircleInfo ci) {
			//This is the only way to read or write to otherCircle array. Since we synchronize the object, there will be no concurrent access
			synchronized(otherCircle) {
				otherCircle[id] = null;	//Notify volatile var that we are done
		    	if(ci == null)return false;
				if( otherCircle[1-id] == null || !otherCircle[1-id].intersect(ci)) {
					otherCircle[id] = ci;
					return true;
				}
				return false;
			}
		}
		
		private PainterConcurrent init(int threadID) {
			id = threadID;
			return this;
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

class CircleInfo{
	int x;
	int y;
	int radius;
	long writerID;
	
	public CircleInfo(int x, int y, int radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
	}
	
	public boolean intersect(CircleInfo other) {
		double distance = Math.sqrt(Math.abs(Math.pow(other.x-x, 2)+Math.pow(other.y-y, 2)));
		return distance < radius+other.radius;
	}
	
}

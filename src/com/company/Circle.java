package com.company;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Circle {

    public void run(String[] args) {

        String defaultImage = "src/smarties.png";
        String imagePath;

        if (args.length == 1) {
            imagePath = defaultImage;
        } else if (args.length == 2) {
            imagePath = args[1];
        } else {
            System.out.println("Incorrect parameters");
            return;
        }

        // Load an image
        Mat src = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_COLOR);
        // Check if image is loaded fine
        if( src.empty() ) {
            System.out.println("Error opening image!");
            System.out.println("Program Arguments: [image_name -- default "
                    + defaultImage +"] \n");
            System.exit(-1);
        }

        Mat gray = new Mat();
        //Convert to grayscale
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        //Apply a median blur to reduce the noise and avoid false circle detection
        Imgproc.medianBlur(gray, gray, 5);

        //Apply Hough Circle transform
        Mat circles = new Mat();
        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0,
                (double)gray.rows()/16, // change this value to detect circles with different distances to each other
                100.0, 30.0, 1, 30); // change the last two parameters
        // (min_radius & max_radius) to detect larger circles

        //Draw detected circles
        for (int x = 0; x < circles.cols(); x++) {
            double[] c = circles.get(0, x);
            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
            // circle center
            Imgproc.circle(src, center, 1, new Scalar(0,100,100), 3, 8, 0 );
            // circle outline
            int radius = (int) Math.round(c[2]);
            Imgproc.circle(src, center, radius, new Scalar(255,0,255), 3, 8, 0 );
        }

        //Dysplay detected circles and wait for the user to exit the program
        HighGui.imshow("detected circles", src);
        HighGui.waitKey();
        System.exit(0);
    }
}

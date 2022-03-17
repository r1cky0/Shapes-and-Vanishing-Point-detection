import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class VanishingPoint {

    public void run(String[] args) {
        //Variables declaration
        Mat res = new Mat();

        int cannyLowThreshold;
        int cannyHighThreshold;
        int houghThreshold;

        int DEFAULT_CLT = 100;
        int DEFAULT_CHT = 200;
        int DEFAULT_HOUGH = 110;

        String defaultImage = "src/res/Vbridge.png";
        String imagePath;

        /*
         *   Check user parameters:
         *   first: type of function, already checked in "main"
         *   second: image path
         *   third: Low Canny edge detector threshold
         *   fourth: High Canny edge detector threshold
         *   fifth: Hough Transform threshold
         */

        if (args.length == 1) {
            imagePath = defaultImage;
            cannyLowThreshold = DEFAULT_CLT;
            cannyHighThreshold = DEFAULT_CHT;
            houghThreshold = DEFAULT_HOUGH;
        } else if (args.length == 2) {
            imagePath = args[1];
            cannyLowThreshold = DEFAULT_CLT;
            cannyHighThreshold = DEFAULT_CHT;
            houghThreshold = DEFAULT_HOUGH;
        } else if (args.length == 5) {
            imagePath = args[1];
            try {
                Integer.parseInt(args[2]);
                Integer.parseInt(args[3]);
                Integer.parseInt(args[4]);
            } catch (NumberFormatException e) {
                System.err.println("Incorrect threshold values");
                return;
            }
            cannyLowThreshold = Integer.parseInt(args[2]);
            cannyHighThreshold = Integer.parseInt(args[3]);
            houghThreshold = Integer.parseInt(args[4]);
        } else {
            System.out.println("Incorrect parameters");
            return;
        }

        // Load image and conversion to grayscale
        Mat src = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_GRAYSCALE);

        // Check if the image is correctly loaded
        if (src.empty()) {
            System.out.println("Error opening image! Wrong path");
            System.exit(-1);
        }
        System.out.println("-Image: " + imagePath);
        System.out.println("-Image Parameters:\n\tHeight: "+src.rows() +"\tWidth: "+ src.cols());
        System.out.println("-Threshold Parameters:\n " + "\tLow Threshold Canny \t" + cannyLowThreshold);
        System.out.println("\tHigh Threshold Canny \t" +cannyHighThreshold);
        System.out.println("\tHough Threshold \t" +houghThreshold);

        Mat srcBlur= new Mat();
        Mat cannyBlurred= new Mat();
        Size BLUR_SIZE= new Size(3,3);

        // Smoothing and Canny filters
        Imgproc.blur(src, srcBlur, BLUR_SIZE);
        Imgproc.Canny(srcBlur, cannyBlurred, cannyLowThreshold, cannyHighThreshold, 3, true);

        // Copy edges to the images that will display the results in BGR
        Imgproc.cvtColor(src, res, Imgproc.COLOR_GRAY2BGR);

        // Applied Hough transform and save results in variable lines
        Mat lines = new Mat();
        Imgproc.HoughLines(cannyBlurred, lines, 1, Math.PI/180, houghThreshold);

        // parametersRhoTheta saves rho e theta parameters
        // intersectionPoints saves intersection points
        ArrayList<Point> parametersRhoTheta = new ArrayList<>();
        ArrayList<Point> intersectionPoints = new ArrayList<>();

        drawLines(res, lines, parametersRhoTheta);
        drawIntersections(res, parametersRhoTheta, intersectionPoints);

        System.out.println("-Intersections detected: " + intersectionPoints.size());

        drawVanishingPoint(res,intersectionPoints);

        HighGui.imshow("Greyscale Image", src);

        HighGui.imshow("Canny Edge Detector", cannyBlurred);

        HighGui.imshow("Vanishing Point", res);

        HighGui.waitKey();
        System.exit(0);
    }

    private void drawLines(Mat res, Mat lines, ArrayList<Point> parametersRhoTheta) {

        for (int x = 0; x < lines.rows(); x++) {
            // theta in radians, rho in pixels
            double rho = lines.get(x, 0)[0];
            double theta = lines.get(x, 0)[1];

            // exclusion of vertical or horizontal straight lines
            if(theta>0.1 && theta<1.50 || theta>1.65 && theta<3.05) {
                double a = Math.cos(theta), b = Math.sin(theta);
                double x0 = a * rho, y0 = b * rho;

                int LINE_LENGTH = 1000;
                // x1000 to define "extremes of straight lines" pt1, pt2 to be drawn

                Point pt1 = new Point(Math.round(x0 + LINE_LENGTH * (-b)), Math.round(y0 + LINE_LENGTH * (a)));
                Point pt2 = new Point(Math.round(x0 - LINE_LENGTH * (-b)), Math.round(y0 - LINE_LENGTH * (a)));

                // method to draw lines
                Imgproc.line(res, pt1, pt2, new Scalar(0, 255, 255), 1, Imgproc.LINE_4, 0);

                Point p = new Point(rho, theta);
                parametersRhoTheta.add(p);
            }
        }
    }

    private void drawIntersections(Mat res, ArrayList<Point> parametersRhoTheta, ArrayList<Point> intersections) {
        for(int i=0; i<parametersRhoTheta.size(); i++) {
            for (int j = i + 1; j < parametersRhoTheta.size(); j++) {
                if (i != j) {
                    if (parametersRhoTheta.get(i).y != parametersRhoTheta.get(j).y) {
                        Point intersectionPoint = computeIntersections(parametersRhoTheta.get(i).x, parametersRhoTheta.get(i).y, parametersRhoTheta.get(j).x, parametersRhoTheta.get(j).y);
                        intersections.add(intersectionPoint);
                        Imgproc.circle(res, intersectionPoint, 2, new Scalar(255, 0, 0), 2, 8, 0);
                    }
                }
            }
        }
    }

    // Computing intersection point between two straight lines with rho and theta parameters with Cramer resolution
    public Point computeIntersections(double rho1, double theta1, double rho2, double theta2) {
        Point pt= new Point();

        double cosT1 = Math.cos(theta1);
        double sinT1 = Math.sin(theta1);
        double cosT2 = Math.cos(theta2);
        double sinT2 = Math.sin(theta2);

        double determinant = cosT1*sinT2 - sinT1*cosT2;

        // Check determinant and computation of intersection point
        if(determinant!=0){
            pt.x = (sinT2*rho1 - sinT1*rho2) / determinant;
            pt.y = (-cosT2*rho1 + cosT1*rho2) / determinant;
        }

        return pt;
    }

    private void drawVanishingPoint(Mat res, ArrayList<Point> intersectionPoints) {
        if (intersectionPoints.size()==1){
            Imgproc.circle(res, intersectionPoints.get(0), 15, new Scalar(0, 0, 255), 2, 8, 0);
        } else if (intersectionPoints.size()>1){
            Imgproc.circle(res, computeVanishingPoint(intersectionPoints), 15, new Scalar(0, 0, 255), 2, 8, 0);
        } else {
            System.out.println("There are no intersections! Please decrease threshold values");
            System.exit(-1);
        }
    }

    // Minimum distance criterion to estimate vanishing point
    public Point computeVanishingPoint(ArrayList<Point> list){
        Point vanishingP = new Point(list.get(0).x,list.get(0).y);
        double minDist;

        double[] distanceSum = new double[list.size()];

        for(int i=0; i<list.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                minDist = Math.sqrt(Math.pow(list.get(i).x - list.get(j).x, 2) + Math.pow(list.get(i).y - list.get(j).y, 2));
                distanceSum[i] += minDist;
            }
        }
        double minSum = distanceSum[0];

        for (int i=0;i<distanceSum.length-1;i++){
            if (distanceSum[i]<minSum){
                minSum=distanceSum[i];
                vanishingP= list.get(i);
            }
        }

        System.out.println("\n-Vanishing Point coordinates" + vanishingP);
        return vanishingP;
    }
}

package com.company;

import org.opencv.core.*;

public class Main {

    public static void main(String[] args) {
        if (args[0].equals("vpoint")) {
            System.out.println("Vanishing point detection: \n");

            //load openCV library
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            new VanishingPoint().run(args);
        } else if (args[0].equals("circles")) {
            System.out.println("Circles detection: \n");

            //load openCV library
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            new Circle().run(args);
        } else {
            System.out.println("Incorrect parameters");
        }
    }
}

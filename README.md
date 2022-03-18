# Shapes-and-Vanishing-Point-detection

collaboration with @jacopoedoardodelcol

program that can find vanishing point of an image or detect circles
- when you run the program you have to specify "vpoint" or "circles"
- in case of "circles" --> needed absolute path of the image (if not specified a default image is used)
- in case of "vpoint" --> needed absolute path of the image and 3 parameters (if 0 extra parameters are specified default values are used, if 1 it must be the absolute image path, if 4 must be path, canny Low Threshold, canny High Threshold, hough Treshold)

to start:
- opencv needed --> download the latest version from the official website (section "releases", in my case is version 4.5.5)
- java needed --> you can check your version with command "java -version" and "javac -version" (windows, similar commands in linux)
- in order to run the program from the command line image paths must be absolute (default image paths have to be modified in case you want to use them)

run the program (windows, similar in linux):
- you can run it from any directory
- command sintax (windows): java [opencv path] [class path];[opencv-455.jar path] Main [vpoint or circles] [optional parameters]

some examples
![image](https://user-images.githubusercontent.com/48349275/158921964-e9263d31-ac48-438d-9618-2af91dc4ec6f.png)
![image](https://user-images.githubusercontent.com/48349275/158997231-4ad2044b-7ef0-43a0-bcac-eb0a4bd04c50.png)


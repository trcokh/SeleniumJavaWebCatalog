package utils.readers;

import org.apache.commons.io.FileUtils;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import static org.bytedeco.opencv.global.opencv_core.*;

public class ImageReader {

    public static boolean areImagesVisuallySimilar(String imageUrl, String localImagePath, double threshold) {
        try {
            Mat img1 = opencv_imgcodecs.imread(localImagePath);
            Mat img2 = loadImageFromUrl(imageUrl);

            if (img1.empty() || img2 == null || img2.empty()) {
                System.err.println("One or both images could not be loaded.");
                return false;
            }

            opencv_imgproc.resize(img2, img2, img1.size());

            Mat gray1 = new Mat();
            Mat gray2 = new Mat();
            opencv_imgproc.cvtColor(img1, gray1, opencv_imgproc.COLOR_BGR2GRAY);
            opencv_imgproc.cvtColor(img2, gray2, opencv_imgproc.COLOR_BGR2GRAY);

            Mat diff = new Mat();
            absdiff(gray1, gray2, diff);
            diff.convertTo(diff, CV_32F);
            diff = new Mat(diff.mul(diff));
            Scalar s = sumElems(diff);
            double mse = s.get(0) / (double) gray1.total();

            return mse < threshold;

        } catch (Exception e) {
            System.err.println("Error during image comparison: " + e.getMessage());
            return false;
        }
    }


    public static String downloadImageFromUrl(String imageUrl) {
        try {
            File downloadedImage = new File("downloaded_image.jpg");
            URL url = URI.create(imageUrl).toURL();
            FileUtils.copyURLToFile(url, downloadedImage);
            return downloadedImage.getAbsolutePath();
        } catch (IOException e) {
            System.err.println("Failed to download image: " + e.getMessage());
            return null;
        }
    }

    public static Mat loadImageFromUrl(String imageUrl) {
        try (InputStream in = URI.create(imageUrl).toURL().openStream()) {
            byte[] imageBytes = in.readAllBytes();
            if (imageBytes.length == 0) {
                System.err.println("Image data is empty.");
                return null;
            }
            return opencv_imgcodecs.imdecode(new Mat(imageBytes), opencv_imgcodecs.IMREAD_COLOR);
        } catch (Exception e) {
            System.err.println("Failed to load image from URL: " + e.getMessage());
            return null;
        }
    }
}
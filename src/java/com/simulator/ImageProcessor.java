package com.simulator;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by shashwat on 19/10/15.
 */
public class ImageProcessor {
    BufferedImage image;

    ImageProcessor(BufferedImage image)
    {
        this.image = image;

    }

    public ImageProcessor()
    {
    }

    void showImage() throws IOException
    {
        image = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("out.png"));
    }

    static
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) throws IOException
    {
        ImageProcessor ip = new ImageProcessor();
        ip.showImage();

        byte[] pixels = ((DataBufferByte)ip.image.getRaster().getDataBuffer()).getData();
        System.out.println(pixels.length);
//        Mat img = new Mat(ip.image.getHeight(),ip.image.getWidth(),CvType.CV_8UC1);
//        img.put(0,0,pixels);

//        Imgcodecs.imwrite("newimg.png",img);
    }
}

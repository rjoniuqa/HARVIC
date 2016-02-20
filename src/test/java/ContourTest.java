import static org.opencv.imgcodecs.Imgcodecs.imencode;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.boundingRect;
import static org.opencv.imgproc.Imgproc.contourArea;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.dilate;
import static org.opencv.imgproc.Imgproc.findContours;
import static org.opencv.imgproc.Imgproc.threshold;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
public class ContourTest {

	public static void main(String[] args) throws IOException{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		BackgroundSubtractorMOG2 bgSubtractor = 
				Video.createBackgroundSubtractorMOG2(500, 16, true);
		
		
		Mat fgMask = new Mat();
		
		File dataDirectory = new File("data/test");
		for(String file : dataDirectory.list()){
			Mat trainingImage = imread("data/test/" + file);
			cvtColor(trainingImage, trainingImage, COLOR_BGR2GRAY);
			bgSubtractor.apply(trainingImage, fgMask, 0.5);
		}
		Mat testImage = imread("data/test/" + dataDirectory.list()[10]);
		imshow("original", testImage);
		Mat grayScale = new Mat();
		cvtColor(testImage, grayScale, COLOR_BGR2GRAY);
		
		bgSubtractor.apply(grayScale, fgMask);
		threshold(fgMask, fgMask, 244, 255, Imgproc.THRESH_BINARY);
		Mat kernel = Mat.ones(new Size(3, 3), CvType.CV_8U);
		dilate(fgMask, fgMask, kernel, new Point(1, 1), 2);
		imshow("foreground", fgMask);
		
		List<MatOfPoint> contourPoints = new ArrayList<MatOfPoint>();
		findContours(fgMask, contourPoints, new Mat(), 
				Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
		for(MatOfPoint point : contourPoints){
			if(contourArea(point) < 1){
				continue;
			}
			Rect rect = boundingRect(point);
			Imgproc.rectangle(testImage, new Point(rect.x, rect.y), 
					new Point(rect.x + rect.width, rect.y + rect.height), 
					new Scalar(0.0, 255.0));
		}
		imshow("bounding boxes", testImage);
	}
	
	public static void imshow(String title, Mat image) throws IOException{
		MatOfByte imageMat = new MatOfByte();
		imencode(".png", image, imageMat);
		byte[] imageBytes = imageMat.toArray();
		BufferedImage bufferedImage = null;
		
		InputStream in = new ByteArrayInputStream(imageBytes);
		bufferedImage = ImageIO.read(in);
		new ImshowFrame(title).show(bufferedImage);
	}
	
	static class ImshowFrame extends JFrame{
		private BufferedImage image;
				
		public ImshowFrame(String title){
			super(title);
		}
		
		public void show(BufferedImage image){
			setSize(image.getWidth(), image.getHeight());
			setVisible(true);
			this.image = image;
		}
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			if(image != null){
				g.drawImage(image, 0, 0, null);
			}
		}
	}
}

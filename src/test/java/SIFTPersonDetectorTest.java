import java.io.File;
import java.io.IOException;

import org.cehci.harvic.gui.Imshow;
import org.cehci.harvic.module.detection.detector.SIFTPersonDetector;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class SIFTPersonDetectorTest {

	public static void main(String[] args) throws IOException{
		SIFTPersonDetectorTest personDetector = new SIFTPersonDetectorTest();
		personDetector.test();
	}
	
	public void test() throws IOException{
		
		File file = new File("data/test");
		for(String filename : file.list()) {
			Mat frame = Imgcodecs.imread("data/test/" + filename);
			Imshow originalImshow = new Imshow("test original");
			originalImshow.onPropertyChange("", null, originalImshow.matToBufferedImage(frame));
			
			SIFTPersonDetector personDetector = new SIFTPersonDetector();
			MatOfRect rects = personDetector.detect(frame);	
			for (Rect rect : rects.toList()) {
				Imgproc.rectangle(frame, new Point(rect.x, rect.y),
						new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0.0, 255.0, 0.0, 0.0));
			}
			
			Imshow imshow = new Imshow("test sift");
			imshow.onPropertyChange("", null, imshow.matToBufferedImage(frame));
			
		}
		
	}
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

}

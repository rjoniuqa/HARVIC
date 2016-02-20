package org.cehci.harvic.module.camera;

import static org.opencv.imgproc.Imgproc.GaussianBlur;
import static org.opencv.imgproc.Imgproc.boundingRect;
import static org.opencv.imgproc.Imgproc.contourArea;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.dilate;
import static org.opencv.imgproc.Imgproc.findContours;
import static org.opencv.imgproc.Imgproc.threshold;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ContourPersonDetector implements PersonDetector {

	private static final double MIN_CONTOUR_AREA = 1;
	private Mat referenceFrame;

	@Override
	public MatOfRect detect(Mat frame) {
		Mat grayScale = new Mat();
		cvtColor(frame, grayScale, Imgproc.COLOR_BGR2GRAY);
		GaussianBlur(grayScale, grayScale, new Size(21, 21), 0);

		if (referenceFrame == null) {
			referenceFrame = grayScale;
		}
		Mat difference = new Mat();
		Core.absdiff(referenceFrame, grayScale, difference);
		threshold(difference, difference, 25, 255, Imgproc.THRESH_BINARY);
		Mat kernel = Mat.ones(new Size(3, 3), CvType.CV_8U);
		dilate(difference, difference, kernel, new Point(1, 1), 2);

		List<MatOfPoint> contourPoints = new ArrayList<MatOfPoint>();
		findContours(difference, contourPoints, new Mat(), 
				Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
		MatOfRect detectedPersons = new MatOfRect();
		List<Rect> boundingBoxes = new ArrayList<Rect>();
		for(MatOfPoint point : contourPoints){
			if(contourArea(point) < MIN_CONTOUR_AREA){
				continue;
			}
			boundingBoxes.add(boundingRect(point));
		}
		detectedPersons.fromList(boundingBoxes);
		
		//TODO: add person detection on detected boxes
		return detectedPersons;
	}

}

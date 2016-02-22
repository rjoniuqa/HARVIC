package org.cehci.harvic.module.camera;

import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.boundingRect;
import static org.opencv.imgproc.Imgproc.contourArea;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.dilate;
import static org.opencv.imgproc.Imgproc.findContours;
import static org.opencv.imgproc.Imgproc.threshold;
import static org.opencv.imgproc.Imgproc.morphologyEx;
import static org.opencv.imgproc.Imgproc.GaussianBlur;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

public class ContourPersonDetector implements PersonDetector {
//
//	private static final double MIN_CONTOUR_AREA = 1;
//	private Mat referenceFrame;
	private BackgroundSubtractorMOG2 bgSubtractor;

	public ContourPersonDetector() {
		bgSubtractor = Video.createBackgroundSubtractorMOG2(500, 16, true);
	}

//	@Override
//	public MatOfRect detect(Mat frame) {
//		Mat grayScale = new Mat();
//		cvtColor(frame, grayScale, Imgproc.COLOR_BGR2GRAY);
//		GaussianBlur(grayScale, grayScale, new Size(21, 21), 0);
//
//		if (referenceFrame == null) {
//			referenceFrame = grayScale;
//		}
//		Mat difference = new Mat();
//		Core.absdiff(referenceFrame, grayScale, difference);
//		threshold(difference, difference, 25, 255, Imgproc.THRESH_BINARY);
//		Mat kernel = Mat.ones(new Size(3, 3), CvType.CV_8U);
//		dilate(difference, difference, kernel, new Point(1, 1), 2);
//
//		List<MatOfPoint> contourPoints = new ArrayList<MatOfPoint>();
//		findContours(difference, contourPoints, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//
//		MatOfRect detectedPersons = new MatOfRect();
//		List<Rect> boundingBoxes = new ArrayList<Rect>();
//		for (MatOfPoint point : contourPoints) {
//			if (contourArea(point) < MIN_CONTOUR_AREA) {
//				continue;
//			}
//			boundingBoxes.add(boundingRect(point));
//		}
//		detectedPersons.fromList(boundingBoxes);
//
//		// TODO: add person detection on detected boxes
//		return detectedPersons;
//	}
	
	@Override
	public MatOfRect detect(Mat frame) {
//		cvtColor(frame, frame, COLOR_BGR2GRAY);
//		bgSubtractor.apply(frame, frame, 0.001);
//		Mat kernel = Mat.ones(new Size(1, 1), CvType.CV_8U);
//		morphologyEx(frame, frame, Imgproc.MORPH_OPEN, kernel);
//		threshold(frame, frame, 254, 255, Imgproc.THRESH_BINARY);
		
		Mat grayScale = new Mat();
		cvtColor(frame, grayScale, COLOR_BGR2GRAY);
//		GaussianBlur(grayScale, grayScale, new Size(21, 21), 0);
		Mat fgMask = new Mat();
		bgSubtractor.apply(grayScale, fgMask, 0.001);	
		Mat kernel = Mat.ones(new Size(1, 1), CvType.CV_8U);
		morphologyEx(fgMask, fgMask, Imgproc.MORPH_OPEN, kernel);
		threshold(fgMask, fgMask, 254, 255, Imgproc.THRESH_BINARY);
//		dilate(fgMask, fgMask, kernel, new Point(1, 1), 2);
		List<MatOfPoint> contourPoints = new ArrayList<MatOfPoint>();
		findContours(fgMask, contourPoints, new Mat(), 
				Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
		
		MatOfRect rects = new MatOfRect();
		List<Rect> boundingBoxes = new ArrayList<>();
		System.out.println(contourPoints.size());
		for(MatOfPoint point : contourPoints){
			if(contourArea(point) < 50){
				continue;
			}
			Rect rect = boundingRect(point);
			boundingBoxes.add(rect);
		}
		rects.fromList(boundingBoxes);
		System.out.println(boundingBoxes.size());
		System.out.println(rects.toList().size());
		return rects;
	}

}

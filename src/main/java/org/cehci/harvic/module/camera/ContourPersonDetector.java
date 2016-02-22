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
import java.util.Arrays;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
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
		
//		MatOfRect rects = new MatOfRect();
//		List<Rect> boundingBoxes = new ArrayList<>();
//		for(MatOfPoint point : contourPoints){
//			if(contourArea(point) < 50){
//				continue;
//			}
//			Rect rect = boundingRect(point);
//			boundingBoxes.add(rect);
//		}
//		rects.fromList(boundingBoxes);
		
		
		MatOfRect rects = new MatOfRect();
		List<CustomRect> boundingBoxes = new ArrayList<>();
		for(MatOfPoint point : contourPoints){
			if(contourArea(point) < 50){
				continue;
			}
			Rect rect = boundingRect(point);
			CustomRect cRect = new CustomRect(rect.x, rect.y, 
					rect.width, rect.height);
			boundingBoxes.add(cRect);
		}
		System.out.print("From " + boundingBoxes.size());
		List<Rect> groupedRectangles = groupRectangles(boundingBoxes, 100);
		rects.fromList(groupedRectangles);
		
		System.out.println("to " + rects.toList().size());
		return rects;
	}

//	private Rect groupRectangles(List<Rect> rectangles, int padding){
//		if(rectangles.size() == 0)
//			return null;
//		
//		int rCount = rectangles.size();
//		int[] x1s = new int[rCount];
//		int[] y1s = new int[rCount];
//		
//		int[] x2s = new int[rCount];
//		int[] y2s = new int[rCount];
//		
//		for(int i = 0; i < rCount; i++){
//			Rect curRectangle = rectangles.get(i);
//
//			x1s[i] = curRectangle.x;
//			y1s[i] = curRectangle.y;
//			
//			x2s[i] = curRectangle.x + curRectangle.width;
//			y2s[i] = curRectangle.y + curRectangle.height;
//		}
//		
//		Arrays.sort(x1s);
//		Arrays.sort(x2s);
//		Arrays.sort(y1s);
//		Arrays.sort(y2s);
//		return new Rect(x1s[0] - padding / 2, y1s[0] - padding / 2, 
//				x2s[rCount - 1] - x1s[0] + padding / 2, 
//				y2s[rCount - 1] - y1s[0] + padding / 2);
//	}
	
	private List<Rect> groupRectangles(List<CustomRect> rectangles, int threshold){
		
		List<CustomRect> clusters = new ArrayList<>(rectangles);
		if(clusters.size() > 1){
		
			int prevClusterSize;
			do{
				prevClusterSize = clusters.size();
				
				Object[] currentDistanceResult = clusters.get(0).findNearest(clusters);
				CustomRect c1 = clusters.get(0);
				CustomRect c2 = (CustomRect) currentDistanceResult[0];
				double nearestDistance = (double) currentDistanceResult[1];
				
				for(int i = 1; i < prevClusterSize; i++){
					CustomRect currCluster = clusters.get(i);
					currentDistanceResult = currCluster.findNearest(clusters);
					if(((double) currentDistanceResult[1]) < nearestDistance){
						c1 = currCluster;
						c2 = (CustomRect) currentDistanceResult[0];
						nearestDistance = (double) currentDistanceResult[1];
					}
				}
				
				if(nearestDistance <= threshold){
					clusters.remove(c1);
					clusters.remove(c2);
					clusters.add(c1.join(c2));
				}
			} while(prevClusterSize != clusters.size() && clusters.size() > 1);
		}
		return new ArrayList<Rect>(clusters);
	}
	
	private class CustomRect extends Rect {
		
		public CustomRect(int xMin, int yMin, int width, int height) {
			super(xMin, yMin, width, height);
		}
		
		public CustomRect(Point a, Point b){
			super(a, b);
		}

		public CustomRect join(Rect r2){
			Rect r1 = this;
//			System.out.println("r1 x1: " + r1.x + " r1 y1: " + r1.y);
//			System.out.println("r1 x2: " + (r1.x + r1.width) + " r1 y2: " + (r1.y + r1.height));
//			System.out.println("r2 x1: " + r2.x + " r2 y1: " + r2.y);
//			System.out.println("r2 x2: " + (r2.x + r2.width) + " r2 y2: " + (r2.y + r2.height));
			int xMin = Math.min(r1.x, r2.x);
			int yMin = Math.min(r1.y, r2.y);

			int r1XMax = r1.x + r1.width;
			int r2XMax = r2.x + r2.width;
			int xMax = Math.max(r1XMax, r2XMax);
			

			int r1YMax = r1.y + r1.height;
			int r2YMax = r2.y + r2.height;
			int yMax = Math.max(r1YMax, r2YMax);
			
			
//			System.out.println("r3 x1: " + xMin + " r3 y1: " + yMin);
//			System.out.println("r3 x2: " + xMax + " r3 y2: " + yMax);
			
			return new CustomRect(new Point(xMin, yMin), new Point(xMax, yMax));
		} 
		
		public Object[] findNearest(List<CustomRect> rectangles){
			List<CustomRect> rectanglesCopy = new ArrayList<>(rectangles);
			rectanglesCopy.remove(this);
			
			if(rectanglesCopy.size() == 0){
				return null;
			}
			
			CustomRect nearest = rectanglesCopy.get(0);
			double lowestDistance = distanceTo(nearest);
			for(int i = 1; i < rectanglesCopy.size(); i++){
				double currentDistance = 0;
				if((currentDistance = distanceTo(rectanglesCopy.get(i))) < lowestDistance){
					lowestDistance = currentDistance;
					nearest = rectanglesCopy.get(i);
				}
			}
			return new Object[]{nearest, lowestDistance};
		}
		
		public double distanceTo(CustomRect cluster){
			Point otherClusterCenter = cluster.getCenter();
			Point myCenter = getCenter();
			
			return Math.sqrt(Math.pow(otherClusterCenter.x - myCenter.x, 2) + 
					Math.pow(otherClusterCenter.y - myCenter.y, 2));
		}
		
		public Point getCenter(){
			return new Point((x + width) / 2, 
					(y + height) / 2);
		}
	}
}

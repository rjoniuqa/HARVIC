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

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.SVM;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

public class ContourPersonDetector implements PersonDetector {
	//
	// private static final double MIN_CONTOUR_AREA = 1;
	// private Mat referenceFrame;
	private BackgroundSubtractorMOG2 bgSubtractor;

	private SVM svm;

	public ContourPersonDetector() {
		bgSubtractor = Video.createBackgroundSubtractorMOG2(500, 16, true);
		svm = SVM.load("svm_persondetect.xml");
	}

	// @Override
	// public MatOfRect detect(Mat frame) {
	// Mat grayScale = new Mat();
	// cvtColor(frame, grayScale, Imgproc.COLOR_BGR2GRAY);
	// GaussianBlur(grayScale, grayScale, new Size(21, 21), 0);
	//
	// if (referenceFrame == null) {
	// referenceFrame = grayScale;
	// }
	// Mat difference = new Mat();
	// Core.absdiff(referenceFrame, grayScale, difference);
	// threshold(difference, difference, 25, 255, Imgproc.THRESH_BINARY);
	// Mat kernel = Mat.ones(new Size(3, 3), CvType.CV_8U);
	// dilate(difference, difference, kernel, new Point(1, 1), 2);
	//
	// List<MatOfPoint> contourPoints = new ArrayList<MatOfPoint>();
	// findContours(difference, contourPoints, new Mat(), Imgproc.RETR_EXTERNAL,
	// Imgproc.CHAIN_APPROX_SIMPLE);
	//
	// MatOfRect detectedPersons = new MatOfRect();
	// List<Rect> boundingBoxes = new ArrayList<Rect>();
	// for (MatOfPoint point : contourPoints) {
	// if (contourArea(point) < MIN_CONTOUR_AREA) {
	// continue;
	// }
	// boundingBoxes.add(boundingRect(point));
	// }
	// detectedPersons.fromList(boundingBoxes);
	//
	// // TODO: add person detection on detected boxes
	// return detectedPersons;
	// }
	public Mat subtractBackground(Mat frame) {
		Mat copy = new Mat();
		frame.copyTo(copy);

		cvtColor(copy, copy, COLOR_BGR2GRAY);
		bgSubtractor.apply(copy, copy, 0.001);
		Mat kernel = Mat.ones(new Size(1, 1), CvType.CV_8U);
		morphologyEx(copy, copy, Imgproc.MORPH_OPEN, kernel);
		threshold(copy, copy, 254, 255, Imgproc.THRESH_BINARY);

		return copy;
	}

	@Override
	public MatOfRect detect(Mat frame) {
		// cvtColor(frame, frame, COLOR_BGR2GRAY);
		// bgSubtractor.apply(frame, frame, 0.001);
		// Mat kernel = Mat.ones(new Size(1, 1), CvType.CV_8U);
		// morphologyEx(frame, frame, Imgproc.MORPH_OPEN, kernel);
		// threshold(frame, frame, 254, 255, Imgproc.THRESH_BINARY);
		Mat grayScale = new Mat();
		cvtColor(frame, grayScale, COLOR_BGR2GRAY);
		// GaussianBlur(grayScale, grayScale, new Size(21, 21), 0);
		Mat fgMask = new Mat();
		bgSubtractor.apply(grayScale, fgMask, 0.0025);
		Mat kernel = Mat.ones(new Size(1, 1), CvType.CV_8U);
		morphologyEx(fgMask, fgMask, Imgproc.MORPH_OPEN, kernel);
		threshold(fgMask, fgMask, 254, 255, Imgproc.THRESH_BINARY);
		// dilate(fgMask, fgMask, kernel, new Point(1, 1), 2);
		List<MatOfPoint> contourPoints = new ArrayList<MatOfPoint>();
		findContours(fgMask, contourPoints, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		// MatOfRect rects = new MatOfRect();
		// List<Rect> boundingBoxes = new ArrayList<>();
		// for(MatOfPoint point : contourPoints){
		// if(contourArea(point) < 50){
		// continue;
		// }
		// Rect rect = boundingRect(point);
		// boundingBoxes.add(rect);
		// }
		// rects.fromList(boundingBoxes);

		MatOfRect rects = new MatOfRect();
		List<CustomRect> boundingBoxes = new ArrayList<>();
		List<Rect> boundingBoxesBasic = new ArrayList<>();
		for (MatOfPoint point : contourPoints) {
			if (contourArea(point) < 50) {
				continue;
			}
			Rect rect = boundingRect(point);
			CustomRect cRect = new CustomRect(rect.x, rect.y, rect.width, rect.height);
			boundingBoxes.add(cRect);
			boundingBoxesBasic.add(rect);
		}
		List<Rect> groupedRectangles = groupRectangles(boundingBoxes, 50);

		rects.fromList(groupedRectangles);
		return rects;

		// rects.fromList(adjustAspectRatio(groupedRectangles));
		// rects.fromList(groupedRectangles);

		// return person

		// return filterPerson(frame, rects);

	}

	private List<Rect> adjustAspectRatio(List<Rect> rectangles) {

		for (int i = 0; i < rectangles.size(); i++) {

			if (rectangles.get(i).height < rectangles.get(i).width
					|| rectangles.get(i).width > rectangles.get(i).height / 2) {
				rectangles.get(i).y += rectangles.get(i).height / 2;
				rectangles.get(i).height = 2 * rectangles.get(i).width;
				rectangles.get(i).y -= rectangles.get(i).height / 2;
			} else if (rectangles.get(i).width < rectangles.get(i).height / 2) {
				rectangles.get(i).x += rectangles.get(i).width / 2;
				rectangles.get(i).width = rectangles.get(i).height / 2;
				rectangles.get(i).x -= rectangles.get(i).width / 2;
			}
			if (rectangles.get(i).x <= 0 || rectangles.get(i).y <= 0
					|| rectangles.get(i).x + rectangles.get(i).width >= 320
					|| rectangles.get(i).y + rectangles.get(i).height >= 240) {
				rectangles.remove(i);
			}
		}

		return rectangles;
	}

	private MatOfRect filterPerson(Mat inputImage, MatOfRect detectedPeople) {
		MatOfRect filteredRects = new MatOfRect();
		List<Rect> rects = new ArrayList<Rect>();
		for (Rect rect : detectedPeople.toList()) {
			Mat image_roi = new Mat(inputImage, rect);
			Size sz = new Size(64, 128);
			Mat resizedImage = new Mat();
			Imgproc.resize(image_roi, resizedImage, sz);
			if (svm.predict(getDescriptorForImage(resizedImage)) == 1) {
				rects.add(rect);
			}
		}
		filteredRects.fromList(rects);
		return filteredRects;
	}

	private Mat getDescriptorForImage(Mat image) {
		Mat grayScale = new Mat();
		MatOfFloat imageDescriptor = new MatOfFloat();
		Imgproc.cvtColor(image, grayScale, Imgproc.COLOR_BGR2GRAY);
		HOGDescriptor hogDescriptor = new HOGDescriptor();
		hogDescriptor.compute(grayScale, imageDescriptor);
		Mat reshapedImageDescriptor = imageDescriptor.reshape(0, 1);
		return reshapedImageDescriptor;
	}

	private List<Rect> groupRectangles(List<CustomRect> rectangles, int threshold) {

		List<CustomRect> clusters = new ArrayList<>(rectangles);
		if (clusters.size() > 1) {

			int prevClusterSize;
			do {
				prevClusterSize = clusters.size();

				Object[] currentDistanceResult = clusters.get(0).findNearest(clusters);
				CustomRect c1 = clusters.get(0);
				CustomRect c2 = (CustomRect) currentDistanceResult[0];
				double nearestDistance = (double) currentDistanceResult[1];

				for (int i = 1; i < prevClusterSize; i++) {
					CustomRect currCluster = clusters.get(i);
					currentDistanceResult = currCluster.findNearest(clusters);
					if (((double) currentDistanceResult[1]) < nearestDistance) {
						c1 = currCluster;
						c2 = (CustomRect) currentDistanceResult[0];
						nearestDistance = (double) currentDistanceResult[1];
					}
				}

				if (nearestDistance <= threshold) {
					clusters.remove(c1);
					clusters.remove(c2);
					clusters.add(c1.join(c2));
				}
			} while (prevClusterSize != clusters.size() && clusters.size() > 1);
		}
		return new ArrayList<Rect>(clusters);
	}

	private class CustomRect extends Rect {

		public CustomRect(int xMin, int yMin, int width, int height) {
			super(xMin, yMin, width, height);
		}

		public CustomRect(Point a, Point b) {
			super(a, b);
		}

		public CustomRect join(Rect r2) {
			Rect r1 = this;
			// System.out.println("r1 x1: " + r1.x + " r1 y1: " + r1.y);
			// System.out.println("r1 x2: " + (r1.x + r1.width) + " r1 y2: " +
			// (r1.y + r1.height));
			// System.out.println("r2 x1: " + r2.x + " r2 y1: " + r2.y);
			// System.out.println("r2 x2: " + (r2.x + r2.width) + " r2 y2: " +
			// (r2.y + r2.height));
			int xMin = Math.min(r1.x, r2.x);
			int yMin = Math.min(r1.y, r2.y);

			int r1XMax = r1.x + r1.width;
			int r2XMax = r2.x + r2.width;
			int xMax = Math.max(r1XMax, r2XMax);

			int r1YMax = r1.y + r1.height;
			int r2YMax = r2.y + r2.height;
			int yMax = Math.max(r1YMax, r2YMax);

			// System.out.println("r3 x1: " + xMin + " r3 y1: " + yMin);
			// System.out.println("r3 x2: " + xMax + " r3 y2: " + yMax);

			return new CustomRect(new Point(xMin, yMin), new Point(xMax, yMax));
		}

		public Object[] findNearest(List<CustomRect> rectangles) {
			List<CustomRect> rectanglesCopy = new ArrayList<>(rectangles);
			rectanglesCopy.remove(this);

			if (rectanglesCopy.size() == 0) {
				return null;
			}

			CustomRect nearest = rectanglesCopy.get(0);
			double lowestDistance = distanceTo(nearest);
			for (int i = 1; i < rectanglesCopy.size(); i++) {
				double currentDistance = 0;
				if ((currentDistance = distanceTo(rectanglesCopy.get(i))) < lowestDistance) {
					lowestDistance = currentDistance;
					nearest = rectanglesCopy.get(i);
				}
			}
			return new Object[] { nearest, lowestDistance };
		}

		public double distanceTo(CustomRect cluster) {
			Point otherClusterCenter = cluster.getCenter();
			Point myCenter = getCenter();

			int othersLeftBoundary = cluster.x;
			int othersRightBoundary = cluster.x + cluster.width;
			int othersBottomBoundary = cluster.y + cluster.height;
			int othersTopBoundary = cluster.y;

			int selfLeftBoundary = x;
			int selfRightBoundary = x + width;
			int selfBottomBoundary = y + height;
			int selfTopBoundary = y;

			Rectangle other = new Rectangle(cluster.x, cluster.y, cluster.width, cluster.height);
			Rectangle self = new Rectangle(x, y, width, height);

			if (self.intersects(other)) {
				System.out.println("overlap");
				return 0;
			}

			int ax, ay, bx, by;
			if (othersLeftBoundary > selfRightBoundary) {
				if(othersTopBoundary <= selfBottomBoundary && othersBottomBoundary >= selfBottomBoundary ||
						othersTopBoundary <= selfTopBoundary && othersBottomBoundary >= selfTopBoundary){
					ay = by = 0; 
				} else {
					if(othersTopBoundary > selfBottomBoundary){
						ay = selfBottomBoundary;
						by = othersTopBoundary;
					} else{
						ay = selfTopBoundary;
						by = othersBottomBoundary;
					}
				}
				ax = selfRightBoundary;
				bx = othersLeftBoundary;
				
			} else if (othersRightBoundary < selfLeftBoundary) {
				if(othersTopBoundary <= selfBottomBoundary && othersBottomBoundary >= selfBottomBoundary ||
						othersTopBoundary <= selfTopBoundary && othersBottomBoundary >= selfTopBoundary){
					ay = by = 0; 
				} else {
					if(othersTopBoundary > selfBottomBoundary){
						ay = selfBottomBoundary;
						by = othersTopBoundary;
					} else{
						ay = selfTopBoundary;
						by = othersBottomBoundary;
					}
				}
				ax = selfLeftBoundary;
				bx = othersRightBoundary;
			} else if(othersTopBoundary > selfBottomBoundary) {
				if(othersLeftBoundary <= selfRightBoundary && othersRightBoundary >= selfRightBoundary ||
						othersLeftBoundary <= selfLeftBoundary && othersRightBoundary >= selfLeftBoundary){
					ax = bx = 0;
				} else {
					if(selfRightBoundary < othersLeftBoundary){
						ax = selfLeftBoundary;
						bx = othersLeftBoundary;
					} else {
						ax = selfRightBoundary;
						bx = othersRightBoundary;
					}
				}
				ay = selfBottomBoundary;
				by = othersTopBoundary;
			} else {
				if(othersLeftBoundary <= selfRightBoundary && othersRightBoundary >= selfRightBoundary ||
						othersLeftBoundary <= selfLeftBoundary && othersRightBoundary >= selfLeftBoundary){
					ax = bx = 0;
				} else {
					if(selfRightBoundary < othersLeftBoundary){
						ax = selfLeftBoundary;
						bx = othersLeftBoundary;
					} else {
						ax = selfRightBoundary;
						bx = othersRightBoundary;
					}
				}
				ay = selfTopBoundary;
				by = othersBottomBoundary;
			}
			
			Point a = new Point(ax, ay);
			Point b = new Point (bx, by);

			 return Math.sqrt(
			 Math.pow(a.x - b.x, 2) +
			 Math.pow(a.y - b.y, 2));
		}

		public Point getCenter() {
			return new Point((x + width) / 2, (y + height) / 2);
		}
	}
}

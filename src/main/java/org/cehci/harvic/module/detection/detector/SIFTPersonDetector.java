package org.cehci.harvic.module.detection.detector;

import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;

import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.cehci.harvic.module.detection.PersonDetector;

public class SIFTPersonDetector implements PersonDetector {

	private FeatureDetector fDetector;

	public SIFTPersonDetector() {
		fDetector = FeatureDetector.create(FeatureDetector.FAST);
	}

	@Override
	public MatOfRect detect(Mat frame) {

		// Mat horizontalEdgeDetected = new Mat();
		// Mat verticalEdgeDetected = new Mat();
		// Imgproc.cvtColor(frame, horizontalEdgeDetected,
		// Imgproc.COLOR_BGR2GRAY);
		// Imgproc.Canny(horizontalEdgeDetected, horizontalEdgeDetected, 100,
		// 150);
		// horizontalEdgeDetected.copyTo(verticalEdgeDetected);
		// Mat horizontalKernel = Mat.ones(new Size(2, 1), CvType.CV_8U);
		// Mat verticalKernel = Mat.ones(new Size(1, 2), CvType.CV_8U);

		// Imgproc.erode(horizontalEdgeDetected, horizontalEdgeDetected,
		// horizontalKernel);
		// Imgproc.erode(verticalEdgeDetected, verticalEdgeDetected,
		// verticalKernel);
		// Mat edgeDetected = new Mat();
		// Core.add(horizontalEdgeDetected, verticalEdgeDetected, edgeDetected);
		// Core.bitwise_and(horizontalEdgeDetected, verticalEdgeDetected,
		// edgeDetected);
		// Imshow imshow = new Imshow("edge");
		// try {
		// imshow.onPropertyChange("", null,
		// imshow.matToBufferedImage(edgeDetected));
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// };

		// morphologyEx(frame, frame, Imgproc.MORPH_CLOSE, kernel, new
		// Point(1,1), 3);
		// threshold(frame, frame, 254, 255, Imgproc.THRESH_BINARY);

		// List<MatOfPoint> contourPoints = new ArrayList<>();
		// findContours(edgeDetected, contourPoints, new Mat(),
		// Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		// List<Rect> rects = new ArrayList<>();
		// for (MatOfPoint point : contourPoints) {
		// if (contourArea(point) < 50) {
		// continue;
		// }
		// rects.add(boundingRect(point));
		// }
		// System.out.println(rects.size());
		// return groupRectangles(rects, 20);
		MatOfKeyPoint keypoints = new MatOfKeyPoint();
		fDetector.detect(frame, keypoints);
		List<Rect> keyPointRects = new ArrayList<Rect>();
		List<KeyPoint> filtered = new ArrayList<>();
		for (KeyPoint keyPoint : keypoints.toList()) {
			if (keyPoint.response > 30) {
				filtered.add(keyPoint);
				Point centerPoint = keyPoint.pt;
				Point min = new Point(centerPoint.x - keyPoint.size / 2, centerPoint.y - keyPoint.size / 2);
				Point max = new Point(centerPoint.x + keyPoint.size / 2, centerPoint.y + keyPoint.size / 2);
				keyPointRects.add(new CustomRect(min, max));
			}
		}
		keypoints.fromList(filtered);
		Features2d.drawKeypoints(frame, keypoints, frame);
		return groupRectangles(keyPointRects, 20);
	}

	private MatOfRect groupRectangles(List<Rect> rectangles, double threshold) {
		if (rectangles.size() == 0) {
			return new MatOfRect();
		}
		double[][] distances = new double[rectangles.size()][rectangles.size()];
		String[] labels = new String[rectangles.size()];
		for (int i = 0; i < rectangles.size(); i++) {
			Rect currentRect = rectangles.get(i);
			labels[i] = i + "";
			for (int j = 0; j < rectangles.size(); j++) {
				if (i == j) {
					distances[i][j] = 0;
					continue;
				}
				Rect otherRect = rectangles.get(j);
				Point currentRectCenter = new Point((currentRect.x + (currentRect.x + currentRect.width)) / 2,
						(currentRect.y + (currentRect.y + currentRect.height)) / 2);
				Point otherRectCenter = new Point((otherRect.x + (otherRect.x + otherRect.width)) / 2,
						(otherRect.y + (otherRect.y + otherRect.height)) / 2);
				double euclidean = Math.pow(currentRectCenter.x - otherRectCenter.x, 2)
						+ Math.pow(currentRectCenter.y - otherRectCenter.y, 2);
				distances[i][j] = Math.sqrt(euclidean);
			}
		}
		ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
		Cluster root = alg.performClustering(distances, labels, new AverageLinkageStrategy());
		List<Cluster> uniqueClusters = getUniqueClusters(root, threshold);
		List<Rect> uniqueRects = new ArrayList<>();
		for (Cluster cluster : uniqueClusters) {
			if (cluster.isLeaf()) {
				uniqueRects.add(rectangles.get(Integer.parseInt(cluster.getName())));
			} else {
				List<Cluster> leafClusters = getLeafClusters(cluster);

				Rect first = rectangles.get(Integer.parseInt(leafClusters.remove(0).getName()));
				int minX = first.x, minY = first.y, maxX = first.x + first.width, maxY = first.y + first.height;
				for (Cluster leafCluster : leafClusters) {
					Rect currentRect = rectangles.get(Integer.parseInt(leafCluster.getName()));
					if (currentRect.x < minX) {
						minX = currentRect.x;
					}
					if (currentRect.y < minY) {
						minY = currentRect.y;
					}
					if (currentRect.x + currentRect.width > maxX) {
						maxX = currentRect.x + currentRect.width;
					}
					if (currentRect.y + currentRect.height > maxY) {
						maxY = currentRect.y + currentRect.height;
					}
				}
				uniqueRects.add(new Rect(new Point(minX, minY), new Point(maxX, maxY)));
			}
		}
		MatOfRect rects = new MatOfRect();
		rects.fromList(uniqueRects);
		return rects;
	}

	private List<Cluster> getLeafClusters(Cluster cluster) {
		List<Cluster> leafClusters = new ArrayList<Cluster>();
		if (cluster.isLeaf()) {
			leafClusters.add(cluster);
		} else {
			List<Cluster> children = cluster.getChildren();
			for (Cluster child : children) {
				leafClusters.addAll(getLeafClusters(child));
			}
		}
		return leafClusters;
	}

	public List<Cluster> getUniqueClusters(Cluster cluster, double threshold) {
		List<Cluster> uniqueClusters = new ArrayList<Cluster>();
		if (cluster.getDistanceValue() > threshold) {
			for (Cluster child : cluster.getChildren()) {
				uniqueClusters.addAll(getUniqueClusters(child, threshold));
			}
		} else {
			uniqueClusters.add(cluster);
		}
		return uniqueClusters;
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

	private class RectCluster {
		public static final int COMPLETE = 0;
		public static final int AVERAGE = 1;
		public static final int SINGLE = 2;

		private List<Rect> rects;

		public void merge(RectCluster cluster) {
			for (Rect rect : cluster.rects) {
				rects.add(rect);
			}
		}

		public double distanceTo(RectCluster cluster, int linkage) {

			switch (linkage) {
			case COMPLETE:
				return distanceCompleteLinkage(cluster);
			default:
				return 0;
			}
		}

		private double distanceCompleteLinkage(RectCluster cluster) {
			List<Rect> otherClusterRectsCopy = new ArrayList<Rect>(cluster.rects);
			List<Rect> myRectsCopy = new ArrayList<Rect>(this.rects);
			Rect farthestRect = otherClusterRectsCopy.remove(0);
			// int maxDistance = fart
			for (Rect rect : otherClusterRectsCopy) {

			}
			return 0;
		}

		private double getRectDistance(Rect a, Rect b) {

			return 0;
		}
	}

	private class CustomRect extends Rect {

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
				if (othersTopBoundary <= selfBottomBoundary && othersBottomBoundary >= selfBottomBoundary
						|| othersTopBoundary <= selfTopBoundary && othersBottomBoundary >= selfTopBoundary) {
					ay = by = 0;
				} else {
					if (othersTopBoundary > selfBottomBoundary) {
						ay = selfBottomBoundary;
						by = othersTopBoundary;
					} else {
						ay = selfTopBoundary;
						by = othersBottomBoundary;
					}
				}
				ax = selfRightBoundary;
				bx = othersLeftBoundary;

			} else if (othersRightBoundary < selfLeftBoundary) {
				if (othersTopBoundary <= selfBottomBoundary && othersBottomBoundary >= selfBottomBoundary
						|| othersTopBoundary <= selfTopBoundary && othersBottomBoundary >= selfTopBoundary) {
					ay = by = 0;
				} else {
					if (othersTopBoundary > selfBottomBoundary) {
						ay = selfBottomBoundary;
						by = othersTopBoundary;
					} else {
						ay = selfTopBoundary;
						by = othersBottomBoundary;
					}
				}
				ax = selfLeftBoundary;
				bx = othersRightBoundary;
			} else if (othersTopBoundary > selfBottomBoundary) {
				if (othersLeftBoundary <= selfRightBoundary && othersRightBoundary >= selfRightBoundary
						|| othersLeftBoundary <= selfLeftBoundary && othersRightBoundary >= selfLeftBoundary) {
					ax = bx = 0;
				} else {
					if (selfRightBoundary < othersLeftBoundary) {
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
				if (othersLeftBoundary <= selfRightBoundary && othersRightBoundary >= selfRightBoundary
						|| othersLeftBoundary <= selfLeftBoundary && othersRightBoundary >= selfLeftBoundary) {
					ax = bx = 0;
				} else {
					if (selfRightBoundary < othersLeftBoundary) {
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
			Point b = new Point(bx, by);

			return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
		}

		public Point getCenter() {
			return new Point((x + width) / 2, (y + height) / 2);
		}
	}

}

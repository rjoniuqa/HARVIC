package org.cehci.harvic.module;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.ml.CvSVM;
import org.opencv.objdetect.HOGDescriptor;

public class TrainModule {

	public static void main(String[] args) {
		try {
			nu.pattern.OpenCV.loadShared();
		} catch (ExceptionInInitializerError e) {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		}
		HOGDescriptor hog = new HOGDescriptor(new Size(64, 128),
				new Size(8, 8), new Size(4, 4), new Size(4, 4), 9);
		Mat trainData = new Mat();
		Mat responses = new Mat();
		MatOfFloat descriptors = new MatOfFloat();
		hog.compute(Highgui.imread("Data Frames/Others/pedestrian_crop.jpg"),
				descriptors);
		trainData.push_back(descriptors);
		responses.push_back(Mat.ones(new Size(1, 1), CvType.CV_32FC1));
		hog.compute(
				Highgui.imread("Data Frames/Others/pedestrian_noperson.jpg"),
				descriptors);
		trainData.push_back(descriptors);
		responses.push_back(Mat.zeros(new Size(1, 1), CvType.CV_32FC1));

		CvSVM svm = new CvSVM();
		svm.train(trainData, responses);
		System.out.println(new Mat());
		// HOGDescriptor humanDetector = new HOGDescriptor();
		// humanDetector.setSVMDetector(svm.);
	}

}

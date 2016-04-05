package org.cehci.harvic.module.detection.detector;

import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.SVM;
import org.opencv.objdetect.HOGDescriptor;

import org.cehci.harvic.module.detection.PersonDetector;

public class HOGPersonDetector implements PersonDetector {

	private HOGDescriptor detector;

	public HOGPersonDetector() {
		detector = new HOGDescriptor(new Size(80, 120), new Size(20, 20), new Size(10, 10), new Size(10, 10), 9);
	}

	public void setSVMDetector(SVM svm) {
		detector.setSVMDetector(svm.getSupportVectors());
	}

	public MatOfRect detect(Mat frame) {
		MatOfRect detectedPersons = new MatOfRect();
		Mat grayScale = new Mat();
		Imgproc.cvtColor(frame, grayScale, Imgproc.COLOR_BGR2GRAY);
		detector.detectMultiScale(grayScale, detectedPersons, new MatOfDouble(), 2.75, new Size(8, 8), new Size(0, 0),
				1.5, 1, true);
		return detectedPersons;
	}
}

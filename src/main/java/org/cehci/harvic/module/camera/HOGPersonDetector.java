package org.cehci.harvic.module.camera;

import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.SVM;
import org.opencv.objdetect.HOGDescriptor;

public class HOGPersonDetector implements PersonDetector {

	private HOGDescriptor detector;

	public HOGPersonDetector() {
//		detector = new HOGDescriptor(new Size(64, 128), new Size(16, 16),
//				new Size(8, 8), new Size(8, 8), 9);
//		detector = new HOGDescriptor(new Size(48, 96), new Size(16, 16),
//				new Size(8, 8), new Size(8, 8), 9);
		
		detector = new HOGDescriptor();
		SVM svm = SVM.load("svm_persondetect.xml");
		detector.setSVMDetector(svm.getSupportVectors());
//	 detector.setSVMDetector(HOGDescriptor.getDaimlerPeopleDetector());
//		 detector.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
	}

	public MatOfRect detect(Mat frame) {
		MatOfRect detectedPersons = new MatOfRect();
		Mat grayScale = new Mat();
		Imgproc.cvtColor(frame, grayScale, Imgproc.COLOR_BGR2GRAY);
		detector.detectMultiScale(grayScale, detectedPersons, new MatOfDouble(), 
				-5,
				new Size(8, 8), new Size(0, 0), 2,
				1, false);
//		detector.detectMultiScale(frame, detectedPersons, new MatOfDouble());
		return detectedPersons;
	}
}

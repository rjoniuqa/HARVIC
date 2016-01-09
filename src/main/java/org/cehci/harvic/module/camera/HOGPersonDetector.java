package org.cehci.harvic.module.camera;

import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;
import org.opencv.objdetect.HOGDescriptor;

public class HOGPersonDetector implements PersonDetector {

	HOGDescriptor detector;
	
	public HOGPersonDetector() {
		detector = new HOGDescriptor();
		detector.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
	}

	public MatOfRect detect(Mat frame) {
		MatOfRect detectedPersons = new MatOfRect();
		detector.detectMultiScale(frame, detectedPersons, new MatOfDouble());
		return detectedPersons;
	}
}

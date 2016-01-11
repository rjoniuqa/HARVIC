package org.cehci.harvic.module.camera;

import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;
import org.opencv.objdetect.HOGDescriptor;

public class HOGPersonDetector implements PersonDetector {

	private HOGDescriptor detector;

	public HOGPersonDetector() {
		detector = new HOGDescriptor(new Size(48, 96), new Size(16, 16), new Size(8, 8), new Size(8, 8), 9);
		detector.setSVMDetector(HOGDescriptor.getDaimlerPeopleDetector());
		// detector.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
	}

	public MatOfRect detect(Mat frame) {
		MatOfRect detectedPersons = new MatOfRect();
		detector.detectMultiScale(frame, detectedPersons, new MatOfDouble());//, 0, new Size(16, 16), new Size(0, 0), 1.0,
				//0.9, true);
		return detectedPersons;
	}
}

package org.cehci.harvic.module.camera;

import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.HOGDescriptor;

public class HOGPersonDetector implements PersonDetector{
	
	public MatOfRect detect(Mat frame){
		MatOfRect detectedPersons = new MatOfRect();
		HOGDescriptor detector = new HOGDescriptor();
		detector.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
		detector.detectMultiScale(frame, detectedPersons, new MatOfDouble());
		return detectedPersons;
	}
}

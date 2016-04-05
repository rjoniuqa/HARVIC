package org.cehci.harvic.module.detection;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

public interface PersonDetector {
	public MatOfRect detect(Mat frame);
}

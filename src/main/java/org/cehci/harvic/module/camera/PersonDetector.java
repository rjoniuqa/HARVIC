package org.cehci.harvic.module.camera;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.RectVector;

public interface PersonDetector {

	public RectVector detect(Mat frame);
}

package org.cehci.harvic.module.camera;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.cehci.harvic.OpeningVideoSourceException;

public interface VideoSource {
	public void open() throws OpeningVideoSourceException;
	public void close();
	public Mat nextFrame();
}

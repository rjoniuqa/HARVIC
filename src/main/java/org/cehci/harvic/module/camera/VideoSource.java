package org.cehci.harvic.module.camera;

import org.cehci.harvic.OpeningVideoSourceException;
import org.opencv.core.Mat;

public interface VideoSource {
	public void open() throws OpeningVideoSourceException;
	public void close();
	public Mat nextFrame();
}

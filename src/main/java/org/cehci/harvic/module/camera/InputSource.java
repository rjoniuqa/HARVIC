package org.cehci.harvic.module.camera;

import org.cehci.harvic.OpeningSourceException;
import org.opencv.core.Mat;

public interface InputSource {
	public void open() throws OpeningSourceException;

	public void close();

	public Mat nextFrame();
}

package org.cehci.harvic.module.detection;

import org.opencv.core.Mat;

import org.cehci.harvic.OpeningSourceException;

public interface InputSource {
	public void open() throws OpeningSourceException;

	public void close();

	public Mat nextFrame();
}

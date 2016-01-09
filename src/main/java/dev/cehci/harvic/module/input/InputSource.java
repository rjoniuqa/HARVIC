package dev.cehci.harvic.module.input;

import org.opencv.core.Mat;

public interface InputSource {
	public Mat nextFrame();
}

package dev.cehci.harvic.module.input;

import org.bytedeco.javacpp.opencv_core.Mat;

public interface InputSource {
	public Mat nextFrame();
}

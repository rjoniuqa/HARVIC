package org.cehci.harvic.module.camera.input;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.cehci.harvic.module.camera.InputSource;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class DirectorySource implements InputSource {

	private Iterator<File> directoryIterator;

	public DirectorySource() {
	}

	@Override
	public Mat nextFrame() {
		File currImage = (File) directoryIterator.next();
		Mat frame = Imgcodecs.imread(currImage.getAbsolutePath());
		return frame;
	}

	public boolean hasNext() {
		return directoryIterator.hasNext();
	}

	public void setDirectory(File directory) {
		File[] files = directory.listFiles();
		List<File> fileList = Arrays.asList(files);
		directoryIterator = fileList.iterator();
	}

	@Override
	public void open() {
	}

	@Override
	public void close() {
	}

}

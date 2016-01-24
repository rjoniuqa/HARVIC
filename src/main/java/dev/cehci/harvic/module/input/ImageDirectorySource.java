package dev.cehci.harvic.module.input;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageDirectorySource implements InputSource {

	private Iterator<File> directoryIterator;
	
	public ImageDirectorySource() {}
	
	@Override
	public Mat nextFrame() {
		// TODO merge imagedirectorysource and opencvvideosource
		File currImage = (File) directoryIterator.next();
		Mat frame = Imgcodecs.imread(currImage.getAbsolutePath());
		Imgproc.resize(frame, frame, new Size(320, 240));
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

}

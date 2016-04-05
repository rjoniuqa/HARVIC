package org.cehci.harvic.module.detection.source;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cehci.harvic.module.detection.InputSource;

public class DirectorySource implements InputSource {

	private Iterator<File> directoryIterator;
	private File directory;

	public DirectorySource(File directory) {
		this.directory = directory;
	}

	@Override
	public Mat nextFrame() {
		if (directoryIterator.hasNext()) {
			File currImage = (File) directoryIterator.next();
			Mat frame = Imgcodecs.imread(currImage.getAbsolutePath());
			return frame;
		}
		return null;

	}

	private List<File> getFiles(File directory) {
		List<File> filesList = new ArrayList<>();
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				filesList.addAll(getFiles(file));
			} else {
				filesList.add(file);
			}
		}
		return filesList;
	}

	@Override
	public void open() {
		List<File> files = getFiles(directory);
		directoryIterator = files.iterator();
	}

	@Override
	public void close() {
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
			+ ((directory == null) ? 0 : directory.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DirectorySource other = (DirectorySource) obj;
		if (directory == null) {
			if (other.directory != null)
				return false;
		} else if (!directory.equals(other.directory))
			return false;
		return true;
	}
}

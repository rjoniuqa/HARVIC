package org.cehci.harvic.module;

import java.io.File;
import java.io.IOException;

import org.cehci.harvic.module.camera.InputSource;
import org.cehci.harvic.module.camera.PersonDetector;
import org.cehci.harvic.module.camera.input.DirectorySource;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

public class ImageModule extends DeviceModule {

	private InputSource imageSource;
	private PersonDetector personDetector;

	public ImageModule(InputSource imageSource, PersonDetector personDetector) {
		this.imageSource = imageSource;
		this.personDetector = personDetector;
	}

	public void capture(File directory) throws IOException {
		Mat frame;
		DirectorySource imageDirectorySource = (DirectorySource) imageSource;
		imageDirectorySource.setDirectory(directory);

		while (imageDirectorySource.hasNext()) {
			frame = imageDirectorySource.nextFrame();
			MatOfRect detectedPeople = personDetector.detect(frame);
			drawBoundingBoxesOnPersons(frame, detectedPeople);
			notifyPropertyChange("frame", null, toBufferedImage(frame));

			try {
				Thread.sleep(500); // 1000 milliseconds is one second.
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}

}

package org.cehci.harvic.module;

import java.io.File;
import java.io.IOException;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.cehci.harvic.module.camera.PersonDetector;

import dev.cehci.harvic.module.input.ImageDirectorySource;
import dev.cehci.harvic.module.input.InputSource;

public class ImageModule extends DeviceModule {

	private InputSource imageSource;
	private PersonDetector personDetector;
	
	public ImageModule(InputSource imageSource, PersonDetector personDetector) {
		this.imageSource = imageSource;
		this.personDetector = personDetector;
	}

	public void capture(File directory) throws IOException {
		Mat frame;
		ImageDirectorySource imageDirectorySource = (ImageDirectorySource) imageSource;
		imageDirectorySource.setDirectory(directory);
		
		while (imageDirectorySource.hasNext()) {
			frame = imageDirectorySource.nextFrame();
			RectVector detectedPeople = personDetector.detect(frame);
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

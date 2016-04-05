package org.cehci.harvic.module.detection;

import org.opencv.ml.SVM;

import java.io.File;

import org.cehci.harvic.OpeningSourceException;
import org.cehci.harvic.module.DetectionModule;
import org.cehci.harvic.module.detection.detector.HOGPersonDetector;
import org.cehci.harvic.module.detection.source.DirectorySource;

public class ImageManager {

	private DetectionModule detectionModule;
	private static ImageManager imageManager;
	private HOGPersonDetector personDetector;

	private ImageManager() {
		personDetector = new HOGPersonDetector();
		personDetector.setSVMDetector(SVM.load("svm_persondetect.xml"));
		detectionModule = new DetectionModule("1", null, personDetector);
	}

	public static ImageManager getInstance() {
		if (imageManager == null) {
			imageManager = new ImageManager();
		}
		return imageManager;
	}

	public void open(File directory) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					DirectorySource directorySource = new DirectorySource(
						directory);
					detectionModule.setInputSource(directorySource);
					detectionModule.enable();
				} catch (OpeningSourceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	public DetectionModule getDetectionModule() {
		return detectionModule;
	}
}

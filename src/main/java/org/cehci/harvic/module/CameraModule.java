package org.cehci.harvic.module;

import org.cehci.harvic.LoadingClassifierException;
import org.cehci.harvic.OpeningVideoSourceException;
import org.cehci.harvic.module.camera.Camera;
import org.cehci.harvic.module.camera.PersonDetector;
import org.cehci.harvic.module.camera.VideoSource;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Scalar;

public class CameraModule extends DeviceModule {

	private boolean isCapturing;
	private Camera camera;
	private Mat blackPlaceholder;
	private VideoSource videoSource;
	private PersonDetector personDetector;

	public CameraModule(Camera camera, VideoSource videoSource,
			PersonDetector personDetector) {
		this.camera = camera;
		this.videoSource = videoSource;
		this.personDetector = personDetector;
	}

	public boolean isCapturing() {
		synchronized (this) {
			return isCapturing;
		}
	}

	public void stop() {
		synchronized (this) {
			isCapturing = false;
		}
	}

	public void capture() throws OpeningVideoSourceException,
			LoadingClassifierException {
		openCamera();
		while (isCapturing()) {
			Mat frame = videoSource.nextFrame();
			MatOfRect detectedPeople = personDetector.detect(frame);
			drawBoundingBoxesOnPersons(frame, detectedPeople);
			notifyPropertyChange("frame", null, toBufferedImage(frame));
		}
		closeCamera();
	}

	private void closeCamera() {
		videoSource.close();
		notifyPropertyChange("status", "open", "close");
		if (blackPlaceholder == null) {
			blackPlaceholder = new Mat(320, 240, CvType.CV_8UC3, new Scalar(0,
					0, 0));
		}
		notifyPropertyChange("frame", null, toBufferedImage(blackPlaceholder));
	}

	private void openCamera() throws OpeningVideoSourceException {
		videoSource.open();
		isCapturing = true;
		notifyPropertyChange("status", "close", "open");
	}

	public String getCameraId() {
		return camera.getId();
	}

	public String getCameraName() {
		return camera.getName();
	}
}

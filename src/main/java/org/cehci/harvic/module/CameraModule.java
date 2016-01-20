package org.cehci.harvic.module;

import static org.bytedeco.javacpp.opencv_imgproc.rectangle;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.cehci.harvic.LoadingClassifierException;
import org.cehci.harvic.OpeningVideoSourceException;
import org.cehci.harvic.module.camera.Camera;
import org.cehci.harvic.module.camera.PersonDetector;
import org.cehci.harvic.module.camera.VideoSource;

public class CameraModule extends DeviceModule {

	private boolean isCapturing;
	private Camera camera;
	private Mat blackPlaceholder;
	private VideoSource videoSource;
	private PersonDetector personDetector;

	public CameraModule(Camera camera, VideoSource videoSource, PersonDetector personDetector) {
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

	public void capture() throws OpeningVideoSourceException, LoadingClassifierException {
		openCamera();
		while (isCapturing()) {
			Mat frame = videoSource.nextFrame();
			RectVector detectedPeople = personDetector.detect(frame);
			drawBoundingBoxesOnPersons(frame, detectedPeople);
			notifyPropertyChange("frame", null, toBufferedImage(frame));
		}
		closeCamera();
	}

	private void closeCamera() {
		videoSource.close();
		notifyPropertyChange("status", "open", "close");
		if (blackPlaceholder == null) {
			blackPlaceholder = new Mat(320, 240, opencv_core.CV_8UC3, new Scalar(0.0, 0.0, 0.0, 0.0));
		}
		notifyPropertyChange("frame", null, toBufferedImage(blackPlaceholder));
	}

	protected void drawBoundingBoxesOnPersons(Mat inputImage, RectVector detectedPeople) {
		for (int i = 0; i < detectedPeople.size(); i++) {
			Rect rect = detectedPeople.get(i);
			rectangle(inputImage, new Point(rect.x(), rect.y()),
					new Point(rect.x() + rect.width(), rect.y() + rect.height()), new Scalar(0.0, 255.0, 0.0, 0.0));
		}
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

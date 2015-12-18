package org.cehci.harvic.module;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Collection;

import org.cehci.harvic.LoadingClassifierException;
import org.cehci.harvic.OpeningVideoSourceException;
import org.cehci.harvic.PropertyChangeObservable;
import org.cehci.harvic.PropertyChangeObserver;
import org.cehci.harvic.module.camera.Camera;
import org.cehci.harvic.module.camera.PersonDetector;
import org.cehci.harvic.module.camera.VideoSource;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public class CameraModule implements PropertyChangeObservable {

	private boolean isCapturing;
	private Camera camera;
	private Collection<PropertyChangeObserver> propertyChangeObservers = new ArrayList<PropertyChangeObserver>();
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
			blackPlaceholder = new Mat(320, 240, CvType.CV_8UC3, new Scalar(0, 0, 0));
		}
		notifyPropertyChange("frame", null, toBufferedImage(blackPlaceholder));
	}

	private void drawBoundingBoxesOnPersons(Mat inputImage, MatOfRect detectedPeople) {
		for (Rect rect : detectedPeople.toArray()) {
			Core.rectangle(inputImage, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
					new Scalar(0, 255, 0));
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

	@Override
	public void attachObserver(PropertyChangeObserver observer) {
		propertyChangeObservers.add(observer);
	}

	@Override
	public void notifyPropertyChange(String property, Object oldValue, Object newValue) {
		for (PropertyChangeObserver observer : propertyChangeObservers) {
			observer.onPropertyChange(property, oldValue, newValue);
		}
	}

	public static Image toBufferedImage(Mat m) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}
}

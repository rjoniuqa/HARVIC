package org.cehci.harvic.module;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Collection;

import org.cehci.harvic.OpeningSourceException;
import org.cehci.harvic.PropertyChangeObservable;
import org.cehci.harvic.PropertyChangeObserver;
import org.cehci.harvic.module.detection.InputSource;
import org.cehci.harvic.module.detection.PersonDetector;

public class DetectionModule implements PropertyChangeObservable {

	private Collection<PropertyChangeObserver> propertyChangeObservers = new ArrayList<PropertyChangeObserver>();
	private InputSource inputSource;
	private PersonDetector personDetector;
	private String id;
	private boolean isEnabled;

	public DetectionModule(String id, InputSource inputSource,
		PersonDetector personDetector) {
		this.id = id;
		this.inputSource = inputSource;
		this.personDetector = personDetector;
	}

	public void setInputSource(InputSource inputSource) {
		if (this.inputSource != null) {
			this.inputSource.close();
		}
		this.inputSource = inputSource;
	}

	public void enable() throws OpeningSourceException {
		inputSource.open();
		Mat frame;
		while (isEnabled() && (frame = inputSource.nextFrame()) != null) {
			MatOfRect detectedPeople = personDetector.detect(frame);
			drawBoundingBoxesOnPersons(frame, detectedPeople);
			notifyPropertyChange("frame", null, toBufferedImage(frame));
		}
		inputSource.close();
	}

	public boolean isEnabled() {
		synchronized (this) {
			return isEnabled;
		}
	}

	public void disable() {
		synchronized (this) {
			isEnabled = false;
		}
	}

	public String getId() {
		return id;
	}

	protected void drawBoundingBoxesOnPersons(Mat inputImage,
		MatOfRect detectedPeople) {
		for (Rect rect : detectedPeople.toList()) {
			Imgproc.rectangle(inputImage, new Point(rect.x, rect.y),
				new Point(rect.x + rect.width, rect.y + rect.height),
				new Scalar(0.0, 255.0, 0.0, 0.0));
		}
	}

	public static Image toBufferedImage(Mat m) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b);
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster()
			.getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}

	@Override
	public void notifyPropertyChange(String property, Object oldValue,
		Object newValue) {
		for (PropertyChangeObserver observer : propertyChangeObservers) {
			observer.onPropertyChange(property, oldValue, newValue);
		}
	}

	@Override
	public void attachObserver(PropertyChangeObserver observer) {
		propertyChangeObservers.add(observer);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		DetectionModule other = (DetectionModule) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}

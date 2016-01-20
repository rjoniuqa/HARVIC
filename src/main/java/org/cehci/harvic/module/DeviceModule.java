package org.cehci.harvic.module;

import static org.bytedeco.javacpp.opencv_imgproc.rectangle;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Collection;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.cehci.harvic.PropertyChangeObservable;
import org.cehci.harvic.PropertyChangeObserver;

public abstract class DeviceModule implements PropertyChangeObservable {
	
	private Collection<PropertyChangeObserver> propertyChangeObservers = new ArrayList<PropertyChangeObserver>();
	
	protected void drawBoundingBoxesOnPersons(Mat inputImage,
			RectVector detectedPeople) {
		for (int i = 0; i < detectedPeople.size(); i++) {
			Rect rect = detectedPeople.get(i);
			rectangle(inputImage, new Point(rect.x(), rect.y()), new Point(
					rect.x() + rect.width(), rect.y() + rect.height()), new Scalar(0.0,
					255.0, 0.0, 0.0));
		}
	}

	public static Image toBufferedImage(Mat m) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		ByteIndexer indexer = m.createIndexer();
		indexer.get(0, 0, b); // get all the pixels
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
}

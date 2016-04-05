package org.cehci.harvic.module.detection.source;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import org.cehci.harvic.OpeningSourceException;
import org.cehci.harvic.module.detection.Camera;
import org.cehci.harvic.module.detection.InputSource;

public class VideoSource implements InputSource {

	private Camera camera;
	private VideoCapture videoCapture;

	public VideoSource(Camera camera) {
		this.camera = camera;
	}

	@Override
	public Mat nextFrame() {
		if (videoCapture != null && videoCapture.isOpened()) {
			if (videoCapture.grab()) {
				Mat frame = new Mat();
				videoCapture.retrieve(frame);
				Imgproc.resize(frame, frame, new Size(320, 240));
				return frame;
			}
		}
		return null;
	}

	@Override
	public void open() throws OpeningSourceException {
		if (videoCapture == null) {
			videoCapture = new VideoCapture();
		}
		if (!videoCapture.open(camera.getId())) {
			throw new OpeningSourceException();
		}
	}

	@Override
	public void close() {
		if (videoCapture != null) {
			videoCapture.release();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((camera == null) ? 0 : camera.hashCode());
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
		VideoSource other = (VideoSource) obj;
		if (camera == null) {
			if (other.camera != null)
				return false;
		} else if (!camera.equals(other.camera))
			return false;
		return true;
	}
}

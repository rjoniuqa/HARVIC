package org.cehci.harvic.module.camera;

import org.cehci.harvic.OpeningVideoSourceException;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class OpenCvVideoSource implements VideoSource {

	private int deviceId;
	private VideoCapture videoCapture;

	public OpenCvVideoSource(int deviceId) {
		this.deviceId = deviceId;
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
	public void open() throws OpeningVideoSourceException {
		if (videoCapture == null) {
			videoCapture = new VideoCapture();
		}
		if (!videoCapture.open(deviceId)) {
			throw new OpeningVideoSourceException();
		}
	}

	@Override
	public void close() {
		if (videoCapture != null) {
			videoCapture.release();
		}
	}

}

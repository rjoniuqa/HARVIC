package org.cehci.harvic.module.camera;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_videoio.VideoCapture;
import org.cehci.harvic.OpeningVideoSourceException;
import static org.bytedeco.javacpp.opencv_imgproc.*;

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
				resize(frame, frame, new Size(320, 640));
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

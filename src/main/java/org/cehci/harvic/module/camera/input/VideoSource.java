package org.cehci.harvic.module.camera.input;

import org.cehci.harvic.OpeningSourceException;
import org.cehci.harvic.module.camera.InputSource;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class VideoSource implements InputSource {

	private int deviceId;
	private VideoCapture videoCapture;

	public VideoSource(int deviceId) {
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
	public void open() throws OpeningSourceException {
		// if (videoCapture == null) {
		// videoCapture = new VideoCapture();
		// }
		// if (!videoCapture.open(deviceId)) {
		// throw new OpeningSourceException();
		// }
		videoCapture = new VideoCapture();
		System.out.println(videoCapture
				.open("http://192.168.254.110/mjpg/1/video.mjpg"));
	}

	@Override
	public void close() {
		if (videoCapture != null) {
			videoCapture.release();
		}
	}

}

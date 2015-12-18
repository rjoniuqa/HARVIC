package org.cehci.harvic;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import javax.swing.UIManager;

import org.cehci.harvic.gui.Gui;
import org.cehci.harvic.gui.GuiBuilder;
import org.cehci.harvic.module.camera.Camera;
import org.cehci.harvic.module.camera.CameraManager;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;

public class Driver {

	private CameraManager cameraManager = CameraManager.getInstance();

	public Driver() {
		String classifierPath = ClassLoader.getSystemClassLoader().getResource("hogcascade_pedestrians.xml").getPath();
		if (classifierPath.startsWith("/", 0)) {
			classifierPath = classifierPath.substring(1, classifierPath.length());
		}
	}

	private static void personDetect() throws IOException {
		InputStream sampleImage = ClassLoader.getSystemResourceAsStream("sample.png");
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] imageBytes = new byte[1024];
		int length;
		while ((length = sampleImage.read(imageBytes)) > 0) {
			byteArrayOutputStream.write(imageBytes, 0, length);
		}
		Mat mat = Highgui.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Highgui.IMREAD_ANYCOLOR);
		Imgproc.resize(mat, mat, new Size(320, 240));
		MatOfRect detectedPersons = new MatOfRect();
		HOGDescriptor detector = new HOGDescriptor();
		detector.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
		detector.detectMultiScale(mat, detectedPersons, new MatOfDouble());

		MatOfKeyPoint imageKeypoints = new MatOfKeyPoint();
		FeatureDetector siftDetector = FeatureDetector.create(FeatureDetector.SIFT);
		Mat croppedPerson = new Mat(mat, detectedPersons.toArray()[0]);
		siftDetector.detect(croppedPerson, imageKeypoints);
		Mat outputImage = new Mat();
		Features2d.drawKeypoints(croppedPerson, imageKeypoints, outputImage);
		outputImage.copyTo(mat);
//		for (Rect rect : detectedPersons.toArray()) {
//			Core.rectangle(mat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
//					new Scalar(0, 255, 0));
//		}
		Highgui.imwrite("person detected.png", mat);
	}

	public static void main(String[] args) throws Exception {
		 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		 new Driver().start();
		try {
			nu.pattern.OpenCV.loadShared();
		} catch (ExceptionInInitializerError e) {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		}

//		personDetect();
		// JFrame frame = new JFrame();
		// frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		// frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		// final JPanel videoPanel = new JPanel();
		// videoPanel.setSize(500, 500);
		// final JLabel currentFrame = new JLabel();
		// videoPanel.add(currentFrame);
		// frame.add(videoPanel, BorderLayout.CENTER);
		// String classifierPath =
		// ClassLoader.getSystemClassLoader().getResource("hogcascade_pedestrians.xml").getPath();
		// if(classifierPath.startsWith("/", 0)){
		// classifierPath = classifierPath.substring(1,
		// classifierPath.length());
		// }
		// System.out.println(classifierPath);
		// CameraModule cameraModule = new CameraModule(0, new
		// VideoCaptureListener() {
		//
		// @Override
		// public void onNewFrame(Mat frame) {
		// Image newFrame = toBufferedImage(frame);
		// currentFrame.setIcon(new ImageIcon(newFrame));
		// }
		// }, classifierPath);
		// frame.setVisible(true);
		// cameraModule.capture();
		//
		// try{
		// nu.pattern.OpenCV.loadShared();
		// } catch(ExceptionInInitializerError e){
		// System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// }
		// InputStream sampleImage =
		// ClassLoader.getSystemResourceAsStream("sample.png");
		// ByteArrayOutputStream byteArrayOutputStream = new
		// ByteArrayOutputStream();
		// byte[] imageBytes = new byte[1024];
		// int length;
		// while((length = sampleImage.read(imageBytes)) > 0){
		// byteArrayOutputStream.write(imageBytes, 0, length);
		// }
		// Mat mat = Highgui.imdecode(new
		// MatOfByte(byteArrayOutputStream.toByteArray()),
		// Highgui.IMREAD_ANYCOLOR);
		// MatOfKeyPoint imageKeypoints = new MatOfKeyPoint();
		// FeatureDetector siftDetector =
		// FeatureDetector.create(FeatureDetector.SIFT);
		// siftDetector.detect(mat, imageKeypoints);
		// Mat outputImage = new Mat();
		// Features2d.drawKeypoints(mat, imageKeypoints, outputImage);
		// Highgui.imwrite("keypoints.png", outputImage);
	}

	private void start() throws InvocationTargetException, InterruptedException {
		System.out.println("HARVIC starting up");
		Collection<Camera> cameras = detectCameras();
		registerCameras(cameras);
		Gui gui = GuiBuilder.build(cameraManager.getRegisteredCameras());
		gui.show();
	}

	private Collection<Camera> detectCameras() {
		System.out.println("Detecting cameras");
		return cameraManager.detectCapturingDevices();
	}

	private void registerCameras(Collection<Camera> cameras) {
		for (Camera camera : cameras) {
			cameraManager.registerCamera(camera);
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
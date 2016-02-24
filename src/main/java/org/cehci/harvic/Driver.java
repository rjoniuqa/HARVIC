package org.cehci.harvic;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import javax.swing.UIManager;

import org.cehci.harvic.gui.Gui;
import org.cehci.harvic.module.camera.Camera;
import org.cehci.harvic.module.camera.CameraManager;
import org.cehci.harvic.module.camera.ImageManager;
import org.opencv.core.Core;

public class Driver {

	private CameraManager cameraManager = CameraManager.getInstance();
	private ImageManager imageManager = ImageManager.getInstance();

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new Driver().start();
	}

	private void start() throws InvocationTargetException, InterruptedException {
		System.out.println("HARVIC starting up");
		Collection<Camera> cameras = detectCameras();
		registerCameras(cameras);
		Gui.Builder builder = new Gui.Builder();
		Gui gui = builder.createCameraTab(cameraManager.getRegisteredCameras())
				.createDirectoryTab(imageManager.getImageModule()).build();
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

	// public static Image toBufferedImage(Mat m) {
	// int type = BufferedImage.TYPE_BYTE_GRAY;
	// if (m.channels() > 1) {
	// type = BufferedImage.TYPE_3BYTE_BGR;
	// }
	// int bufferSize = m.channels() * m.cols() * m.rows();
	// byte[] b = new byte[bufferSize];
	// m.get(0, 0, b); // get all the pixels
	// BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
	// final byte[] targetPixels = ((DataBufferByte) image.getRaster()
	// .getDataBuffer()).getData();
	// System.arraycopy(b, 0, targetPixels, 0, b.length);
	// return image;
	//
	// }
}
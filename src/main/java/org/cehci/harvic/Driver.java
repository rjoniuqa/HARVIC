package org.cehci.harvic;

import org.opencv.core.Core;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import javax.swing.UIManager;

import org.cehci.harvic.gui.Gui;
import org.cehci.harvic.module.detection.Camera;
import org.cehci.harvic.module.detection.CameraManager;
import org.cehci.harvic.module.detection.ImageManager;

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

	private void start()
		throws InvocationTargetException, InterruptedException {
		System.out.println("HARVIC starting up");
		Collection<Camera> cameras = detectCameras();
		registerCameras(cameras);
		Gui.Builder builder = new Gui.Builder();
		Gui gui = builder.createCameraTab(cameraManager.getRegisteredCameras())
			.createDirectoryTab(imageManager.getDetectionModule()).build();
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
}
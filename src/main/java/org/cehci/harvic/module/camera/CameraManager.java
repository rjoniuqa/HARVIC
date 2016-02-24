package org.cehci.harvic.module.camera;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cehci.harvic.LoadingClassifierException;
import org.cehci.harvic.OpeningSourceException;
import org.cehci.harvic.module.CameraModule;
import org.cehci.harvic.module.camera.input.VideoSource;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryService;

public class CameraManager {

	private List<CameraModule> registeredCameras = new ArrayList<CameraModule>();
	private static CameraManager cameraManager;

	private CameraManager() {
	}

	public static CameraManager getInstance() {
		if (cameraManager == null) {
			cameraManager = new CameraManager();
		}
		return cameraManager;
	}

	public Collection<Camera> detectCapturingDevices() {
		Collection<Camera> detectedCameras = new ArrayList<Camera>();
		for (Webcam webcam : Webcam.getWebcams()) {
			detectedCameras.add(new Camera(webcam.hashCode() + webcam.getName(), webcam.getName()));
		}
		WebcamDiscoveryService discoveryService = Webcam.getDiscoveryService();
		discoveryService.stop();
		return detectedCameras;
	}

	public void registerCamera(Camera camera) {
		registeredCameras
				.add(new CameraModule(camera, new VideoSource(registeredCameras.size()), new ContourPersonDetector()));
	}

	public boolean isRegistered(Camera camera) {
		return registeredCameras.contains(camera);
	}

	public Collection<CameraModule> getRegisteredCameras() {
		return new ArrayList<CameraModule>(registeredCameras);
	}

	public void open(String cameraId) {
		CameraModule cameraModule = getCameraModule(cameraId);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					cameraModule.capture();
				} catch (OpeningSourceException | LoadingClassifierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}).start();
	}

	public void close(String cameraId) {
		CameraModule cameraModule = getCameraModule(cameraId);
		cameraModule.stop();
	}

	private CameraModule getCameraModule(String cameraId) {
		for (CameraModule cameraModule : registeredCameras) {
			if (cameraModule.getCameraId().equals(cameraId)) {
				return cameraModule;
			}
		}
		return null;
	}
}

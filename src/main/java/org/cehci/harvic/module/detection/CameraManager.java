package org.cehci.harvic.module.detection;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cehci.harvic.OpeningSourceException;
import org.cehci.harvic.module.DetectionModule;
import org.cehci.harvic.module.detection.detector.HOGPersonDetector;
import org.cehci.harvic.module.detection.source.VideoSource;

public class CameraManager {

	private List<DetectionModule> registeredCameras = new ArrayList<>();
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
			detectedCameras.add(new Camera(webcam.hashCode() + webcam.getName(),
				webcam.getName()));
		}
		WebcamDiscoveryService discoveryService = Webcam.getDiscoveryService();
		discoveryService.stop();
		return detectedCameras;
	}

	public void registerCamera(Camera camera) {
		DetectionModule detectionModule = new DetectionModule(
			camera.getName() + " - " + camera.getId(), new VideoSource(camera),
			new HOGPersonDetector());
		registeredCameras.add(detectionModule);
	}

	public boolean isRegistered(Camera camera) {
		return registeredCameras.contains(camera);
	}

	public Collection<DetectionModule> getRegisteredCameras() {
		return new ArrayList<DetectionModule>(registeredCameras);
	}

	public void open(String cameraId) {
		DetectionModule detectionModule = getDetectionModule(cameraId);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					detectionModule.enable();
				} catch (OpeningSourceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void close(String cameraId) {
		DetectionModule detectionModule = getDetectionModule(cameraId);
		detectionModule.disable();
	}

	private DetectionModule getDetectionModule(String cameraId) {
		for (DetectionModule detectionModule : registeredCameras) {
			if (detectionModule.getId().equals(cameraId)) {
				return detectionModule;
			}
		}
		return null;
	}
}

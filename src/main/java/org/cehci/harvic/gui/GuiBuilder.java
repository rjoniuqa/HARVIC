package org.cehci.harvic.gui;

import java.util.Collection;

import org.cehci.harvic.gui.camera.CameraGui;
import org.cehci.harvic.gui.camera.CameraPanel;
import org.cehci.harvic.gui.log.LogGui;
import org.cehci.harvic.module.CameraModule;

public class GuiBuilder {

	public static Gui build(Collection<CameraModule> cameras) {
		CameraGui cameraGui = new CameraGui();
		LogGui logGui = new LogGui();
		Gui gui = new Gui(logGui, cameraGui);

		int i = 0;
		for (CameraModule camera : cameras) {
			CameraPanel newCameraPanel = new CameraPanel(camera.getCameraName(), camera.getCameraId(), i + 1);
			camera.attachObserver(newCameraPanel);
			gui.addCameraPanel(newCameraPanel);
			i++;
		}
		return gui;
	}
}

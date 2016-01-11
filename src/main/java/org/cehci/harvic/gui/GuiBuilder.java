package org.cehci.harvic.gui;

import java.util.Collection;

import org.cehci.harvic.gui.camera.CameraGui;
import org.cehci.harvic.gui.camera.CameraPanel;
import org.cehci.harvic.gui.log.LogGui;
import org.cehci.harvic.module.CameraModule;
import org.cehci.harvic.module.ImageModule;

import dev.cehci.harvic.gui.input.InputGui;
import dev.cehci.harvic.gui.input.InputPanel;

public class GuiBuilder {

	public static Gui build(Collection<CameraModule> cameras,
			ImageModule imageModule) {
		CameraGui cameraGui = new CameraGui();
		LogGui logGui = new LogGui();
		InputGui inputGui = new InputGui();
		Gui gui = new Gui(logGui, cameraGui, inputGui);

		int i = 0;
		for (CameraModule camera : cameras) {
			CameraPanel newCameraPanel = new CameraPanel(
					camera.getCameraName(), camera.getCameraId(), i + 1);
			camera.attachObserver(newCameraPanel);
			gui.addCameraPanel(newCameraPanel);
			i++;
		}

		InputPanel newInputPanel = new InputPanel();
		imageModule.attachObserver(newInputPanel);
		gui.addInputPanel(newInputPanel);

		return gui;
	}
}

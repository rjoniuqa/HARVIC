package org.cehci.harvic.gui.event;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;

import org.cehci.harvic.module.detection.CameraManager;

public class OpenCameraAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4686025201726103529L;

	public OpenCameraAction() {
		super();
	}

	public OpenCameraAction(String name, Icon icon) {
		super(name, icon);
	}

	public OpenCameraAction(String name) {
		super(name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JButton button = (JButton) event.getSource();
		CameraManager.getInstance().open(button.getName());
		button.setAction(Actions.CLOSE_CAMERA);
	}
}

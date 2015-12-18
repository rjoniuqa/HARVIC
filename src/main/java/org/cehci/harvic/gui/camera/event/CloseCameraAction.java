package org.cehci.harvic.gui.camera.event;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;

import org.cehci.harvic.module.camera.CameraManager;

public class CloseCameraAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -705019865154045741L;

	public CloseCameraAction() {
		super();
	}

	public CloseCameraAction(String name, Icon icon) {
		super(name, icon);
	}

	public CloseCameraAction(String name) {
		super(name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JButton button = (JButton) event.getSource();
		String cameraId = button.getName();
		CameraManager.getInstance().close(cameraId);
		button.setAction(Actions.OPEN_CAMERA);
	}

}

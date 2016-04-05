package org.cehci.harvic.gui.event;

import javax.swing.Action;

public class Actions {
	public static final Action OPEN_CAMERA = new OpenCameraAction("Open");
	public static final Action CLOSE_CAMERA = new CloseCameraAction("Close");
	public static final Action SELECT_DIRECTORY = new SelectDirectoryAction(
		"Select directory");
}

package org.cehci.harvic.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.cehci.harvic.gui.camera.CameraGui;
import org.cehci.harvic.gui.camera.CameraPanel;
import org.cehci.harvic.gui.log.LogGui;

public class Gui {

	private JFrame appFrame;
	private JPanel appContentPane;
	private CameraGui cameraGui;
	private LogGui logGui;

	public Gui(LogGui logGui, CameraGui cameraGui) {
		this.logGui = logGui;
		this.cameraGui = cameraGui;
		initializeComponents();
		addComponents();
	}

	private void initializeComponents() {
		appContentPane = new JPanel(new GridBagLayout());
		appContentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		appFrame = new JFrame("HAR-VIC");
		appFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		appFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
		appFrame.setContentPane(appContentPane);

		Dimension minimumCameraGuiSize = cameraGui.getMinimumSize();
		Dimension minimumLogGuiSize = logGui.getMinimumSize();
		appFrame.setMinimumSize(new Dimension((int) (minimumCameraGuiSize.getWidth() + minimumLogGuiSize.getWidth()),
				(int) Math.max(minimumCameraGuiSize.getHeight(), minimumLogGuiSize.getHeight())));
	}

	private void addComponents() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridwidth = 3;
		constraints.weightx = 1;
		constraints.weighty = 1;
		appContentPane.add(cameraGui.getContentPane(), constraints);
		constraints.gridx = 3;
		constraints.gridwidth = 2;
		constraints.weightx = 1;
		appContentPane.add(logGui.getContentPane(), constraints);
	}

	public void show() {
		appFrame.setVisible(true);
	}

	public void addCameraPanel(CameraPanel cameraPanel) {
		cameraGui.addCameraPanel(cameraPanel);
	}
}

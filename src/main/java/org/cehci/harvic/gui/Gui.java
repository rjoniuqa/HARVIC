package org.cehci.harvic.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.cehci.harvic.gui.camera.CameraGui;
import org.cehci.harvic.gui.camera.CameraPanel;
import org.cehci.harvic.gui.log.LogGui;

import dev.cehci.harvic.gui.input.InputGui;
import dev.cehci.harvic.gui.input.InputPanel;

public class Gui {

	private JFrame appFrame;
	private JPanel cameraContentPane;
	private JPanel imageContentPane;
	private JTabbedPane tabbedPane;
	private CameraGui cameraGui;
	private LogGui logGui;
	private InputGui inputGui;

	public Gui(LogGui logGui, CameraGui cameraGui, InputGui inputGui) {
		this.logGui = logGui;
		this.cameraGui = cameraGui;
		this.inputGui = inputGui;
		initializeComponents();
		addComponents();
	}

	private void initializeComponents() {
		cameraContentPane = new JPanel(new GridBagLayout());
		cameraContentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		imageContentPane = new JPanel(new GridBagLayout());
		imageContentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		tabbedPane = new JTabbedPane();
		tabbedPane.add("Camera", cameraContentPane);
		tabbedPane.add("Image", imageContentPane);
		
		appFrame = new JFrame("HAR-VIC");
		appFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		appFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
		appFrame.setContentPane(tabbedPane);

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
		cameraContentPane.add(cameraGui.getContentPane(), constraints);
		constraints.gridx = 3;
		constraints.gridwidth = 2;
		constraints.weightx = 1;
		cameraContentPane.add(logGui.getContentPane(), constraints);
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.weighty = 1;
		imageContentPane.add(inputGui.getContentPane(), constraints);
	}

	public void show() {
		appFrame.setVisible(true);
	}

	public void addCameraPanel(CameraPanel cameraPanel) {
		cameraGui.addCameraPanel(cameraPanel);
	}
	
	public void addInputPanel(InputPanel inputPanel) {
		inputGui.addInputPanel(inputPanel);
	}
}

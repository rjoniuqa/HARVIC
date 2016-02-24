package org.cehci.harvic.gui;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import org.cehci.harvic.gui.event.Actions;
import org.cehci.harvic.gui.log.LogGui;
import org.cehci.harvic.module.CameraModule;
import org.cehci.harvic.module.ImageModule;

public class Gui {

	private JFrame appFrame;
	private JTabbedPane tabbedPane;

	public static class Builder {
		private List<Tab> tabs;

		public Builder() {
			tabs = new ArrayList<>();
		}

		public Builder createCameraTab(Collection<CameraModule> cameras) {
			LogGui logGui = new LogGui();
			CameraTab cameraTab = new CameraTab(logGui);
			for (CameraModule camera : cameras) {
				InputFeedPanel newCameraPanel = new InputFeedPanel(camera.getCameraName(), camera.getCameraId());
				camera.attachObserver(newCameraPanel);
				cameraTab.addFeedPanel(newCameraPanel);
			}
			tabs.add(cameraTab);
			return this;
		}

		public Builder createDirectoryTab(ImageModule imageModule) {
			InputFeedPanel inputFeedPanel = new InputFeedPanel("", "1");
			inputFeedPanel.setActionButtonAction(Actions.SELECT_DIRECTORY);
			imageModule.attachObserver(inputFeedPanel);
			DirectoryTab directoryTab = new DirectoryTab(inputFeedPanel);
			tabs.add(directoryTab);
			return this;
		}

		public Gui build() {
			return new Gui(this);
		}
	}

	private Gui(Builder builder) {
		initializeComponents();
		for (Tab tab : builder.tabs) {
			addTab(tab.getTabPanel(), tab.getTitle());
		}
	}

	private void initializeComponents() {
		tabbedPane = new JTabbedPane();
		appFrame = new JFrame("HAR-VIC");
		appFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		appFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
		appFrame.setContentPane(tabbedPane);
	}

	public void show() {
		appFrame.setVisible(true);
	}

	public void addTab(JPanel panel, String title) {
		tabbedPane.add(title, panel);
	}
}

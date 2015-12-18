package org.cehci.harvic.gui.camera;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.cehci.harvic.gui.camera.event.Actions;

public class CameraGui {

	private List<CameraPanel> cameraPanels = new ArrayList<CameraPanel>();
	private JPanel contentPane;
	private JPanel subContentPane;
	private JScrollPane scrollPane;
	private static final JPanel DUMMY_RIGHT = new JPanel(), DUMMY_BOTTOM = new JPanel();
	public CameraGui() {
		initializeComponents();
	}

	private void initializeComponents() {
		subContentPane = new JPanel(new GridBagLayout());
		scrollPane = new JScrollPane(subContentPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(new Dimension(725, 400)));
		scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		contentPane = new JPanel(new GridBagLayout());
		contentPane.setMinimumSize(new Dimension(725, 400));
		contentPane.setBorder(BorderFactory.createTitledBorder("Cameras"));
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		contentPane.add(scrollPane, constraints);
	}

	public void addCameraPanel(CameraPanel cameraPanel) {
		cameraPanels.add(cameraPanel);
		cameraPanel.setCameraButtonAction(Actions.OPEN_CAMERA);

		int nextX = (cameraPanels.size() - 1) % 2;
		int nextY = (cameraPanels.size() - 1) / 2;
		
		subContentPane.remove(DUMMY_BOTTOM);
		subContentPane.remove(DUMMY_RIGHT);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = 1;
		constraints.gridx = nextX;
		constraints.gridy = nextY;
		constraints.anchor = GridBagConstraints.CENTER;
		subContentPane.add(cameraPanel, constraints);
		
		if (nextX == 0) {
			constraints.gridx = 1;
			subContentPane.add(DUMMY_RIGHT, constraints);
		}
		constraints.gridx = 0;
		constraints.gridwidth = 2;
		constraints.gridy = nextY + 1;
		constraints.weighty = 1;
		subContentPane.add(DUMMY_BOTTOM, constraints);
	}

	public JPanel getContentPane() {
		return contentPane;
	}

	public Dimension getMinimumSize() {
		return contentPane.getMinimumSize();
	}
}

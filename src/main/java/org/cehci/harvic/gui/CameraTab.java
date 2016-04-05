package org.cehci.harvic.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.cehci.harvic.gui.event.Actions;
import org.cehci.harvic.gui.log.LogGui;

public class CameraTab implements Tab {

	private LogGui logGui;
	private JPanel tabPanel;

	private List<InputFeedPanel> inputFeedPanels = new ArrayList<InputFeedPanel>();
	private JPanel mainContentPanel;
	private JPanel subContentPanel;
	private JScrollPane scrollPane;
	private static final JPanel DUMMY_RIGHT = new JPanel(),
		DUMMY_BOTTOM = new JPanel();

	public CameraTab(LogGui logGui) {
		this.logGui = logGui;
		initializeComponents();
		layoutComponents();
	}

	public void initializeComponents() {
		tabPanel = new JPanel(new GridBagLayout());
		tabPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		subContentPanel = new JPanel(new GridBagLayout());
		scrollPane = new JScrollPane(subContentPanel,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(new Dimension(725, 400)));
		scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		mainContentPanel = new JPanel(new GridBagLayout());
		mainContentPanel.setMinimumSize(new Dimension(725, 400));
		mainContentPanel.setBorder(BorderFactory.createTitledBorder("Cameras"));

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		mainContentPanel.add(scrollPane, constraints);
	}

	private void layoutComponents() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridwidth = 3;
		constraints.weightx = 1;
		constraints.weighty = 1;
		tabPanel.add(mainContentPanel, constraints);
		constraints.gridx = 3;
		constraints.gridwidth = 2;
		constraints.weightx = 1;
		tabPanel.add(logGui.getContentPane(), constraints);
	}

	public void addFeedPanel(InputFeedPanel inputFeedPanel) {
		inputFeedPanel.setActionButtonAction(Actions.OPEN_CAMERA);
		inputFeedPanels.add(inputFeedPanel);

		int nextX = (inputFeedPanels.size() - 1) % 2;
		int nextY = (inputFeedPanels.size() - 1) / 2;

		subContentPanel.remove(DUMMY_BOTTOM);
		subContentPanel.remove(DUMMY_RIGHT);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = 1;
		constraints.gridx = nextX;
		constraints.gridy = nextY;
		constraints.anchor = GridBagConstraints.CENTER;
		subContentPanel.add(inputFeedPanel, constraints);

		if (nextX == 0) {
			constraints.gridx = 1;
			subContentPanel.add(DUMMY_RIGHT, constraints);
		}
		constraints.gridx = 0;
		constraints.gridwidth = 2;
		constraints.gridy = nextY + 1;
		constraints.weighty = 1;
		subContentPanel.add(DUMMY_BOTTOM, constraints);
	}

	public JPanel getTabPanel() {
		return tabPanel;
	}

	public String getTitle() {
		return "Camera sources";
	}
}

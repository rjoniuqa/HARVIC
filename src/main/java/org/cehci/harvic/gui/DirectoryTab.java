package org.cehci.harvic.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class DirectoryTab implements Tab {

	private JPanel directorySourceContentPane;
	private InputFeedPanel inputFeedPanel;

	public DirectoryTab(InputFeedPanel inputFeedPanel) {
		this.inputFeedPanel = inputFeedPanel;
		initializeComponents();
		layoutComponents();
	}

	public void initializeComponents() {
		directorySourceContentPane = new JPanel(new GridBagLayout());
		directorySourceContentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	public void layoutComponents() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.weighty = 1;
		directorySourceContentPane.add(inputFeedPanel, constraints);
	}

	@Override
	public String getTitle() {
		return "Directory source";
	}

	@Override
	public JPanel getTabPanel() {
		return directorySourceContentPane;
	}

}

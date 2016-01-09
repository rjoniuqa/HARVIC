package dev.cehci.harvic.gui.input;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import dev.cehci.harvic.gui.input.event.SelectDirectoryAction;

public class InputGui {
	
	private JPanel contentPane;
	
	public InputGui(){
		initializeComponents();
	}

	private void initializeComponents() {
		// TODO Auto-generated method stub
		contentPane = new JPanel(new GridBagLayout());
		contentPane.setMinimumSize(new Dimension(725, 400));
	}
	
	public void addInputPanel(InputPanel inputPanel) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = 1;
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.anchor = GridBagConstraints.CENTER;
		contentPane.add(inputPanel, constraints);
		inputPanel.setOpenButtonAction(new SelectDirectoryAction());
	}
	
	public JPanel getContentPane(){
		return contentPane;
	}
	
	public Dimension getMinimumSize() {
		return contentPane.getMinimumSize();
	}
	
	
	
}

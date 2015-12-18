package org.cehci.harvic.gui.log;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class LogGui {

	private JPanel contentPane;

	public LogGui() {
		initializeComponents();
	}

	private void initializeComponents() {
		contentPane = new JPanel();
		contentPane = new JPanel();
		contentPane.setMinimumSize(new Dimension(300, 400));
		contentPane.setBorder(BorderFactory.createTitledBorder("System Log"));
	}

	public JPanel getContentPane() {
		return contentPane;
	}
	
	public Dimension getMinimumSize(){
		return contentPane.getMinimumSize();
	}
}

package org.cehci.harvic.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.lang.reflect.InvocationTargetException;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.cehci.harvic.PropertyChangeObserver;
import org.cehci.harvic.gui.event.Actions;

public class InputFeedPanel extends JPanel implements PropertyChangeObserver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2896655979540081068L;
	private JLabel inputNameLabel;
	private JLabel inputFeedImage;
	private JButton actionButton;

	public InputFeedPanel(String inputSourceId) {
		super(new GridBagLayout());
		setPreferredSize(new Dimension(325, 325));
		initializeComponents(inputSourceId);
		addComponents();
	}

	private void initializeComponents(String inputSourceId) {
		inputNameLabel = new JLabel(inputSourceId);
		inputNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		inputFeedImage = new JLabel();
		inputFeedImage.setPreferredSize(new Dimension(325, 240));
		inputFeedImage.setBackground(Color.black);
		inputFeedImage.setOpaque(true);
		actionButton = new JButton(Actions.OPEN_CAMERA);
		actionButton.setName(inputSourceId);
	}

	private void addComponents() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(0, 0, 10, 0);
		add(inputNameLabel, constraints);
		constraints.gridy = 1;
		add(inputFeedImage, constraints);
		constraints.gridy = 2;
		constraints.insets = new Insets(0, 0, 0, 0);
		add(actionButton, constraints);
	}

	private void updateFeed(ImageIcon newFrame)
		throws InvocationTargetException, InterruptedException {
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				inputFeedImage.setIcon(newFrame);
			}
		});
	}

	public void setActionButtonAction(Action actionButtonAction) {
		actionButton.setAction(actionButtonAction);
	}

	@Override
	public void onPropertyChange(String property, Object oldValue,
		Object newValue) {
		if (property.equals("frame")) {
			ImageIcon newFrame = new ImageIcon((Image) newValue);
			try {
				updateFeed(newFrame);
			} catch (InvocationTargetException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

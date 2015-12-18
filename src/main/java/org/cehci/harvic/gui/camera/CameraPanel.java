package org.cehci.harvic.gui.camera;

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
import org.cehci.harvic.gui.camera.event.Actions;

public class CameraPanel extends JPanel implements PropertyChangeObserver{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2896655979540081068L;
	private JLabel cameraNameLabel;
	private JLabel cameraIndexLabel;
	private JLabel cameraFeed;
	private JButton powerButton;
	
	public CameraPanel(String cameraName, String cameraId, int cameraIndex){
		super(new GridBagLayout());
		setPreferredSize(new Dimension(325, 325));
		initializeComponents(cameraName, cameraId, cameraIndex);
		addComponents();
	}
	
	private void initializeComponents(String cameraName, String cameraId, int cameraIndex){
		cameraNameLabel = new JLabel(cameraName);
		cameraNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		cameraIndexLabel = new JLabel("Camera #" + cameraIndex);
		cameraIndexLabel.setHorizontalAlignment(SwingConstants.CENTER);
		cameraFeed = new JLabel();
		cameraFeed.setPreferredSize(new Dimension(325, 240));
		cameraFeed.setBackground(Color.black);
		cameraFeed.setOpaque(true);
		powerButton = new JButton(Actions.OPEN_CAMERA);
		powerButton.setName(cameraId);
	}
	
	private void addComponents(){
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(0, 0, 10, 0);
		add(cameraNameLabel, constraints);
		constraints.gridy = 1;
		add(cameraFeed, constraints);
		constraints.gridy = 2;
		add(cameraIndexLabel, constraints);
		constraints.gridy = 3;
		constraints.insets = new Insets(0, 0, 0, 0);
		add(powerButton, constraints);
	}
	
	private void updateFeed(ImageIcon newFrame) throws InvocationTargetException, InterruptedException{
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				cameraFeed.setIcon(newFrame);
			}
		});
	}
	
	public JLabel getFeedLabel(){
		return cameraFeed;
	}
	
	public void setCameraButtonAction(Action cameraButtonAction){
		powerButton.setAction(cameraButtonAction);
	}

	@Override
	public void onPropertyChange(String property, Object oldValue, Object newValue) {
		if(property.equals("frame")){
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

package dev.cehci.harvic.gui.input;

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
import javax.swing.SwingUtilities;

import org.cehci.harvic.PropertyChangeObserver;

public class InputPanel extends JPanel implements PropertyChangeObserver {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4939916124294917644L;
	private JLabel inputFeed;
	private JButton openButton;
	
	public InputPanel(){
		super(new GridBagLayout());
		setPreferredSize(new Dimension(325, 325));
		initializeComponents();
		addComponents();
	}
	
	private void initializeComponents() {
		// TODO Auto-generated method stub
		inputFeed = new JLabel();
		inputFeed.setPreferredSize(new Dimension(325, 240));
		inputFeed.setBackground(Color.black);
		inputFeed.setOpaque(true);
		openButton = new JButton("open");
	}
	
	private void addComponents() {
		// TODO Auto-generated method stub
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(0, 0, 10, 0);
		add(inputFeed, constraints);
		constraints.gridy = 1;
		constraints.insets = new Insets(0, 0, 0, 0);
		add(openButton, constraints);
	}
	
	private void updateFeed(ImageIcon newFrame) throws InvocationTargetException, InterruptedException{
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				inputFeed.setIcon(newFrame);
			}
		});
	}
	
	public void setOpenButtonAction(Action openButtonAction){
		openButton.setAction(openButtonAction);
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

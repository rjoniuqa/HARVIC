package dev.cehci.harvic.gui.input.event;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import dev.cehci.harvic.module.input.ImageManager;

public class SelectDirectoryAction extends AbstractAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -365524743020669457L;
	
	public SelectDirectoryAction() {
        putValue(NAME, "Open Directory"); // bounds properties
        putValue(SHORT_DESCRIPTION, "Select Directory");
    }
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Select Directory");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		//
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			ImageManager.getInstance().open(chooser.getSelectedFile());
		} else {
			System.out.println("No Selection ");
		}
	}
	
	
	
}

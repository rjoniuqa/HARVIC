package org.cehci.harvic.gui.event;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFileChooser;

import org.cehci.harvic.module.detection.ImageManager;

public class SelectDirectoryAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -365524743020669457L;

	public SelectDirectoryAction(String arg0, Icon arg1) {
		super(arg0, arg1);
	}

	public SelectDirectoryAction(String arg0) {
		super(arg0);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Select Directory");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			ImageManager.getInstance().open(chooser.getSelectedFile());
		}
	}

}

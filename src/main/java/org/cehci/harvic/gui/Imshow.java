package org.cehci.harvic.gui;

import static org.opencv.imgcodecs.Imgcodecs.imencode;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.cehci.harvic.PropertyChangeObserver;

public class Imshow extends JFrame implements PropertyChangeObserver {
	/**
	 * 
	 */
	private static final long serialVersionUID = -556911798653655197L;
	private BufferedImage image;

	public Imshow(String title) {
		super(title);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (image != null) {
			g.drawImage(image, 0, 0, null);
		}
	}

	public BufferedImage matToBufferedImage(Mat image) throws IOException {
		MatOfByte imageMat = new MatOfByte();
		imencode(".png", image, imageMat);
		byte[] imageBytes = imageMat.toArray();
		BufferedImage bufferedImage = null;

		InputStream in = new ByteArrayInputStream(imageBytes);
		bufferedImage = ImageIO.read(in);
		return bufferedImage;
	}

	@Override
	public void onPropertyChange(String property, Object oldValue,
		Object newValue) {
		// TODO Auto-generated method stub
		image = (BufferedImage) newValue;
		repaint();
		if (!isVisible()) {
			setVisible(true);
			setSize(image.getWidth(), image.getHeight());
		}
	}
}
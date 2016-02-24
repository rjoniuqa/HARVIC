package org.cehci.harvic.module.camera;

import java.io.File;
import java.io.IOException;

import org.cehci.harvic.module.ImageModule;
import org.cehci.harvic.module.camera.input.DirectorySource;

public class ImageManager {

	private ImageModule imageModule;
	private static ImageManager imageManager;

	private ImageManager() {
		imageModule = new ImageModule(new DirectorySource(), new ContourPersonDetector());
	}

	public static ImageManager getInstance() {
		if (imageManager == null) {
			imageManager = new ImageManager();
		}
		return imageManager;
	}

	public void open(File directory) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					imageModule.capture(directory);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}).start();
	}

	public ImageModule getImageModule() {
		return imageModule;
	}
}
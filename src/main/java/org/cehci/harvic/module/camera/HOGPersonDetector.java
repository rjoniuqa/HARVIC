package org.cehci.harvic.module.camera;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.CvFileStorage;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.FileNode;
import org.bytedeco.javacpp.opencv_core.FileStorage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_ml.SVM;
import org.bytedeco.javacpp.opencv_objdetect.HOGDescriptor;

public class HOGPersonDetector implements PersonDetector {

	private HOGDescriptor detector;

	public HOGPersonDetector() {
		SVM svm = SVM.create();
//		FileStorage fs = new FileStorage();
//		fs.open("svm_persondetect.xml", FileStorage.READ);
		CvFileStorage fs = CvFileStorage.open("svm_persondetect.xml", null, FileStorage.READ);
		FileNode filenode = new FileNode(fs);
		svm.read(filenode);
		detector = new HOGDescriptor();
		// detector = new HOGDescriptor(new Size(64, 128), new Size(8, 8), new
		// Size(8, 8), new Size(4, 4), 9);
//		detector.load("svm_persondetect.xml");
		detector.setSVMDetector(svm.getSupportVectors());
		// detector.setSVMDetector(HOGDescriptor.getDaimlerPeopleDetector());
		// detector.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
	}

	public RectVector detect(Mat frame) {
		RectVector detectedPersons = new RectVector();
		detector.detectMultiScale(frame, detectedPersons, new DoublePointer(), 0, new Size(8, 8), new Size(0, 0), 1.0,
				0.9, true);
		return detectedPersons;
	}
}

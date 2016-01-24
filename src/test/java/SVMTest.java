
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.ml.SVM;
import org.opencv.objdetect.HOGDescriptor;

public class SVMTest {

	static {
		System.out.println("load");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {
		new SVMTest().test();
	}

	public void test() {
		System.out.println("test");

		// HOGDescriptor.getDefaultPeopleDetector();
		// FileStorage fs = new FileStorage();
		// fs.open("svm_persondetect.xml", FileStorage.READ);
		// fs.getFirstTopLevelNode();
		SVM svm = SVM.load("svm_persondetect.xml");
		HOGDescriptor descriptor = new HOGDescriptor();
		descriptor.setSVMDetector(svm.getSupportVectors());
	}
	
	public void getPrimalForm(SVM svm){
		
//		int svCount = svm.getSupportVectors().rows();
//		Mat sv = svm.getSupportVectors();
//		svm.getDeci
//		int varCount = svm.getVarCount();
//		
//		sv.reshape(varCount, 0);
//		
//		int sv_count = get_support_vector_count();
//	    const CvSVMDecisionFunc* df = decision_func;
//	    const double* alphas = df[0].alpha;
//	    double rho = df[0].rho;
//	    int var_count = get_var_count();
//	    support_vector.resize(var_count, 0);
//	    for (unsigned int r = 0; r < (unsigned)sv_count; r++) {
//	      float myalpha = alphas[r];
//	      const float* v = get_support_vector(r);
//	      for (int j = 0; j < var_count; j++,v++) {
//	        support_vector[j] += (-myalpha) * (*v);
//	      }
//	    }
//	    support_vector.push_back(rho);
	}
}

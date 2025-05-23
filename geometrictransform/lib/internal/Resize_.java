import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog; /**
*/
public class Resize_ {

	public Resize_() {
	
		ImagePlus imp = WindowManager.getCurrentImage();
		if (imp == null) {
			IJ.showMessage("No open image.");
			return;
		}
		if (imp.getType() != ImagePlus.GRAY8 && imp.getType() != ImagePlus.GRAY32) {
			IJ.showMessage("Only process the 8-bit or 32-bit image");
			return;
		}
		int nx = imp.getWidth();
		int ny = imp.getHeight();
			
		ImageAccess input = new ImageAccess(imp.getProcessor());
		String list[] = {"Nearest-Neighbor", "Bilinear", "Cubic Spline"};
		GenericDialog gd = new GenericDialog("Resize");
		gd.addChoice("Interpolation", list, "Linear");
		gd.addNumericField("Size in X", 400, 0);
		gd.addNumericField("Size in Y", 250, 0);
		gd.showDialog();
		if (gd.wasCanceled())
			return;
		String interpolation = gd.getNextChoice();
		int mx = (int)gd.getNextNumber();
		int my = (int)gd.getNextNumber();
		
		ImageAccess output = CodeClass.resize(input, mx, my, interpolation);
		output.show("Resize [" + interpolation + "]");
	}
}

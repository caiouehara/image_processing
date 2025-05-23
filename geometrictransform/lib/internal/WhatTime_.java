import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.process.ImageProcessor;

import java.awt.*; /**
*/
public class WhatTime_ {

	public WhatTime_() {
	
		ImagePlus imp = WindowManager.getCurrentImage();
		if (imp == null) {
			IJ.showMessage("No open image.");
			return;
		}
		if (imp.getType() != ImagePlus.GRAY8 && imp.getType() != ImagePlus.GRAY32) {
			IJ.showMessage("Only process the 8-bit or 32-bit image");
			return;
		}
		ImageAccess image = new ImageAccess(imp.getProcessor());

		String s = CodeClass.whatTime(image);
		ImageProcessor ip = imp.getProcessor();
		ip.setColor(Color.white);
		ip.setFont(new Font("Courier", Font.BOLD, 24));
		ip.drawString(s, 10, 30);
		imp.updateAndDraw();
		
		
	}
}

public class PointwiseTransform extends Object {

	/**
	* Question 2.1 Contrast reversal
	*/
	static public ImageAccess inverse(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		ImageAccess output = new ImageAccess(nx, ny);
		double value = 0.0;
		for (int x=0; x<nx; x++) {
			for (int y = 0; y < ny; y++) {
				value = input.getPixel(x, y);
				value = 255 - value;
				output.putPixel(x, y, value);
			}
		}
		return output;
	}

	/**
	* Question 2.2 Stretch normalized constrast
	*/
	static public ImageAccess rescale(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		double max = input.getMaximum();
		double min = input.getMinimum();

		ImageAccess output = new ImageAccess(nx, ny);

		double alpha = 255.0 / (max - min);
		double beta = min;

		for (int y = 0; y < ny; y++) {
			for (int x = 0; x < nx; x++) {
				double fxy = input.getPixel(x, y);
				double gxy = alpha * (fxy - beta);
				output.putPixel(x, y, gxy);
			}
		}
		return output;
	}

	/**
	* Question 2.3 Saturate an image
	*/
	static public ImageAccess saturate(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		ImageAccess output = new ImageAccess(nx, ny);

		for (int y = 0; y < ny; y++) {
			for (int x = 0; x < nx; x++) {
				double value = input.getPixel(x, y);
				if (value > 10000) {
					output.putPixel(x, y, 10000);
				} else {
					output.putPixel(x, y, value);
				}
			}
		}

		return rescale(output);
	}

	/**
	 * Question 4.1 Maximum Intensity Projection
	 */
	static public ImageAccess zprojectMaximum(ImageAccess[] zstack) {
		int nx = zstack[0].getWidth();
		int ny = zstack[0].getHeight();
		int nz = zstack.length;
		ImageAccess output = new ImageAccess(nx, ny);

		for (int y = 0; y < ny; y++) {
			for (int x = 0; x < nx; x++) {
				double maxValue = Double.NEGATIVE_INFINITY;
				for (int z = 0; z < nz; z++) {
					double pixelValue = zstack[z].getPixel(x, y);
					if (pixelValue > maxValue) {
						maxValue = pixelValue;
					}
				}
				output.putPixel(x, y, maxValue);
			}
		}
		return output;
	}

	/**
	 * Question 4.2 Z-stack Mean
	 */
	static public ImageAccess zprojectMean(ImageAccess[] zstack) {
		int nx = zstack[0].getWidth();
		int ny = zstack[0].getHeight();
		int nz = zstack.length;
		ImageAccess output = new ImageAccess(nx, ny);

		for (int y = 0; y < ny; y++) {
			for (int x = 0; x < nx; x++) {
				double sum = 0;
				for (int z = 0; z < nz; z++) {
					sum += zstack[z].getPixel(x, y);
				}
				double meanValue = sum / nz;
				output.putPixel(x, y, meanValue);
			}
		}
		return output;
	}
}
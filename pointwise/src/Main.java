import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

public class Main {
    public static void main(String[] args) {
        ImagePlus image = IJ.openImage("./data/microtubules.tif", 1);
        if (image != null) {
            ImageProcessor ip = image.getProcessor();
            ImageAccess ass = new ImageAccess(ip);
            PointwiseTransform.inverse(ass);
            image.show("");
            ass.show("");
        }
    }
}

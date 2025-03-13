import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import ij.process.ImageProcessor;

public class Main {
    public static void main(String[] args) {
        Main.q3();
    }
    public static void q3() {
        String[] imageFiles = {
                "./data/yeast1.tif",
                "./data/yeast2.tif",
                "./data/yeast3.tif",
                "./data/yeast4.tif"
        };

        for (String file : imageFiles) {
            ImagePlus imp = IJ.openImage(file);
            if (imp == null) {
                IJ.log("Não foi possível abrir a imagem: " + file);
                continue;
            }

            imp.setTitle("Original - " + file);
            imp.show();
            
            IJ.run(imp, "Make Binary", "");

            ResultsTable rt = new ResultsTable();
            int options = ParticleAnalyzer.SHOW_NONE;
            int measurements = 0;
            ParticleAnalyzer pa = new ParticleAnalyzer(options, measurements, rt, 0, Double.POSITIVE_INFINITY);

            if (pa.analyze(imp)) {
                int numParticles = rt.getCounter();
                IJ.log("Imagem: " + file + " - Número de partículas detectadas: " + numParticles);
            } else {
                IJ.log("Falha na análise de partículas para a imagem: " + file);
            }

            rt.show("Resultados - " + file);
        }
    }

    public static void q4(){
        ImagePlus image = IJ.openImage("./data/yeast_stack.tif");
        if (image != null) {
            ImageStack stack = image.getStack();
            int nSlices = stack.getSize();
            ImageAccess[] zstack = new ImageAccess[nSlices];
            for (int i = 0; i < nSlices; i++) {
                ImageProcessor ip = stack.getProcessor(i + 1);
                zstack[i] = new ImageAccess(ip);
            }

            ImageAccess maxProjection = PointwiseTransform.zprojectMaximum(zstack);
            maxProjection.show("Projeção Máxima");

            ImageAccess meanProjection = PointwiseTransform.zprojectMean(zstack);
            meanProjection.show("Projeção Média");
        }
    }
}

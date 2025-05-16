
import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

public class FrequencyFiltersRunner {

    public static void main(String[] args) {
        // Nomes dos arquivos a serem processados (ajuste os caminhos conforme necessário)
        String[] filenames = {"fourier.tif", "lena.tif"};
        // Frequências de corte (em radianos)
        double[] cutoffFrequencies = { Math.PI / 2, Math.PI / 4, Math.PI / 8, Math.PI / 16 };
        // Ordem para os filtros Butterworth
        int butterOrder = 2;

        // Processa cada imagem
        for (String filename : filenames) {
            ImagePlus imp = IJ.openImage(filename);
            if (imp == null) {
                System.out.println("Erro ao abrir a imagem: " + filename);
                continue;
            }
            imp.setTitle(filename + " - Original");
            imp.show();
            System.out.println("Processando imagem: " + filename);

            // Cria objeto ImageAccess a partir do ImageProcessor da imagem
            ImageAccess original = new ImageAccess(imp.getProcessor());

            // Para cada frequência de corte, aplica os filtros
            for (double D0 : cutoffFrequencies) {
                System.out.println("Aplicando filtros com D0 = " + D0);

                // Passa-baixa ideal
                IdealLowPass_ idealLP = new IdealLowPass_(D0);
                ImageAccess resIdealLP = idealLP.filter(original.duplicate());
                displayAndPrintStats(resIdealLP, filename + " - Ideal Low Pass, D0=" + D0);

                // Passa-baixa Butterworth
                ButterLowPass_ butterLP = new ButterLowPass_(D0, butterOrder);
                ImageAccess resButterLP = butterLP.filter(original.duplicate());
                displayAndPrintStats(resButterLP, filename + " - Butterworth Low Pass, D0=" + D0);

                // Passa-alta ideal
                IdealHighPass_ idealHP = new IdealHighPass_(D0);
                ImageAccess resIdealHP = idealHP.filter(original.duplicate());
                displayAndPrintStats(resIdealHP, filename + " - Ideal High Pass, D0=" + D0);

                // Passa-alta Butterworth
                ButterHighPass_ butterHP = new ButterHighPass_(D0, butterOrder);
                ImageAccess resButterHP = butterHP.filter(original.duplicate());
                displayAndPrintStats(resButterHP, filename + " - Butterworth High Pass, D0=" + D0);
            }
        }
    }

    /**
     * Converte o resultado (ImageAccess) para um ImageProcessor, exibe a imagem
     * e imprime no console a média e o desvio padrão dos pixels.
     */
    private static void displayAndPrintStats(ImageAccess iaResult, String title) {
        // Converte para ImageProcessor (por exemplo, FloatProcessor)
        ImageProcessor ipResult = iaResult.createFloatProcessor();
        ImagePlus impResult = new ImagePlus(title, ipResult);
        impResult.show();

        // Calcula a média e o desvio padrão
        int width = ipResult.getWidth();
        int height = ipResult.getHeight();
        double sum = 0;
        double sumSq = 0;
        int n = width * height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double val = ipResult.getPixelValue(x, y);
                sum += val;
                sumSq += val * val;
            }
        }
        double mean = sum / n;
        double variance = (sumSq / n) - (mean * mean);
        double stdDev = Math.sqrt(variance);

        System.out.println(title + " - Média: " + mean + ", Desvio Padrão: " + stdDev);
    }
}

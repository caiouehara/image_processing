import ij.*;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        q2();
    }

    public static void q2(){
        // 2.1 – Observação da FFT direta em imagens dos conjuntos N e T.
        // Exemplo: utilizando a imagem "cat.tif" do conjunto N.
        ImagePlus impCat = IJ.openImage("./data/cat.tif");
        impCat.show("Imagem Original - Cat");

        // Cria os objetos ImageAccess para a parte real e imaginária
        ImageAccess real = new ImageAccess(impCat.getProcessor());
        ImageAccess imaginary = new ImageAccess(impCat.getWidth(), impCat.getHeight());
        // (imaginary já é inicializado com zeros)

        // Aplica a FFT direta (FFTDirect)
        FFT.doFFT(real, imaginary);

        // 2.2 – Inversa: reconstrua a imagem a partir dos coeficientes FFT
        // Cria cópias dos coeficientes para aplicar a FFT inversa
        ImageAccess realFFT = real.duplicate();
        ImageAccess imagFFT = imaginary.duplicate();

        // Aplica a FFT inversa (FFTInverse)
        FFT.inverseFFT(realFFT, imagFFT);
        ImageAccess reconImage = realFFT; // a parte real contém a imagem reconstruída
        reconImage.show("Imagem Reconstruída - Cat");

        // Calcula o máximo da diferença absoluta entre a imagem original e a reconstruída
        ImageAccess origAccess = new ImageAccess(impCat.getProcessor());
        double maxDiff = computeMaxDifference(origAccess, reconImage);
        IJ.log("Máxima diferença absoluta: " + maxDiff);
    }
    public static void q3(){
        // 2.1 – Observação da FFT direta em imagens dos conjuntos N e T.
        // Exemplo: utilizando a imagem "cat.tif" do conjunto N.
        ImagePlus impCat = IJ.openImage("./data/cat.tif");
        impCat.show("Imagem Original - Cat");

        // 2.3 – Importância da fase:
        // Exemplo: utilizar a magnitude de "cat.tif" e a fase de "lena.tif"
        ImagePlus impLena = IJ.openImage("./data/lena.tif");
        impLena.show("Imagem Original - Lena");

        // Cria ImageAccess para as duas imagens
        ImageAccess realCat = new ImageAccess(impCat.getProcessor());
        ImageAccess imagCat = new ImageAccess(impCat.getWidth(), impCat.getHeight());
        ImageAccess realLena = new ImageAccess(impLena.getProcessor());
        ImageAccess imagLena = new ImageAccess(impLena.getWidth(), impLena.getHeight());

        // Aplica FFT para ambas
        FFT.doFFT(realCat, imagCat);
        FFT.doFFT(realLena, imagLena);

        // Converte para representação polar: real passa a ser magnitude e imaginary passa a ser fase
        FFT.convertCartesianToPolar(realCat, imagCat);  // para cat.tif
        FFT.convertCartesianToPolar(realLena, imagLena);  // para lena.tif

        // Cria nova combinação: magnitude de cat e fase de lena
        ImageAccess combMag = realCat.duplicate();
        ImageAccess combPhase = imagLena.duplicate();

        // Converte de volta para o sistema retangular
        FFT.convertPolarToCartesian(combMag, combPhase);

        // Aplica FFT inversa para reconstruir a imagem com a combinação
        FFT.inverseFFT(combMag, combPhase);
        combMag.show("Reconstruída: Magnitude de Cat e Fase de Lena");

        // 2.4 e 2.5 – Reconstrução progressiva:
        // Aqui seria implementado o processo de reconstrução progressiva (ex.: selecionando coeficientes de baixa frequência,
        // 36 coeficientes, 5000 coeficientes, etc.) e comparando as diferentes ordens de seleção (baixa frequência, média, coeficientes
        // menores/maiores, randômico). Como o plugin FourierProgressiveReconstruction não está disponível, apenas registramos a intenção.
        IJ.log("Tarefas de reconstrução progressiva (2.4 e 2.5) não foram implementadas nesta demo.");
    }

    // Método auxiliar para calcular a imagem de magnitude a partir dos coeficientes FFT
    private static ImageAccess computeMagnitude(ImageAccess real, ImageAccess imaginary) {
        int width = real.getWidth();
        int height = real.getHeight();
        ImageAccess mag = new ImageAccess(width, height);
        double[] rowReal = new double[width];
        double[] rowImag = new double[width];
        double[] rowMag = new double[width];

        for (int y = 0; y < height; y++) {
            real.getRow(y, rowReal);
            imaginary.getRow(y, rowImag);
            for (int x = 0; x < width; x++) {
                rowMag[x] = Math.sqrt(rowReal[x] * rowReal[x] + rowImag[x] * rowImag[x]);
            }
            mag.putRow(y, rowMag);
        }
        return mag;
    }

    // Método auxiliar para calcular a máxima diferença absoluta entre duas imagens
    private static double computeMaxDifference(ImageAccess orig, ImageAccess recon) {
        int width = orig.getWidth();
        int height = orig.getHeight();
        double maxDiff = 0;
        double[] rowOrig = new double[width];
        double[] rowRecon = new double[width];

        for (int y = 0; y < height; y++) {
            orig.getRow(y, rowOrig);
            recon.getRow(y, rowRecon);
            for (int x = 0; x < width; x++) {
                double diff = Math.abs(rowOrig[x] - rowRecon[x]);
                if (diff > maxDiff) {
                    maxDiff = diff;
                }
            }
        }
        return maxDiff;
    }
}
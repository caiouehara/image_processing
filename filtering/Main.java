import ij.*;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Main.q3_2();
    }

    public static void q3_2()
        {
            String path = IJ.getFilePath("Selecione o arquivo dendrite.tif");
            if (path == null) {
                IJ.showMessage("Nenhum arquivo selecionado.");
                return;
            }
            ImagePlus image = IJ.openImage(path);
            if (image == null) {
                IJ.showMessage("Erro ao abrir dendrite.tif.");
                return;
            }
            image.show();

            ImageProcessor ip = image.getProcessor();
            ImageAccess original = new ImageAccess(ip);
            ImageAccess background = FilteringSession.doMovingAverage5_Recursive(original);

            int width = original.getWidth();
            int height = original.getHeight();
            ImageAccess diff = original.duplicate(); // cria cópia para os cálculos
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    double value = original.getPixel(x, y) - background.getPixel(x, y);
                    diff.putPixel(x, y, value);
                }
            }

            diff.normalizeContrast();

            double T = IJ.getNumber("Digite o valor do limiar para extrair o dendrito:", 50);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    double value = diff.getPixel(x, y);
                    if (value < T) {
                        diff.putPixel(x, y, 0);
                    } else {
                        diff.putPixel(x, y, 255);
                    }
                }
            }

            FloatProcessor processor = diff.createFloatProcessor();
            ImagePlus result = new ImagePlus("Dendrite Extraído", processor);
            result.show();

            IJ.log("Extração do dendrito realizada com: filtro de médias-móveis 5x5 recursivo e limiar T = " + T);
    }

    public static void q3(){
        String path = IJ.getFilePath("Selecione a imagem brain14.tif para segmentação");
        if (path == null) {
            IJ.showMessage("Nenhuma imagem selecionada.");
            return;
        }
        ImagePlus image = IJ.openImage(path);
        if (image == null) {
            IJ.showMessage("Erro ao abrir a imagem.");
            return;
        }
        image.show(); // Exibe a imagem original

        int L = (int) IJ.getNumber("Digite o comprimento da janela (L) para o filtro (recomendado: 5):", 5);
        if(L != 5){
            IJ.showMessage("Essa implementação está fixada para 5x5. L será ajustado para 5.");
            L = 5;
        }

        ImageProcessor ip = image.getProcessor();
        ImageAccess ia = new ImageAccess(ip);  // A classe ImageAccess fornece métodos convenientes :contentReference[oaicite:2]{index=2}&#8203;:contentReference[oaicite:3]{index=3}
        ImageAccess smoothedAccess = FilteringSession.doMovingAverage5_Recursive(ia);
        FloatProcessor smoothedProcessor = smoothedAccess.createFloatProcessor();
        ImagePlus smoothedImage = new ImagePlus("Imagem Suavizada", smoothedProcessor);
        smoothedImage.show();

        IJ.run(smoothedImage, "Threshold...", ""); // Abre a janela de ajuste de limiar

        IJ.log("Segmentação realizada com janela L = " + L + " e limiar T definido via Threshold.");
    }

    public static void q2() {

        // List to store the execution times (in nanoseconds)
        List<Long> times = new ArrayList<>();

        // Run the test 10 times
        for (int i = 0; i < 10; i++) {
            long startTime = System.nanoTime();
            Main.q2_separeble();  // Replace with your actual method call
            long endTime = System.nanoTime();
            times.add(endTime - startTime);
        }

        System.out.println("Separable results");

        // Compute maximum, minimum, and average times in nanoseconds
        long maxTimeNs = Collections.max(times);
        long minTimeNs = Collections.min(times);
        double avgTimeNs = times.stream().mapToLong(Long::longValue).average().orElse(0.0);

        // Convert nanoseconds to seconds (1 second = 1,000,000,000 ns)
        double maxTimeSec = maxTimeNs / 1_000_000_000.0;
        double minTimeSec = minTimeNs / 1_000_000_000.0;
        double avgTimeSec = avgTimeNs / 1_000_000_000.0;

        System.out.println("Maximum time: " + maxTimeSec + " s");
        System.out.println("Average time: " + avgTimeSec + " s");
        System.out.println("Minimum time: " + minTimeSec + " s");


        System.out.println("Non Separable results");
        // List to store the execution times (in nanoseconds)
        times = new ArrayList<>();

        // Run the test 10 times
        for (int i = 0; i < 10; i++) {
            long startTime = System.nanoTime();
            Main.q2_nonseparable();  // Replace with your actual method call
            long endTime = System.nanoTime();
            times.add(endTime - startTime);
        }

        // Compute maximum, minimum, and average times in nanoseconds
        maxTimeNs = Collections.max(times);
        minTimeNs = Collections.min(times);
        avgTimeNs = times.stream().mapToLong(Long::longValue).average().orElse(0.0);

        // Convert nanoseconds to seconds (1 second = 1,000,000,000 ns)
        maxTimeSec = maxTimeNs / 1_000_000_000.0;
        minTimeSec = minTimeNs / 1_000_000_000.0;
        avgTimeSec = avgTimeNs / 1_000_000_000.0;

        System.out.println("Maximum time: " + maxTimeSec + " s");
        System.out.println("Average time: " + avgTimeSec + " s");
        System.out.println("Minimum time: " + minTimeSec + " s");

        System.out.println("Recursive results");
        // List to store the execution times (in nanoseconds)
        times = new ArrayList<>();

        // Run the test 10 times
        for (int i = 0; i < 10; i++) {
            long startTime = System.nanoTime();
            Main.q2_recursive();  // Replace with your actual method call
            long endTime = System.nanoTime();
            times.add(endTime - startTime);
        }

        // Compute maximum, minimum, and average times in nanoseconds
        maxTimeNs = Collections.max(times);
        minTimeNs = Collections.min(times);
        avgTimeNs = times.stream().mapToLong(Long::longValue).average().orElse(0.0);

        // Convert nanoseconds to seconds (1 second = 1,000,000,000 ns)
        maxTimeSec = maxTimeNs / 1_000_000_000.0;
        minTimeSec = minTimeNs / 1_000_000_000.0;
        avgTimeSec = avgTimeNs / 1_000_000_000.0;

        System.out.println("Maximum time: " + maxTimeSec + " s");
        System.out.println("Average time: " + avgTimeSec + " s");
        System.out.println("Minimum time: " + minTimeSec + " s");
    }

    public static void q2_nonseparable(){
        ImagePlus image = IJ.openImage("./data/africa.tif");

        if (image != null) {
            ImageStack stack = image.getStack();
            int nSlices = stack.getSize();
            ImageAccess[] zstack = new ImageAccess[nSlices];
            for (int i = 0; i < nSlices; i++) {
                ImageProcessor ip = stack.getProcessor(i + 1);
                zstack[i] = new ImageAccess(ip);
                ImageAccess transfomed = FilteringSession.doMovingAverage5_NonSeparable(zstack[i]);
                transfomed.show("Avarage Filter");
            }
        }
    }

    public static void q2_separeble(){
        ImagePlus image = IJ.openImage("./data/africa.tif");

        if (image != null) {
            ImageStack stack = image.getStack();
            int nSlices = stack.getSize();
            ImageAccess[] zstack = new ImageAccess[nSlices];
            for (int i = 0; i < nSlices; i++) {
                ImageProcessor ip = stack.getProcessor(i + 1);
                zstack[i] = new ImageAccess(ip);
                ImageAccess transfomed = FilteringSession.doMovingAverage5_Separable(zstack[i]);
                transfomed.show("Avarage Filter");
            }
        }
    }

    public static void q2_recursive(){
        ImagePlus image = IJ.openImage("./data/africa.tif");

        if (image != null) {
            ImageStack stack = image.getStack();
            int nSlices = stack.getSize();
            ImageAccess[] zstack = new ImageAccess[nSlices];
            for (int i = 0; i < nSlices; i++) {
                ImageProcessor ip = stack.getProcessor(i + 1);
                zstack[i] = new ImageAccess(ip);
                ImageAccess transfomed = FilteringSession.doMovingAverage5_Recursive(zstack[i]);
                transfomed.show("Avarage Filter");
            }
        }
    }

    public static void q1() {
        // List to store the execution times (in nanoseconds)
        List<Long> times = new ArrayList<>();

        // Run the test 10 times
        for (int i = 0; i < 10; i++) {
            long startTime = System.nanoTime();
            Main.q1_vertical();  // Replace with your actual method call
            long endTime = System.nanoTime();
            times.add(endTime - startTime);
        }

        System.out.println("Vertical results");

        // Compute maximum, minimum, and average times in nanoseconds
        long maxTimeNs = Collections.max(times);
        long minTimeNs = Collections.min(times);
        double avgTimeNs = times.stream().mapToLong(Long::longValue).average().orElse(0.0);

        // Convert nanoseconds to seconds (1 second = 1,000,000,000 ns)
        double maxTimeSec = maxTimeNs / 1_000_000_000.0;
        double minTimeSec = minTimeNs / 1_000_000_000.0;
        double avgTimeSec = avgTimeNs / 1_000_000_000.0;

        System.out.println("Maximum time: " + maxTimeSec + " s");
        System.out.println("Average time: " + avgTimeSec + " s");
        System.out.println("Minimum time: " + minTimeSec + " s");

        // List to store the execution times (in nanoseconds)
       times = new ArrayList<>();

        // Run the test 10 times
        for (int i = 0; i < 10; i++) {
            long startTime = System.nanoTime();
            Main.q1_horizontal();  // Replace with your actual method call
            long endTime = System.nanoTime();
            times.add(endTime - startTime);
        }

        System.out.println("Horizontal results");

        // Compute maximum, minimum, and average times in nanoseconds
        maxTimeNs = Collections.max(times);
        minTimeNs = Collections.min(times);
        avgTimeNs = times.stream().mapToLong(Long::longValue).average().orElse(0.0);

        // Convert nanoseconds to seconds (1 second = 1,000,000,000 ns)
        maxTimeSec = maxTimeNs / 1_000_000_000.0;
        minTimeSec = minTimeNs / 1_000_000_000.0;
        avgTimeSec = avgTimeNs / 1_000_000_000.0;

        System.out.println("Maximum time: " + maxTimeSec + " s");
        System.out.println("Average time: " + avgTimeSec + " s");
        System.out.println("Minimum time: " + minTimeSec + " s");

    }

    public static void q1_vertical() {
        ImagePlus image = IJ.openImage("./data/africa.tif");
        image.show("Original");
        if (image != null) {
            ImageStack stack = image.getStack();
            int nSlices = stack.getSize();
            ImageAccess[] zstack = new ImageAccess[nSlices];
            for (int i = 0; i < nSlices; i++) {
                ImageProcessor ip = stack.getProcessor(i + 1);
                zstack[i] = new ImageAccess(ip);
                ImageAccess transfomed = FilteringSession.detectEdgeVertical_NonSeparable(zstack[i]);
                transfomed.show("Transformed image Vertical");
            }
        }
    }

    public static void q1_horizontal() {
        ImagePlus image = IJ.openImage("./data/africa.tif");

        if (image != null) {
            ImageStack stack = image.getStack();
            int nSlices = stack.getSize();
            ImageAccess[] zstack = new ImageAccess[nSlices];
            for (int i = 0; i < nSlices; i++) {
                ImageProcessor ip = stack.getProcessor(i + 1);
                zstack[i] = new ImageAccess(ip);
                ImageAccess transfomed = FilteringSession.detectEdgeHorizontal_NonSeparable(zstack[i]);
                transfomed.show("Transformed image Horizontal");
            }
        }
    }
}

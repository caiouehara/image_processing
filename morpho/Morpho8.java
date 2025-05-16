import ij.*;

public class Morpho8 {

	public static void main(String[] args) {
		
		ImageAccess proc = Morpho8.doOpen(img);
		proc = Morpho8.doOpen(proc);        // repete para reforçar
	}

	/**
	 * Dilatação (8-conexa): para cada pixel retorna o maior valor do seu
	 * vizinho 3×3.
	 */
	public static ImageAccess doDilation(ImageAccess img) {
		int nx = img.getWidth();
		int ny = img.getHeight();
		ImageAccess out = new ImageAccess(nx, ny);
		double[] arr = new double[9];

		for (int x = 0; x < nx; x++)
			for (int y = 0; y < ny; y++) {
				img.getPattern(x, y, arr, ImageAccess.PATTERN_SQUARE_3x3);
				double max = arr[0];
				for (int k = 1; k < 9; k++)
					if (arr[k] > max)
						max = arr[k];
				out.putPixel(x, y, max);
			}
		return out;
	}

	/**
	 * Erosão (8‑conexa): retorna o menor valor do bloco 3×3.
	 */
	public static ImageAccess doErosion(ImageAccess img) {
		int nx = img.getWidth();
		int ny = img.getHeight();
		ImageAccess out = new ImageAccess(nx, ny);
		double[] arr = new double[9];

		for (int x = 0; x < nx; x++)
			for (int y = 0; y < ny; y++) {
				img.getPattern(x, y, arr, ImageAccess.PATTERN_SQUARE_3x3);
				double min = arr[0];
				for (int k = 1; k < 9; k++)
					if (arr[k] < min)
						min = arr[k];
				out.putPixel(x, y, min);
			}
		return out;
	}

	/** Abertura = erosão seguida de dilatação. */
	public static ImageAccess doOpen(ImageAccess img) {
		return doDilation(doErosion(img));
	}

	/** Fechamento = dilatação seguida de erosão. */
	public static ImageAccess doClose(ImageAccess img) {
		return doErosion(doDilation(img));
	}

	/** Gradiente morfológico = dilatada − erodida (contraste normalizado). */
	public static ImageAccess doGradient(ImageAccess img) {
		ImageAccess grad = doDilation(img);
		grad.subtract(grad, doErosion(img));
		grad.normalizeContrast();
		return grad;
	}

	/** Top‑Hat Bright = imagem − abertura. */
	public static ImageAccess doTopHatBright(ImageAccess img) {
		ImageAccess res = img.duplicate();
		res.subtract(res, doOpen(img));
		res.normalizeContrast();
		return res;
	}

	/** Top‑Hat Dark = fechamento − imagem. */
	public static ImageAccess doTopHatDark(ImageAccess img) {
		ImageAccess res = doClose(img);
		res.subtract(res, img);
		res.normalizeContrast();
		return res;
	}

	/**
	 * Filtro Mediana 3×3: ordena o bloco 3×3 e devolve a mediana (posição 4).
	 */
	public static ImageAccess doMedian(ImageAccess img) {
		int nx = img.getWidth();
		int ny = img.getHeight();
		ImageAccess out = new ImageAccess(nx, ny);
		double[] arr = new double[9];

		for (int x = 0; x < nx; x++)
			for (int y = 0; y < ny; y++) {
				img.getPattern(x, y, arr, ImageAccess.PATTERN_SQUARE_3x3);
				sortArray(arr);
				out.putPixel(x, y, arr[4]); // mediana (5.º elemento em array ordenado)
			}
		return out;
	}

	/** Selection‑sort simples para vetor de double (in‑place). */
	private static void sortArray(double[] array) {
		int len = array.length;
		for (int k = 0; k < len - 1; k++) {
			double min = array[k];
			int lmin = k;
			for (int l = k + 1; l < len; l++)
				if (array[l] < min) {
					min = array[l];
					lmin = l;
				}
			double tmp = array[lmin];
			array[lmin] = array[k];
			array[k] = tmp;
		}
	}
}

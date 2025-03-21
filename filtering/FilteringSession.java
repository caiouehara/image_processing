import ij.*;

public class FilteringSession {

	/*******************************************************************************
	 *
	 * E D G E   D E T E C T O R   S E C T I O N
	 *
	 ******************************************************************************/

	/**
	 * Detects the vertical edges inside an ImageAccess object.
	 * This is the non-separable version of the edge detector.
	 * The kernel of the filter has the following form:
	 *
	 *     -------------------
	 *     | -1  |  0  |  1  |
	 *     -------------------
	 *     | -1  |  0  |  1  |
	 *     -------------------
	 *     | -1  |  0  |  1  |
	 *     -------------------
	 *
	 * Mirror border conditions are applied.
	 */
	static public ImageAccess detectEdgeVertical_NonSeparable(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		double arr[][] = new double[3][3];
		double pixel;
		ImageAccess out = new ImageAccess(nx, ny);
		for (int x = 0; x < nx; x++) {
			for (int y = 0; y < ny; y++) {
				input.getNeighborhood(x, y, arr);
				pixel = arr[2][0]+arr[2][1]+arr[2][2]-arr[0][0]-arr[0][1]-arr[0][2];
				pixel = pixel / 6.0;
				out.putPixel(x, y, pixel);
			}
		}
		return out;
	}

	/**
	 * Detects the vertical edges inside an ImageAccess object.
	 * This is the separable version of the edge detector.
	 * The kernel of the filter applied to the rows has the following form:
	 *     -------------------
	 *     | -1  |  0  |  1  |
	 *     -------------------
	 *
	 * The kernel of the filter applied to the columns has the following 
	 * form:
	 *     -------
	 *     |  1  |
	 *     -------
	 *     |  1  |
	 *     -------
	 *     |  1  |
	 *     -------
	 *
	 * Mirror border conditions are applied.
	 */
	static public ImageAccess detectEdgeVertical_Separable(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		ImageAccess out = new ImageAccess(nx, ny);
		double rowin[]  = new double[nx];
		double rowout[] = new double[nx];
		for (int y = 0; y < ny; y++) {
			input.getRow(y, rowin);
			doDifference3(rowin, rowout);
			out.putRow(y, rowout);
		}
		
		double colin[]  = new double[ny];
		double colout[] = new double[ny];
		for (int x = 0; x < nx; x++) {
			out.getColumn(x, colin);
			doAverage3(colin, colout);
			out.putColumn(x, colout);
		}
		return out;
	}

	static public ImageAccess detectEdgeHorizontal_NonSeparable(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		double arr[][] = new double[3][3];
		double pixel;
		ImageAccess out = new ImageAccess(nx, ny);
		for (int y = 0; y < ny; y++) {
			for (int x = 0; x < nx; x++) {
				input.getNeighborhood(x, y, arr);
				pixel = arr[2][0]+arr[2][1]+arr[2][2]-arr[0][0]-arr[0][1]-arr[0][2];
				pixel = pixel / 6.0;
				out.putPixel(x, y, pixel);
			}
		}
		return out;
	}

	static public ImageAccess detectEdgeHorizontal_Separable(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		ImageAccess out = new ImageAccess(nx, ny);

		// First pass: Compute vertical differences by processing each column
		double colin[]  = new double[ny];
		double colout[] = new double[ny];
		for (int x = 0; x < nx; x++) {
			input.getColumn(x, colin);
			doDifference3(colin, colout);
			out.putColumn(x, colout);
		}

		// Second pass: Smooth horizontally by processing each row
		double rowin[]  = new double[nx];
		double rowout[] = new double[nx];
		for (int y = 0; y < ny; y++) {
			out.getRow(y, rowin);
			doAverage3(rowin, rowout);
			out.putRow(y, rowout);
		}

		return out;
	}

	/**
	 * Implements an one-dimensional average filter of length 3.
	 * The filtered value of a pixel is the averaged value of
	 * its local neighborhood of length 3.
	 * Mirror border conditions are applied.
	 */
	static private void doAverage3(double vin[], double vout[]) {
		int n = vin.length;
		vout[0] = (vin[0] + 2.0 * vin[1]) / 3.0;
		for (int k = 1; k < n-1; k++) {
			vout[k] = (vin[k-1] + vin[k] + vin[k+1]) / 3.0;
		}
		vout[n-1] = (vin[n-1] + 2.0 * vin[n-2]) / 3.0;
	}

	/**
	 * Implements an one-dimensional centered difference filter of 
	 * length 3. The filtered value of a pixel is the difference of 
	 * its two neighborhing values.
	 * Mirror border conditions are applied.
	 */
	static private void doDifference3(double vin[], double vout[]) {
		int n = vin.length;
		vout[0] = 0.0;
		for (int k = 1; k < n-1; k++) {
			vout[k] = (vin[k+1] - vin[k-1]) / 2.0;
		}
		vout[n-1] = 0.0;
	}

	/*******************************************************************************
	 *
	 * M O V I N G   A V E R A G E   5 * 5   S E C T I O N
	 *
	 ******************************************************************************/

	// Método auxiliar para condições de fronteira reflexiva.
	// Se o índice ficar negativo, ele é refletido; se exceder o tamanho, é refletido em relação à borda.
	private static int reflect(int idx, int max) {
		if (idx < 0) {
			return -idx;
		} else if (idx >= max) {
			return 2 * max - idx - 2;
		} else {
			return idx;
		}
	}

	// ----------------------------------------------------------
	// 2.1 Filtro de Médias-Móveis 5x5 Não-Separável
	// Para cada pixel, soma-se a janela 5x5 centrada (com condições reflexivas)
	// e divide-se por 25.
	public static ImageAccess doMovingAverage5_NonSeparable(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		ImageAccess out = new ImageAccess(nx, ny);

		// Para cada pixel da imagem
		for (int y = 0; y < ny; y++) {
			for (int x = 0; x < nx; x++) {
				double sum = 0.0;
				// Varre a janela 5x5 centrada em (x,y)
				for (int j = -2; j <= 2; j++) {
					for (int i = -2; i <= 2; i++) {
						int xIndex = reflect(x + i, nx);
						int yIndex = reflect(y + j, ny);
						// Supondo que exista um método getPixel(x,y) que retorna o valor do pixel
						sum += input.getPixel(xIndex, yIndex);
					}
				}
				// Coloca o valor médio no pixel de saída
				out.putPixel(x, y, sum / 25.0);
			}
		}
		return out;
	}

	// ----------------------------------------------------------
	// 2.2 Filtro de Médias-Móveis 5x5 Separável
	// Primeiro, aplica-se a média móvel 1D (horizontal) e depois vertical.
	public static ImageAccess doMovingAverage5_Separable(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		ImageAccess temp = new ImageAccess(nx, ny);
		ImageAccess out = new ImageAccess(nx, ny);

		double[] rowin = new double[nx];
		double[] rowout = new double[nx];
		// Passagem horizontal: processa cada linha usando doAverage5
		for (int y = 0; y < ny; y++) {
			input.getRow(y, rowin);
			doAverage5(rowin, rowout);
			temp.putRow(y, rowout);
		}

		double[] colin = new double[ny];
		double[] colout = new double[ny];
		// Passagem vertical: processa cada coluna usando doAverage5
		for (int x = 0; x < nx; x++) {
			temp.getColumn(x, colin);
			doAverage5(colin, colout);
			out.putColumn(x, colout);
		}

		return out;
	}

	// Rotina 1D para média móvel com janela 5 (implementação direta)
	public static void doAverage5(double[] in, double[] out) {
		int n = in.length;
		for (int i = 0; i < n; i++) {
			double sum = 0.0;
			// Considera os 5 pixels centrados em i: de i-2 a i+2
			for (int j = -2; j <= 2; j++) {
				int idx = reflect(i + j, n);
				sum += in[idx];
			}
			out[i] = sum / 5.0;
		}
	}

	// ----------------------------------------------------------
	// 2.3 Filtro de Médias-Móveis 5x5 Separável Recursivo
	// A ideia é utilizar uma versão recursiva (ou incremental) para o cálculo da média 1D.
	public static ImageAccess doMovingAverage5_Recursive(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		ImageAccess temp = new ImageAccess(nx, ny);
		ImageAccess out = new ImageAccess(nx, ny);

		double[] rowin = new double[nx];
		double[] rowout = new double[nx];
		// Passagem horizontal com média recursiva
		for (int y = 0; y < ny; y++) {
			input.getRow(y, rowin);
			doAverage5_recursive(rowin, rowout);
			temp.putRow(y, rowout);
		}

		double[] colin = new double[ny];
		double[] colout = new double[ny];
		// Passagem vertical com média recursiva
		for (int x = 0; x < nx; x++) {
			temp.getColumn(x, colin);
			doAverage5_recursive(colin, colout);
			out.putColumn(x, colout);
		}

		return out;
	}

	// Rotina 1D para média móvel com janela 5 usando abordagem recursiva/incremental.
	// Para cada posição, a média é atualizada subtraindo o valor que "sai" da janela
	// e somando o valor que "entra", considerando as condições reflexivas.
	public static void doAverage5_recursive(double[] in, double[] out) {
		int n = in.length;
		// Cálculo explícito da soma para a posição 0: índices -2, -1, 0, 1, 2
		double sum = 0.0;
		for (int j = -2; j <= 2; j++) {
			int idx = reflect(0 + j, n);
			sum += in[idx];
		}
		out[0] = sum / 5.0;

		// Para i de 1 a n-1, usa a relação recursiva:
		// S(i) = S(i-1) - in[reflect(i-3)] + in[reflect(i+2)]
		for (int i = 1; i < n; i++) {
			int leaving = reflect(i - 3, n);  // elemento que sai da janela
			int entering = reflect(i + 2, n); // elemento que entra na janela
			sum = sum - in[leaving] + in[entering];
			out[i] = sum / 5.0;
		}
	}


	/*******************************************************************************
	 *
	 * S O B E L
	 *
	 ******************************************************************************/

	static public ImageAccess doSobel(ImageAccess input) {
		IJ.showMessage("Question 4");
		return input.duplicate();
	}


	/*******************************************************************************
	 *
	 * M O V I N G   A V E R A G E   L * L   S E C T I O N
	 *
	 ******************************************************************************/

	static public ImageAccess doMovingAverageL_Recursive(ImageAccess input, int length) {
		IJ.showMessage("Question 5");
		return input.duplicate();
	}

}

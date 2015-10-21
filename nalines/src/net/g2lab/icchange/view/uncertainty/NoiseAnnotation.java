package net.g2lab.icchange.view.uncertainty;

import java.awt.Color;
import java.awt.Graphics2D;

import net.g2lab.layer.Layer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * Annotation lines as noisy lines distorted by uncertainty values.
 * 
 * @author <a href="mailto:christoph.kinkeldey@hcu-hamburg.de">Christoph
 *         Kinkeldey</a>
 * 
 */
public class NoiseAnnotation extends AbstractAnnotation {

	static final Log LOG = LogFactory.getLog(Class.class.getName());

	private static final Color NOISE_COLOR = new Color(255, 255, 255, 255);

	private static final int NUM_NOISE_PARTICLES = 4;

	/**
	 * maximum width of the noise in respect to the grid width (percent)
	 */
	private int maxNoiseWidth = 50;

	private int particleSizePx = 1;

	public static int opacity = 150;
	
	private boolean isDegrees;

	public NoiseAnnotation(Layer uncertaintyLayer, double gridSize) {
		this(uncertaintyLayer, gridSize, false);
	}

	public NoiseAnnotation(Layer uncertaintyLayer, double gridSize,
			boolean isDegrees) {
		super(uncertaintyLayer, gridSize);
		this.isDegrees = isDegrees;
	}
	
	/**
	 * @return the maxNoiseWidth
	 */
	public int getMaxNoiseWidth() {
		return maxNoiseWidth;
	}

	/**
	 * @param maxNoiseWidth
	 *            the maxNoiseWidth to set
	 */
	public void setMaxNoiseWidth(int maxNoiseWidth) {
		this.maxNoiseWidth = maxNoiseWidth;
	}

	public void setParticleSizePx(int particleSizePx) {
		this.particleSizePx = particleSizePx;
	}
	
	/**
	 * @return the offsetX
	 */
	public double getOffsetX() {
		return offsetX;
	}

	public void setOffset(double offsetX, double offsetY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		LOG.debug("offset: " + offsetX + ", " + offsetY);
	}

	/**
	 * @return the offsetY
	 */
	public double getOffsetY() {
		return offsetY;
	}

	public void drawAnnotations(Graphics2D g2d, int widthPx, int heightPx, ReferencedEnvelope envelope) {
		this.widthPx = widthPx;
		this.heightPx = heightPx;
		this.scaleX = (widthPx + 2 * offsetXPx) / envelope.getWidth();
		this.scaleY = (heightPx + 2 * offsetYPx) / envelope.getHeight();
		this.envelope = envelope;
		drawGrid(g2d, scaleX, scaleY, offsetX, offsetY);
	}

	private void drawGrid(Graphics2D g2d, double scaleX, double scaleY,
			double offsetX, double offsetY) {
		double xGlobal, yGlobal;
		double gridSizeX = this.gridSizeX;
		double gridSizeY = this.gridSizeY;
		int gridSizeXPx = (int) Math.round(scaleX * gridSizeX);
		int gridSizeYPx = (int) Math.round(scaleY * gridSizeY);
		LOG.debug("grid size px: " + gridSizeXPx + " x " + gridSizeYPx);

		int bandSizeXPx = (int) (gridSizeXPx * maxNoiseWidth / 100.);
		int bandSizeYPx = (int) (gridSizeYPx * maxNoiseWidth / 100.);
		int particleWidthPx = particleSizePx;
		int particleHeightPx = particleSizePx;

		double startX = envelope.getMinX() + offsetX;
		double startY = envelope.getMinY() + offsetY;
		int numStepsX = 1 + (int) ((envelope.getWidth() - offsetX) / gridSizeX);
		int numStepsY = 1 + (int) ((envelope.getHeight() - offsetY) / gridSizeY);
		for (int i = 0; i < numStepsY; i++) {
			yGlobal = startY + i * gridSizeY;
			for (int j = 0; j < numStepsX; j++) {
				xGlobal = startX + j * gridSizeX;
				drawRow(g2d, gridSizeXPx, bandSizeYPx, particleWidthPx,
						particleHeightPx, startX, startY, xGlobal, yGlobal, i,
						j);
				drawColumn(g2d, gridSizeYPx, bandSizeXPx, bandSizeYPx,
						particleWidthPx, particleHeightPx, startX, xGlobal,
						yGlobal, i, j);
			}
		}
	}

	private void drawRow(Graphics2D g2d, int gridSizeXPx, int bandSizeYPx,
			int particleWidthPx, int particleHeightPx, double startX,
			double startY, double xGlobal, double yGlobal, int i, int j) {
		int xPx;
		int yPx;
		double uncertainty;
		xPx = getXCoordPx(xGlobal);
		yPx = getYCoordPx(yGlobal);
		for (int n = 0; n < gridSizeXPx; n += particleWidthPx) {
			xGlobal = getX(xPx - gridSizeXPx / 2 + n);
			uncertainty = getUncertainty(xGlobal, yGlobal);
			// LOG.debug("uncertainty == " + uncertainty);
			if (uncertainty >= 0 && uncertainty <= 100) {
				// g2d.setColor(new Color(NOISE_COLOR.getRed(),
				// NOISE_COLOR.getGreen(), NOISE_COLOR.getBlue(), 100));
				// g2d.drawLine(xPx, yPx, xPx + gridSizeXPx, yPx);

				g2d.setColor(new Color(NOISE_COLOR.getRed(), NOISE_COLOR
						.getGreen(), NOISE_COLOR.getBlue(), opacity));
				for (int np = 0; np < NUM_NOISE_PARTICLES; np++) {
					drawParticleX(g2d, xPx, yPx, uncertainty, gridSizeXPx,
							bandSizeYPx, particleWidthPx, particleHeightPx, n);
				}
			}
		}
	}

	private void drawColumn(Graphics2D g2d, int gridSizeYPx, int bandSizeXPx,
			int bandSizeYPx, int particleWidthPx, int particleHeightPx,
			double startX, double xGlobal, double yGlobal, int i, int j) {
		int xPx;
		int yPx;
		double uncertainty;
		xPx = getXCoordPx(xGlobal);
		yPx = getYCoordPx(yGlobal);
		for (int n = 0; n < gridSizeYPx; n += particleHeightPx) {
			yGlobal = getY(yPx - gridSizeYPx / 2 + n);
			uncertainty = getUncertainty(xGlobal, yGlobal);
			// LOG.debug("uncertainty == " + uncertainty);
			if (uncertainty >= 0 && uncertainty <= 100) {
				// g2d.setColor(new Color(NOISE_COLOR.getRed(),
				// NOISE_COLOR.getGreen(), NOISE_COLOR.getBlue(), 100));

				g2d.setColor(new Color(NOISE_COLOR.getRed(), NOISE_COLOR
						.getGreen(), NOISE_COLOR.getBlue(), opacity));
				int effBandSizeYPx = (int) (uncertainty * bandSizeYPx);
				// g2d.drawLine(xPx + effBandSizeYPx, yPx, xPx + effBandSizeYPx,
				// yPx + gridSizeYPx);
				// point = new GeometryFactory().createPoint(new Coordinate(xPx,
				// yPx));
				if (g2d != null && (n <= gridSizeYPx / 2. - effBandSizeYPx / 2.
						|| n >= gridSizeYPx / 2 + effBandSizeYPx / 2.)) {
					for (int np = 0; np < NUM_NOISE_PARTICLES; np++) {
						drawParticleY(g2d, xPx, yPx, uncertainty, gridSizeYPx,
								bandSizeXPx, particleWidthPx, particleHeightPx, n);
					}
					
				}
			}
		}
	}

	private void drawParticleX(Graphics2D g2d, int xPx0, int yPx0,
			double uncertainty, int gridSizeXPx, int bandSizePx,
			int particleWidthPx, int particleHeightPx, int n) {
		int xPx;
		int yPx;
		double delta = getDelta(uncertainty, maxNoiseWidth / 100. * bandSizePx);
		xPx = (int) (xPx0 - gridSizeXPx / 2. + n);
		yPx = (int) (yPx0 + delta);
		g2d.fillRect(xPx, yPx, particleWidthPx, particleHeightPx);
	}

	private void drawParticleY(Graphics2D g2d, int x, int y,
			double uncertainty, int gridSizeYPx, int bandSizePx,
			int particleWidthPx, int particleHeightPx, int n) {
		double delta = getDelta(uncertainty, maxNoiseWidth / 100. * bandSizePx);
		int xPx = (int) (x + delta);
		int yPx = (int) (y - gridSizeYPx / 2. + n);
		g2d.fillRect(xPx, yPx, particleWidthPx, particleHeightPx);
	}

	private double getDelta(double uncertainty, double maxSize) {
		return maxSize * uncertainty * (-1 + 2 * Math.random());
	}

	public static double estimateDistanceDeg(double distance) {
		return distance / 6378137. * 180. / Math.PI;
	}



}

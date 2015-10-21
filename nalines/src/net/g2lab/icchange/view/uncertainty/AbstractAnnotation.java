/**
 * 
 */
package net.g2lab.icchange.view.uncertainty;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import net.g2lab.layer.Layer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * @author <a href="mailto:christoph.kinkeldey@hcu-hamburg.de">Christoph Kinkeldey</a>
 *
 */
public abstract class AbstractAnnotation {
	
	private static final Log LOG = LogFactory.getLog(Class.class.getName());

	Layer uncertLayer;
	ReferencedEnvelope envelope;
	protected int widthPx, heightPx;
	protected double scaleX, scaleY;
	protected double gridSizeX, gridSizeY;
	protected double offsetX = 0;
	protected double offsetY = 0;
	protected int offsetXPx = 0; 
	protected int offsetYPx = 0;
	
	public AbstractAnnotation(Layer layer, double gridSize) {
		this.uncertLayer = layer;
		this.gridSizeX = gridSize;
		this.gridSizeY = gridSize;
	}
	
	public Layer getUncertaintyLayer() {
		return uncertLayer;
	}

	/**
	 * @return the envelope
	 */
	public ReferencedEnvelope getEnvelope() {
		return envelope;
	}



	/**
	 * @param envelope the envelope to set
	 */
	public void setEnvelope(ReferencedEnvelope envelope) {
		this.envelope = envelope;
	}



	public double getX(int xPx) {
		return (xPx - offsetXPx) / scaleX + envelope.getMinX();
	}
	
	public double getY(int yPx) {
		return (heightPx - yPx - offsetYPx) / scaleY + envelope.getMinY();
	}
	
	public int getXCoordPx(double x) {
		return offsetXPx + (int) Math.round(scaleX * (x - envelope.getMinX()));
	}

	public int getYCoordPx(double y) {
		return (int) (heightPx - offsetYPx - Math.round(scaleY * (y - envelope.getMinY())));
	}
	
	public int getWidthPx(double width) {
		return (int) Math.round(scaleX * width);
	}

	public int getHeightPx(double height) {
		return (int) Math.round(scaleY * height);
	}
	
	public double getWidth(int widthPx) {
		return (int) getX(widthPx);
	}
	
	public double getHeight(int heightPx) {
		return (int) getX(heightPx);
	}



	
	/**
	 * @return the offsetX
	 */
	public double getOffsetX() {
		return offsetX;
	}

	/**
	 * @param offsetX the offsetX to set
	 */
	public void setOffsetX(double offsetX) {
		this.offsetX = offsetX;
	}

	/**
	 * @return the offsetY
	 */
	public double getOffsetY() {
		return offsetY;
	}

	/**
	 * @param offsetY the offsetY to set
	 */
	public void setOffsetY(double offsetY) {
		this.offsetY = offsetY;
	}

	/**
	 * @return the gridSizeX
	 */
	public double getGridSizeX() {
		return gridSizeX;
	}

	/**
	 * @param gridSizeX the gridSizeX to set
	 */
	public void setGridSizeX(double gridSizeX) {
		this.gridSizeX = gridSizeX;
	}

	/**
	 * @return the gridSizeY
	 */
	public double getGridSizeY() {
		return gridSizeY;
	}

	/**
	 * @param gridSizeY the gridSizeY to set
	 */
	public void setGridSizeY(double gridSizeY) {
		this.gridSizeY = gridSizeY;
	}

	public AffineTransform getTransform() {
		AffineTransform transform = new AffineTransform();
		transform.translate(uncertLayer.getEnvelope().getMinX(), uncertLayer.getEnvelope().getMinY());
		transform.scale(scaleX, scaleY);
		return transform;
	}
	
	protected double getUncertainty(double x, double y) {
//		return uncertLayer.getIntAttribute("u", x, y) / 100.;
		return uncertLayer.getDoubleAttribute("u", x, y) / 100.;
	}

	public void draw(Graphics2D g2d, int widthPx, int heightPx, ReferencedEnvelope bounds) {
		this.drawAnnotations(g2d, widthPx, heightPx, bounds);
	}

	public abstract void drawAnnotations(Graphics2D graphics, int widthPx, int heightPx, ReferencedEnvelope bounds);
}

/**
 * 
 */
package net.g2lab.icchange.view.uncertainty;

import java.awt.Color;
import java.awt.Dimension;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jump.feature.FeatureCollection;

/**
 * @author <a href="mailto:christoph.kinkeldey@hcu-hamburg.de">Christoph
 *         Kinkeldey</a>
 * 
 */
public abstract class AnnotationPanelTestBase {

	
	public static final String CLASS_ATTRIBUTE = "Class_name";
	public static final String UNCERTAINTY_ATTRIBUTE = "u";
	// Vegetation_wenig Deich Gehölze Schatten Rohrkolben Teichsimse Schilf Wasser Gruenland Rohrglanzgras Strandsimse
	public static final Color[] CLASS_COLORS = new Color[] {
		new Color(100, 200, 100),
		new Color(88, 105, 34),
		new Color(36,128,82),
		new Color(50, 50, 50),
		new Color(158, 219, 228),
		new Color(158, 170, 215),
		new Color(57, 145, 188),
		new Color(0, 77, 204),
		new Color(207, 240, 117),
		new Color(122, 142, 245),
		new Color(120, 102, 204)
	};
	
	protected Envelope getSmallWindow() {
		double x0 = 534975;
		double y0 = 5950230;
		double x1 = 535400;
		double y1 = 5950550;
		return new Envelope(x0, x1, y0, y1);
	}

	protected Envelope getMediumWindow() {
		double x0 = 534723.5980056414;
		double y0 = 5949222.678808471;
		double x1 = 536353.8505729294;
		double y1 = 5950479.591093174;
		return new Envelope(x0, x1, y0, y1);
	}

	protected Envelope getOverallWindow(FeatureCollection polygons, FeatureCollection uncertainty) {
		Envelope envelope = new Envelope();
		envelope.expandToInclude(polygons.getEnvelope());
		envelope.expandToInclude(uncertainty.getEnvelope());
		return envelope;
	}

	protected Dimension getDimension(int height, Envelope envelope) {
		double ratio = envelope.getWidth() / envelope.getHeight();
		return new Dimension((int) (height*ratio), height);
	}
}

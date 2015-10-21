package net.g2lab.icchange.view.uncertainty;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureCollection;

/**
 * @author <a href="mailto:christoph.kinkeldey@hcu-hamburg.de">Christoph
 *         Kinkeldey</a>
 * 
 */
public class AnnotationPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
	
	private static final Log LOG = LogFactory.getLog(Class.class.getName());
	
	private static final Color BACKGROUND_COLOR = Color.LIGHT_GRAY;

	protected FeatureCollection polygons;
	private AbstractAnnotation annotation;
	private Envelope envelope;
	
	protected boolean drawPolygons = false;
	protected boolean drawAnnotations = true;
	protected boolean drawPoints = false;
	private Map<String, Color> colors;

	private int clickX, clickY;


	/** A panel showing uncertainty information as annotation lines with the given type of annotation.
	 * @param annotation
	 * @param polygons
	 * @param envelope
	 */
	public AnnotationPanel(AbstractAnnotation annotation, Envelope envelope) {
		this.annotation = annotation;
		this.polygons = null;
		this.envelope = envelope;
		this.setOpaque(false);
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
		this.addKeyListener(this);

//		String[] objClasses = parseDistinctObjectClasses(polygons.getFeatures());
//		LOG.info(objClasses.length + " object classes.");
//		String classes = "";
//		for (String objClass : objClasses) {
//			classes += objClass + " ";
//		}
//		LOG.info(classes);
		
//		colors = createColors(objClasses);
	}

	private Map<String, Color> createColors(String[] objClasses) {
		Map<String, Color> colors = new HashMap<String, Color>();
		int numColors = AnnotationPanelTestBase.CLASS_COLORS.length;
		for (int i = 0; i < objClasses.length; i++) {
			colors.put(objClasses[i], AnnotationPanelTestBase.CLASS_COLORS[i%(numColors)]);
		}
		return colors;
	}

	public boolean isDrawPolygons() {
		return drawPolygons;
	}

	public void setDrawPolygons(boolean drawPolygons) {
		this.drawPolygons = drawPolygons;
	}

	public boolean isDrawAnnotations() {
		return drawAnnotations;
	}

	public void setDrawAnnotations(boolean drawAnnotations) {
		this.drawAnnotations = drawAnnotations;
	}

	public boolean isDrawPoints() {
		return drawPoints;
	}

	public void setDrawPoints(boolean drawPoints) {
		this.drawPoints = drawPoints;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
	
		super.paintComponent(g);
		double scaleX = (double) getWidth() / envelope.getWidth();
		double scaleY = (double) getHeight() / envelope.getHeight();
		scaleX = Math.min(scaleX, scaleY);
		scaleY = scaleX;
		
		Graphics2D g2d = (Graphics2D) g;
		
		if (drawPolygons) {
			drawPolygons(g2d, scaleX, scaleY);
		}
		if (drawAnnotations) {
//			annotation.drawAnnotations(g2d, getWidth(), getHeight(), envelope);
		}
	}


	private void drawPolygons(Graphics2D g2d, double scaleX, double scaleY) {
		for (Feature feature : ((List<Feature>) polygons.getFeatures())) {
			drawPolygonFeature(g2d, feature, scaleX, scaleY);
		}
	}

	private void drawPolygonFeature(Graphics2D g2d, Feature feature, double scaleX, double scaleY) {
		LineString outerRing = ((com.vividsolutions.jts.geom.Polygon) feature
				.getGeometry()).getExteriorRing();
		Polygon polygon;
		int numHoles = ((com.vividsolutions.jts.geom.Polygon) feature
				.getGeometry()).getNumInteriorRing();
		for (int i = 0; i < numHoles; i++) {
			LineString interiorRing = ((com.vividsolutions.jts.geom.Polygon) feature
					.getGeometry()).getInteriorRingN(i);
			polygon = createPolygon(interiorRing, scaleX, scaleY);
			g2d.setColor(getBackground());
			g2d.fill(polygon);
		}
		
		polygon = createPolygon(outerRing, scaleX, scaleY);
		g2d.setColor(getColor(feature));
		g2d.fill(polygon);
	}

	private Color getColor(Feature feature) {
		String objClass = parseObjectClass(feature);
		return colors.get(objClass);
	}

	private Polygon createPolygon(LineString outerRing, double scaleX, double scaleY) {
		Polygon polygon = new Polygon();
		Coordinate coord;
		int x;
		int y;
		for (int i = 0; i < outerRing.getCoordinates().length; i++) {
			coord = outerRing.getCoordinates()[i];
			x = (int) (scaleX * (coord.x - envelope.getMinX()));
			y = getHeight() - (int) (scaleY * (coord.y - envelope.getMinY()));
			polygon.addPoint(x, y);
		}
		return polygon;
	}



	protected String[] parseDistinctObjectClasses(List<Feature> features) {
		Set<String> distinct = new HashSet<String>();
		for (Feature feature : features) {
			distinct.add(parseObjectClass(feature));
		}
		return distinct.toArray(new String[0]);
	}
	
	protected String parseObjectClass(Feature feature) {
		String attribute = (String) feature.getAttribute(AnnotationPanelTestBase.CLASS_ATTRIBUTE);
		return attribute.trim();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int wheelRot = e.getWheelRotation();
		if (wheelRot > 0) {
			zoom(2f);
		} else {
			zoom(.5f);
		}
		repaint();
	}

	private void zoom(float factor) {
		double gridSizeX = annotation.getGridSizeX();
		double gridSizeY = annotation.getGridSizeY();
		double offsetX = annotation.getOffsetX();
		double offsetY = annotation.getOffsetY();
		gridSizeX *= factor;
		gridSizeY *= factor;
		offsetX -= factor * gridSizeX;
		offsetY -= factor * gridSizeY;
		annotation.setGridSizeX(gridSizeX);
		annotation.setGridSizeY(gridSizeY);
		annotation.setOffsetX(offsetX);
		annotation.setOffsetY(offsetY);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.clickX = e.getX();
		this.clickY = e.getY();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
//		int deltaXPx = e.getX();
//		int deltaYPx = e.getY();
//		
//		double offsetX = deltaXPx % annotation.getGridSizeX();
//		double offsetY = deltaYPx % annotation.getGridSizeY();
//		annotation.setOffsetX(offsetX );
//		annotation.setOffsetY(offsetY);
//		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int deltaXPx = e.getX();
		int deltaYPx = e.getY();
		
		double offsetX = deltaXPx % annotation.getGridSizeX();
		double offsetY = deltaYPx % annotation.getGridSizeY();
		annotation.setOffsetX(offsetX );
		annotation.setOffsetY(-1*offsetY);
		repaint();
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println(e.getKeyCode());
		if (e.getKeyChar() == KeyEvent.VK_KP_UP) {
			zoom(2f);
			repaint();
		} else if (e.getKeyChar() == KeyEvent.VK_DOWN) {
			zoom(.5f);
			repaint();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}

package net.g2lab.icchange.view.uncertainty;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.g2lab.io.RasterIO;
import net.g2lab.layer.Layer;
import net.g2lab.layer.RasterLayer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geometry.jts.LiteShape;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureCollection;
import com.vividsolutions.jump.io.DriverProperties;
import com.vividsolutions.jump.io.IllegalParametersException;
import com.vividsolutions.jump.io.ShapefileReader;
import com.vividsolutions.jump.io.ShapefileWriter;

public class NoiseAnnotationStudyTest extends AnnotationPanelTestBase {

	private static final Log LOG = LogFactory.getLog(Class.class.getName());
	
	private static final Color BACKGROUND_COLOR = new Color(100, 100, 100);

	private static final int RASTER_WIDTH = 
			1000
			;

	private static final int OUTPUT_RASTER_WIDTH = 1000;

	private static final String CRS_EPSG_CODE =
	// "EPSG:25832"; // ETRS89 UTM32
	"EPSG:4647"; // ETRS89 UTM32 8digit
	// "EPSG:32632"; // WGS84 UTM32

	public static final String DIR =
//	"C:\\temp\\nalines\\0\\"
	"C:\\temp\\nalines\\1\\"
//	"C:\\temp\\nalines\\2\\"
//	"C:\\temp\\nalines\\3\\"
//	"C:\\temp\\nalines\\4\\"
//	"C:\\temp\\nalines\\5\\"
//	"C:\\temp\\nalines\\6\\"
//	"C:\\temp\\nalines\\7\\"
//	"C:\\temp\\nalines\\8\\"
//	"C:\\temp\\nalines\\9\\"
//	"C:\\temp\\nalines\\10\\"
	;

	public static final String OUT_SUBDIR = "annotationlines/";

	public static final boolean DRAW_BACKGROUND = false;
	
	public static String BG_FILENAME = 
//			"0.shp"
//			"1.shp"
//			"2.shp"
//			"3.shp"
//			"4.shp"
//			"5.shp"
//			"6.shp"
			"7.shp"
//			"8.shp"
//			"9.shp"
//			"10.shp"
			
//			"2-cont-1.shp"
			
			;

	public static String[] UNCERTAINTY_FILENAMES = { 
		"1-u-obj-1-4class.tif", 
//		"1-u-obj-1-5class.tif", "1-u-obj-1-6class.tif"
//		"1-u-obj-1-8class.tif"
//		"2-u-obj-1-4class.tif", "2-u-obj-1-5class.tif", "2-u-obj-1-6class.tif"
//		"2-u-obj-1-8class.tif"
//		"3-u-obj-1-4class.tif", "3-u-obj-1-5class.tif", "3-u-obj-1-6class.tif"
//		"3-u-obj-1-8class.tif"
//		"4-u-obj-1-4class.tif", "4-u-obj-1-5class.tif", "4-u-obj-1-6class.tif"
//		"4-u-obj-1-8class.tif"
//		"5-u-obj-1-4class.tif", "5-u-obj-1-5class.tif", "5-u-obj-1-6class.tif"
//		"5-u-obj-1-8class.tif"
//		"6-u-obj-1-4class.tif", "6-u-obj-1-5class.tif", "6-u-obj-1-6class.tif"
//		"6-u-obj-1-8class.tif"
//		"7-u-obj-1-4class.tif", "7-u-obj-1-5class.tif", "7-u-obj-1-6class.tif"
//		"7-u-obj-1-8class.tif"
//		"8-u-obj-1-4class.tif", "8-u-obj-1-5class.tif", "8-u-obj-1-6class.tif"
//		"8-u-obj-1-8class.tif"
//		"9-u-obj-1-4class.tif", "9-u-obj-1-5class.tif", "9-u-obj-1-6class.tif"
//		"9-u-obj-1-8class.tif"
//		"10-u-obj-1-4class.tif", "10-u-obj-1-5class.tif", "10-u-obj-1-6class.tif"
//		"10-u-obj-1-8class.tif"
		
//		"1-u-cont-1-4class.tif", "1-u-cont-1-6class.tif", "1-u-cont-1-8class.tif"
//		"1-u-cont-2-4class.tif", "1-u-cont-2-6class.tif", "1-u-cont-2-8class.tif"
		
//		"2-u-cont-1-4class.tif", "2-u-cont-1-6class.tif", "2-u-cont-1-8class.tif"
//		"2-u-cont-2-4class.tif", "2-u-cont-2-6class.tif", "2-u-cont-2-8class.tif",
//		"2-u-cont-3-4class.tif",  "2-u-cont-1-5class.tif", "2-u-cont-1-6class.tif",
		
//		"3-u-cont-1-4class.tif", "3-u-cont-1-6class.tif", "3-u-cont-1-8class.tif"
//		"3-u-cont-2-4class.tif", "3-u-cont-2-6class.tif", "3-u-cont-2-8class.tif"
		
//		"4-u-cont-1-4class.tif", "4-u-cont-1-6class.tif", "4-u-cont-1-8class.tif"
//		"4-u-cont-2-4class.tif", "4-u-cont-2-6class.tif", "4-u-cont-2-8class.tif"
		
//		"7-u-cont-1-4class.tif", "7-u-cont-1-6class.tif", "7-u-cont-1-8class.tif"
//		"7-u-cont-2-4class.tif", "7-u-cont-2-6class.tif", "7-u-cont-2-8class.tif"
		
//		"0.tif", "50.tif", "100.tif"
	};

	private static final int[] GRID_WIDTH = {
//		10 //0
		5 // for figures
//		3 // 1
//		2 // rest
		};

	/**
	 * number of pixels per meter of grid
	 */
	private static final int[] PARTICLE_SIZE =
	{ 1, 2 } // obj
//	{ 1 } // cont
	// 10// 0
	;

	private static final int[] BAND_WIDTH = {
		40, 50 // obj
//		50 // cont
		}; 
											

	private static final boolean DRAW_AREAS = false;
	
	private static final double[][] AREA_CENTERS = {
//			{ 32535031.955, 5950346.769 }, { 32535085.944, 5950295.793 } // 1
//			{ 32536362.847, 5947940.844 }, { 32536396.839, 5947946.848 } // 2
//			{32534622.532, 5951564.657}, {32534656.523, 5951578.722} // 3
//			{32536211.306, 5948283.568}, {32536243.332, 5948259.599} // 4
//			{32516103.749, 5970861.737}, {32516111.776, 5970827.694} // 5
//			{32516308.683, 5970843.812}, {32516336.799, 5970859.684} // 6
//			{32535021.643, 5950515.286}, {32535019.690, 5950485.306} // 7
//			{32534856.636, 5950688.380}, {32534892.611, 5950672.381} // 8
//			{32534767.646, 5950985.831}, {32534799.598, 5951019.895} // 9
//			{32534887.554, 5950546.373}, {32534921.583, 5950522.332} // 10
			
//			{ 32535058.682, 5950318.842 }, { 32535079.593, 5950291.989  } // 1-cont-1
//			{ 32535031.682, 5950291.842 }, { 32535079.593, 5950291.989  } // 1-cont-2
			
//		{ 32536368.847, 5947954.844 }, { 32536390.839, 5947946.848 } // 2-cont-1-old
//		{ 32536368.847, 5947955 }, { 32536390.839, 5947947 } // 2-cont-1
//		{ 32536374.847, 5947958.844 }, { 32536386.839, 5947942.848 } // 2-cont-2-old
//		{ 32536375.15, 5947959.5 }, { 32536387, 5947943.5 } // 2-cont-2
			
//		{32534622.532, 5951566.657}, {32534658.523, 5951573.722} // 3-cont-1
//		{32534622.532, 5951566.657}, {32534648.523, 5951580.722} // 3-cont-2-old
//		{32534622.532, 5951564.657}, {32534648.523, 5951580.722} // 3-cont-2
		
//		{32536210.344, 5948283.548}, {32536246.337, 5948249.530} // 4-cont-1
//		{32536214.262, 5948265.594}, {32536212.315, 5948249.664} // 4-cont-2
		
//		{32535021.723, 5950517.265}, {32535021.717, 5950491.237} // 7-cont-1
//		{32535003.75, 5950503.274}, {32535039.727, 5950481.246} // 7-cont-2-old
		{32535024.723, 5950517.265}, {32535012.717, 5950491.237} // 7-cont-2
	}; 

	private static final String[] AREA_NAMES = { "A", "B"};
	private static final double AREA_SIZE = 
//			12 // 1
			8 // rest
			;

	private static final Font AREA_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 45);

	private static final Color[] CLASS_COLORS = new Color[] {
		new Color(178, 223, 138), 
			new Color(166, 206, 227),
			new Color(31, 120, 180), 
			new Color(51, 160, 44) 
			};

	private static final double[] ANGLES = { 
		0, 
		Math.PI / 2., 
		Math.PI, 
		3. / 2. * Math.PI
	};

	private static boolean RANDOM_CLASS_COLORS = true;

	private static boolean RANDOM_ANGLE = true;

	private int shiftColors = 0;
	
	public static void main(String[] args) {
		URL inURL;
		try {
			for (String uncertaintyFilename : UNCERTAINTY_FILENAMES) {
				inURL = new File(DIR + uncertaintyFilename).toURI().toURL();
				URL outURL = new File(DIR).toURI().toURL();
				for (int gridSize : GRID_WIDTH) {
					for (int bandWidth : BAND_WIDTH) {
						for (int particleSize : PARTICLE_SIZE) {
							new NoiseAnnotationStudyTest(inURL,
									uncertaintyFilename, outURL, gridSize,
									bandWidth, particleSize);
						}
					}
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	public NoiseAnnotationStudyTest(URL inURL, String uncertaintyFilename,
			URL outURL, int gridSize, int bandWidth, int particleSize) {
		try {

			GridCoverage2D uncertainty = new RasterIO().readCoverage(inURL);
			Layer uncertaintyLayer = new RasterLayer(uncertainty);

			ShapefileReader reader = new ShapefileReader();

			FeatureCollection background = null;
			if (DRAW_BACKGROUND) {
				background = reader.read(new DriverProperties(DIR
						+ BG_FILENAME));
				LOG.info(background.size() + " polygon features read.");
			}
			
			Envelope envelope = uncertaintyLayer.getEnvelope();
			// getSmallWindow();
			// getMediumWindow();
			// getOverallWindow(polygons, uncertaintyGrid);
			// uncertaintyGrid.getEnvelope();
			LOG.info("Bounding Box: " + envelope);

			// LOG.info("Computing bounding polygon.");
			// Polygon boundingPolygon = createBoundingPolygon(polygons);

			CoordinateReferenceSystem crs = CRS.decode(CRS_EPSG_CODE);
			LOG.debug("output crs: " + crs);
			
			LOG.info("Computing annotations.");
			NoiseAnnotation annotation = new NoiseAnnotation(uncertaintyLayer, gridSize);
			annotation.setMaxNoiseWidth(bandWidth);
			annotation.setParticleSizePx(particleSize);
			
			ReferencedEnvelope bounds = new ReferencedEnvelope(envelope, crs);			

			int rasterWidthPx = RASTER_WIDTH;// (int) (envelope.getWidth() *
												// particleRatio);

			double angle = 0;
			if (RANDOM_ANGLE) {
				angle = ANGLES[(int) (Math.random() * (ANGLES.length - 1))];
			}
//			int angleDeg = (int) (angle / Math.PI * 180);
			
			GridCoverage2D outRaster = createRaster(background, annotation,
					envelope, crs, rasterWidthPx, particleSize, angle, bounds);
			
			String outDir = outURL.getFile() + OUT_SUBDIR;
			if (!new File(outDir).exists()) {
				new File(outDir).mkdir();
			}
			String outPath = outDir + uncertaintyFilename.split("\\.")[0]
					+ "-al-" + gridSize + "_" + ((int) particleSize) + "_"
					+ bandWidth + ".tif";
			
			File file = new File(outPath);
			GeoTiffWriter writer = new GeoTiffWriter(file);
			// WorldImageWriter writer = new WorldImageWriter(file);
			writer.write(outRaster, null);
			LOG.info("written raster to " + writer.getDestination().toString());

			// showMap(envelope, annotation);

			// } catch (IllegalParametersException e) {
			// e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	private Polygon createBoundingPolygon(FeatureCollection polygons) {
//		Geometry multi = new GeometryFactory()
//				.createMultiPolygon(getPolygonArray(polygons));
//		return (Polygon) multi.union();
//	}

	private Polygon[] getPolygonArray(FeatureCollection polygonFeatures) {
		List<Polygon> polygonList = new ArrayList<Polygon>();
		List<Feature> featureList = polygonFeatures.getFeatures();
		for (Feature feature : featureList) {
			polygonList.add((Polygon) feature.getGeometry());
		}
		return polygonList.toArray(new Polygon[0]);
	}

	private GridCoverage2D createRaster(FeatureCollection background,
			AbstractAnnotation annotation, Envelope envelope,
			CoordinateReferenceSystem crs, int rasterWidth, double particleRatio, double angle, ReferencedEnvelope bounds)
			throws IOException, NoSuchAuthorityCodeException, FactoryException {
		int rasterHeight = (int) (rasterWidth * envelope.getHeight() / envelope
				.getWidth());
		LOG.info("creating a raster of " + rasterWidth + " x " + rasterHeight);
		BufferedImage image = new BufferedImage(rasterWidth, rasterHeight,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setColor(BACKGROUND_COLOR);
		g2d.drawRect(0,0,rasterWidth, rasterHeight);

		if (RANDOM_ANGLE) {
			AffineTransform at = new AffineTransform();
			angle = ANGLES[(int) (Math.random() * (ANGLES.length - 1))];
			at.translate(image.getWidth() / 2, image.getHeight() / 2);
			at.rotate(angle);
			at.translate(-image.getWidth() / 2, -image.getHeight() / 2);
			// AffineTransformOp op = new AffineTransformOp(at,
			// AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			// image = op.filter(image, null);
			g2d.setTransform(at);
		} 
		
		annotation.draw(g2d, image.getWidth(), image.getHeight(), bounds);
		
		if (background != null) {
			drawBackground(background, annotation, envelope, g2d);
			annotation.draw(g2d, image.getWidth(), image.getHeight(), bounds);
		}
		if (DRAW_AREAS) {
			drawAreas(annotation, g2d);
			drawAreaLabels(annotation, angle, g2d);
		}
		
		if (RASTER_WIDTH != OUTPUT_RASTER_WIDTH) {
			int scaledWidth = OUTPUT_RASTER_WIDTH;
			int scaledHeight = (int) (((double) OUTPUT_RASTER_WIDTH)
					/ RASTER_WIDTH * image.getHeight());
			image = scaleImage(image, scaledWidth, scaledHeight,
					BACKGROUND_COLOR);
		}
		GridCoverage2D coverage = createCoverage(envelope, crs, image);

		LOG.debug("raster bounding box: " + coverage.getEnvelope());
		return coverage;
	}

	public BufferedImage scaleImage(BufferedImage img, int width, int height,
			Color background) {
		int imgWidth = img.getWidth();
		int imgHeight = img.getHeight();
		if (imgWidth * height < imgHeight * width) {
			width = imgWidth * height / imgHeight;
		} else {
			height = imgHeight * width / imgWidth;
		}
		BufferedImage newImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = newImage.createGraphics();
		try {
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.setBackground(background);
			g.clearRect(0, 0, width, height);
			g.drawImage(img, 0, 0, width, height, null);
		} finally {
			g.dispose();
		}
		return newImage;
	}

	private GridCoverage2D createCoverage(Envelope envelope,
			CoordinateReferenceSystem crs, BufferedImage image) {
		GridCoverageFactory factory = new GridCoverageFactory();
		double minX = envelope.getMinX();
		double maxX = envelope.getMaxX();
		double minY = envelope.getMinY();
		double maxY = envelope.getMaxY();

		org.opengis.geometry.Envelope bBox = new ReferencedEnvelope(minX, maxX,
				minY, maxY, crs);
		GridCoverage2D coverage = factory.create("GridCoverage", image, bBox);
		return coverage;
	}

	private void drawBackground(FeatureCollection background,
			AbstractAnnotation annotation, Envelope envelope, Graphics2D g2d) {
		g2d.setColor(Color.WHITE);
		this.shiftColors = RANDOM_CLASS_COLORS ? (int) Math.round(Math.random() * CLASS_COLORS.length) : 0;
		List features = background.getFeatures();
		for (int i = 0; i < features.size(); i++) {
			Feature feature = (Feature) features.get(i);
			drawPolygonFeature(g2d, feature, annotation, envelope);
		}
		for (int i = 0; i < features.size(); i++) {
			Feature feature = (Feature) features.get(i);
			drawPolygonFeatureNoHoles(g2d, feature, annotation, envelope);
		}
	}

	private void drawAreas(AbstractAnnotation annotation, Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(2));
		int widthPx = annotation.getWidthPx(AREA_SIZE);
		int heightPx = annotation.getHeightPx(AREA_SIZE);
		for (int i = 0; i < AREA_CENTERS.length; i++) {
			int xPx = annotation.getXCoordPx(AREA_CENTERS[i][0] - AREA_SIZE
					/ 2.);
			int yPx = annotation.getYCoordPx(AREA_CENTERS[i][1] + AREA_SIZE
					/ 2.);
			g2d.drawRect(xPx, yPx, widthPx, heightPx);

		}
	}
	
	private void drawAreaLabels(AbstractAnnotation annotation, double angle, Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(2));
		int widthPx = annotation.getWidthPx(AREA_SIZE);
		int heightPx = annotation.getHeightPx(AREA_SIZE);
		// Derive a new font using a rotation transform
		AffineTransform fontAT = new AffineTransform();
	    fontAT.rotate(-angle);
	    Font reversedFont = AREA_FONT.deriveFont(fontAT);
		for (int i = 0; i < AREA_CENTERS.length; i++) {
			int xPx = annotation.getXCoordPx(AREA_CENTERS[i][0] - AREA_SIZE / 2.);
			int yPx = annotation.getYCoordPx(AREA_CENTERS[i][1] + AREA_SIZE / 2.);

			String name = AREA_NAMES[i];
			g2d.setFont(reversedFont);
			int fontHeightPx = g2d.getFontMetrics(AREA_FONT).getAscent();
			
			int charX = xPx + widthPx + 12;
			int charY = yPx + fontHeightPx;
			
			if (angle == Math.PI / 2.) {
				charX = xPx - g2d.getFontMetrics(AREA_FONT).getDescent();
				charY = yPx + heightPx;
			} else if (angle == Math.PI) {
				charX = xPx - 12;
				charY = yPx + heightPx - fontHeightPx;
			} else if (angle == 3./2.*Math.PI) {
				charX = xPx + widthPx - g2d.getFontMetrics(AREA_FONT).getAscent();
				charY = yPx + heightPx + 12;
			}
			
			g2d.drawChars(name.toCharArray(), 0, 1, charX , charY);
		}
	}

	private void drawPolygonFeature(Graphics2D g2d, Feature feature,
			AbstractAnnotation annotation, Envelope envelope) {
		LineString outerRing = ((com.vividsolutions.jts.geom.Polygon) feature
				.getGeometry()).getExteriorRing();
		java.awt.Polygon polygon;
		polygon = createPolygon(outerRing, annotation, envelope);
		g2d.setColor(getColor(feature));
		g2d.fill(polygon);
	}

	private void drawPolygonFeatureNoHoles(Graphics2D g2d, Feature feature,
			AbstractAnnotation annotation, Envelope envelope) {
		java.awt.Polygon polygon;
		int numHoles = ((com.vividsolutions.jts.geom.Polygon) feature
				.getGeometry()).getNumInteriorRing();
		// for (int i = 0; i < numHoles; i++) {
		// LineString interiorRing = ((com.vividsolutions.jts.geom.Polygon)
		// feature
		// .getGeometry()).getInteriorRingN(i);
		// polygon = createPolygon(interiorRing, annotation, envelope);
		// g2d.setColor(BACKGROUND_COLOR);
		// g2d.fill(polygon);
		// }
		if (numHoles == 0) {
			LineString outerRing = ((com.vividsolutions.jts.geom.Polygon) feature
					.getGeometry()).getExteriorRing();
			polygon = createPolygon(outerRing, annotation, envelope);
			g2d.setColor(getColor(feature));
			g2d.fill(polygon);
		}
	}

	private Color getColor(Feature feature) {
		int classCode = (Integer) feature.getAttribute("classcode");
		int color = (classCode + shiftColors) % (CLASS_COLORS.length - 1);
		LOG.debug("color no. " + color);
		return CLASS_COLORS[color];
	}

	private java.awt.Polygon createPolygon(LineString outerRing,
			AbstractAnnotation annotation, Envelope envelope) {
		java.awt.Polygon polygon = new java.awt.Polygon();
		Coordinate coord;
		int xPx;
		int yPx;
		for (int i = 0; i < outerRing.getCoordinates().length; i++) {
			coord = outerRing.getCoordinates()[i];
			xPx = annotation.getXCoordPx(coord.x);
			yPx = annotation.getYCoordPx(coord.y);
			polygon.addPoint(xPx, yPx);
		}
		return polygon;
	}

	private void createShapefile(FeatureCollection collection)
			throws IllegalParametersException, Exception {
		ShapefileWriter writer = new ShapefileWriter();
		DriverProperties props = new DriverProperties();
		props.set(ShapefileWriter.FILE_PROPERTY_KEY, "c:\\temp\\out-noise.shp");
		props.set(ShapefileWriter.SHAPE_TYPE_PROPERTY_KEY, "xy");
		writer.write(collection, props);
	}

	public void drawGeometry(Graphics2D g2d, Geometry geom,
			AffineTransform transform) {
		Coordinate center = geom.getCoordinate();
		double size = 1;
		Coordinate[] coordinates = new Coordinate[] {
				new Coordinate(center.x, center.y - size / 2.),
				new Coordinate(center.x, center.y + size / 2.),
				new Coordinate(center.x + size / 2., center.y + size / 2.),
				new Coordinate(center.x + size / 2., center.y - size / 2.),
				new Coordinate(center.x, center.y - size / 2.), };
		Geometry box = new GeometryFactory().createLinearRing(coordinates);
		LiteShape shape = new LiteShape(box, transform, false);
		g2d.fill(shape);
	}

}

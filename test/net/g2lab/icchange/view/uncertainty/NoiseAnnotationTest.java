package net.g2lab.icchange.view.uncertainty;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import net.g2lab.io.RasterIO;
import net.g2lab.layer.Layer;
import net.g2lab.layer.RasterLayer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.gce.image.WorldImageWriter;
import org.geotools.geometry.jts.LiteShape;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.coverage.grid.Format;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
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

public class NoiseAnnotationTest extends AnnotationPanelTestBase {

	private static final Color BACKGROUND_COLOR = new Color(150, 150, 150);

	private static final Log LOG = LogFactory.getLog(Class.class.getName());

	private static final int FRAME_WIDTH = 800;

	/** How many pixels per raster unit (e.g., per meter) */
	private static final double RASTER_PX_PER_UNIT = 10;
//	20;

	private static final String CRS_EPSG_CODE = "EPSG:25832" // ETRS89 UTM32
	// "EPSG:4647" // ETRS89 UTM32 8digit
	// "EPSG:32632" // WGS84 UTM32
	// "EPSG:4326" // WGS84 geographic
	;

	public static final String DIR =
	// "C:\\temp\\tub\\120510_Classif_Biotope\\"
	// "C:\\temp\\kn\\"
//	 "C:/temp/ubonn/es/"
//	"C:/temp/tub/es"
//	 "C:\\temp\\icc\\0\\"
	// "c:/temp/icchange/"
			""
	;

	public static final String OUT_SUBDIR = "a8nlines/";

	public static final boolean DRAW_BACKGROUND = false;
	public static String BG_FILENAME = "0.shp";

	public static String UNCERTAINTY_FILENAME =
	// "uncert_clip-3.tif"
	// "raster-0-20.tif"
	// "raster-40-60.tif"
	// "raster-mixed.tif"
	// "raster-20-100.tif"
//	 "f-esch-u-25832.jpg"
//	"es100716-u-25832.jpg"
	// "es-sub-u.tif"
	// "f-sub-uncertainty.tif"
	// "S-sub-u-obj.tif"
	// "f-esch-u-4326_subset.tif"

	// "1-u-obj-int.tif"
	// "1-u-4class.tif"
	// "1-u-5class-1.tif"
	// "1-u-obj-1-5class.tif"
	// "1-u-obj-1a-5class.tif"
	// "1-u-obj-2-5class.tif"
	// "1-u-cont-1-5class.tif"
	// "0-20-100.tif"
	// "0-0_50_100.tif"
	 "50.tif"
	;

	private static final double[] GRID_WIDTH = { 10
//		80, 40, 20, 10, 5 
		};

	/**
	 * number of pixels per meter of grid
	 */
	private static final double[] PARTICLE_SIZE = {
	// 20 // kn
//	8 // kliwas
		// 20 // icc
//		 10// icc
		10
	};

	/**
	 * width of noise band in percent of grid width
	 */
	private static final int[] BAND_WIDTH = { 40, 50 };

	private static final Color[] CLASS_COLORS = new Color[] {
			new Color(178, 223, 138), new Color(166, 206, 227),
			new Color(31, 120, 180), new Color(178, 223, 138),
			new Color(51, 160, 44) };

	private static final double[] ANGLES = { 0, Math.PI / 2., Math.PI,
			3. / 2. * Math.PI, 2. * Math.PI };

	private static boolean RANDOM_CLASS_COLORS = true;

	private static boolean RANDOM_ANGLE = false;

	private int shiftColors = 0;

	public static void main(String[] args) {
		URL inURL;
		try {
			inURL = new File(DIR + UNCERTAINTY_FILENAME).toURI().toURL();
			URL outURL = new File(DIR).toURI().toURL();
			for (double gridSize : GRID_WIDTH) {
				for (int bandWidth : BAND_WIDTH) {
					for (double particleRatio : PARTICLE_SIZE) {
						new NoiseAnnotationTest(inURL, outURL, gridSize,
								bandWidth, particleRatio);
					}
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	public NoiseAnnotationTest(URL inURL, URL outURL, double gridSize,
			int bandWidth, double particleRatio) {
		try {

			GridCoverage2D uncertainty = new RasterIO().readCoverage(inURL);
			Layer uncertaintyLayer = new RasterLayer(uncertainty);

			ShapefileReader reader = new ShapefileReader();

			FeatureCollection background = null;
			if (DRAW_BACKGROUND) {
				background = reader
						.read(new DriverProperties(DIR + BG_FILENAME));
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

			LOG.info("Computing annotations.");
			boolean isDegrees = CRS_EPSG_CODE.compareTo("EPSG:4326") == 0;
			NoiseAnnotation annotation = new NoiseAnnotation(uncertaintyLayer,
					gridSize, isDegrees);
			annotation.setMaxNoiseWidth(bandWidth);

			CoordinateReferenceSystem crs = CRS.decode(CRS_EPSG_CODE);
			LOG.debug("output crs: " + crs);

			int rasterWidthPx = (int) (envelope.getWidth() * RASTER_PX_PER_UNIT / gridSize);

			String outDir = outURL.getFile() + OUT_SUBDIR;
			if (!new File(outDir).exists()) {
				new File(outDir).mkdir();
			}
			String outPath = outDir + UNCERTAINTY_FILENAME.split("\\.")[0]
					+ "-al-" + ((int) gridSize) + "_" + ((int) particleRatio)
					+ "_" + (int) RASTER_PX_PER_UNIT
					// + ".tif"
					+ ".jpg";

			GridCoverage2D outRaster = createRaster(background, annotation,
					envelope, crs, rasterWidthPx, outPath);
			File file = new File(outPath);
			WorldImageWriter writer = new WorldImageWriter(file);
			final Format writerParams = writer.getFormat();
			final ParameterValueGroup writeParameters = writerParams
					.getWriteParameters();
			final ParameterValue format = writeParameters.parameter("Format");
			format.setValue("jpg");

			// GeoTiffWriter writer = new GeoTiffWriter(file);
//			writer.write(outRaster, null);
//			LOG.info("written raster to " + writer.getDestination().toString());

			 showMap(envelope, annotation);

			// } catch (IllegalParametersException e) {
			// e.printStackTrace();
		} catch (Exception e) {
			LOG.error(e, e);
		}
	}



	private Polygon[] getPolygonArray(FeatureCollection polygonFeatures) {
		List<Polygon> polygonList = new ArrayList<Polygon>();
		List<Feature> featureList = polygonFeatures.getFeatures();
		for (Feature feature : featureList) {
			polygonList.add((Polygon) feature.getGeometry());
		}
		return polygonList.toArray(new Polygon[0]);
	}

	private void showMap(Envelope envelope, AbstractAnnotation annotation) {
		AnnotationPanel map = new AnnotationPanel(annotation, envelope);
		map.setBackground(BACKGROUND_COLOR);
		map.setDrawPolygons(true);
		map.setDrawAnnotations(true);
		map.setDrawPoints(false);
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(map);
		frame.setSize(getDimension(FRAME_WIDTH, envelope));
		frame.setVisible(true);
	}

	private GridCoverage2D createRaster(FeatureCollection background,
			AbstractAnnotation annotation, Envelope envelope,
			CoordinateReferenceSystem crs, int rasterWidth, String outPath)
			throws IOException, NoSuchAuthorityCodeException, FactoryException {
		int rasterHeight = (int) (rasterWidth * envelope.getHeight() / envelope
				.getWidth());
		LOG.info("creating a raster of " + rasterWidth + " x " + rasterHeight);
		BufferedImage image = new BufferedImage(rasterWidth, rasterHeight,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = (Graphics2D) image.getGraphics();

		if (RANDOM_ANGLE) {
			AffineTransform at = new AffineTransform();
			double angle = ANGLES[(int) (Math.random() * (ANGLES.length - 1))];
			at.translate(image.getWidth() / 2, image.getHeight() / 2);
			at.rotate(angle);
			at.translate(-image.getWidth() / 2, -image.getHeight() / 2);
			// AffineTransformOp op = new AffineTransformOp(at,
			// AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			// image = op.filter(image, null);
			g2d.setTransform(at);
		}

//		annotation.draw(g2d, image.getWidth(), image.getHeight(), envelope);
		if (DRAW_BACKGROUND) {
			drawBackground(background, annotation, envelope, g2d);
//			annotation.draw(g2d, image.getWidth(), image.getHeight(), envelope);
		}
		GridCoverage2D coverage = createCoverage(envelope, crs, image);

		LOG.debug("raster bounding box: " + coverage.getEnvelope());
		return coverage;
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
		g2d.setColor(Color.BLACK);
		this.shiftColors = RANDOM_CLASS_COLORS ? (int) (Math.random()
				* CLASS_COLORS.length - 1) : 0;
		List features = background.getFeatures();
		for (int i = 0; i < features.size(); i++) {
			Feature feature = (Feature) features.get(i);
			drawPolygonFeature(g2d, feature, annotation, envelope);
		}
	}

	private void drawPolygonFeature(Graphics2D g2d, Feature feature,
			AbstractAnnotation annotation, Envelope envelope) {
		LineString outerRing = ((com.vividsolutions.jts.geom.Polygon) feature
				.getGeometry()).getExteriorRing();
		java.awt.Polygon polygon;
		int numHoles = ((com.vividsolutions.jts.geom.Polygon) feature
				.getGeometry()).getNumInteriorRing();
		for (int i = 0; i < numHoles; i++) {
			LineString interiorRing = ((com.vividsolutions.jts.geom.Polygon) feature
					.getGeometry()).getInteriorRingN(i);
			polygon = createPolygon(interiorRing, annotation, envelope);
			g2d.setColor(BACKGROUND_COLOR);
			g2d.fill(polygon);
		}

		polygon = createPolygon(outerRing, annotation, envelope);
		g2d.setColor(getColor(feature));
		g2d.fill(polygon);
	}

	private Color getColor(Feature feature) {
		int classCode = (Integer) feature.getAttribute("classcode");
		return CLASS_COLORS[(classCode + shiftColors) % (CLASS_COLORS.length)];
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

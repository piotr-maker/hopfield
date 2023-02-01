package hopfield;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


public class PaintPanel extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	private Color color = Color.BLACK;
	private Color bgColor = Color.WHITE;
	private int previousPenX = -1;
	private int previousPenY = -1;
	private BufferedImage image;
	private boolean paintable = true;

	private int width = 400;
	private int height = 400;

	public PaintPanel() {
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		clear();
	}

	public BufferedImage getScaledBufferedImage(int width, int height) {
		BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		double [] data = serialize(width, height);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int index = x * width + y;
				int grayScale = 255 - (int)(data[index] * 255.0);
				Color gray = new Color(grayScale, grayScale, grayScale);
				scaledImage.setRGB(x, y, gray.getRGB());
			}
		}
		return scaledImage;
	}

	public static int [] serializeScaledImage(BufferedImage scaledImage) {
		int width = scaledImage.getWidth();
		int height = scaledImage.getHeight();
		int [] data = new int[width * height];

		int index = 0;
		for(int x = 0; x < scaledImage.getWidth(); x++) {
			for(int y = 0; y < scaledImage.getHeight(); y++) {
				Color color = new Color(scaledImage.getRGB(x, y));
				data[index] = color.getRed() < 220 ? 1 : 0;
				index++;
			}
		}
		return data;
	}

	public void deserialize(int cols, int rows, int [] data) {
		int horizontalSections = (int)image.getWidth() / cols;
		int verticalSections = (int)image.getHeight() / rows;

		for(int x = 0; x < cols; x++) {
			for(int y = 0; y < rows; y++) {
				if(data[x * cols + y] != 1)
					continue;
				for (int i = 0; i < horizontalSections; i++) {
					for (int j = 0; j < verticalSections; j++ ) {
						image.setRGB(x * horizontalSections + i, y * verticalSections + j, Color.BLACK.getRGB());
					}
				}
			}
		}
	}

	private double [] serialize(int cols, int rows) {
		double [] data = new double[cols * rows];
		int horizontalSections = (int)image.getWidth() / cols;
		int verticalSections = (int)image.getHeight() / rows;
		int index = 0;
		for (int x = 0; x < cols; x++) {
			for (int y = 0; y < rows; y++) {
				int pixelCounter = 0;
				for (int i = 0; i < horizontalSections; i++) {
					for (int j = 0; j < verticalSections; j++ ) {
						if(image.getRGB(x * horizontalSections + i, y * verticalSections + j) == color.getRGB()) {
							pixelCounter++;
						}
					}
				}
				double avarage = ((255 * pixelCounter)/ (horizontalSections * verticalSections) / 255.0);
				data[index] = avarage;
				index++;  
			}
		}
		return data;
	}

	public void clear() {
		previousPenX = -1;
		previousPenY = -1;
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				image.setRGB(i, j, bgColor.getRGB());
			}
		}
		repaint();
	}
	
	public void disablePaint() {
		this.paintable = false;
	}
	
	public void enablePaint() {
		this.paintable = true;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, this);
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		if(previousPenX != -1 && previousPenY != -1 && paintable) {
			Graphics2D g2 = (Graphics2D) image.getGraphics();
			g2.setStroke(new BasicStroke(12));
			g2.setColor(color);
			g2.drawLine(previousPenX, previousPenY, event.getX(), event.getY());
			repaint();
		}

		previousPenX = event.getX();
		previousPenY = event.getY();
	}

	@Override
	public void mouseMoved(MouseEvent event) {

	}

	@Override
	public void mouseClicked(MouseEvent event) {

	}

	@Override
	public void mousePressed(MouseEvent event) {

	}

	@Override
	public void mouseReleased(MouseEvent event) {
		previousPenX = -1;
		previousPenY = -1;
	}

	@Override
	public void mouseEntered(MouseEvent event) {

	}

	@Override
	public void mouseExited(MouseEvent event) {

	}

}
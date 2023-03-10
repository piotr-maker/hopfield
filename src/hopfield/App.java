package hopfield;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.Timer;

import Diagnostics.Debugger;

enum Letter {
	N, W, M, END_OF_LETTERS
}

public class App extends JFrame {
	private static final long serialVersionUID = 1L;
	private int [] data;
	private int width, height;
	private final int MARGIN = 15;
	private final int COLS = 8, ROWS = 8;
	

	private Timer timer;
	private JCheckBox stabilityBox;
	private JRadioButton syncUpdateButton;
	private JRadioButton asyncUpdateButton;
	private Network network;
	private PaintPanel paintPanel = new PaintPanel(COLS, ROWS);
	private PaintPanel outputPanel = new PaintPanel(COLS, ROWS);

	public App(String title) {
		super(title);
		width = paintPanel.getPreferredSize().width * 2 + 4 * MARGIN + 115;
		height = paintPanel.getPreferredSize().height + 4 * MARGIN + 10;
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension dimensions = kit.getScreenSize();
		setBounds((dimensions.width - width)/2, (dimensions.height-height)/2, width, height);
		setLayout(new FlowLayout(FlowLayout.LEADING, MARGIN, MARGIN));
		setResizable(false);
		outputPanel.disablePaint();
		stabilityBox = new JCheckBox("Stabilizacja");
		stabilityBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				learn(e.getStateChange() == 1);
			}
		});
		
		learn(false);
		timer = new Timer(20,new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				data = network.testAsync(data);
				outputPanel.deserialize(COLS, ROWS, data);
				outputPanel.repaint();
			}
		});
		
		JButton playButton = new JButton("Play");
		playButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				BufferedImage image = paintPanel.getScaledBufferedImage(COLS, ROWS);
				data = PaintPanel.serializeScaledImage(image);
				if(asyncUpdateButton.isSelected()) {
					if(playButton.getText() == "Play") {
						playButton.setText("Stop");
						timer.start();
					} else {
						playButton.setText("Play");
						timer.stop();
					}
				} else {
					data = network.testSync(data);
				}
				outputPanel.deserialize(COLS, ROWS, data);
				outputPanel.repaint();
			}
		});
		
		JButton clearButton = new JButton("Wyczy????");
		clearButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				playButton.setText("Play");
				timer.stop();
				paintPanel.clear();
				outputPanel.clear();
			}
		});
		
		ButtonGroup lettersButtonGroup = new ButtonGroup();
		syncUpdateButton = new JRadioButton("Akt. synch.");
		syncUpdateButton.setSelected(true);
		asyncUpdateButton = new JRadioButton("Akt. asynch.");
		asyncUpdateButton.setSelected(false);
		lettersButtonGroup.add(syncUpdateButton);
		lettersButtonGroup.add(asyncUpdateButton);
		
		int rows = Math.round((float)(paintPanel.getPreferredSize().height) / (float)((playButton.getPreferredSize().height + 5)));
		GridLayout panelLayout = new GridLayout(rows, 1, 0, 5);
		JPanel menuPanel = new JPanel(panelLayout);
		menuPanel.add(stabilityBox);
		menuPanel.add(syncUpdateButton);
		menuPanel.add(asyncUpdateButton);
		menuPanel.add(playButton);
		menuPanel.add(clearButton);

		add(menuPanel);
		add(paintPanel);
		add(outputPanel);
		setVisible(true);
	}
	
	protected void learn(boolean stabilityOn) {
		network = new Network(COLS, ROWS);
		for(int i = 0; i < Letter.END_OF_LETTERS.ordinal(); i++) {
			int [] data; 
			data = loadImage(Letter.values()[i]);
			network.learn(stabilityOn, data);
		}
	}
	
	protected int [] loadImage(Letter letter) {
		String file = new String("assets/" + letter.toString() + ".png");
		BufferedImage image;
		try (FileInputStream fis = new FileInputStream(file)) {
			image = ImageIO.read(fis);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Nie znaleziono pliku: " + file, "", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		int [] data = PaintPanel.serializeScaledImage(image);
		return data;
	}
	
	public static void main(String [] args) {
		Debugger.setEnabled(true);
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new App("Hopfield network");
			}
		});
	}
}

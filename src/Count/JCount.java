package Count;

import javax.swing.*;
import java.awt.*;

public class JCount extends JPanel {
	private static final int NUMBER_OF_COUNTERS = 4;
	private static final int SLEEP = 100; // ms
	private static final int REFRESH_RATE = 10000;

	private JLabel label;
	private JTextField textField;
	private JButton start;
	private JButton stop;
	private WorkerThread worker;

	/**
	 * Constructor.
	 */
	public JCount() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		addGraphics();
		addListeners();
	}

	/**
	 * Adds button listeners.
	 */
	private void addListeners() {
		start.addActionListener(e -> {
			if (worker != null) {
				worker.interrupt();
			}
			worker = new WorkerThread(Integer.parseInt(textField.getText()));
			worker.start();
		});

		stop.addActionListener(e -> {
			if (worker != null) {
				worker.interrupt();
			}
		});
	}

	/**
	 * Adds buttons, fields and labels to the window.
	 */
	private void addGraphics() {
		textField = new JTextField("0", 5);
		label = new JLabel("0");
		start = new JButton("Start");
		stop = new JButton("Stop");

		add(textField);
		add(label);
		add(start);
		add(stop);
		add(Box.createRigidArea(new Dimension(0, 40)));
	}

	/**
	 * Crates and shows GUI. :-)
	 */
	private static void createAndShowGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {
		}

		JFrame frame = new JFrame();
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		for (int i = 0; i < NUMBER_OF_COUNTERS; i++) {
			frame.add(new JCount());
		}

		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> createAndShowGUI());
	}

	private class WorkerThread extends Thread {
		private int value;

		public WorkerThread(int value) {
			this.value = value;
		}

		@Override
		public void run() {
			for (int i = 0; i <= value; i++) {
				if (i % REFRESH_RATE == 0) {
					try {
						sleep(SLEEP);
					} catch (InterruptedException e) {
						return;
					}

					final String value = Integer.toString(i);
					SwingUtilities.invokeLater(() -> label.setText(value));
				}
			}
		}
	}
}

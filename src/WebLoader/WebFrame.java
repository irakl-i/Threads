package WebLoader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;


public class WebFrame extends JFrame {
	private static final int WINDOW_WIDTH = 700;
	private static final int WINDOW_HEIGHT = 500;

	private DefaultTableModel model;
	private JTable table;
	private JPanel panel;

	private JButton singleThreadButton;
	private JButton concurrentButton;
	private JButton stopButton;

	private JLabel runningLabel;
	private JLabel completedLabel;
	private JLabel elapsedLabel;

	private JProgressBar progressBar;

	private JTextField textField;

	private String fileName;
	private boolean running;
	private int runningThreads;
	private int completedThreads;
	private long start;

	private Semaphore workerLock;
	private Semaphore launcherLock;
	private List<WebWorker> workers;
	private Thread launcher;


	/**
	 * Constructor
	 *
	 * @param title    of window
	 * @param fileName
	 */
	public WebFrame(String title, String fileName) {
		super(title);

		// System look.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {
		}

		// Initialize instance variables.
		this.completedThreads = 0;
		this.workers = new ArrayList<>();
		this.runningThreads = 0; // Not counting the main and the swing thread, although we could.
		this.running = false; // Threads are not running yet.
		this.fileName = fileName;
		this.launcherLock = new Semaphore(1);

		// Add graphics elements.
		addTable();
		addButtons();
		addLabels();
		addMisc();
		add(panel);

		loadFile();
		addListeners();


		// Default stuff.
		setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		setLocationByPlatform(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new WebFrame("WebLoader", "files/links2.txt"));
	}

	/**
	 * Adds text field and progress bar to the panel.
	 */
	private void addMisc() {
		textField = new JTextField();
		textField.setMaximumSize(new Dimension(50, 10));
		progressBar = new JProgressBar(0, table.getRowCount());

		panel.add(textField);
		panel.add(progressBar);

		// This needs to be here because we're adding
		// the button at the bottom of the screen
		panel.add(stopButton);
	}

	/**
	 * Adds labels to the panel.
	 */
	private void addLabels() {
		runningLabel = new JLabel("Running: ");
		completedLabel = new JLabel("Completed: ");
		elapsedLabel = new JLabel("Elapsed: ");

		panel.add(runningLabel);
		panel.add(completedLabel);
		panel.add(elapsedLabel);
	}

	/**
	 * Adds buttons to the panel.
	 */
	private void addButtons() {
		singleThreadButton = new JButton("Single Thread Fetch");
		concurrentButton = new JButton("Concurrent Fetch");
		stopButton = new JButton("Stop");

		panel.add(singleThreadButton);
		panel.add(concurrentButton);
	}

	/**
	 * Adds table to the window.
	 */
	private void addTable() {
		model = new DefaultTableModel(new String[]{"url", "status"}, 0);
		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(600, 300));

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(scrollPane);
	}

	/**
	 * Adds button listeners.
	 */
	private void addListeners() {
		// Single thread button.
		singleThreadButton.addActionListener(e -> {
			if (running) return;
			reset();
			clearTable();
			start(1);
		});

		// Concurrent threads button.
		concurrentButton.addActionListener(e -> {
			if (running) return;
			int count = Integer.parseInt(textField.getText());
			reset();
			clearTable();
			start(count);
		});

		// Stop button.
		stopButton.addActionListener(e -> {
			new Thread(() -> {
				try {
					launcherLock.acquire();
				} catch (InterruptedException ignored) {
				}
				if (launcher != null) launcher.interrupt();
				for (WebWorker worker : workers) {
					worker.interrupt();
				}
				launcherLock.release();
			}).start();
		});
	}

	/**
	 * Prepares the GUI and launches threads.
	 *
	 * @param count number of threads
	 */
	private void start(final int count) {
		// Launch the threads.
		launchThreads(count);

		// Update graphics and instance variables.
		running = true;

		singleThreadButton.setEnabled(false);
		concurrentButton.setEnabled(false);
		stopButton.setEnabled(true);

		progressBar.setMaximum(model.getRowCount());
	}

	/**
	 * Launches threads from a new thread, and marks the start time.
	 *
	 * @param count number of threads
	 */
	private void launchThreads(final int count) {
		launcher = new Thread(() -> {
			try {
				launcherLock.acquire();
			} catch (InterruptedException ignored) {
			}
			workerLock = new Semaphore(count);
			// Increment running threads, because this thread is running.
			runningThreads++;
			// Construct each worker.
			for (int i = 0; i < model.getRowCount(); i++) {
				if (launcher.isInterrupted()) return;
				workers.add(new WebWorker((String) model.getValueAt(i, 0), i, WebFrame.this));
			}

			// Run workers.
			for (WebWorker worker : workers) {
				if (launcher.isInterrupted()) return;
				// This limits how many threads can be running concurrently.
				try {
					workerLock.acquire();
				} catch (InterruptedException ignored) {
				}

				worker.start();
				runningThreads++;
				// Update running threads label.
				SwingUtilities.invokeLater(() -> runningLabel.setText("Running: " + runningThreads));
			}

			// Decrement running threads if the launcher thread is finished.
			runningThreads--;
			launcherLock.release();
		});
		start = System.currentTimeMillis(); // Mark the start.
		launcher.start();
	}

	/**
	 * Updates the table's 1st column with status.
	 *
	 * @param status data
	 * @param row
	 */
	public void update(String status, int row) {
		// Release the semaphore, because we know that if we're at this
		// point that means one of the threads is done working.
		workerLock.release();

		// Update the value.
		model.setValueAt(status, row, 1);

		// Update the labels and progress bar.
		completedLabel.setText("Completed: " + ++completedThreads);
		runningLabel.setText("Running: " + --runningThreads);
		progressBar.setValue(completedThreads);

		// Checks if there are no more running threads,
		// update the elapsed label and resets GUI.
		if (runningThreads == 0) {
			elapsedLabel.setText("Elapsed: " + (System.currentTimeMillis() - start) + "ms");
			if (running) reset();
		}
	}

	/**
	 * Toggles the buttons.
	 */
	private void toggleButtons() {
		singleThreadButton.setEnabled(!singleThreadButton.isEnabled());
		concurrentButton.setEnabled(!concurrentButton.isEnabled());
		stopButton.setEnabled(!stopButton.isEnabled());
	}

	/**
	 * Resets the GUI.
	 */
	private void reset() {
		toggleButtons();
		progressBar.setValue(0);

		completedThreads = 0;
		runningThreads = 0;
		running = false;

		workers.clear();
	}

	/**
	 * Removes old entries from the 1st row of the table.
	 */
	private void clearTable() {
		elapsedLabel.setText("Elapsed: ");
		for (int i = 0; i < model.getRowCount(); i++) {
			model.setValueAt("", i, 1);
			model.fireTableDataChanged();
		}
	}

	/**
	 * Loads and reads file line-by-line.
	 */
	private void loadFile() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line;

			while ((line = reader.readLine()) != null) {
				Object[] data = {line, ""}; // Table model takes Object[] arguments
				model.addRow(data);
			}
			// Close the file after we're done.
			reader.close();
		} catch (Exception ignored) {
		}
	}
}

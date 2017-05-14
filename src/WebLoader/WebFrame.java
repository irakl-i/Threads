package WebLoader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.Semaphore;


public class WebFrame extends JFrame {
	private static final int WINDOW_WIDTH = 700;
	private static final int WINDOW_HEIGHT = 500;

	private String fileName;
	private DefaultTableModel model;
	private JTable table;
	private JPanel panel;
	private JButton singleButton;
	private JButton concurrentButton;
	private JButton stopButton;
	private JTextField textField;
	private JLabel runningLabel;
	private JLabel completedLabel;
	private JLabel elapsedLabel;
	private JProgressBar progressBar;
	private boolean running;
	private int runningThreads;
	private Semaphore semaphore;

	public WebFrame(String title, String fileName) {
		super(title);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {
		}

		this.runningThreads = 0;
		this.running = false;
		this.fileName = fileName;

		model = new DefaultTableModel(new String[]{"url", "status"}, 0);
		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		JScrollPane scrollpane = new JScrollPane(table);
		scrollpane.setPreferredSize(new Dimension(600, 300));

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(scrollpane);

		singleButton = new JButton("Single Thread Fetch");
		concurrentButton = new JButton("Concurrent Fetch");
		stopButton = new JButton("Stop");
		textField = new JTextField();
		textField.setMaximumSize(new Dimension(50, 10));
		runningLabel = new JLabel("Running: 0");
		completedLabel = new JLabel("Completed: 0");
		elapsedLabel = new JLabel("Elapsed: 0.0");
		progressBar = new JProgressBar(0, table.getRowCount());

		panel.add(singleButton);
		panel.add(concurrentButton);
		panel.add(textField);
		panel.add(runningLabel);
		panel.add(completedLabel);
		panel.add(elapsedLabel);
		panel.add(progressBar);
		panel.add(stopButton);

		loadFile();
		addListeners();

		//WebWorker worker = new WebWorker("http://freeuni.edu.ge/", 1, this);
		//worker.start();


		add(panel);
		setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		setLocationByPlatform(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	public static void main(String[] args) {
		new WebFrame("WebLoader", "files/links.txt");
	}

	private void addListeners() {
		singleButton.addActionListener(e -> {
			semaphore = new Semaphore(1);
			start();
		});

		concurrentButton.addActionListener(e -> {
			semaphore = new Semaphore(Integer.parseInt(textField.getText()));
			start();
		});
	}

	private void start() {
		running = true;
		startThreads();
		singleButton.setEnabled(false);
		concurrentButton.setEnabled(false);
		stopButton.setEnabled(true);
		progressBar.setMaximum(model.getRowCount());
	}

	private void startThreads() {
		new Thread(() -> {
			int count = semaphore.availablePermits();
			for (int i = 0; i < count; i++) {
				WebWorker worker = new WebWorker((String) model.getValueAt(i, 0), i, WebFrame.this, semaphore);
				worker.start();
				runningThreads++;
			}
		});
	}

	public void update(String status) {
		model.setValueAt(status, 0, 1);
	}

	private void loadFile() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line;

			while ((line = reader.readLine()) != null) {
				Object[] data = {line, ""};
				model.addRow(data);
			}

			reader.close();
		} catch (Exception ignored) {
		}
	}
}

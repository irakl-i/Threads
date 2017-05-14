package WebLoader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


public class WebFrame extends JFrame {
	private static final int WINDOW_WIDTH = 700;
	private static final int WINDOW_HEIGHT = 500;

	private String fileName;
	private DefaultTableModel model;
	private JTable table;
	private JPanel panel;
	private JButton single;
	private JButton concurrent;
	private JButton stop;
	private JTextField textField;
	private JLabel running;
	private JLabel completed;
	private JLabel elapsed;
	private JProgressBar progressBar;

	public WebFrame(String title, String fileName) {
		super(title);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {
		}

		this.fileName = fileName;

		model = new DefaultTableModel(new String[]{"url", "status"}, 0);
		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		JScrollPane scrollpane = new JScrollPane(table);
		scrollpane.setPreferredSize(new Dimension(600, 300));

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(scrollpane);

		single = new JButton("Single Thread Fetch");
		concurrent = new JButton("Concurrent Fetch");
		stop = new JButton("Stop");
		stop.setEnabled(false);
		textField = new JTextField();
		textField.setMaximumSize(new Dimension(50, 20));
		running = new JLabel("Running: 0");
		completed = new JLabel("Completed: 0");
		elapsed = new JLabel("Elapsed: 0.0");
		progressBar = new JProgressBar(0, table.getRowCount());

		panel.add(single);
		panel.add(concurrent);
		panel.add(textField);
		panel.add(running);
		panel.add(completed);
		panel.add(elapsed);
		panel.add(progressBar);
		panel.add(stop);

		loadFile();


		add(panel);
		setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		setLocationByPlatform(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
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

	public static void main(String[] args) {
		new WebFrame("WebLoader", "files/links.txt");
	}
}

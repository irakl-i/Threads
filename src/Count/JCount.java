package Count;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JCount extends JPanel {
	private JLabel label;
	private JTextField textField;
	private JButton start;
	private JButton stop;
	private WorkerThread worker;

	public JCount() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		textField = new JTextField("0", 5);
		label = new JLabel("0");
		start = new JButton("Start");
		stop = new JButton("Stop");

		add(textField);
		add(label);
		add(start);
		add(stop);
		add(Box.createRigidArea(new Dimension(0, 40)));

		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (worker != null) {
					worker.interrupt();
				}
				worker = new WorkerThread(Integer.parseInt(textField.getText()));
				worker.start();
			}
		});

		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(worker != null) {
					worker.interrupt();
				}
			}
		});
	}

	private static void createAndShowGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {
		}

		JFrame frame = new JFrame();
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		for (int i = 0; i < 4; i++) {
			frame.add(new JCount());
		}

		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private class WorkerThread extends Thread {
		private int value;

		public WorkerThread(int value) {
			this.value = value;
		}

		@Override
		public void run() {
			for (int i = 0; i <= value; i++) {
				if(i % 10000 == 0) {
					try {
						sleep(100);
					} catch (InterruptedException e) {
						return;
					}

					final String value = i + "";
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							label.setText(value);
						}
					});
				}

			}
		}
	}
}

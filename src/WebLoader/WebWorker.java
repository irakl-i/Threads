package WebLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WebWorker extends Thread {
	private String url;
	private int row;
	private WebFrame frame;
	private String status;

	public WebWorker(String url, int row, WebFrame frame) {
		this.url = url;
		this.row = row;
		this.frame = frame;
	}

	@Override
	public void run() {
		download();
		frame.update(status, row);
	}

	private void download() {
		InputStream input = null;
		StringBuilder contents = null;
		try {
			long start = System.currentTimeMillis();

			URL url = new URL(this.url);
			URLConnection connection = url.openConnection();

			// Set connect() to throw an IOException
			// if connection does not succeed in this many msecs.
			connection.setConnectTimeout(5000);

			connection.connect();
			input = connection.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			char[] array = new char[1000];
			int len;
			contents = new StringBuilder(1000);
			while ((len = reader.read(array, 0, array.length)) > 0) {
				contents.append(array, 0, len);
				Thread.sleep(100);
			}

			long end = System.currentTimeMillis();
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			status = dateFormat.format(new Date()) + " " + (end - start) + "ms " + contents.length() + " bytes";
		}
		// Otherwise control jumps to a catch...
		catch (IOException ignored) {
			status = "err";
		} catch (InterruptedException exception) {
			status = "interrupted";
		}
		// "finally" clause, to close the input stream
		// in any case
		finally {
			try {
				if (input != null) input.close();
			} catch (IOException ignored) {
			}
		}
	}

}

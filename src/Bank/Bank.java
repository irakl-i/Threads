package Bank;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class Bank {
	private static final int NUM_ACCOUNTS = 20;
	private static final int STARTING_BALANCE = 1000;

	private final String fileName;
	private final int threads;
	private final Transaction nullTrans;

	private BlockingQueue<Transaction> transactions;
	private List<Account> accounts;
	private CountDownLatch latch;

	public Bank(int threads, String fileName) {
		this.threads = threads;
		this.fileName = fileName;
		this.nullTrans = new Transaction(-1, 0, 0);
		this.transactions = new ArrayBlockingQueue<Transaction>(NUM_ACCOUNTS + threads);
		this.accounts = new ArrayList<Account>(NUM_ACCOUNTS);
		this.latch = new CountDownLatch(threads);

		setupAccounts();
		startThreads();
		readFile();
		try {
			latch.await();
		} catch (InterruptedException ignored) {}
		printResults();
	}

	public static void main(String[] args) {
		new Bank(Integer.parseInt(args[1]), args[0]);
	}

	/**
	 * Prints the results.
	 */
	private void printResults() {
		for (Account account : accounts) {
			System.out.println(account);
		}
	}

	/**
	 * Starts threads.
	 */
	private void startThreads() {
		for (int i = 0; i < threads; i++) {
			new Worker().start();
		}
	}

	/**
	 * Loads and reads from file line-by-line.
	 */
	private void readFile() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line;

			while ((line = reader.readLine()) != null) {
				int values[] = parseValues(line);
				transactions.put(new Transaction(values[0], values[1], values[2]));
			}

			for (int i = 0; i < threads; i++) {
				transactions.put(nullTrans);
			}

			reader.close();
		} catch (Exception ignored) {
		}
	}

	/**
	 * Parses values from string
	 * @param line
	 * @return array of ints
	 */
	private int[] parseValues(String line) {
		String[] parts = line.split(" ");
		int[] values = new int[parts.length];

		for (int i = 0; i < parts.length; i++) {
			values[i] = Integer.parseInt(parts[i]);
		}

		return values;
	}

	/**
	 * Sets up accounts.
	 */
	private void setupAccounts() {
		for (int i = 0; i < NUM_ACCOUNTS; i++) {
			accounts.add(new Account(i, STARTING_BALANCE));
		}
	}

	public class Worker extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					Transaction transaction = transactions.take();
					if (transaction.equals(nullTrans)) break; // could be compared with == as well
					makeTransaction(transaction);
				} catch (InterruptedException ignored) {
				}
			}
			latch.countDown();
		}

		/**
		 * Makes the transaction between two accounts.
		 * @param transaction
		 */
		private void makeTransaction(Transaction transaction) {
			Account sender = accounts.get(transaction.getSender());
			Account receiver = accounts.get(transaction.getReceiver());
			int amount = transaction.getAmount();
			sender.withdraw(amount);
			receiver.deposit(amount);
		}
	}
}

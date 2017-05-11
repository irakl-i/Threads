package Bank;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

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

		for (int i = 0; i < threads; i++) {
			new Worker().start();
		}

		readFile();

		for (Account account : accounts) {
			System.out.println(account);
		}

	}

	public static void main(String[] args) {
		new Bank(Integer.parseInt(args[1]), args[0]);
	}

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int[] parseValues(String line) {
		String[] parts = line.split(" ");
		int[] values = new int[parts.length];

		for (int i = 0; i < parts.length; i++) {
			values[i] = Integer.parseInt(parts[i]);
		}

		return values;
	}

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
					if (transaction == nullTrans) break;
					makeTransaction(transaction);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			latch.countDown();
		}

		private void makeTransaction(Transaction transaction) {
			Account sender = accounts.get(transaction.getSender());
			Account receiver = accounts.get(transaction.getReceiver());
			int amount = transaction.getAmount();
			sender.withdraw(amount);
			receiver.deposit(amount);
		}
	}
}

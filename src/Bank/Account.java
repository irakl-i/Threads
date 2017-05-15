package Bank;

public class Account {
	private int id;
	private int balance;
	private int transactions;

	/**
	 * Constructor.
	 *
	 * @param id
	 * @param balance
	 */
	public Account(int id, int balance) {
		this.id = id;
		this.balance = balance;
		this.transactions = 0;
	}

	/**
	 * Return ID.
	 *
	 * @return id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns balance
	 *
	 * @return balance
	 */
	public int getBalance() {
		return balance;
	}

	/**
	 * Returns number of transactions.
	 *
	 * @return number of transactions.
	 */
	public int getTransactions() {
		return transactions;
	}

	/**
	 * Deposits given amount to the account.
	 *
	 * @param amount
	 */
	public synchronized void deposit(int amount) {
		transactions++;
		this.balance += amount;
	}

	/**
	 * Withdraws given amount from the account.
	 *
	 * @param amount
	 */
	public synchronized void withdraw(int amount) {
		transactions++;
		this.balance -= amount;
	}

	@Override
	public String toString() {
		return "Acc:" + getId() + " Bal: " + getBalance() + " Trans: " + getTransactions();
	}
}

package Bank;

public class Account {
	private int id;
	private int balance;
	private int transactions;

	public Account(int id, int balance) {
		this.id = id;
		this.balance = balance;
		this.transactions = 0;
	}

	public int getId() {
		return id;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public int getTransactions() {
		return transactions;
	}

	public void setTransactions(int transactions) {
		this.transactions = transactions;
	}

	public synchronized void deposit(int amount) {
		transactions++;
		this.balance += amount;
	}

	public synchronized void withdraw(int amount) {
		transactions++;
		this.balance -= amount;
	}

	@Override
	public String toString() {
		return "Acc:" + getId() + " Bal: " + getBalance() + " Trans: " + getTransactions();
	}
}

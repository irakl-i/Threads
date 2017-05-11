package Bank;

public class Transaction {
	private final int sender;
	private final int receiver;
	private final int amount;

	public Transaction(int sender, int receiver, int amount) {
		this.sender = sender;
		this.receiver = receiver;
		this.amount = amount;
	}

	public int getAmount() {
		return amount;
	}

	public int getSender() {
		return sender;
	}

	public int getReceiver() {
		return receiver;
	}

	@Override
	public String toString() {
		return sender + " " + receiver + " " + amount;
	}
}

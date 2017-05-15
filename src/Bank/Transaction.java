package Bank;

public class Transaction {
	private final int sender;
	private final int receiver;
	private final int amount;

	/**
	 * Constructor.
	 * @param sender
	 * @param receiver
	 * @param amount
	 */
	public Transaction(int sender, int receiver, int amount) {
		this.sender = sender;
		this.receiver = receiver;
		this.amount = amount;
	}

	/**
	 * Returns amount.
	 * @return amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Returns sender.
	 * @return sender
	 */
	public int getSender() {
		return sender;
	}

	/**
	 * Returns receiver.
	 * @return receiver
	 */
	public int getReceiver() {
		return receiver;
	}

	@Override
	public String toString() {
		return sender + " " + receiver + " " + amount;
	}

	@Override
	public boolean equals(Object obj) {
		Transaction that = (Transaction) obj;
		return that.getAmount() == this.getAmount() && that.getReceiver() == this.getReceiver() && that.getSender() == this.getSender();
	}
}

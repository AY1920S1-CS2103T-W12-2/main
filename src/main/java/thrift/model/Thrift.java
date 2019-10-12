package thrift.model;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import javafx.collections.ObservableList;
import thrift.commons.core.index.Index;
import thrift.model.transaction.Transaction;
import thrift.model.transaction.TransactionList;

/**
 * Wraps all data at the THRIFT level
 */
public class Thrift implements ReadOnlyThrift {

    private final TransactionList transactions;

    /*
     * The 'unusual' code block below is a non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */
    {
        transactions = new TransactionList();
    }

    public Thrift() {}

    /**
     * Creates an THRIFT using the Transaction in the {@code toBeCopied}
     */
    public Thrift(ReadOnlyThrift toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the transaction list with {@code transactions}.
     */
    public void setTransactions(List<Transaction> transactions) {
        this.transactions.setTransactions(transactions);
    }

    /**
     * Resets the existing data of this transactions list with {@code newData}.
     */
    public void resetData(ReadOnlyThrift newData) {
        requireNonNull(newData);

        setTransactions(newData.getTransactionList());
    }

    //// transaction-level operations

    /**
     * Adds a transaction to THRIFT.
     */
    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    /**
     * Adds a transaction to a specified index in THRIFT.
     */
    public void addTransaction(Transaction t, Index index) {
        transactions.add(t, index);
    }

    /**
     * Replaces the given transaction {@code target} in the list with {@code updatedTransaction}.
     * {@code target} must exist in THRIFT.
     * The transaction identity of {@code updatedTransaction} must not be the same as another existing transaction in
     * THRIFT.
     */
    public void setTransaction(Transaction target, Transaction updatedTransaction) {
        requireNonNull(updatedTransaction);

        transactions.setTransaction(target, updatedTransaction);
    }

    /**
     * Returns true if the specified transaction exists in the transactions list.
     */
    public boolean hasTransaction(Transaction t) {
        requireNonNull(t);
        return transactions.contains(t);
    }

    /**
     * Removes {@code key} from this {@code Thrift}.
     * {@code key} must exist in THRIFT.
     */
    public void removeTransaction(Transaction key) {
        transactions.remove(key);
    }

    /**
     * Removes last transaction from this {@code Thrift}.
     */
    public void removeLastTransaction() {
        transactions.removeLast();
    }

    //// util methods

    /**
     * Returns an Optional that contains the {@link Index} of the {@code transaction}.
     *
     * @param transaction is the transaction that you are interested in its index in the full transaction list.
     * @return an Optional containing the index of the transaction.
     */
    public Optional<Index> getTransactionIndex(Transaction transaction) {
        requireNonNull(transaction);
        return transactions.getIndex(transaction);
    }

    @Override
    public String toString() {
        return transactions.asUnmodifiableObservableList().size() + " transactions";
        // TODO: refine later
    }

    @Override
    public ObservableList<Transaction> getTransactionList() {
        return transactions.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Thrift // instanceof handles nulls
                && transactions.equals(((Thrift) other).transactions));
    }

    @Override
    public int hashCode() {
        return transactions.hashCode();
    }
}

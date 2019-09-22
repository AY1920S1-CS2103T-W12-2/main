package seedu.address.model.transaction;

import static java.util.Objects.requireNonNull;

import java.util.Date;


/**
 * Represents the Date whereby the Transaction is created on.
 */
public class TransactionDate {

    public final Date value;

    /**
     * Constructs a {@code TransactionDate}.
     *
     * @param value Description describing the Transaction.
     */
    public TransactionDate(Date value) {
        requireNonNull(value);
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof TransactionDate // instanceof handles nulls
                && value.equals(((TransactionDate) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}

package thrift.model.transaction;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import thrift.model.tag.Tag;

/**
 * Represents an Income in the transactions list.
 * Guarantees: details are present and not null, field values are validated and immutable.
 */
public class Income extends Transaction {

    //Data fields
    private final Description description;
    private final TransactionDate date;
    private final Value value;
    private final Remark remark;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Every field must be present and not null.
     */
    public Income(Description description, Value value, Remark remark, TransactionDate date, Set<Tag> tags) {
        this.description = description;
        this.value = value;
        this.remark = remark;
        this.date = date;
        this.tags.addAll(tags);
    }

    public Description getDescription() {
        return description;
    }

    public TransactionDate getDate() {
        return date;
    }

    public Value getValue() {
        return value;
    }

    public Remark getRemark() {
        return remark;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both income have the same description and value. This allows easier
     * cloning of income transactions in the future.
     */
    public boolean isSameTransaction(Transaction otherIncome) {
        requireNonNull(otherIncome);
        if (otherIncome == this) {
            return true;
        }

        return otherIncome != null
                && otherIncome.getDescription().equals(getDescription())
                && otherIncome.getValue().equals(getValue());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Income)) {
            return false;
        }

        Income otherIncome = (Income) other;
        return otherIncome.getDescription().equals(getDescription())
                && otherIncome.getDate().equals(getDate())
                && otherIncome.getValue().equals(getValue())
                && otherIncome.getRemark().equals(getRemark())
                && otherIncome.getTags().equals(getTags());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own.
        return Objects.hash(description, date, value, tags);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[+] ")
                .append(getDescription())
                .append(" ($")
                .append(getValue())
                .append(") Date: ")
                .append(getDate())
                .append(" Remarks: ")
                .append(getRemark())
                .append(" Tags: ");
        getTags().forEach(builder::append);
        return builder.toString();
    }
}


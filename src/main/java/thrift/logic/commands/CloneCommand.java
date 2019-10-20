package thrift.logic.commands;

import static java.util.Objects.requireNonNull;
import static thrift.commons.util.CollectionUtil.requireAllNonNull;
import static thrift.model.transaction.TransactionDate.DATE_FORMATTER;

import java.util.Date;
import java.util.List;
import java.util.Set;

import thrift.commons.core.Messages;
import thrift.commons.core.index.Index;
import thrift.logic.commands.exceptions.CommandException;
import thrift.logic.parser.CliSyntax;
import thrift.model.Model;
import thrift.model.tag.Tag;
import thrift.model.transaction.Description;
import thrift.model.transaction.Expense;
import thrift.model.transaction.Income;
import thrift.model.transaction.Remark;
import thrift.model.transaction.Transaction;
import thrift.model.transaction.TransactionDate;
import thrift.model.transaction.Value;

/**
 * Clones a transaction specified by its index in THRIFT.
 */
public class CloneCommand extends Command implements Undoable {

    public static final String COMMAND_WORD = "clone";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Clones the transaction specified by its index number used in the displayed transaction list.\n"
            + "Parameters: " + CliSyntax.PREFIX_INDEX + "INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " " + CliSyntax.PREFIX_INDEX + "1";

    public static final String MESSAGE_CLONE_TRANSACTION_SUCCESS = "Cloned transaction: %1$s";

    private final Index targetIndex;
    private Transaction clonedTransaction;

    /**
     * Creates a CloneCommand instance to clone an {@code Expense} or {@code Income}
     *
     * @param targetIndex from the displayed list of the transaction to be cloned
     */
    public CloneCommand(Index targetIndex) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
        this.clonedTransaction = null;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Transaction> lastShownList = model.getFilteredTransactionList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_TRANSACTION_DISPLAYED_INDEX);
        }

        Transaction transactionToClone = lastShownList.get(targetIndex.getZeroBased());
        clonedTransaction = createClonedTransaction(transactionToClone);
        if (clonedTransaction instanceof Expense) {
            model.addExpense((Expense) clonedTransaction);
        } else if (clonedTransaction instanceof Income) {
            model.addIncome((Income) clonedTransaction);
        }

        return new CommandResult(String.format(MESSAGE_CLONE_TRANSACTION_SUCCESS, clonedTransaction));
    }

    /**
     * Creates a clone of the transaction at {@link #targetIndex} of the displayed list.
     *
     * @param transactionToClone {@link Transaction} that a clone should be created of, with current Date.
     * @return {@link Expense} or {@link Income} clone of {@code transactionToClone} containing current Date.
     */
    private Transaction createClonedTransaction(Transaction transactionToClone) {

        Description clonedDescription = transactionToClone.getDescription();
        Value clonedValue = transactionToClone.getValue();
        Remark clonedRemark = transactionToClone.getRemark();
        Set<Tag> clonedTags = transactionToClone.getTags();
        TransactionDate currentDate = new TransactionDate(DATE_FORMATTER.format(new Date()));
        if (transactionToClone instanceof Expense) {
            return new Expense(clonedDescription, clonedValue, clonedRemark, currentDate, clonedTags);
        } else {
            return new Income(clonedDescription, clonedValue, clonedRemark, currentDate, clonedTags);
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof CloneCommand // instanceof handles nulls
                && targetIndex.equals(((CloneCommand) other).targetIndex)); // state check
    }

    @Override
    public void undo(Model model) {
        requireNonNull(model);
        model.deleteLastTransaction();
    }

    @Override
    public void redo(Model model) {
        requireAllNonNull(model, clonedTransaction);
        if (clonedTransaction instanceof Expense) {
            model.addExpense((Expense) clonedTransaction);
        } else if (clonedTransaction instanceof Income) {
            model.addIncome((Income) clonedTransaction);
        }
    }
}

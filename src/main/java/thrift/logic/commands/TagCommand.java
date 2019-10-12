package thrift.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
import thrift.model.transaction.Transaction;
import thrift.model.transaction.TransactionDate;
import thrift.model.transaction.Value;

public class TagCommand extends Command{

    public static final String COMMAND_WORD = "tag";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Tags the transaction identified "
            + "by the index number used in the displayed transaction list. "
            + "New tags will be added to existing tags.\n"
            + "Parameters: " + CliSyntax.PREFIX_INDEX + "INDEX (must be a positive integer) "
            + CliSyntax.PREFIX_TAG + "TAG...\n"
            + "Example: " + COMMAND_WORD + " "
            + CliSyntax.PREFIX_INDEX+ "1 "
            + CliSyntax.PREFIX_TAG + "Food";


    public static final String MESSAGE_TAG_TRANSACTION_SUCCESS = "Updated Transaction: %1$s";
    public static final String MESSAGE_TAG_EXISTED = "\nTags [%1$s] existed and will be ignored.";
    public static final String MESSAGE_ORIGINAL_TRANSACTION = "\n\nOriginal: %1$s";
    public static final String MESSAGE_NOT_TAGGED = "At least one tag must be provided.";
    public static final String MESSAGE_NO_NEW_TAGS = "No new tags were provided, the transaction was not updated.";

    private final Index index;
    private final Set<Tag> tagSet;
    private final StringBuilder existedTags;

    /**
     * @param index of the transaction in the filtered transaction list to update
     * @param tagSet of new tags to be added to the current set of tags, without duplicates.
     */
    public TagCommand(Index index, Set<Tag> tagSet) {
        requireNonNull(index);
        requireNonNull(tagSet);

        this.index = index;
        this.tagSet = tagSet;
        existedTags = new StringBuilder();
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Transaction> lastShownList = model.getFilteredTransactionList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_TRANSACTION_DISPLAYED_INDEX);
        }

        Transaction transactionToTag = lastShownList.get(index.getZeroBased());
        String originalTransactionNotification = String.format(MESSAGE_ORIGINAL_TRANSACTION, transactionToTag);
        Transaction updatedTransaction = createTaggedTransaction(transactionToTag, tagSet);
        String taggedTransactionNotification = String.format(MESSAGE_TAG_TRANSACTION_SUCCESS, updatedTransaction);
        String existedTagsNotification = String.format(MESSAGE_TAG_EXISTED, existedTags.toString());

        model.setTransaction(transactionToTag, updatedTransaction);
        model.updateFilteredTransactionList(Model.PREDICATE_SHOW_ALL_TRANSACTIONS);
        return new CommandResult(taggedTransactionNotification + originalTransactionNotification + existedTagsNotification);
    }


    /**
     * Creates and returns a {@code Transaction} with the details of {@code transactionToTag}
     * appended with {@code tagSet} with duplicates.
     */
    private Transaction createTaggedTransaction(Transaction transactionToTag,
                                                        Set<Tag> tagSet) throws CommandException {
        assert transactionToTag != null;

        Description updatedDescription = transactionToTag.getDescription();
        Value updatedValue = transactionToTag.getValue();
        TransactionDate updatedDate = transactionToTag.getDate();
        Set<Tag> updatedTags = new HashSet<Tag>(transactionToTag.getTags());

        int noOfTagsIgnored = 0;
        boolean isNotEmpty = false;
        for (Tag newTag : tagSet) {
            if (isNotEmpty) {
                existedTags.append(", ");
            }

            if (updatedTags.contains(newTag)) {
                existedTags.append(newTag.tagName);
                noOfTagsIgnored++;
                isNotEmpty = true;
            } else {
                updatedTags.add(newTag);
            }
        }

        if (tagSet.size() == noOfTagsIgnored) {
            throw new CommandException(MESSAGE_NO_NEW_TAGS);
        }

        if (transactionToTag instanceof Expense) {
            return new Expense(updatedDescription, updatedValue, updatedDate, updatedTags);
        } else {
            return new Income(updatedDescription, updatedValue, updatedDate, updatedTags);
        }
    }
}

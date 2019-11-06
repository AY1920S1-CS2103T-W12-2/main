package thrift.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static thrift.logic.commands.CommandTestUtil.assertCommandFailure;
import static thrift.logic.commands.CommandTestUtil.assertCommandSuccess;
import static thrift.logic.commands.CommandTestUtil.assertRedoCommandSuccess;
import static thrift.logic.commands.CommandTestUtil.assertUndoCommandSuccess;
import static thrift.logic.commands.CommandTestUtil.showTransactionAtIndex;
import static thrift.model.transaction.TransactionDate.DATE_FORMATTER;
import static thrift.testutil.Assert.assertThrows;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;

import thrift.commons.core.Messages;
import thrift.commons.core.index.Index;
import thrift.model.Model;
import thrift.model.ModelManager;
import thrift.model.UserPrefs;
import thrift.model.clone.Occurrence;
import thrift.model.transaction.Expense;
import thrift.model.transaction.Income;
import thrift.model.transaction.Transaction;
import thrift.testutil.ExpenseBuilder;
import thrift.testutil.IncomeBuilder;
import thrift.testutil.TypicalIndexes;
import thrift.testutil.TypicalTransactions;

public class CloneCommandTest {

    private Model model = new ModelManager(TypicalTransactions.getTypicalThrift(), new UserPrefs());
    private final int zeroOccurrence = 0;
    private final int oneOccurrence = 1;
    private final int twelveOccurrences = 12;
    private final int fiveOccurrences = 5;

    @Test
    public void constructor_nullReceivedFields_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CloneCommand(null, null));
        assertThrows(NullPointerException.class, () ->
             new CloneCommand(null, new Occurrence("monthly", fiveOccurrences)));
    }

    @Test
    public void execute_validIndexBaseOccurrenceUnfilteredList_success() {
        Transaction transactionToClone = model.getFilteredTransactionList()
                .get(TypicalIndexes.INDEX_SECOND_TRANSACTION.getZeroBased());
        CloneCommand cloneCommand = new CloneCommand(TypicalIndexes.INDEX_SECOND_TRANSACTION,
                new Occurrence("daily", zeroOccurrence));

        Income expectedTransaction = new IncomeBuilder()
                .withDescription(transactionToClone.getDescription().value)
                .withDate(DATE_FORMATTER.format(new Date()))
                .withValue(transactionToClone.getValue().getUnformattedString())
                .withRemark(transactionToClone.getRemark().value)
                .withTags(transactionToClone.getTags().iterator().next().tagName)
                .build();
        String expectedMessage = String.format(CloneCommand.MESSAGE_CLONE_TRANSACTION_SUCCESS, transactionToClone)
                + "\n" + String.format(CloneCommand.MESSAGE_NUM_CLONED_TRANSACTIONS, "for today", oneOccurrence);

        ModelManager expectedModel = new ModelManager(model.getThrift(), new UserPrefs());
        expectedModel.addIncome(expectedTransaction);

        assertCommandSuccess(cloneCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexBaseOccurrenceUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredTransactionList().size() + 1);
        CloneCommand cloneCommand = new CloneCommand(outOfBoundIndex, new Occurrence("daily", zeroOccurrence));

        assertCommandFailure(cloneCommand, model, Messages.MESSAGE_INVALID_TRANSACTION_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexBaseOccurrenceFilteredList_success() {
        Transaction transactionToClone = model.getFilteredTransactionList()
                .get(TypicalIndexes.INDEX_FIRST_TRANSACTION.getZeroBased());
        CloneCommand cloneCommand = new CloneCommand(TypicalIndexes.INDEX_FIRST_TRANSACTION,
                new Occurrence("daily", zeroOccurrence));

        Expense expectedTransaction = new ExpenseBuilder()
                .withDescription(transactionToClone.getDescription().value)
                .withDate(DATE_FORMATTER.format(new Date()))
                .withValue(transactionToClone.getValue().getUnformattedString())
                .withRemark(transactionToClone.getRemark().value)
                .withTags(transactionToClone.getTags().iterator().next().tagName)
                .build();
        String expectedMessage = String.format(CloneCommand.MESSAGE_CLONE_TRANSACTION_SUCCESS, transactionToClone)
                + "\n" + String.format(CloneCommand.MESSAGE_NUM_CLONED_TRANSACTIONS, "for today", oneOccurrence);

        ModelManager expectedModel = new ModelManager(model.getThrift(), new UserPrefs());
        expectedModel.addExpense(expectedTransaction);

        assertCommandSuccess(cloneCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexBaseOccurrenceFilteredList_throwsCommandException() {
        showTransactionAtIndex(model, TypicalIndexes.INDEX_FIRST_TRANSACTION);

        Index outOfBoundIndex = TypicalIndexes.INDEX_SECOND_TRANSACTION;
        // ensures that outOfBoundIndex is still in bounds of thrift list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getThrift().getTransactionList().size());

        CloneCommand cloneCommand = new CloneCommand(outOfBoundIndex, new Occurrence("for today", zeroOccurrence));

        assertCommandFailure(cloneCommand, model, Messages.MESSAGE_INVALID_TRANSACTION_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexValidOccurrenceUnfilteredList_success() {
        Transaction transactionToClone = model.getFilteredTransactionList()
                .get(TypicalIndexes.INDEX_SECOND_TRANSACTION.getZeroBased());
        CloneCommand cloneCommand = new CloneCommand(TypicalIndexes.INDEX_SECOND_TRANSACTION,
                new Occurrence("monthly", twelveOccurrences));

        Date originalDate = transactionToClone.getDate().getDate();
        String expectedMessage = String.format(CloneCommand.MESSAGE_CLONE_TRANSACTION_SUCCESS, transactionToClone)
                + "\n" + String.format(CloneCommand.MESSAGE_NUM_CLONED_TRANSACTIONS, "monthly", twelveOccurrences);

        ModelManager expectedModel = new ModelManager(model.getThrift(), new UserPrefs());
        for (int i = 1; i <= twelveOccurrences; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(originalDate);
            calendar.add(Calendar.MONTH, i);
            String date = DATE_FORMATTER.format(calendar.getTime());

            Income expectedTransaction = new IncomeBuilder()
                    .withDescription(transactionToClone.getDescription().value)
                    .withDate(date)
                    .withValue(transactionToClone.getValue().getUnformattedString())
                    .withRemark(transactionToClone.getRemark().value)
                    .withTags(transactionToClone.getTags().iterator().next().tagName)
                    .build();
            expectedModel.addIncome(expectedTransaction);
        }

        assertCommandSuccess(cloneCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexValidOccurrenceUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredTransactionList().size() + 1);
        CloneCommand cloneCommand = new CloneCommand(outOfBoundIndex, new Occurrence("weekly", fiveOccurrences));

        assertCommandFailure(cloneCommand, model, Messages.MESSAGE_INVALID_TRANSACTION_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexValidOccurrenceFilteredList_success() {
        Transaction transactionToClone = model.getFilteredTransactionList()
                .get(TypicalIndexes.INDEX_FIRST_TRANSACTION.getZeroBased());
        CloneCommand cloneCommand = new CloneCommand(TypicalIndexes.INDEX_FIRST_TRANSACTION,
                new Occurrence("yearly", fiveOccurrences));

        Date originalDate = transactionToClone.getDate().getDate();
        String expectedMessage = String.format(CloneCommand.MESSAGE_CLONE_TRANSACTION_SUCCESS, transactionToClone)
                + "\n" + String.format(CloneCommand.MESSAGE_NUM_CLONED_TRANSACTIONS, "yearly", fiveOccurrences);

        ModelManager expectedModel = new ModelManager(model.getThrift(), new UserPrefs());

        for (int i = 1; i <= fiveOccurrences; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(originalDate);
            calendar.add(Calendar.YEAR, i);
            String date = DATE_FORMATTER.format(calendar.getTime());

            Expense expectedTransaction = new ExpenseBuilder()
                    .withDescription(transactionToClone.getDescription().value)
                    .withDate(date)
                    .withValue(transactionToClone.getValue().getUnformattedString())
                    .withRemark(transactionToClone.getRemark().value)
                    .withTags(transactionToClone.getTags().iterator().next().tagName)
                    .build();
            expectedModel.addExpense(expectedTransaction);
        }

        assertCommandSuccess(cloneCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexValidOccurrenceFilteredList_throwsCommandException() {
        showTransactionAtIndex(model, TypicalIndexes.INDEX_FIRST_TRANSACTION);

        Index outOfBoundIndex = TypicalIndexes.INDEX_SECOND_TRANSACTION;
        // ensures that outOfBoundIndex is still in bounds of thrift list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getThrift().getTransactionList().size());

        CloneCommand cloneCommand = new CloneCommand(outOfBoundIndex, new Occurrence("daily", twelveOccurrences));

        assertCommandFailure(cloneCommand, model, Messages.MESSAGE_INVALID_TRANSACTION_DISPLAYED_INDEX);
    }

    @Test
    public void undo_undoCloneCommandExpenseWithNoOccurrence_success() {
        Model expectedModel = new ModelManager(model.getThrift(), new UserPrefs());

        Transaction transactionToClone = model.getFilteredTransactionList()
                .get(TypicalIndexes.INDEX_FIRST_TRANSACTION.getZeroBased());

        Expense expectedTransaction = new ExpenseBuilder()
                .withDescription(transactionToClone.getDescription().value)
                .withDate(DATE_FORMATTER.format(new Date()))
                .withValue(transactionToClone.getValue().getUnformattedString())
                .withRemark(transactionToClone.getRemark().value)
                .withTags(transactionToClone.getTags().stream().map(tag -> tag.tagName).toArray(String[]::new))
                .build();

        //simulates user performing clone command
        expectedModel.addExpense(expectedTransaction);
        String expectedMessage = String.format(CloneCommand.MESSAGE_CLONE_TRANSACTION_SUCCESS, transactionToClone)
                + "\n" + String.format(CloneCommand.MESSAGE_NUM_CLONED_TRANSACTIONS, "for today", oneOccurrence);
        CloneCommand cloneCommand = new CloneCommand(TypicalIndexes.INDEX_FIRST_TRANSACTION,
                new Occurrence("daily", zeroOccurrence));
        assertCommandSuccess(cloneCommand, model, expectedMessage, expectedModel);

        //undo
        expectedModel.deleteLastTransaction();
        assertUndoCommandSuccess(cloneCommand, model, expectedModel);
    }

    @Test
    public void redo_redoCloneCommandExpenseWithNoOccurrence_success() {
        Model expectedModel = new ModelManager(model.getThrift(), new UserPrefs());

        Transaction transactionToClone = model.getFilteredTransactionList()
                .get(TypicalIndexes.INDEX_FIRST_TRANSACTION.getZeroBased());

        Expense expectedTransaction = new ExpenseBuilder()
                .withDescription(transactionToClone.getDescription().value)
                .withDate(DATE_FORMATTER.format(new Date()))
                .withValue(transactionToClone.getValue().getUnformattedString())
                .withRemark(transactionToClone.getRemark().value)
                .withTags(transactionToClone.getTags().stream().map(tag -> tag.tagName).toArray(String[]::new))
                .build();

        //simulates user performing clone command
        expectedModel.addExpense(expectedTransaction);
        String expectedMessage = String.format(CloneCommand.MESSAGE_CLONE_TRANSACTION_SUCCESS, transactionToClone)
                + "\n" + String.format(CloneCommand.MESSAGE_NUM_CLONED_TRANSACTIONS, "for today", oneOccurrence);
        CloneCommand cloneCommand = new CloneCommand(TypicalIndexes.INDEX_FIRST_TRANSACTION,
                new Occurrence("daily", zeroOccurrence));
        assertCommandSuccess(cloneCommand, model, expectedMessage, expectedModel);

        //undo
        expectedModel.deleteLastTransaction();
        assertUndoCommandSuccess(cloneCommand, model, expectedModel);

        //redo
        expectedModel.addExpense(expectedTransaction);
        assertRedoCommandSuccess(cloneCommand, model, expectedModel);
    }

    @Test
    public void undo_undoCloneCommandIncomeWithNoOccurrence_success() {
        Model expectedModel = new ModelManager(model.getThrift(), new UserPrefs());

        Transaction transactionToClone = model.getFilteredTransactionList()
                .get(TypicalIndexes.INDEX_SECOND_TRANSACTION.getZeroBased());

        Income expectedTransaction = new IncomeBuilder()
                .withDescription(transactionToClone.getDescription().value)
                .withDate(DATE_FORMATTER.format(new Date()))
                .withValue(transactionToClone.getValue().getUnformattedString())
                .withRemark(transactionToClone.getRemark().value)
                .withTags(transactionToClone.getTags().stream().map(tag -> tag.tagName).toArray(String[]::new))
                .build();

        //simulates user performing clone command
        expectedModel.addIncome(expectedTransaction);
        String expectedMessage = String.format(CloneCommand.MESSAGE_CLONE_TRANSACTION_SUCCESS, transactionToClone)
                + "\n" + String.format(CloneCommand.MESSAGE_NUM_CLONED_TRANSACTIONS, "for today", oneOccurrence);
        CloneCommand cloneCommand = new CloneCommand(TypicalIndexes.INDEX_SECOND_TRANSACTION,
                new Occurrence("daily", zeroOccurrence));
        assertCommandSuccess(cloneCommand, model, expectedMessage, expectedModel);

        //undo
        expectedModel.deleteLastTransaction();
        assertUndoCommandSuccess(cloneCommand, model, expectedModel);
    }

    @Test
    public void redo_redoCloneCommandIncomeWithNoOccurrence_success() {
        Model expectedModel = new ModelManager(model.getThrift(), new UserPrefs());

        Transaction transactionToClone = model.getFilteredTransactionList()
                .get(TypicalIndexes.INDEX_SECOND_TRANSACTION.getZeroBased());

        Income expectedTransaction = new IncomeBuilder()
                .withDescription(transactionToClone.getDescription().value)
                .withDate(DATE_FORMATTER.format(new Date()))
                .withValue(transactionToClone.getValue().getUnformattedString())
                .withRemark(transactionToClone.getRemark().value)
                .withTags(transactionToClone.getTags().stream().map(tag -> tag.tagName).toArray(String[]::new))
                .build();

        //simulates user performing clone command
        expectedModel.addIncome(expectedTransaction);
        String expectedMessage = String.format(CloneCommand.MESSAGE_CLONE_TRANSACTION_SUCCESS, transactionToClone)
                + "\n" + String.format(CloneCommand.MESSAGE_NUM_CLONED_TRANSACTIONS, "for today", oneOccurrence);
        CloneCommand cloneCommand = new CloneCommand(TypicalIndexes.INDEX_SECOND_TRANSACTION,
                new Occurrence("daily", zeroOccurrence));
        assertCommandSuccess(cloneCommand, model, expectedMessage, expectedModel);

        //undo
        expectedModel.deleteLastTransaction();
        assertUndoCommandSuccess(cloneCommand, model, expectedModel);

        //redo
        expectedModel.addIncome(expectedTransaction);
        assertRedoCommandSuccess(cloneCommand, model, expectedModel);
    }

    @Test
    public void equals() {
        CloneCommand cloneFirstCommand = new CloneCommand(TypicalIndexes.INDEX_FIRST_TRANSACTION,
                new Occurrence("daily", 1));
        CloneCommand cloneFirstCommandDuplicate = new CloneCommand(TypicalIndexes.INDEX_FIRST_TRANSACTION,
                new Occurrence("daily", 1));
        CloneCommand cloneSecondCommand = new CloneCommand(TypicalIndexes.INDEX_SECOND_TRANSACTION,
                new Occurrence("daily", 1));

        // same object -> returns true
        assertTrue(cloneFirstCommand.equals(cloneFirstCommand));

        // same values for different objects -> returns true
        assertTrue(cloneFirstCommand.equals(cloneFirstCommandDuplicate));

        // different types -> returns false
        assertFalse(cloneFirstCommand.equals(new DeleteCommand(TypicalIndexes.INDEX_FIRST_TRANSACTION)));

        // comparing with null -> returns false
        assertFalse(cloneFirstCommand.equals(null));

        // different values for different objects -> returns false
        assertFalse(cloneFirstCommand.equals(cloneSecondCommand));
    }
}

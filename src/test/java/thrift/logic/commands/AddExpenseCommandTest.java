package thrift.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static thrift.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import thrift.commons.core.GuiSettings;
import thrift.commons.core.index.Index;
import thrift.model.Model;
import thrift.model.ReadOnlyThrift;
import thrift.model.ReadOnlyUserPrefs;
import thrift.model.Thrift;
import thrift.model.transaction.Budget;
import thrift.model.transaction.Expense;
import thrift.model.transaction.Income;
import thrift.model.transaction.Transaction;
import thrift.testutil.ExpenseBuilder;

public class AddExpenseCommandTest {

    @Test
    public void constructor_nullExpense_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddExpenseCommand(null));
    }

    @Test
    public void execute_expenseAcceptedByModel_addSuccessful() {
        ModelStubAcceptingTransactionAdded modelStub = new ModelStubAcceptingTransactionAdded();
        Expense validExpense = new ExpenseBuilder().build();

        CommandResult commandResult = new AddExpenseCommand(validExpense).execute(modelStub, null);

        assertEquals(String.format(AddExpenseCommand.MESSAGE_SUCCESS, validExpense), commandResult.getFeedbackToUser());
        assertEquals(Arrays.asList(validExpense), modelStub.transactionsAdded);
    }

    @Test
    public void undoAndRedo_success() {
        ModelStubUndoRedoAddExpenses modelStub = new ModelStubUndoRedoAddExpenses();

        //add expense
        Expense validExpense = new ExpenseBuilder().build();
        modelStub.addExpense(validExpense);
        AddExpenseCommand addExpenseCommand = new AddExpenseCommand(validExpense);
        modelStub.keepTrackCommands(addExpenseCommand);
        assertEquals(1, modelStub.getThrift().getTransactionList().size());
        assertFalse(modelStub.undoableCommandStack.isEmpty());

        //test undo
        Undoable undoable = modelStub.getPreviousUndoableCommand();
        assertSame(undoable, addExpenseCommand);
        undoable.undo(modelStub);
        assertEquals(0, modelStub.getThrift().getTransactionList().size());
        assertTrue(modelStub.undoableCommandStack.isEmpty());

        //test redo
        undoable = modelStub.getUndoneCommand();
        assertSame(undoable, addExpenseCommand);
        undoable.redo(modelStub);
        assertEquals(1, modelStub.getThrift().getTransactionList().size());
        assertFalse(modelStub.undoableCommandStack.isEmpty());
    }

    @Test
    public void equals() {
        Expense one = new ExpenseBuilder().withDescription("Expense One").build();
        Expense two = new ExpenseBuilder().withDescription("Expense Two").build();
        AddExpenseCommand addOneCommand = new AddExpenseCommand(one);
        AddExpenseCommand addTwoCommand = new AddExpenseCommand(two);

        // same object -> returns true
        assertTrue(addOneCommand.equals(addOneCommand));

        // same values -> returns true
        AddExpenseCommand addOneCommandCopy = new AddExpenseCommand(one);
        assertTrue(addOneCommand.equals(addOneCommandCopy));

        // different types -> returns false
        assertFalse(addOneCommand.equals(1));

        // null -> returns false
        assertFalse(addOneCommand.equals(null));

        // different transaction -> returns false
        assertFalse(addOneCommand.equals(addTwoCommand));
    }

    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getThriftFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setThriftFilePath(Path thriftFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addExpense(Expense expense) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addExpense(Expense expense, Index index) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addIncome(Income income) {
            throw new AssertionError("This method should not be called");
        }

        @Override
        public void addIncome(Income income, Index index) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setThrift(ReadOnlyThrift newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyThrift getThrift() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasTransaction(Transaction transaction) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<Index> getIndexInFullTransactionList(Transaction transaction) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteTransaction(Transaction transaction) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteTransaction(Index index) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Transaction deleteLastTransaction() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public String getCurrentMonthYear() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setCurrentMonthYear(Calendar monthYear) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public double getCurrentMonthBudget() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<Budget> setBudget(Budget budget) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void resetBudgetForThatMonth(Budget budget) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setTransaction(Transaction target, Transaction updatedTransaction) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setTransactionWithIndex(Index actualIndex, Transaction updatedTransaction) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Transaction> getFilteredTransactionList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredTransactionListToCurrentMonth() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredTransactionList(Predicate<Transaction> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateBalanceForCurrentMonth() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public double getBalance() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateExpenseForCurrentMonth() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public double getExpense() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateIncomeForCurrentMonth() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public double getIncome() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean isInView(Transaction transaction) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void keepTrackCommands(Undoable command) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Undoable getPreviousUndoableCommand() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasUndoableCommand() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Undoable getUndoneCommand() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasUndoneCommand() {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that contains a single transaction.
     */
    private class ModelStubWithTransaction extends ModelStub {
        private final Transaction transaction;

        ModelStubWithTransaction(Transaction transaction) {
            requireNonNull(transaction);
            this.transaction = transaction;
        }

        @Override
        public boolean hasTransaction(Transaction transaction) {
            requireNonNull(transaction);
            return this.transaction.isSameTransaction(transaction);
        }
    }

    /**
     * A Model stub that always accept the transaction being added.
     */
    private class ModelStubAcceptingTransactionAdded extends ModelStub {
        final ArrayList<Transaction> transactionsAdded = new ArrayList<>();

        @Override
        public boolean hasTransaction(Transaction transaction) {
            requireNonNull(transaction);
            return transactionsAdded.stream().anyMatch(transaction::isSameTransaction);
        }

        @Override
        public void addExpense(Expense expense) {
            requireNonNull(expense);
            transactionsAdded.add(expense);
        }

        @Override
        public boolean isInView(Transaction transaction) {
            return transactionsAdded.contains(transaction);
        }

        @Override
        public ReadOnlyThrift getThrift() {
            return new Thrift();
        }
    }

    /**
     * A Model stub that allows user to perform redo operation.
     */
    private class ModelStubUndoRedoAddExpenses extends ModelStub {
        final Stack<Undoable> undoableCommandStack = new Stack<>();
        final Stack<Undoable> redoCommandStack = new Stack<>();
        final ThriftStub thriftStub;

        public ModelStubUndoRedoAddExpenses() {
            thriftStub = new ThriftStub();
        }

        @Override
        public void keepTrackCommands(Undoable command) {
            undoableCommandStack.push(command);
        }

        @Override
        public Undoable getPreviousUndoableCommand() {
            Undoable undoable = undoableCommandStack.pop();
            redoCommandStack.push(undoable);
            return undoable;
        }

        @Override
        public Thrift getThrift() {
            return thriftStub;
        }

        @Override
        public void addExpense(Expense expense) {
            thriftStub.addTransaction(expense);
        }

        @Override
        public void deleteTransaction(Transaction transaction) {
            thriftStub.removeTransaction(transaction);
        }

        @Override
        public Undoable getUndoneCommand() {
            Undoable undoable = redoCommandStack.pop();
            undoableCommandStack.push(undoable);
            return undoable;
        }

        @Override
        public Transaction deleteLastTransaction() {
            return thriftStub.removeLastTransaction();
        }
    }

    /**
     * A Thrift stub that contains an empty list of transaction.
     */
    private class ThriftStub extends Thrift {
        final List<Transaction> transactionsAdded = new ArrayList<>();
        @Override
        public void removeTransaction(Transaction transaction) {
            transactionsAdded.remove(transaction);
        }

        @Override
        public void addTransaction(Transaction transaction) {
            transactionsAdded.add(transaction);
        }

        @Override
        public ObservableList<Transaction> getTransactionList() {
            return FXCollections.observableArrayList(transactionsAdded);
        }

        @Override
        public Transaction removeLastTransaction() {
            return transactionsAdded.remove(transactionsAdded.size() - 1);
        }
    }
}

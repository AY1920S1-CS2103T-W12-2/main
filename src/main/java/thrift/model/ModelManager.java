package thrift.model;

import static java.util.Objects.requireNonNull;
import static thrift.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.Calendar;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import thrift.commons.core.GuiSettings;
import thrift.commons.core.LogsCenter;
import thrift.commons.core.index.Index;
import thrift.commons.util.CollectionUtil;
import thrift.logic.commands.Undoable;
import thrift.model.transaction.Budget;
import thrift.model.transaction.Expense;
import thrift.model.transaction.Income;
import thrift.model.transaction.Transaction;
import thrift.model.transaction.TransactionIsInMonthYearPredicate;

/**
 * Represents the in-memory model of the THRIFT data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final Thrift thrift;
    private final UserPrefs userPrefs;
    private final FilteredList<Transaction> filteredTransactions;
    private final PastUndoableCommands pastUndoableCommands;
    private final Calendar currentMonthYear;
    private double balance;

    /**
     * Initializes a ModelManager with the given thrift, userPrefs and pastUndoableCommands.
     */
    public ModelManager(ReadOnlyThrift thrift, ReadOnlyUserPrefs userPrefs, PastUndoableCommands pastUndoableCommands) {
        super();
        requireAllNonNull(thrift, userPrefs);

        logger.fine("Initializing with THRIFT: " + thrift + " and user prefs " + userPrefs);

        this.thrift = new Thrift(thrift);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredTransactions = new FilteredList<>(this.thrift.getTransactionList());
        this.pastUndoableCommands = pastUndoableCommands;
        currentMonthYear = Calendar.getInstance();
        balance = 1;
    }

    public ModelManager() {
        this(new Thrift(), new UserPrefs(), new PastUndoableCommands());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getThriftFilePath() {
        return userPrefs.getThriftFilePath();
    }

    @Override
    public void setThriftFilePath(Path thriftFilePath) {
        requireNonNull(thriftFilePath);
        userPrefs.setThriftFilePath(thriftFilePath);
    }

    //=========== THRIFT ================================================================================

    @Override
    public void setThrift(ReadOnlyThrift thrift) {
        this.thrift.resetData(thrift);
    }

    @Override
    public ReadOnlyThrift getThrift() {
        return thrift;
    }

    @Override
    public boolean hasTransaction(Transaction t) {
        requireNonNull(t);
        return thrift.hasTransaction(t);
    }

    @Override
    public Optional<Index> getIndexInFullTransactionList(Transaction transaction) {
        return thrift.getTransactionIndex(transaction);
    }

    @Override
    public void deleteTransaction(Transaction transaction) {
        thrift.removeTransaction(transaction);
    }

    @Override
    public void deleteTransaction(Index index) {
        thrift.removeTransactionByIndex(index);
        updateFilteredTransactionList(PREDICATE_SHOW_ALL_TRANSACTIONS);
    }

    @Override
    public void deleteLastTransaction() {
        thrift.removeLastTransaction();
        updateFilteredTransactionList(PREDICATE_SHOW_ALL_TRANSACTIONS);
    }

    @Override
    public void addExpense(Expense expense) {
        thrift.addTransaction(expense);
        updateFilteredTransactionList(PREDICATE_SHOW_ALL_TRANSACTIONS);
    }

    @Override
    public void addExpense(Expense expense, Index index) {
        thrift.addTransaction(expense, index);
        updateFilteredTransactionList(PREDICATE_SHOW_ALL_TRANSACTIONS);
    }

    @Override
    public void addIncome(Income income) {
        thrift.addTransaction(income);
        updateFilteredTransactionList(PREDICATE_SHOW_ALL_TRANSACTIONS);
    }

    @Override
    public void addIncome(Income income, Index index) {
        thrift.addTransaction(income, index);
        updateFilteredTransactionList(PREDICATE_SHOW_ALL_TRANSACTIONS);
    }

    @Override
    public double getCurrentMonthBudget() {
        Optional<Budget> optBudget = thrift.getBudgetList().getBudgetForMonthYear(currentMonthYear);
        if (optBudget.isPresent()) {
            return optBudget.get().getBudgetValue().getMonetaryValue();
        } else {
            return 0;
        }
    }

    @Override
    public void setBudget(Budget budget) {
        thrift.setBudget(budget);
    }

    @Override
    public void setTransaction(Transaction target, Transaction updatedTransaction) {
        CollectionUtil.requireAllNonNull(target, updatedTransaction);
        thrift.setTransaction(target, updatedTransaction);
        updateFilteredTransactionList(PREDICATE_SHOW_ALL_TRANSACTIONS);
    }

    @Override
    public void setTransactionWithIndex(Index actualIndex, Transaction updatedTransaction) {
        CollectionUtil.requireAllNonNull(actualIndex, updatedTransaction);
        thrift.setTransactionWithIndex(actualIndex, updatedTransaction);
        updateFilteredTransactionList(PREDICATE_SHOW_ALL_TRANSACTIONS);
    }

    @Override
    public Transaction getLastTransactionFromThrift() {
        return thrift.getLastTransaction();
    }

    //=========== Filtered Transaction List Accessors =============================================================
    /**
     * Returns an unmodifiable view of the list of {@code Transaction} backed by the internal list of
     * {@code versionedThrift}
     */
    @Override
    public ObservableList<Transaction> getFilteredTransactionList() {
        return filteredTransactions;
    }

    /** Filters the view of the transaction list to only show transactions that occur in the current month. */
    @Override
    public void updateFilteredTransactionListToCurrentMonth() {
        filteredTransactions.setPredicate(new TransactionIsInMonthYearPredicate(currentMonthYear));
    }

    @Override
    public void updateFilteredTransactionList(Predicate<Transaction> predicate) {
        requireNonNull(predicate);
        filteredTransactions.setPredicate(predicate);
    }

    @Override
    public void updateBalance() {
        balance += filteredTransactions.stream()
                .mapToDouble(t -> {
                    double value = t.getValue().getMonetaryValue();
                    if (t instanceof Expense) {
                        return value * -1;
                    }
                    return value;
                })
                .sum();
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return thrift.equals(other.thrift)
                && userPrefs.equals(other.userPrefs)
                && filteredTransactions.equals(other.filteredTransactions);
    }

    //=========== Past Commands History =============================================================
    @Override
    public void keepTrackCommands(Undoable command) {
        pastUndoableCommands.addPastCommand(command);
    }

    @Override
    public Undoable getPreviousUndoableCommand() {
        return pastUndoableCommands.getCommandToUndo();
    }

    @Override
    public boolean hasUndoableCommand() {
        return pastUndoableCommands.hasUndoCommand();
    }

    @Override
    public Undoable getUndoneCommand() {
        return pastUndoableCommands.getCommandToRedo();
    }

    @Override
    public boolean hasUndoneCommand() {
        return pastUndoableCommands.hasRedoCommand();
    }
}

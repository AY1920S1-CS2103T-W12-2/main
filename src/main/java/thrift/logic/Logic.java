package thrift.logic;

import java.nio.file.Path;

import javafx.collections.ObservableList;
import thrift.commons.core.GuiSettings;
import thrift.logic.commands.Command;
import thrift.logic.commands.CommandResult;
import thrift.logic.commands.exceptions.CommandException;
import thrift.logic.parser.exceptions.ParseException;
import thrift.model.Model;
import thrift.model.ReadOnlyThrift;
import thrift.model.transaction.Transaction;
import thrift.ui.BalanceBar;
import thrift.ui.TransactionListPanel;

/**
 * API of the Logic component
 */
public interface Logic {
    /**
     * Executes the command and returns the result.
     *
     * @param commandText The command as entered by the user.
     * @param transactionListPanel The TransactionListPanel to be manipulated by execution of certain commands.
     * @param balanceBar The BalanceBar that displays the current month, budget and balance.
     * @return the result of the command execution.
     * @throws CommandException If an error occurs during command execution.
     * @throws ParseException If an error occurs during parsing.
     */
    CommandResult execute(String commandText, TransactionListPanel transactionListPanel, BalanceBar balanceBar)
            throws CommandException, ParseException;

    /**
     * Returns the Thrift.
     *
     * @see Model#getThrift()
     */
    ReadOnlyThrift getThrift();

    /** Returns if the given command requires a refresh of the filteredlist. */
    boolean requireFilteredListRefresh(Command command);

    /** Returns the current month and year in MMM yyyy format. */
    String getCurrentMonthYear();

    /** Returns the current month's budget. */
    double getCurrentMonthBudget();

    /** Returns the current month's balance. */
    double getCurrentMonthBalance();

    /** Returns an unmodifiable view of the filtered list of transactions. */
    ObservableList<Transaction> getFilteredTransactionList();

    /** Filters the view of the transaction list to only show transactions that occur in the current month. */
    void setFilteredTransactionListToCurrentMonth();

    /**
     * Returns the user prefs' thrift file path.
     */
    Path getThriftFilePath();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Set the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);
}

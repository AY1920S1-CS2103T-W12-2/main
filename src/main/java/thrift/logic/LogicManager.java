package thrift.logic;

import static java.util.Objects.requireNonNull;
import static thrift.commons.util.CollectionUtil.requireAllNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import thrift.commons.core.GuiSettings;
import thrift.commons.core.LogsCenter;
import thrift.logic.commands.AddExpenseCommand;
import thrift.logic.commands.AddIncomeCommand;
import thrift.logic.commands.BudgetCommand;
import thrift.logic.commands.CloneCommand;
import thrift.logic.commands.Command;
import thrift.logic.commands.CommandResult;
import thrift.logic.commands.DeleteCommand;
import thrift.logic.commands.NonScrollingCommand;
import thrift.logic.commands.RedoCommand;
import thrift.logic.commands.ScrollingCommand;
import thrift.logic.commands.UndoCommand;
import thrift.logic.commands.Undoable;
import thrift.logic.commands.UpdateCommand;
import thrift.logic.commands.exceptions.CommandException;
import thrift.logic.parser.ThriftParser;
import thrift.logic.parser.exceptions.ParseException;
import thrift.model.Model;
import thrift.model.ReadOnlyThrift;
import thrift.model.transaction.Transaction;
import thrift.storage.Storage;
import thrift.ui.BalanceBar;
import thrift.ui.TransactionListPanel;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_MESSAGE = "Could not save data to file: ";
    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final ThriftParser thriftParser;

    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        thriftParser = new ThriftParser();
    }

    @Override
    public CommandResult execute(String commandText, TransactionListPanel transactionListPanel, BalanceBar balanceBar)
            throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");
        Command command = thriftParser.parseCommand(commandText);
        CommandResult commandResult = processParsedCommand(command, commandText, transactionListPanel, balanceBar);
        try {
            storage.saveThrift(model.getThrift());
        } catch (IOException ioe) {
            throw new CommandException(FILE_OPS_ERROR_MESSAGE + ioe, ioe);
        }

        return commandResult;
    }

    @Override
    public ReadOnlyThrift getThrift() {
        return model.getThrift();
    }

    @Override
    public CommandResult processParsedCommand(Command command, String commandText,
                                               TransactionListPanel transactionListPanel,
                                               BalanceBar balanceBar) throws CommandException {
        requireAllNonNull(command, commandText);
        CommandResult commandResult = parseScrollable(command, transactionListPanel);
        parseRefreshable(command, balanceBar);
        parseUndoable(command, commandText);
        return commandResult;
    }

    @Override
    public CommandResult parseScrollable(Command command, TransactionListPanel transactionListPanel)
            throws CommandException {
        requireNonNull(command);
        CommandResult commandResult;
        if (command instanceof ScrollingCommand) {
            commandResult = ((ScrollingCommand) command).execute(model, transactionListPanel);
        } else {
            commandResult = ((NonScrollingCommand) command).execute(model);
        }
        return commandResult;
    }

    @Override
    public void parseRefreshable(Command command, BalanceBar balanceBar) {
        requireNonNull(command);
        if (isRefreshingFilteredList(command)) {
            model.updateBalanceForCurrentMonth();
            updateBalanceBar(balanceBar);
        }
    }

    @Override
    public void parseUndoable(Command command, String commandText) {
        requireAllNonNull(command, commandText);
        if (command instanceof Undoable) {
            model.keepTrackCommands((Undoable) command);
            logger.info("[UNDOABLE COMMAND][" + commandText + "] is tracked");
        }
    }

    @Override
    public void updateBalanceBar(BalanceBar balanceBar) {
        balanceBar.setMonthYear(getCurrentMonthYear());
        balanceBar.setMonthBudget(getCurrentMonthBudget());
        balanceBar.setMonthBalance(getCurrentMonthBalance());
    }

    @Override
    public boolean isRefreshingFilteredList(Command command) {
        requireNonNull(command);
        if (command instanceof AddIncomeCommand
                || command instanceof AddExpenseCommand
                || command instanceof BudgetCommand
                || command instanceof CloneCommand
                || command instanceof DeleteCommand
                || command instanceof RedoCommand
                || command instanceof UpdateCommand
                || command instanceof UndoCommand) {
            logger.info("[PREPARING TO UPDATE MONTHLY BALANCE OR BUDGET]");
            return true;
        }
        return false;
    }

    @Override
    public double getCurrentMonthBudget() {
        return model.getCurrentMonthBudget();
    }

    @Override
    public String getCurrentMonthYear() {
        return model.getCurrentMonthYear();
    }

    @Override
    public double getCurrentMonthBalance() {
        return model.getBalance();
    }

    @Override
    public ObservableList<Transaction> getFilteredTransactionList() {
        return model.getFilteredTransactionList();
    }

    @Override
    public void setFilteredTransactionListToCurrentMonth() {
        model.updateFilteredTransactionListToCurrentMonth();
    }

    @Override
    public Path getThriftFilePath() {
        return model.getThriftFilePath();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}

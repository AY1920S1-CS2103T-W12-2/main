package thrift.logic.parser;

import static thrift.logic.parser.CommandParserTestUtil.assertParseFailure;
import static thrift.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import thrift.commons.core.Messages;
import thrift.logic.commands.AddExpenseCommand;
import thrift.logic.commands.AddIncomeCommand;
import thrift.logic.commands.BudgetCommand;
import thrift.logic.commands.CloneCommand;
import thrift.logic.commands.DeleteCommand;
import thrift.logic.commands.ExitCommand;
import thrift.logic.commands.FindCommand;
import thrift.logic.commands.HelpCommand;
import thrift.logic.commands.ListCommand;
import thrift.logic.commands.RedoCommand;
import thrift.logic.commands.TagCommand;
import thrift.logic.commands.UndoCommand;
import thrift.logic.commands.UntagCommand;
import thrift.logic.commands.UpdateCommand;
import thrift.logic.parser.exceptions.ParseException;

public class HelpCommandParserTest {

    private final HelpCommandParser parser = new HelpCommandParser();

    @Test
    public void parse_addExpense_success() throws ParseException {
        assertParseSuccess(parser, AddExpenseCommand.COMMAND_WORD, new HelpCommand(
                AddExpenseCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_addIncome_success() throws ParseException {
        assertParseSuccess(parser, AddIncomeCommand.COMMAND_WORD, new HelpCommand(
                AddIncomeCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_budget_success() throws ParseException {
        assertParseSuccess(parser, BudgetCommand.COMMAND_WORD, new HelpCommand(
                BudgetCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_clone_success() throws ParseException {
        assertParseSuccess(parser, CloneCommand.COMMAND_WORD, new HelpCommand(
                CloneCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_delete_success() throws ParseException {
        assertParseSuccess(parser, DeleteCommand.COMMAND_WORD, new HelpCommand(
                DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_exit_success() throws ParseException {
        assertParseSuccess(parser, ExitCommand.COMMAND_WORD, new HelpCommand(
                ExitCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_find_success() throws ParseException {
        assertParseSuccess(parser, FindCommand.COMMAND_WORD, new HelpCommand(
                FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_help_throwParseException() throws ParseException {
        assertParseFailure(parser, HelpCommand.COMMAND_WORD, String.format(
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE)));
    }

    @Test
    public void parse_list_success() throws ParseException {
        assertParseSuccess(parser, ListCommand.COMMAND_WORD, new HelpCommand(
                ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_redo_success() throws ParseException {
        assertParseSuccess(parser, RedoCommand.COMMAND_WORD, new HelpCommand(
                RedoCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_tag_success() throws ParseException {
        assertParseSuccess(parser, TagCommand.COMMAND_WORD, new HelpCommand(
                TagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_undo_success() throws ParseException {
        assertParseSuccess(parser, UndoCommand.COMMAND_WORD, new HelpCommand(
                UndoCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_untag_success() throws ParseException {
        assertParseSuccess(parser, UntagCommand.COMMAND_WORD, new HelpCommand(
                UntagCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_update_success() throws ParseException {
        assertParseSuccess(parser, UpdateCommand.COMMAND_WORD, new HelpCommand(
                UpdateCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_parseInvalidCommand_success() throws ParseException {
        assertParseFailure(parser, "abc", String.format(
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE)));
    }
}

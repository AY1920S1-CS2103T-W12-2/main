package thrift.logic.commands;

import static java.util.Objects.requireNonNull;

import thrift.model.Model;
import thrift.model.Thrift;

/**
 * Clears the address book.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "Address book has been cleared!";


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.setThrift(new Thrift());
        return new CommandResult(MESSAGE_SUCCESS);
    }
}

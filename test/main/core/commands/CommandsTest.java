package main.core.commands;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import main.core.Executor;
import main.core.commands.commands.KickCommand;
import main.server.ServerConnectionManager;
import main.util.exception.ArgumentMissingException;
import main.util.exception.CommandNotFoundException;
import main.util.exception.IllegalTargetException;
import main.util.exception.InvalidUserIdException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TextMessageEvent.class, Executor.class})
public class CommandsTest {

   @Test(expected = CommandNotFoundException.class)
   public void testConsoleInvalidCommand() throws CommandNotFoundException {
      Commands.handle("!notacommand");
   }

   @Test(expected = IllegalArgumentException.class)
   public void testConsoleInvalidInput() throws CommandNotFoundException {
      Commands.handle("blah");
   }

   @Test(expected = ArgumentMissingException.class)
   public void testConsoleNoArgumentWhenRequired() throws CommandNotFoundException,
       ArgumentMissingException, IllegalTargetException, InvalidUserIdException {
      KickCommand commandMock = mock(KickCommand.class);
      ServerConnectionManager scm = mock(ServerConnectionManager.class);
      doReturn(scm).when(Executor.getServer(anyString()));
      TS3ApiAsync api = mock(TS3ApiAsync.class);
      doReturn(api).when(scm.getApiAsync());
      doNothing().when(commandMock).handle(anyString());
      doThrow(ArgumentMissingException.class).when(commandMock).handle(anyString());

      Commands.handle("!kick");
   }
}

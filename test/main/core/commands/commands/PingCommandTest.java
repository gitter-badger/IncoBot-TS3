package main.core.commands.commands;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import main.util.MessageHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TextMessageEvent.class)
public class PingCommandTest {

   private TextMessageEvent event = mock(TextMessageEvent.class);
   private MessageHandler mockHandler = mock(MessageHandler.class);
   private PingCommand command = new PingCommand(event);
   private PingCommand commandSpy = spy(command);

   @Before
   public void setUp() {
      when(event.getMessage()).thenReturn("!ping");
      when(mockHandler.sendToConsoleWith(anyString())).thenReturn(mockHandler);
      when(mockHandler.sendToServer()).thenReturn(mockHandler);
      when(mockHandler.sendToChannel()).thenReturn(mockHandler);
      when(mockHandler.returnToSender(any(TextMessageEvent.class))).thenReturn(mockHandler);
      when(commandSpy.getMessageHandler(anyString())).thenReturn(mockHandler);
   }

   @Test
   public void testClientEventFromServer() {
      //GIVEN - An event and command
      when(event.getTargetMode()).thenReturn(TextMessageTargetMode.SERVER);

      //WHEN - A PingCommand is handled
      commandSpy.handle();

      //THEN - Expect it to send messages to console and server.
      verify(mockHandler, times(1)).sendToConsoleWith("COMMAND RESPONSE");
      verify(mockHandler, times(1)).sendToServer();
   }

   @Test
   public void testClientEventFromChannel() {
      //GIVEN - An event and command
      when(event.getTargetMode()).thenReturn(TextMessageTargetMode.CHANNEL);

      //WHEN - A PingCommand is handled
      commandSpy.handle();

      //THEN - Expect it to send messages to console and server.
      verify(mockHandler, times(1)).sendToConsoleWith("COMMAND RESPONSE");
      verify(mockHandler, times(1)).sendToChannel();
   }

   @Test
   public void testClientEventFromUser() {
      //GIVEN - An event and command
      when(event.getTargetMode()).thenReturn(TextMessageTargetMode.CLIENT);

      //WHEN - A PingCommand is handled
      commandSpy.handle();

      //THEN - Expect it to send messages to console and server.
      verify(mockHandler, times(1)).sendToConsoleWith("COMMAND RESPONSE");
      verify(mockHandler, times(1)).returnToSender(event);
   }

   @Test
   public void testConsoleEvent() {
      //GIVEN - A command
      command = new PingCommand();
      commandSpy = spy(command);
      when(commandSpy.getMessageHandler(anyString())).thenReturn(mockHandler);

      //WHEN - PingCommand is handled
      commandSpy.handle();

      //THEN - Expect it to output only to console.
      verify(mockHandler, times(1)).sendToConsoleWith("COMMAND RESPONSE");
   }
}

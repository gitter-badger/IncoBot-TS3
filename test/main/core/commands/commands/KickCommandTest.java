package main.core.commands.commands;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.CommandFuture;
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
public class KickCommandTest {
   private TextMessageEvent event = mock(TextMessageEvent.class);
   private MessageHandler handler = mock(MessageHandler.class);
   private KickCommand command = new KickCommand(event);
   private KickCommand commandSpy = spy(command);
   private TS3ApiAsync mockApi = mock(TS3ApiAsync.class);

   @Before
   public void setUp() {
      when(handler.sendToConsoleWith(anyString())).thenReturn(handler);
      when(handler.sendToServer()).thenReturn(handler);
      when(handler.sendToChannel()).thenReturn(handler);
      when(handler.returnToSender(any(TextMessageEvent.class))).thenReturn(handler);
      when(commandSpy.getMessageHandler(anyString())).thenReturn(handler);
   }

   @Test
   public void testClientEventFromServer() {
      when(event.getTargetMode()).thenReturn(TextMessageTargetMode.SERVER);
      when(event.getMessage()).thenReturn("!kick 999 test");
      doReturn(mockApi).when(commandSpy).getApi(anyString());
      doReturn(true).when(commandSpy).validTarget(anyInt());
      doReturn("testNickname").when(commandSpy).getTargetName(anyInt());

      try {
         commandSpy.handle(event.getMessage());
      } catch (Exception e) {
         e.printStackTrace();
      }

      verify(handler, times(1)).sendToConsoleWith("KICK");
      verify(mockApi, times(1)).kickClientFromServer(anyString(), anyInt());
   }

   @Test
   public void testClientEventFromChannel() {
      when(event.getTargetMode()).thenReturn(TextMessageTargetMode.CHANNEL);
      when(event.getMessage()).thenReturn("!kick 999 test");
      doReturn(mockApi).when(commandSpy).getApi(anyString());
      doReturn(true).when(commandSpy).validTarget(anyInt());
      doReturn("testNickname").when(commandSpy).getTargetName(anyInt());

      try {
         commandSpy.handle(event.getMessage());
      } catch (Exception e) {
         e.printStackTrace();
      }

      verify(handler, times(1)).sendToConsoleWith("KICK");
      verify(mockApi, times(1)).kickClientFromServer(anyString(), anyInt());
   }

   @Test
   public void testClientEventFromUser() {
      when(event.getTargetMode()).thenReturn(TextMessageTargetMode.CLIENT);
      when(event.getMessage()).thenReturn("!kick 999 test");
      doReturn(mockApi).when(commandSpy).getApi(anyString());
      doReturn(true).when(commandSpy).validTarget(anyInt());
      doReturn("testNickname").when(commandSpy).getTargetName(anyInt());

      try {
         commandSpy.handle(event.getMessage());
      } catch (Exception e) {
         e.printStackTrace();
      }

      verify(handler, times(1)).sendToConsoleWith("KICK");
      verify(mockApi, times(1)).kickClientFromServer(anyString(), anyInt());
   }

   @Test
   public void testConsoleEvent() {
      command = new KickCommand();
      commandSpy = spy(command);
      doReturn(mockApi).when(commandSpy).getApi(anyString());
      doReturn(true).when(commandSpy).validTarget(anyInt());
      doReturn("testNickname").when(commandSpy).getTargetName(anyInt());
      CommandFuture<Boolean> response = mock(CommandFuture.class);
      doReturn(true).when(response).isSuccessful();
      doReturn(response).when(mockApi).kickClientFromServer(anyString(), anyInt());

      try {
         commandSpy.handle("!kick 999 test");
      } catch (Exception e) {
         e.printStackTrace();
      }

      verify(commandSpy, times(1)).getMessageHandler(anyString());
   }
}

package main.core.commands.commands;

import static com.google.common.base.Preconditions.checkArgument;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.CommandFuture.FailureListener;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.exception.TS3Exception;
import com.google.common.annotations.VisibleForTesting;
import java.util.logging.Level;
import main.core.Executor;
import main.server.ServerConnectionManager;
import main.util.ErrorMessages;
import main.util.MessageHandler;
import main.util.exception.ArgumentMissingException;
import main.util.exception.IllegalTargetException;
import main.util.exception.InvalidUserIdException;

/**
 * Command used to forcefully disconnect a client from the server.
 */
public class KickCommand {

   private TextMessageEvent event;

   /**
    * Create a KickCommand instance to handle console execution.
    */
   public KickCommand() {
   }

   /**
    * Create a KickCommand instance to handle client execution.
    *
    * @param event the {@link TextMessageEvent} containing the call for this command.
    */
   public KickCommand(TextMessageEvent event) {
      this.event = event;
   }

   public void handle(String input) throws ArgumentMissingException, IllegalTargetException,
       InvalidUserIdException {
      TS3ApiAsync api = getApi("testInstance");
      checkArgument(api != null);
      String[] params = input.split("\\s", 3);
      int target = Integer.parseInt(params[1]);
      String reason;
      String targetName;

      if (!validTarget(target)) {
         throw new IllegalTargetException();
      }

      //Set reason, throw ArgumentMissingException if not present.
      try {
         reason = params[2];
      } catch (IndexOutOfBoundsException e) {
         throw new ArgumentMissingException("kick", "reason");
      }

      //Determine targetName by ID, throw InvalidUserIdException if no connected client has that ID.
      try {
         targetName = getTargetName(target);
      } catch (Exception e) {
         if (e.getCause().getMessage().contains("invalid clientID")) {
            throw new InvalidUserIdException(String.valueOf(target));
         } else {
            getMessageHandler(ErrorMessages.UNKNOWN_ERROR)
                .sendToConsoleWith(Level.WARNING)
                .returnToSender(event);
         }
         return;
      }

      //Log to console.
      if (event != null) {
         getMessageHandler(String.format("%s attempted to kick %s from the server for: %s", event
             .getInvokerName(), targetName, reason))
             .sendToConsoleWith("KICK");
      } else {
         getMessageHandler(String.format("Attempting to kick %s from the server for: %s",
             targetName, reason))
             .sendToConsoleWith("KICK");
      }

      //Execute kick.
      api.kickClientFromServer(reason, target).onFailure(e -> {
            getMessageHandler(String.format("%s could not be kicked from the server. Encountered "
                + "error: %s", targetName, e.getMessage()))
                .sendToConsoleWith("KICK");
         }
      );
   }

   @VisibleForTesting
   MessageHandler getMessageHandler(String message) {
      return new MessageHandler(message);
   }

   @VisibleForTesting
   TS3ApiAsync getApi(String instanceName) {
      return Executor.getServer(instanceName).getApiAsync();
   }

   @VisibleForTesting
   Boolean validTarget(int targetId) {
      //Valid if target is not bot.
      return targetId != Executor.getServer("testInstance").getBotId();
   }

   @VisibleForTesting
   String getTargetName(int targetId) {
      return getApi("testInstance").getClientInfo(targetId).getUninterruptibly().getNickname();
   }
}

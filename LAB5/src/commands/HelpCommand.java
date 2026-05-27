package commands;
import util.Response;
import java.util.Map;
import util.Req;

public class HelpCommand implements Command {
   private Map<String, Command> commands;
   public HelpCommand(Map<String,Command> commands) {
       this.commands = commands;
   }
   public String getName() {
       return "help";
   }
   public String getDescription() {
       return "вывести все команды";
   }
   public Response execute(Req req) {
       if (commands == null || commands.isEmpty()) {
           return new Response("Нет доступных команд");
       }
       StringBuilder text = new StringBuilder();
       for(Command cmd : commands.values()) {
           text.append(cmd.getName())
                   .append("-")
                   .append(cmd.getDescription())
                   .append("\n");
       }
       return new Response(text.toString());
   }
    public void setCommands(Map<String, Command> commands) {
        this.commands = commands;
    }
}

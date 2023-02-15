public class MenuCommand {
    Command command;
    String caption;
    public MenuCommand(String caption, Command command) {
        this.caption = caption;
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}

package ui;

import static ui.EscapeSequences.*;

public class HelpHelper {

    public static void printPreLoginHelp() {
        System.out.println(SET_TEXT_COLOR_BLUE + SET_TEXT_BOLD + "Available commands:" + RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT);
        System.out.println(SET_TEXT_COLOR_GREEN + "  help" + RESET_TEXT_COLOR + "                            Show helpful commands");
        System.out.println(SET_TEXT_COLOR_GREEN + "  quit" + RESET_TEXT_COLOR + "                            Quit the app");
        System.out.println(SET_TEXT_COLOR_GREEN + "  register" + RESET_TEXT_COLOR + " <user> <pass> <email>     Register a new user");
        System.out.println(SET_TEXT_COLOR_GREEN + "  login" + RESET_TEXT_COLOR + "    <user> <pass>            Login as an existing user");
    }

    public static void printPostLoginHelp() {
        System.out.println(SET_TEXT_COLOR_BLUE + SET_TEXT_BOLD + "Available commands:" + RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT);
        System.out.println(SET_TEXT_COLOR_GREEN + "  help" + RESET_TEXT_COLOR + "                            Show helpful commands");
        System.out.println(SET_TEXT_COLOR_GREEN + "  logout" + RESET_TEXT_COLOR + "                          Log out");
        System.out.println(SET_TEXT_COLOR_GREEN + "  create" + RESET_TEXT_COLOR + " <game-name>               Create a new game");
        System.out.println(SET_TEXT_COLOR_GREEN + "  list" + RESET_TEXT_COLOR + "                            List all games");
        System.out.println(SET_TEXT_COLOR_GREEN + "  join" + RESET_TEXT_COLOR + " <game-id> <WHITE|BLACK>    Join a game");
        System.out.println(SET_TEXT_COLOR_GREEN + "  observe" + RESET_TEXT_COLOR + " <game-id>                Observe a game");
    }

    public static void printGameplayHelp() {
        System.out.println(SET_TEXT_COLOR_BLUE + SET_TEXT_BOLD + "Available commands:" + RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT);
        System.out.println(SET_TEXT_COLOR_GREEN + "  help" + RESET_TEXT_COLOR + "                            Show helpful commands");
        System.out.println(SET_TEXT_COLOR_GREEN + "  move" + RESET_TEXT_COLOR + " <from> <to> [promo]        Make a move (e.g., move e2 e4)");
        System.out.println(SET_TEXT_COLOR_GREEN + "  highlight" + RESET_TEXT_COLOR + " <square>              " +
                "Highlight legal moves from a square (e.g., highlight b1)");
        System.out.println(SET_TEXT_COLOR_GREEN + "  resign" + RESET_TEXT_COLOR + "                          Resign from the game");
        System.out.println(SET_TEXT_COLOR_GREEN + "  redraw" + RESET_TEXT_COLOR + "                          Redraw the board if needed");
        System.out.println(SET_TEXT_COLOR_GREEN + "  leave" + RESET_TEXT_COLOR + "                           Leave the game");
    }
}

package Game;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Model model = new Model();
        JFrame game = new JFrame();
        Controller controller = new Controller(model);

        game.setTitle("2048");
        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game.setSize(450, 500);
        game.setResizable(false);

        game.add(controller.getView());

        game.setLocationRelativeTo(null);
        game.setVisible(true);
    }
}

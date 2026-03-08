package src;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        // Apply System Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
                 IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.out.println("Failed to apply system look and feel");
        }

        // Launch Admin Login Dialog
        SwingUtilities.invokeLater(() -> {
            AdminLoginDialog dialog = new AdminLoginDialog(null);
            dialog.setVisible(true);
        });
    }
}


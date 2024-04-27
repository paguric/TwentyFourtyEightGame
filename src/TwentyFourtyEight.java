import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class TwentyFourtyEight {
    public static final int CASELLE_PER_LATO = 4;
    public static final int LARGHEZZA_FINESTRA = 720;
    public static final int ALTEZZA_FINESTRA = 720;
    public static final int GAP_FRA_CASELLE = 10;
    private static TwentyFourtyEight istanza = null;
    private static final JFrame finestra = new JFrame();
    private static final JPanel griglia = new JPanel();
    public static final Random RANDOM = new Random();
    private static Casella caselle[][] = new Casella[CASELLE_PER_LATO][CASELLE_PER_LATO];

    private TwentyFourtyEight() {
        finestra.setSize(LARGHEZZA_FINESTRA, ALTEZZA_FINESTRA);
        finestra.setResizable(false);
        finestra.setLocationRelativeTo(null);
        finestra.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        finestra.setTitle("2048");
        
        griglia.setLayout(new GridLayout(CASELLE_PER_LATO, CASELLE_PER_LATO, GAP_FRA_CASELLE, GAP_FRA_CASELLE));
        griglia.setPreferredSize(new Dimension(LARGHEZZA_FINESTRA, ALTEZZA_FINESTRA));
        for (int i = 0; i < CASELLE_PER_LATO; i++) {
            for (int j = 0; j < CASELLE_PER_LATO; j++) {
                caselle[i][j] = new Casella();
                griglia.add(caselle[i][j]);
            }
        }

        finestra.add(griglia);
        finestra.setVisible(true);
        finestra.revalidate();
        finestra.repaint();
    }

    public static TwentyFourtyEight getInstance() {
        if (istanza == null) {
            istanza = new TwentyFourtyEight();
        }
        return istanza;
    }

    public void iniziaPartita() {
        // inizializza prime due caselle
        generaCasella();
        generaCasella();

        finestra.revalidate();
        finestra.repaint();
    }

    private void generaCasella() {
        int x = RANDOM.nextInt(0, CASELLE_PER_LATO);
        int y = RANDOM.nextInt(0, CASELLE_PER_LATO);
        if (caselle[x][y].getNumero() == 0) {
            caselle[x][y].genera();
        }
    }

    public void aggiornaFinestra() {
        finestra.revalidate();
        finestra.repaint();
    }

}

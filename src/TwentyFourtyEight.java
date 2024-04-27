import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
    private static boolean caselleLibere = true;

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

        finestra.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                switch (c) {
                    case 'w' -> spostaUnisciSposta(Direzione.SU);
                    case 'a' -> spostaUnisciSposta(Direzione.SINISTRA);
                    case 's' -> spostaUnisciSposta(Direzione.GIU);
                    case 'd' -> spostaUnisciSposta(Direzione.DESTRA);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        finestra.setFocusable(true);
        finestra.requestFocusInWindow();

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
        if (!grigliaHaSpazio()) return;

        int x = 0;
        int y = 0;
        while(true) {
            x = RANDOM.nextInt(0, CASELLE_PER_LATO);
            y = RANDOM.nextInt(0, CASELLE_PER_LATO);
            if (caselle[x][y].getNumero() == 0) {
                caselle[x][y].genera();
                break;
            }
        }

    }

    private boolean grigliaHaSpazio() {
        for (int i = 0; i < CASELLE_PER_LATO; i++) {
            for (int j = 0; j < CASELLE_PER_LATO; j++) {
                if (caselle[i][j].getNumero() == 0) return true;
            }
        }
        return false;
    }


    /* ad ogni input dell'utente, vanno
        spostate le celle nella direzione indicata
        unite le celle nella direzione OPPOSTA da quella indicata
        spostare di nuovo le celle nella direzione indicata
        non so perchè sia così complicato, il gioco originale dovrebbe far così
    */
    private void spostaUnisciSposta(Direzione direzione) {
        boolean casellaMossaOUnita = false;
        casellaMossaOUnita = muoviCaselle(direzione);
        casellaMossaOUnita = unisciCaselle(direzione) || casellaMossaOUnita;
        casellaMossaOUnita = muoviCaselle(direzione) || casellaMossaOUnita;

        // se nessuna cella si muove, non deve generare niente
        if (casellaMossaOUnita) generaCasella();

        if (!(spostamentiRimanenti() || unioniRimanenti())) {
            terminaPartita();
            return;
        }

        finestra.repaint();
    }

    private boolean muoviCaselle(Direzione direzione) {
        int rigaSuccessiva = 0;
        int colonnaSuccessiva = 0;
        boolean casellaMossa = false;

        while (spazioDisponibile(direzione)) {
            for (int i = 0; i < CASELLE_PER_LATO; i++) {

                for (int j = 0; j < CASELLE_PER_LATO; j++) {

                    if (caselle[i][j].getNumero() == 0) continue;

                    rigaSuccessiva = calcolaProssimaRiga(direzione, i);
                    colonnaSuccessiva = calcolaProssimaColonna(direzione, j);

                    if (spazioLibero(direzione, i, j)) {
                        caselle[rigaSuccessiva][colonnaSuccessiva].setNumero(caselle[i][j].getNumero());
                        caselle[i][j].setNumero(0);
                        casellaMossa = true;

                    }
                }

            }
        }

        return casellaMossa;

    }

    private boolean unisciCaselle(Direzione direzione) {
        int prossimaRiga = 0;
        int prossimaColonna = 0;
        Direzione direzioneInversa = Direzione.inverti(direzione);
        boolean casellaUnita = false;

        switch (direzione) {
            case SU -> {
                // Direzione.SU -> scorre e unisce dall'alto verso il basso
                for (int j = 0; j < CASELLE_PER_LATO; j++) {        // colonne
                    for (int i = 0; i < CASELLE_PER_LATO - 1; i++) {    // righe
                        prossimaRiga = calcolaProssimaRiga(direzioneInversa, i);
                        if (unionePossibile(direzione, prossimaRiga, j)) {
                            caselle[i][j].raddoppia();
                            caselle[prossimaRiga][j].setNumero(0);
                            casellaUnita = true;
                        }
                    }
                }
            }

            case GIU -> {
                // Direzione.GIU -> scorre e unisce dal basso verso l'alto
                for (int j = 0; j < CASELLE_PER_LATO; j++) {        // colonne
                    for (int i = CASELLE_PER_LATO -1; i > 0; i--) {    // righe
                        prossimaRiga = calcolaProssimaRiga(direzioneInversa, i);
                        if (unionePossibile(direzione, prossimaRiga, j)) {
                            caselle[i][j].raddoppia();
                            caselle[prossimaRiga][j].setNumero(0);
                            casellaUnita = true;
                        }
                    }
                }
            }

            case DESTRA -> {
                // Direzione.DESTRA -> scorre e unisce da destra verso sinistra
                for (int i = 0; i < CASELLE_PER_LATO; i++) {
                    for (int j = CASELLE_PER_LATO -1; j > 0; j--) {
                        prossimaColonna = calcolaProssimaColonna(direzioneInversa, j);
                        if (unionePossibile(direzione, i, prossimaColonna)) {
                            caselle[i][j].raddoppia();
                            caselle[i][prossimaColonna].setNumero(0);
                            casellaUnita = true;
                        }
                    }
                }
            }

            case SINISTRA -> {
                // Direzione.SINISTRA -> scorre e unisce da sinistra verso destra
                for (int i = 0; i < CASELLE_PER_LATO; i++) {
                    for (int j = 0; j < CASELLE_PER_LATO -1; j++) {
                        prossimaColonna = calcolaProssimaColonna(direzioneInversa, j);
                        if (unionePossibile(direzione, i, prossimaColonna)) {
                            caselle[i][j].raddoppia();
                            caselle[i][prossimaColonna].setNumero(0);
                            casellaUnita = true;
                        }
                    }
                }
            }

        }

        return casellaUnita;
    }

    private int calcolaProssimaRiga(Direzione direzione, int riga) {
        return direzione == Direzione.SU ? riga - 1 : direzione == Direzione.GIU ? riga + 1 : riga;
    }

    private int calcolaProssimaColonna(Direzione direzione, int colonna) {
        return direzione == Direzione.DESTRA ? colonna + 1 : direzione == Direzione.SINISTRA ? colonna - 1 : colonna;
    }

    private boolean controllaProssimeCoordinate(int rigaSuccessiva, int colonnaSuccessiva) {
        if (rigaSuccessiva < 0 || rigaSuccessiva >= CASELLE_PER_LATO || colonnaSuccessiva < 0 || colonnaSuccessiva >= CASELLE_PER_LATO) {
            return false;
        }
        return true;
    }

    private boolean spazioLibero(Direzione direzione, final int riga, final int colonna) {
        int rigaSuccessiva = calcolaProssimaRiga(direzione, riga);
        int colonnaSuccessiva = calcolaProssimaColonna(direzione, colonna);

        if (!controllaProssimeCoordinate(rigaSuccessiva, colonnaSuccessiva)) {
            return false;
        }

        return caselle[rigaSuccessiva][colonnaSuccessiva].getNumero() == 0;
    }

    private boolean unionePossibile(Direzione direzione, final int riga, final int colonna) {
        if (caselle[riga][colonna].getNumero() == 0) return false;

        int rigaSuccessiva = calcolaProssimaRiga(direzione, riga);
        int colonnaSuccessiva = calcolaProssimaColonna(direzione, colonna);

        if (!controllaProssimeCoordinate(rigaSuccessiva, colonnaSuccessiva)) {
            return false;
        }

        return caselle[riga][colonna].getNumero() == caselle[rigaSuccessiva][colonnaSuccessiva].getNumero();
    }

    private boolean spazioDisponibile(Direzione direzione) {
        for (int i = 0; i < CASELLE_PER_LATO; i++) {
            for (int j = 0; j < CASELLE_PER_LATO; j++) {
                if (caselle[i][j].getNumero() == 0) continue;
                if (spazioLibero(direzione, i, j)) return true;
            }
        }
        return false;
    }

    private boolean unioneDisponibile(Direzione direzione) {
        for (int i = 0; i < CASELLE_PER_LATO; i++) {
            for (int j = 0; j < CASELLE_PER_LATO; j++) {
                if (caselle[i][j].getNumero() == 0) continue;
                if (unionePossibile(direzione, i, j)) return true;
            }
        }
        return false;
    }

    private boolean spostamentiRimanenti() {
        return spazioDisponibile(Direzione.SU) || spazioDisponibile(Direzione.GIU) || spazioDisponibile(Direzione.DESTRA) || spazioDisponibile(Direzione.SINISTRA);
    }

    private boolean unioniRimanenti() {
        return unioneDisponibile(Direzione.SU) || unioneDisponibile(Direzione.GIU) || unioneDisponibile(Direzione.DESTRA) || unioneDisponibile(Direzione.SINISTRA);
    }

    private void terminaPartita() {
        finestra.remove(griglia);

        JTextArea testo = new JTextArea("Hai perso!");
        testo.setEditable(false);
        testo.setHighlighter(null);
        testo.setBackground(null);
        testo.setFont(testo.getFont().deriveFont(45f));

        JPanel pannello = new JPanel();
        pannello.setPreferredSize(new Dimension(LARGHEZZA_FINESTRA, ALTEZZA_FINESTRA));
        pannello.setLayout(new GridBagLayout());
        pannello.add(testo);

        finestra.add(pannello);
        finestra.repaint();
    }

}

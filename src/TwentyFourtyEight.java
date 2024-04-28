import javax.swing.*;
import javax.swing.text.Position;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class TwentyFourtyEight {
    public static final Random RANDOM = new Random();
    public static final int CASELLE_PER_LATO = 4;
    public static final int LARGHEZZA_FINESTRA = 720;
    public static final int ALTEZZA_FINESTRA = 720;
    public static final int GAP_FRA_CASELLE = 10;
    public static final int ALTEZZA_PANNELLO_PUNTEGGIO = 60;
    private static final float DIMENSIONE_TESTO = 22f;
    private static TwentyFourtyEight istanza = null;
    private static Casella caselle[][] = new Casella[CASELLE_PER_LATO][CASELLE_PER_LATO];
    private static int punteggio = 0;
    private static final JFrame finestra = new JFrame();
    private static JTextArea testoPunteggio = new JTextArea("" + punteggio);

    private TwentyFourtyEight() {
        finestra.setSize(LARGHEZZA_FINESTRA, ALTEZZA_FINESTRA);
        finestra.setResizable(false);
        finestra.setLocationRelativeTo(null);
        finestra.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        finestra.setTitle("2048");

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
        finestra.setVisible(true);
    }

    public static TwentyFourtyEight getInstance() {
        if (istanza == null) {
            istanza = new TwentyFourtyEight();
        }
        return istanza;
    }
    public void mostraSchermataIniziale() {
        JPanel schermataIniziale = new JPanel(new GridBagLayout());
        schermataIniziale.setPreferredSize(new Dimension(LARGHEZZA_FINESTRA, ALTEZZA_FINESTRA));

        JTextArea testo = new JTextArea("2048");
        testo.setEditable(false);
        testo.setHighlighter(null);
        testo.setBackground(null);
        testo.setFont(testo.getFont().deriveFont(DIMENSIONE_TESTO * 2));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 50, 5); // Spaziatura
        schermataIniziale.add(testo, gbc);

        JButton bottone = new JButton("Nuova partita");
        bottone.setFont(testo.getFont().deriveFont(DIMENSIONE_TESTO));
        bottone.setPreferredSize(new Dimension(LARGHEZZA_FINESTRA / 4, ALTEZZA_FINESTRA / 6));
        bottone.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        bottone.setBackground(Color.GRAY);
        bottone.addActionListener(e -> iniziaPartita());

        gbc.gridy++;
        schermataIniziale.add(bottone, gbc);

        finestra.add(schermataIniziale);

        finestra.revalidate();
    }

    private void iniziaPartita() {
        finestra.getContentPane().removeAll();

        JPanel pannelloGioco = new JPanel();
        pannelloGioco.setPreferredSize(new Dimension(LARGHEZZA_FINESTRA, ALTEZZA_FINESTRA));

        JPanel griglia = new JPanel();

        griglia.setLayout(new GridLayout(CASELLE_PER_LATO, CASELLE_PER_LATO, GAP_FRA_CASELLE, GAP_FRA_CASELLE));

        for (int i = 0; i < CASELLE_PER_LATO; i++) {
            for (int j = 0; j < CASELLE_PER_LATO; j++) {
                caselle[i][j] = new Casella();
                griglia.add(caselle[i][j]);
            }
        }
        
        griglia.setPreferredSize(new Dimension(CASELLE_PER_LATO * Casella.LATO, CASELLE_PER_LATO * Casella.LATO));

        pannelloGioco.add(griglia);

        JPanel pannelloTesto = new JPanel(new GridBagLayout());
        pannelloTesto.setPreferredSize(new Dimension(LARGHEZZA_FINESTRA, ALTEZZA_PANNELLO_PUNTEGGIO));

        testoPunteggio.setEditable(false);
        testoPunteggio.setHighlighter(null);
        testoPunteggio.setBackground(null);
        testoPunteggio.setFont(testoPunteggio.getFont().deriveFont(DIMENSIONE_TESTO));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        pannelloTesto.add(testoPunteggio, gbc);
        pannelloGioco.add(pannelloTesto);

        finestra.add(pannelloGioco);

        // inizializza prime due caselle
        generaCasella();
        generaCasella();

        finestra.revalidate();
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
                            punteggio += caselle[i][j].getNumero();
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
                            punteggio += caselle[i][j].getNumero();
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
                            punteggio += caselle[i][j].getNumero();
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
                            punteggio += caselle[i][j].getNumero();
                            caselle[i][prossimaColonna].setNumero(0);
                            casellaUnita = true;
                        }
                    }
                }
            }

        }

        testoPunteggio.setText("Punteggio: " + punteggio);

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
        finestra.getContentPane().removeAll();

        JPanel schermataFinale = new JPanel();
        schermataFinale.setPreferredSize(new Dimension(LARGHEZZA_FINESTRA, ALTEZZA_FINESTRA));
        schermataFinale.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextArea testo = new JTextArea("Hai perso!");
        testo.setEditable(false);
        testo.setHighlighter(null);
        testo.setBackground(null);
        testo.setFont(testo.getFont().deriveFont(DIMENSIONE_TESTO * 3/2));

        JButton giocaAncora = new JButton("Gioca ancora");
        giocaAncora.setFont(testo.getFont().deriveFont(DIMENSIONE_TESTO * 2 /3));
        giocaAncora.addActionListener(e -> {
            riavviaPartita();
        });

        JButton chiudiFinestra = new JButton("Torna al desktop");
        chiudiFinestra.setFont(testo.getFont().deriveFont(DIMENSIONE_TESTO * 2 /3));
        chiudiFinestra.addActionListener(e -> finestra.dispose());

        schermataFinale.add(testo, gbc);

        gbc.gridy++;
        schermataFinale.add(testoPunteggio, gbc);

        gbc.gridy++;
        schermataFinale.add(giocaAncora, gbc);

        gbc.gridx++;
        schermataFinale.add(chiudiFinestra, gbc);

        finestra.add(schermataFinale);
        finestra.revalidate();
    }

    private void riavviaPartita() {
        punteggio = 0;
        for (int i = 0; i < CASELLE_PER_LATO; i++) {
            for (int j = 0; j < CASELLE_PER_LATO; j++) {
                caselle[i][j].setNumero(0);
            }
        }

        iniziaPartita();
    }

}

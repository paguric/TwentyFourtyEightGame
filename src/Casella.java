import javax.swing.*;
import java.awt.*;

public class Casella extends JPanel {
    public static final int LATO = (TwentyFourtyEight.LARGHEZZA_FINESTRA - TwentyFourtyEight.ALTEZZA_PANNELLO_PUNTEGGIO) / TwentyFourtyEight.CASELLE_PER_LATO - 2 * TwentyFourtyEight.GAP_FRA_CASELLE;
    private static final int[] NUMERI_GENERAZIONE = new int[] {2,4};
    private int numero;
    private JTextArea etichettaNumero = new JTextArea();
    private String[] colori = {
            "#F0F8FF", // Tonalità blu chiaro
            "#B0E0E6",
            "#87CEEB",
            "#00BFFF",
            "#1E90FF",
            "#4169E1",
            "#0000FF",
            "#0000CD",
            "#00008B",
            "#000080" // Tonalità blu scuro
    };

    public Casella() {
//        setPreferredSize(new Dimension(LATO, LATO));
        setLayout(new GridBagLayout());

        etichettaNumero.setEditable(false);
        etichettaNumero.setHighlighter(null);
        etichettaNumero.setBackground(null);
        etichettaNumero.setFont(etichettaNumero.getFont().deriveFont(32f));
        
        add(etichettaNumero);

        setVisible(true);
    }

    public int getNumero() {
        return numero;
    }

    public void raddoppia() {
        numero *= 2;
        aggiornaEtichettaNumero();
    }

    public void genera() {
        int numero = TwentyFourtyEight.RANDOM.nextInt(NUMERI_GENERAZIONE[0], NUMERI_GENERAZIONE[1] + 1);
        while (numero != NUMERI_GENERAZIONE[0] && numero != NUMERI_GENERAZIONE[1]) {
            numero = TwentyFourtyEight.RANDOM.nextInt(NUMERI_GENERAZIONE[0], NUMERI_GENERAZIONE[1] + 1);
        }
        this.numero = numero;

        aggiornaEtichettaNumero();
    }

    public void setNumero(int numero) {
        this.numero = numero;
        aggiornaEtichettaNumero();
    }

    private void aggiornaEtichettaNumero() {
        String numeroString = "";
        if (numero != 0) {
            numeroString = "" + numero;
        }
        etichettaNumero.setText(numeroString);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (numero == 0) {
            setBackground(Color.decode(colori[0]));
        } else {
            int potenza = (int)(Math.log(numero) / Math.log(2));
            if (potenza >= colori.length) {
                setBackground(Color.RED);
            } else {
                setBackground(Color.decode(colori[potenza]));
            }
        }
        setBorder(BorderFactory.createLineBorder(Color.darkGray));
    }
}

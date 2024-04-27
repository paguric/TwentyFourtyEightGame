import javax.swing.*;
import java.awt.*;

public class Casella extends JPanel {
    private static final int LATO = TwentyFourtyEight.LARGHEZZA_FINESTRA / TwentyFourtyEight.CASELLE_PER_LATO - 2 * TwentyFourtyEight.GAP_FRA_CASELLE;
    private static final int[] NUMERI_GENERAZIONE = new int[] {2,4};
    private int numero;
    private JTextArea etichettaNumero = new JTextArea();

    public Casella() {
        setPreferredSize(new Dimension(LATO, LATO));
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
    }

    public void genera() {
        int numero = TwentyFourtyEight.RANDOM.nextInt(NUMERI_GENERAZIONE[0], NUMERI_GENERAZIONE[1] + 1);
        while (numero != NUMERI_GENERAZIONE[0] && numero != NUMERI_GENERAZIONE[1]) {
            numero = TwentyFourtyEight.RANDOM.nextInt(NUMERI_GENERAZIONE[0], NUMERI_GENERAZIONE[1] + 1);
        }
        this.numero = numero;

        aggiornaEtichettaNumero();
    }

    private void aggiornaEtichettaNumero() {
        etichettaNumero.append("" + numero);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawRect(0,0,LATO,LATO);

    }
}

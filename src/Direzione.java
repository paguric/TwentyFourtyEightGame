public enum Direzione {
    SU, GIU, DESTRA, SINISTRA;
    public static Direzione inverti(Direzione direzione) {
        Direzione inversa = null;
        switch (direzione) {
            case SU -> inversa = GIU;
            case GIU -> inversa = SU;
            case DESTRA -> inversa = SINISTRA;
            case SINISTRA -> inversa = DESTRA;
        }
        return inversa;
    }
}

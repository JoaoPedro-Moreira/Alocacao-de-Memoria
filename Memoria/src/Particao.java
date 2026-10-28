class Particao {
    private double tamanho;
    private Processo processoAlocado;

    public Particao(double tamanho) {
        this.tamanho = tamanho;
        this.processoAlocado = null;
    }

    public double getTamanho() {
        return tamanho;
    }

    public Processo getProcessoAlocado() {
        return processoAlocado;
    }

    public boolean estaLivre() {
        return processoAlocado == null;
    }

    public boolean alocarProcesso(Processo processo) {
        if (estaLivre() && processo.getTamanho() <= tamanho) {
            processoAlocado = processo;
            processo.setEstado("carregado");
            return true;
        }

        return false;
    }

    public void liberarParticao() {
        if (processoAlocado != null) {
            processoAlocado.setEstado("finalizado");
            processoAlocado = null;
        }
    }
}

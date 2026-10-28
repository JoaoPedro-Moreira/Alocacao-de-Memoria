class Processo {
    private int id;
    private String nome;
    private double tamanho;
    private String estado;

    public Processo(int id, String nome, double tamanho) {
        this.id = id;
        this.nome = nome;
        this.tamanho = tamanho;
        this.estado = "aguardando";
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public double getTamanho() {
        return tamanho;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}



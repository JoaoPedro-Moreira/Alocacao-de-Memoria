import java.util.Scanner;
import java.util.ArrayList;


public class Main {
    static Scanner teclado = new Scanner(System.in);
    static int proximoId = 1;

    public static void main(String[] args) {
        int opcao;

        do {
            System.out.println("=== GERENCIAMENTO DE MEMORIA ===");
            System.out.println("1 - Particao fixa");
            System.out.println("2 - Particao variavel");
            System.out.println("3 - Swap");
            System.out.println("4 - Paginacao");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");

            opcao = teclado.nextInt();

            switch (opcao) {
                case 1:
                    particaoFixa();
                    break;
                case 2:
                    particaoVariavel();
                    break;
                case 3:
                    swap();
                    break;
                case 4:
                    paginacao();
                    break;
                case 0:
                    System.out.println("Programa encerrado.");
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }
        } while (opcao != 0);
    }

    static Processo criarProcesso() {
        System.out.print("Nome do processo: ");
        String nome = teclado.next();

        System.out.print("Tamanho do processo em MB: ");
        double tamanho = teclado.nextDouble();

        while (tamanho <= 0) {
            System.out.print("Digite um tamanho maior que zero: ");
            tamanho = teclado.nextDouble();
        }

        return new Processo(proximoId++, nome, tamanho);
    }

    static void mostrarProcesso(Processo p) {
        System.out.println(
                "ID: " + p.getId()
                        + " | Nome: " + p.getNome()
                        + " | Tamanho: " + p.getTamanho() + " MB"
                        + " | Estado: " + p.getEstado()
        );
    }


    static void particaoFixa() {
        ArrayList<Particao> particoes = new ArrayList<>();

        System.out.print("Quantidade de particoes: ");
        int quantidade = teclado.nextInt();

        for (int i = 0; i < quantidade; i++) {
            System.out.print("Tamanho da particao " + (i + 1) + ": ");
            double tamanho = teclado.nextDouble();

            while (tamanho <= 0) {
                System.out.print("Digite um tamanho positivo: ");
                tamanho = teclado.nextDouble();
            }

            particoes.add(new Particao(tamanho));
        }

        System.out.print("Quantidade de processos: ");
        int quantidadeProcessos = teclado.nextInt();

        for (int i = 0; i < quantidadeProcessos; i++) {
            Processo p = criarProcesso();
            boolean alocado = false;

            for (int j = 0; j < particoes.size(); j++) {
                if (particoes.get(j).alocarProcesso(p)) {
                    System.out.println("Processo alocado na particao " + (j + 1));
                    alocado = true;
                    break;
                }
            }

            if (!alocado) {
                System.out.println("Nao foi possivel alocar o processo.");
            }
        }

        System.out.println("ESTADO DAS PARTICOES");
        double desperdicio = 0;

        for (int i = 0; i < particoes.size(); i++) {
            Particao p = particoes.get(i);

            System.out.print("Particao " + (i + 1) + " (" + p.getTamanho() + " MB): ");

            if (p.estaLivre()) {
                System.out.println("Livre");
            } else {
                Processo processo = p.getProcessoAlocado();
                mostrarProcesso(processo);
                desperdicio += p.getTamanho() - processo.getTamanho();
            }
        }

        System.out.println("Fragmentacao interna: " + desperdicio + " MB");
    }

    static void particaoVariavel() {
        ArrayList<Bloco> memoria = new ArrayList<>();

        System.out.print("Tamanho total da memoria em MB: ");
        double total = teclado.nextDouble();

        while (total <= 0) {
            System.out.print("Digite um tamanho positivo: ");
            total = teclado.nextDouble();
        }

        memoria.add(new Bloco(total));

        System.out.print("Quantidade de processos: ");
        int quantidade = teclado.nextInt();

        for (int i = 0; i < quantidade; i++) {
            Processo processo = criarProcesso();
            boolean alocado = false;

            for (int j = 0; j < memoria.size(); j++) {
                Bloco bloco = memoria.get(j);

                if (bloco.processo == null &&
                        bloco.tamanho >= processo.getTamanho()) {

                    double sobra = bloco.tamanho - processo.getTamanho();

                    bloco.tamanho = processo.getTamanho();
                    bloco.processo = processo;
                    processo.setEstado("carregado");

                    if (sobra > 0) {
                        memoria.add(j + 1, new Bloco(sobra));
                    }

                    System.out.println("Processo alocado com sucesso.");
                    alocado = true;
                    break;
                }
            }

            if (!alocado) {
                System.out.println("Memoria insuficiente para o processo.");
            }
        }

        System.out.println("MAPA DA MEMORIA");
        double livre = 0;
        double ocupada = 0;

        for (int i = 0; i < memoria.size(); i++) {
            Bloco bloco = memoria.get(i);

            if (bloco.processo == null) {
                System.out.println("Bloco livre: " + bloco.tamanho + " MB");
                livre += bloco.tamanho;
            } else {
                System.out.println(
                        bloco.processo.getNome() + ": "
                                + bloco.tamanho + " MB"
                );
                ocupada += bloco.tamanho;
            }
        }

        System.out.println("Memoria ocupada: " + ocupada + " MB");
        System.out.println("Memoria livre: " + livre + " MB");
    }


    static void swap() {
        ArrayList<Processo> ram = new ArrayList<>();
        ArrayList<Processo> areaSwap = new ArrayList<>();

        System.out.print("Capacidade da RAM em MB: ");
        double capacidadeRAM = teclado.nextDouble();

        System.out.print("Capacidade da area de swap em MB: ");
        double capacidadeSwap = teclado.nextDouble();

        while (capacidadeRAM <= 0 || capacidadeSwap < 0) {
            System.out.println("RAM deve ser positiva e swap nao pode ser negativa.");
            System.out.print("Capacidade da RAM: ");
            capacidadeRAM = teclado.nextDouble();
            System.out.print("Capacidade da swap: ");
            capacidadeSwap = teclado.nextDouble();
        }

        System.out.print("Quantidade de processos: ");
        int quantidade = teclado.nextInt();

        for (int i = 0; i < quantidade; i++) {
            Processo p = criarProcesso();

            if (p.getTamanho() > capacidadeRAM) {
                System.out.println("Processo maior que a capacidade da RAM.");
                continue;
            }

            double usadaRAM = somarMemoria(ram);
            double usadaSwap = somarMemoria(areaSwap);

            while (usadaRAM + p.getTamanho() > capacidadeRAM
                    && !ram.isEmpty()) {

                Processo removido = ram.get(0);

                if (usadaSwap + removido.getTamanho() > capacidadeSwap) {
                    break;
                }

                ram.remove(0);
                removido.setEstado("suspenso");
                areaSwap.add(removido);

                System.out.println(
                        "Processo " + removido.getNome()
                                + " transferido da RAM para a swap."
                );

                usadaRAM = somarMemoria(ram);
                usadaSwap = somarMemoria(areaSwap);
            }

            if (usadaRAM + p.getTamanho() <= capacidadeRAM) {
                ram.add(p);
                p.setEstado("carregado");
                System.out.println("Processo carregado na RAM.");
            } else {
                System.out.println(
                        "Nao foi possivel carregar " + p.getNome() + ": memoria insuficiente ou swap cheia."
                );
            }
        }

        System.out.println("PROCESSOS NA RAM");
        mostrarLista(ram);

        System.out.println("PROCESSOS NA SWAP");
        mostrarLista(areaSwap);

        System.out.println("RAM utilizada: " + somarMemoria(ram) + " MB");
        System.out.println("Swap utilizada: " + somarMemoria(areaSwap) + " MB");
    }

    static double somarMemoria(ArrayList<Processo> processos) {
        double total = 0;

        for (int i = 0; i < processos.size(); i++) {
            total += processos.get(i).getTamanho();
        }

        return total;
    }

    static void mostrarLista(ArrayList<Processo> processos) {
        if (processos.isEmpty()) {
            System.out.println("Nenhum processo.");
        }

        for (int i = 0; i < processos.size(); i++) {
            mostrarProcesso(processos.get(i));
        }
    }


    static void paginacao() {
        ArrayList<Pagina> quadros = new ArrayList<>();

        System.out.print("Quantidade de quadros da RAM: ");
        int quantidadeQuadros = teclado.nextInt();

        System.out.print("Tamanho de cada quadro em MB: ");
        double tamanhoQuadro = teclado.nextDouble();

        while (quantidadeQuadros <= 0 || tamanhoQuadro <= 0) {
            System.out.println("Quantidade e tamanho devem ser positivos.");
            System.out.print("Quantidade de quadros: ");
            quantidadeQuadros = teclado.nextInt();
            System.out.print("Tamanho de cada quadro: ");
            tamanhoQuadro = teclado.nextDouble();
        }

        for (int i = 0; i < quantidadeQuadros; i++) {
            quadros.add(new Pagina());
        }

        System.out.print("Quantidade de processos: ");
        int quantidadeProcessos = teclado.nextInt();

        for (int i = 0; i < quantidadeProcessos; i++) {
            Processo p = criarProcesso();

            int paginasNecessarias =
                    (int) Math.ceil(p.getTamanho() / tamanhoQuadro);

            int livres = 0;

            for (int j = 0; j < quadros.size(); j++) {
                if (quadros.get(j).processo == null) {
                    livres++;
                }
            }

            if (paginasNecessarias > livres) {
                System.out.println("Quadros insuficientes para " + p.getNome());
                continue;
            }

            int numeroPagina = 1;

            for (int j = 0; j < quadros.size(); j++) {
                if (quadros.get(j).processo == null
                        && numeroPagina <= paginasNecessarias) {

                    quadros.get(j).processo = p;
                    quadros.get(j).numeroPagina = numeroPagina;
                    numeroPagina++;
                }
            }

            p.setEstado("carregado");
            System.out.println(
                    "Processo " + p.getNome() + " dividido em "
                            + paginasNecessarias + " pagina(s)."
            );
        }

        System.out.println(" TABELA DE QUADROS");
        double desperdicio = 0;

        for (int i = 0; i < quadros.size(); i++) {
            Pagina quadro = quadros.get(i);

            System.out.print("Quadro " + i + ": ");

            if (quadro.processo == null) {
                System.out.println("Livre");
            } else {
                System.out.println(
                        "Processo " + quadro.processo.getNome()
                                + " | Pagina " + quadro.numeroPagina
                );

                if (quadro.numeroPagina ==
                        (int) Math.ceil(
                                quadro.processo.getTamanho() / tamanhoQuadro
                        )) {

                    desperdicio +=
                            tamanhoQuadro * quadro.numeroPagina
                                    - quadro.processo.getTamanho();
                }
            }
        }

        System.out.println(
                "Desperdicio no ultimo quadro de cada processo: "
                        + desperdicio + " MB"
        );
    }
}
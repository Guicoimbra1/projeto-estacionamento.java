import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;


public class Main {

    //Declaração das variáveis que são usadas no programa
    static String[][] estacionamento;
    static int corredores, vagasPorCorredor, qntMotos, qntCarros, qntVans;
    static double totalFaturadoMotos, totalFaturadoCarros, totalFaturadoVans;
    static double primeiroValor, valorAdicional;

    // Função para carregar dados
    public static void carregarDados(Scanner scanner) {
        limpartela();
        scanner.nextLine();
        System.out.print("Informe o nome do arquivo (ex: dados.txt): ");
        String nomeArquivo = scanner.nextLine();

        try {
            FileReader reader = new FileReader(nomeArquivo);
            BufferedReader buffer = new BufferedReader(reader);
            String linha;

            while ((linha = buffer.readLine()) != null) {
                if (linha.length() >= 10 && linha.contains("=")) {
                    String[] partes = linha.split("=");
                    String vaga = partes[0];
                    String conteudo = partes[1];

                    char corredorLetra = vaga.charAt(0);
                    int corredor = corredorLetra - 'A';
                    int posicao = Integer.parseInt(vaga.substring(1)) - 1;

                    if (corredor >= 0 && corredor < corredores && posicao >= 0 && posicao < vagasPorCorredor) {
                        estacionamento[corredor][posicao] = conteudo;
                    } else {
                        System.out.println("Erro ao ler o arquivo. Os dados informados não condizem com o tamanho do estacionamento informado.");
                        System.out.println("Pressione ENTER para continuar...");
                        scanner.nextLine();
                        return;
                    }
                }
                else{
                    System.out.println("Erro ao ler o arquivo. Verifique se o conteúdo do arquivo está no formato correto (ex: vaga=tipo:horas:minutos) tente novamente.");
                    System.out.println("Pressione ENTER para continuar...");
                    scanner.nextLine();
                    return;
                }
            }

            buffer.close();
            reader.close();
            System.out.println("Dados carregados com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo. Verifique o nome e tente novamente.");
            System.out.println("Pressione ENTER para continuar...");
            scanner.nextLine();
            return;
        }
        System.out.println("Pressione ENTER para continuar...");
        scanner.nextLine();
    }

    // Função para consultar vaga
    public static void consultarVaga(Scanner scanner) {
        limpartela();
        scanner.nextLine();
        System.out.print("Informe a vaga (ex: A1, B3, C10): ");
        String vaga = scanner.nextLine().toUpperCase();

        if (vaga.length() < 2) {
            System.out.println("Formato inválido! Pressione ENTER para continuar.");
            scanner.nextLine();
            return;
        }

        char corredorLetra = vaga.charAt(0);
        int corredor = corredorLetra - 'A';
        int posicao;

        try {
            posicao = Integer.parseInt(vaga.substring(1)) - 1;
        } catch (Exception e) {
            System.out.println("Formato inválido! Pressione ENTER para continuar.");
            scanner.nextLine();
            return;
        }

        if (corredor < 0 || corredor >= corredores || posicao < 0 || posicao >= vagasPorCorredor) {
            System.out.println("Vaga inexistente! Pressione ENTER para continuar.");
            scanner.nextLine();
            return;
        }

        String valor = estacionamento[corredor][posicao];
        if (valor.equals(".")) {
            System.out.println("A vaga " + vaga + " está livre.");
        } else {
            String[] dados = valor.split(":");
            String tipo = dados[0];
            String hora = dados[1];
            String minuto = dados[2];
            System.out.println("Vaga " + vaga + " está OCUPADA por um(a) " +
                               (tipo.equals("M") ? "Moto" :
                                tipo.equals("C") ? "Carro" : "Van") +
                               " desde " + hora + ":" + minuto + ".");
        }

        System.out.println("Pressione ENTER para continuar...");
        scanner.nextLine();
    }

    // Função para registrar entrada
    public static void entrada(Scanner scanner) {
        limpartela();
        System.out.println("Opção de registrar entrada selecionada."); //Pede o tipo de veículo
        System.out.println("Informe o tipo de veículo");
        System.out.println("Digite \"M\" para Moto");
        System.out.println("Digite \"C\" para Carro");
        System.out.println("Digite \"V\" para Van");
        System.out.print("Digite sua escolha: ");
        String tipoVeiculo = scanner.next();
        if (!tipoVeiculo.equalsIgnoreCase("M") && !tipoVeiculo.equalsIgnoreCase("C") && !tipoVeiculo.equalsIgnoreCase("V")) {
            System.out.println("O valor informado deve ser \"M\", \"C\" ou \"V\".");
            valorInvalido(scanner);
            return;
        }
        System.out.print("Informe a hora de entrada no formato HH:MM (24 horas): "); // Pede a hora de entrada
        String horaEntrada = scanner.next();
        if (!horaEntrada.contains(":")) {
            System.out.println("Hora inválida. O formato deve ser HH:MM (24 horas)."); //verifica se o formato está correto
            valorInvalido(scanner);
            return;
        }
        int hora = Integer.parseInt(horaEntrada.split(":")[0]);
        int minutos = Integer.parseInt(horaEntrada.split(":")[1]);
        if (hora < 0 || hora > 23 || minutos < 0 || minutos > 59) {
            System.out.print("Hora inválida. O formato deve ser HH:MM (24 horas)."); //verifica se a hora e minutos são válidos
            valorInvalido(scanner);
            return;
        }
        limpartela();
        scanner.nextLine(); // Consumir a quebra de linha pendente
        System.out.println("Informe a vaga onde o veículo será estacionado. Exemplo: A1 (Corredor A, Vaga 1)"); // Pede a vaga
        System.out.println("(Ou apenas o corredor para a primeira vaga livre do corredor). ");
        System.out.println("(Ou deixe em branco para alocação automática na primeira vaga disponível)");
        System.out.print("Digite sua escolha: ");
        String vagaEscolhida = scanner.nextLine();
        limpartela();
        if (vagaEscolhida.length() == 0) { // Alocação automática na primeira vaga disponível
            for (int i = 0; i < corredores; i++) {
                for (int j = 0; j < vagasPorCorredor; j++) {
                    if (estacionamento[i][j].equals(".")) {
                        estacionamento[i][j] = tipoVeiculo.toUpperCase() + ":" + horaEntrada;
                        System.out.println("Veículo alocado na vaga " + (char)('A' + i) + (j + 1) + ".");
                        System.out.println("Pressione \"Enter\" para continuar.");
                        scanner.nextLine();
                        return;
                    }
                }
            }
            System.out.println("Desculpe o estacionamento está cheio. Não há vagas disponíveis.");
            System.out.println("Pressione \"Enter\" para continuar.");
            scanner.nextLine();
            return;
        }
        int corredorIndex = vagaEscolhida.toUpperCase().charAt(0) - 'A'; // Converte a letra do corredor para índice e verifica se a vaga existe
        if (corredorIndex < 0 || corredorIndex >= corredores || (vagaEscolhida.length() > 1 && (Integer.parseInt(vagaEscolhida.substring(1)) < 1 || Integer.parseInt(vagaEscolhida.substring(1)) > vagasPorCorredor))) {
            System.out.println("Vaga inexistente. O corredor Deve ser entre A e " + (char)('A' + corredores - 1) + ".");
            System.out.println("E o número da vaga deve ser entre 1 e " + vagasPorCorredor + ".");
            System.out.println();
            System.out.println("Pressione \"Enter\" para retornar.");
            scanner.nextLine();
            return;
        }
        else if (vagaEscolhida.length() == 1) { // Alocação automática na primeira vaga disponível do corredor escolhido
            for (int j = 0; j < vagasPorCorredor; j++) {
                if (estacionamento[corredorIndex][j].equals(".")) {
                    estacionamento[corredorIndex][j] = tipoVeiculo.toUpperCase() + ":" + horaEntrada;
                    System.out.println("Veículo alocado na vaga " + (char)('A' + corredorIndex) + (j + 1) + ".");
                    System.out.println("Pressione \"Enter\" para continuar.");
                    scanner.nextLine();
                    return;
                }
            }
            System.out.println("Desculpe o corredor " + (char)('A' + corredorIndex) + " está cheio. Não há vagas disponíveis.");
            System.out.println("Pressione \"Enter\" para continuar.");
            scanner.nextLine();
            return;
        } else { // Alocação na vaga específica escolhida
            int vagaIndex = Integer.parseInt(vagaEscolhida.substring(1)) - 1;
            if (estacionamento[corredorIndex][vagaIndex].equals(".")) {
                estacionamento[corredorIndex][vagaIndex] = tipoVeiculo.toUpperCase() + ":" + horaEntrada;
                System.out.println("Veículo alocado na vaga " + vagaEscolhida.toUpperCase() + ".");
                System.out.println("Pressione \"Enter\" para continuar.");
                scanner.nextLine();
                return;
            } else {
                System.out.println("Desculpe a vaga " + vagaEscolhida.toUpperCase() + " já está ocupada.");
                System.out.println("Pressione \"Enter\" para continuar.");
                scanner.nextLine();
                return;
            }
        }
    }

    // Função para registrar saída
    public static void saida(Scanner scanner) {
        limpartela();
        // Pede a vaga ao usuário
        System.out.println("Opção de saída selecionada."); 
        System.out.println("Informe a vaga onde o veículo está estacionado. Exemplo: A1 (Corredor A, Vaga 1)");
        System.out.print("Digite sua escolha: "); 
        String vagaEscolhida = scanner.next();
        limpartela();
        // Converte a letra do corredor para índice e verifica se a vaga existe
        int corredorIndex = vagaEscolhida.toUpperCase().charAt(0) - 'A'; 
        if (vagaEscolhida.length() < 2 ||corredorIndex < 0 || corredorIndex >= corredores || (vagaEscolhida.length() > 1 && (Integer.parseInt(vagaEscolhida.substring(1)) < 1 || Integer.parseInt(vagaEscolhida.substring(1)) > vagasPorCorredor))) {
            System.out.println("É necessário informar a vaga completa. Exemplo: A1 (Corredor A, Vaga 1)");
            System.out.println("Vaga inexistente. O corredor Deve ser entre A e " + (char)('A' + corredores - 1) + ".");
            System.out.println("E o número da vaga deve ser entre 1 e " + vagasPorCorredor + ".");
            System.out.println();
            System.out.println("Pressione \"Enter\" para retornar.");
            scanner.nextLine();
            scanner.nextLine();
            return;
        }     
        // Verifica se a vaga está ocupada
        int vagaIndex = Integer.parseInt(vagaEscolhida.substring(1)) - 1;
        if (estacionamento[corredorIndex][vagaIndex].equals(".")) { 
            System.out.println("A vaga " + vagaEscolhida.toUpperCase() + " já está vazia.");
            System.out.println("Pressione \"Enter\" para continuar.");
            scanner.nextLine();
            scanner.nextLine();
            return;
        }
        // Pede a hora de saída
        System.out.println("Informe a hora de saída no formato HH:MM (24 horas) o horário deve ser maior do que o de entrada -> " + estacionamento[corredorIndex][vagaIndex].substring(2));
        System.out.print("Digite o horário: "); 
        String horaEntrada = scanner.next();
        limpartela();
        // verifica se o formato está correto
        if (!horaEntrada.contains(":")) { 
            System.out.println("Hora inválida. O formato deve ser HH:MM (24 horas).");
            valorInvalido(scanner);
            return;
        }
        // verifica se a hora e minutos são válidos
        int hora = Integer.parseInt(horaEntrada.split(":")[0]);
        int minutos = Integer.parseInt(horaEntrada.split(":")[1]);
        if (hora < 0 || hora > 23 || minutos < 0 || minutos > 59) { 
            System.out.print("Hora inválida. O formato deve ser HH:MM (24 horas).");
            valorInvalido(scanner);
            return;
        }
        // Verifica se a hora de saída é maior que a de entrada
        int minutosNaVaga = diferencaDeHoras(hora, minutos, estacionamento[corredorIndex][vagaIndex].substring(2));
        if (minutosNaVaga < 0) { 
            System.out.println("Hora de saída inválida. Deve ser maior do que a hora de entrada.");
            valorInvalido(scanner);
            return;
        }
        String tempoNaVaga = String.format("%02d:%02d", minutosNaVaga / 60, minutosNaVaga % 60); 
        char tipoVeiculo = estacionamento[corredorIndex][vagaIndex].charAt(0);
        double valorTotal = 0;
        // calcula o valor total a ser pago
        minutosNaVaga -= 30; 
        if (tipoVeiculo == 'M') {valorTotal += primeiroValor * 0.7;} 
        else if (tipoVeiculo == 'C') {valorTotal += primeiroValor;}   
        else if (tipoVeiculo == 'V') {valorTotal += primeiroValor * 1.3;}
        while (minutosNaVaga > 0) {
            if (tipoVeiculo == 'M') {valorTotal += valorAdicional * 0.7; minutosNaVaga -= 30;} 
            else if (tipoVeiculo == 'C') {valorTotal += valorAdicional; minutosNaVaga -= 30;}   
            else if (tipoVeiculo == 'V') {valorTotal += valorAdicional * 1.3; minutosNaVaga -= 30;}
        }
        limpartela();
        // Pede a confirmação da saída
        System.out.println("O veículo do tipo " + (tipoVeiculo == 'M' ? "Moto" : tipoVeiculo == 'C' ? "Carro" : "Van") + " esteve estacionado por " + tempoNaVaga + " e o valor total a ser pago é R$" + String.format("%.2f", valorTotal) + ".");
        System.out.println("Deseja confirmar a saída e o pagamento? "+ "Digite \"S\" para Sim ou \"N\" para Não.");
        System.out.print("Digite sua escolha: ");
        String confirmar = scanner.next().toUpperCase();
        while (!confirmar.equals("S") && !confirmar.equals("N")) {
            System.out.println("Opção inválida. Digite \"S\" para Sim ou \"N\" para Não.");
            System.out.print("Digite sua escolha: ");
            confirmar = scanner.next().toUpperCase();
        }
        // Confirma a saída e atualiza os dados do estacionamento
        if (confirmar.equals("S")) { 
            estacionamento[corredorIndex][vagaIndex] = ".";
            if (tipoVeiculo == 'M') {totalFaturadoMotos += valorTotal; qntMotos++;} 
            else if (tipoVeiculo == 'C') {totalFaturadoCarros += valorTotal; qntCarros++;}   
            else if (tipoVeiculo == 'V') {totalFaturadoVans += valorTotal; qntVans++;}
            else if (tipoVeiculo == 'C') {valorTotal += primeiroValor;}   
            else if (tipoVeiculo == 'V') {valorTotal += primeiroValor * 1.3;}
            limpartela();
            System.out.println("Saída confirmada. Obrigado!");
            System.out.println("Pressione \"Enter\" para continuar.");
            scanner.nextLine();
            scanner.nextLine();
            return;
        // Cancela a saída
        } else if (confirmar.equals("N")) {
            limpartela();
            System.out.println("Saída cancelada. Nenhum valor foi cobrado.");
            System.out.println("Pressione \"Enter\" para continuar.");
            scanner.nextLine();
            scanner.nextLine();
            return;
        }

    }
    

    // Função para mostrar ocupação
    public static void ocupacao(Scanner scanner) {
        limpartela();

        int totalVagas = corredores * vagasPorCorredor;
        int motos = 0, carros = 0, vans = 0;

        // Cabeçalho numérico
        System.out.print("   ");
        for (int j = 0; j < vagasPorCorredor; j++) {
            System.out.printf("%02d ", j + 1);
        }
        System.out.println();

        // Mapa e contagem
        for (int i = 0; i < corredores; i++) {
            char letraCorredor = (char) ('A' + i);
            System.out.print(letraCorredor + "  ");
            for (int j = 0; j < vagasPorCorredor; j++) {
                String vaga = estacionamento[i][j];
                if (vaga.equals(".")) {
                    System.out.print(".  ");
                } else if (vaga.startsWith("M")) {
                    System.out.print("M  ");
                    motos++;
                } else if (vaga.startsWith("C")) {
                    System.out.print("C  ");
                    carros++;
                } else if (vaga.startsWith("V")) {
                    System.out.print("V  ");
                    vans++;
                } else {
                    System.out.print("?  ");
                }
            }
            System.out.println();
        }

        int ocupadas = motos + carros + vans;
        int livres = totalVagas - ocupadas;

        // Percentuais
        double pm = motos * 100.0 / (ocupadas == 0 ? 1 : ocupadas);
        double pc = carros * 100.0 / (ocupadas == 0 ? 1 : ocupadas);
        double pv = vans * 100.0 / (ocupadas == 0 ? 1 : ocupadas);
        double po = ocupadas * 100.0 / totalVagas;
        double pl = livres * 100.0 / totalVagas;

        // Gráfico de barras (20 blocos máx.)
        String barraM = gerarBarra(pm);
        String barraC = gerarBarra(pc);
        String barraV = gerarBarra(pv);
        String barraO = gerarBarra(po);
        String barraL = gerarBarra(pl);

        System.out.println();
        System.out.printf("Moto  : %2d - %5.1f%% |%s| (%d vagas de %d)%n", motos, pm, barraM, motos, ocupadas);
        System.out.printf("Carro : %2d - %5.1f%% |%s| (%d vagas de %d)%n", carros, pc, barraC, carros, ocupadas);
        System.out.printf("Van   : %2d - %5.1f%% |%s| (%d vagas de %d)%n", vans, pv, barraV, vans, ocupadas);
        System.out.println("------------------------------------------");
        System.out.printf("Ocupadas: %2d - %5.1f%% |%s| (%d vagas de %d)%n", ocupadas, po, barraO, ocupadas, totalVagas);
        System.out.printf("Livres  : %2d - %5.1f%% |%s| (%d vagas de %d)%n", livres, pl, barraL, livres, totalVagas);

        System.out.println("\nPressione ENTER para retornar ao menu...");
        scanner.nextLine();
        scanner.nextLine();
    }

    // Função para mostrar financeiro
    public static void financeiro(Scanner scanner) {
        limpartela();
    
        System.out.printf("%-10s %6s %10s%n", "Veículo", "Quant", "Valor(R$)");
        System.out.println("----------------------------------");

        int totalQuant = qntMotos + qntCarros + qntVans;
        double totalValor = totalFaturadoMotos + totalFaturadoCarros + totalFaturadoVans;

        System.out.printf("%-10s %6d %10.2f%n", "Moto", qntMotos, totalFaturadoMotos);
        System.out.printf("%-10s %6d %10.2f%n", "Carro", qntCarros, totalFaturadoCarros);
        System.out.printf("%-10s %6d %10.2f%n", "Van", qntVans, totalFaturadoVans);

        System.out.println("----------------------------------");
        System.out.printf("%-10s %6d %10.2f%n", "Total", totalQuant, totalValor);
        System.out.println("\nPressione ENTER para retornar ao menu...");
        scanner.nextLine();
        scanner.nextLine();
    }

    // Função para salvar dados em um arquivo txt no formato correto
    public static void salvarDados(Scanner scanner) {
        limpartela();
        scanner.nextLine();
        System.out.print("Informe o nome do arquivo para salvar os dados (ex: dados.txt): ");
        String nomeArquivo = scanner.nextLine();
        //Verifica se já existe um arquivo com esse nome
        try {
            FileReader testReader = new FileReader(nomeArquivo);
            testReader.close();
            System.out.print("Um arquivo com esse nome já existe. Deseja sobrescrevê-lo? (S/N): ");
            String resposta = scanner.nextLine().toUpperCase();
            if (!resposta.equals("S")) {
                System.out.println("Operação cancelada. Nenhum dado foi salvo.");
                System.out.println("Pressione ENTER para continuar...");
                scanner.nextLine();
                return;
            }
        } catch (IOException e) {
            // Arquivo não existe.
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo));

            for (int i = 0; i < corredores; i++) {
                for (int j = 0; j < vagasPorCorredor; j++) {
                    if (!estacionamento[i][j].equals(".")) {
                        char corredorLetra = (char) ('A' + i);
                        String vaga = corredorLetra + Integer.toString(j + 1);
                        String conteudo = estacionamento[i][j];
                        writer.write(vaga + "=" + conteudo);
                        writer.newLine();
                    }
                }
            }

            writer.close();
            limpartela();
            System.out.println("Dados salvos com sucesso no arquivo '" + nomeArquivo + "'!");
        } catch (IOException e) {
            System.out.println("Erro ao salvar os dados no arquivo. Tente novamente.");
        }
        System.out.println("Pressione ENTER para continuar...");
        scanner.nextLine();
    }

    // Função para mostrar integrantes
    public static void integrantes(Scanner scanner) {
        limpartela();
        System.out.println("Integrantes do Grupo:");
        System.out.println("------------------------------------");
        System.out.println("1. Bruno da Silva Morinel");
        System.out.println("2. Guilherme Cruz Coimbra");
        System.out.println("3. Isadora Moraes Rodrigues");
        System.out.println("4. Pedro Piccoli Bruschi");
        System.out.println("------------------------------------");
        System.out.println();
        System.out.println("\nPressione ENTER para retornar ao menu...");
        scanner.nextLine();
        scanner.nextLine();
    }

    // Função para limpar a tela do console
    public static void limpartela() {
        System.out.print("\033\143");
    }

    // Função auxiliar para criar barra de 20 caracteres com base na porcentagem
    public static String gerarBarra(double percentual) {
        int blocos = (int) Math.round(percentual / 5); // 20 blocos máx (100/5)
        StringBuilder barra = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            if (i < blocos) barra.append("=");
            else barra.append(".");
        }
        return barra.toString();
    }


    // Função para exibir mensagem de valor inválido
    public static void valorInvalido(Scanner scanner) {
        System.out.println();
        System.out.println("Valor inválido. Pressione \"Enter\" para retornar.");
        scanner.nextLine();
        scanner.nextLine();
    }
    // Função para calcular a diferença de horas e minutos entre entrada e saída
    public static int diferencaDeHoras(int horaSaida, int minutoSaida, String horaEntradaStr) {
        int horaEntrada = Integer.parseInt(horaEntradaStr.split(":")[0]);
        int minutoEntrada = Integer.parseInt(horaEntradaStr.split(":")[1]);

        int totalMinutosEntrada = horaEntrada * 60 + minutoEntrada;
        int totalMinutosSaida = horaSaida * 60 + minutoSaida;

        return totalMinutosSaida - totalMinutosEntrada;
    }

    // Função para obter os valores iniciais do estacionamento e garantir que sejam válidos
    public static void inicio(Scanner scanner) {
        while (true) {
            limpartela();
            System.out.println("Bem-vindo ao programa de gerenciamento do estacionamento!");
            System.out.println("Para começar, preencha as informações necessárias:");
            System.out.println();
            System.out.println("Informe o valor de referência a ser cobrado para os primeiros 30 minutos (Moto paga 70%, carro paga 100% e van paga 130% desse valor):");
            System.out.print("R$");
            try {primeiroValor = scanner.nextDouble();} catch (Exception e) {valorInvalido(scanner); continue;}
            if (primeiroValor < 0) {
                valorInvalido(scanner);
                continue;
            }
            System.out.println("Informe o valor de referência a ser cobrado para cada 30 minutos adicionais (Moto paga 70%, carro paga 100% e van paga 130% desse valor):");
            System.out.print("R$");
            try {valorAdicional = scanner.nextDouble();} catch (Exception e) {valorInvalido(scanner); continue;}
            if (valorAdicional < 0) {
                valorInvalido(scanner);
                continue;
            }
            System.out.print("Informe a quantidade corredores no estacionamento (entre 5 e 15): ");
            try {corredores = scanner.nextInt();} catch (Exception e) {valorInvalido(scanner); continue;}
            if (corredores < 5 || corredores > 15) {
                valorInvalido(scanner);
                continue;
            }
            System.out.print("Informe a quantidade vagas por corredor (entre 5 e 20): ");
            try {vagasPorCorredor = scanner.nextInt();} catch (Exception e) {valorInvalido(scanner); continue;}
            if (vagasPorCorredor < 5 || vagasPorCorredor > 20) {
                valorInvalido(scanner);
                continue;
            }
            break;
        }    
    }

    // Função para exibir o menu e obter a escolha do usuário
    public static int menu(Scanner scanner) {

        limpartela();

        System.out.println("Bem-vindo ao programa de gerenciamento do estacionamento!");
        System.out.println("Escolha a opção desejada:");
        System.out.println("[--------------------------------]");
        System.out.println("[1] - Carregar Dados.");
        System.out.println("[2] - Consultar Vaga.");
        System.out.println("[3] - Entrada.");
        System.out.println("[4] - Saída.");
        System.out.println("[5] - Ocupação.");
        System.out.println("[6] - Financeiro.");
        System.out.println("[7] - Salvar Dados.");
        System.out.println("[8] - Integrantes.");
        System.out.println();
        System.out.println("[9] !Sair!");
        System.out.println("[--------------------------------]");
        System.out.print("\nEscolha uma opção: ");


        int item = scanner.nextInt();
        return item;
    } 

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        int escolha = 0;
        inicio(scanner);

        estacionamento = new String[corredores][vagasPorCorredor]; // Cria a matriz com base nos valores fornecidos pelo usuário.
        for (int i = 0; i < corredores; i++) Arrays.fill(estacionamento[i], "."); // Preenche todos os elementos da matriz com o valor inicial "."

        while (escolha !=9){

            try {escolha = menu(scanner);} catch (Exception e) {scanner.next(); continue;} // Tratamento de erro para entradas inválidas

            switch (escolha) {
                case 1:
                    carregarDados(scanner);
                    break;
                case 2:
                    consultarVaga(scanner);
                    break;
                case 3:
                    entrada(scanner);
                    break;
                case 4: 
                    saida(scanner);
                    break;
                case 5: 
                    ocupacao(scanner);
                    break;
                case 6: 
                    financeiro(scanner);
                    break;
                case 7: 
                    salvarDados(scanner);
                    break;
                case 8: 
                    integrantes(scanner);
                    break;
                case 9:
                    System.out.println("Saindo...");
                    break;
                default:
                    break;
            }
        }
    }
}

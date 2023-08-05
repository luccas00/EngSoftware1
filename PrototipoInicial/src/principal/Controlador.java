package principal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import common.*;

public class Controlador extends JFrame {

    // Variaveis geradas pelo Chat GPT
    private static final String LOGIN_FILE = "login.csv";
    private static final String DATA_FILE_STRING = common.Paths.getDataPath();
    private List<Cadaver> cadaveres;
    private JPanel mainPanel;
    private JPanel homePanel;
    private CardLayout cardLayout;

    public Controlador() {

        // Parametros gerados pelo Chat GPT
        setTitle("Gerenciamento Necroterio");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Esse array list recebe todos os registros do arquivo quando o programa é iniciado
        cadaveres = new ArrayList<>();

        // Variavies do Chat GPT
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        // Chamada da funçao para criar painel home
        createHomePanel();

        mainPanel.add(homePanel, "home");

        add(mainPanel);

        // Verificar se existe um ADMIN cadastrado
        AdminCheck();

        if (realizarLogin()) {
            Inicializar(); // Verifica os arquivos de dados
            lerProdutosDoArquivo(); // Essa funçao usa a variavel List<Cadaver> cadaveres
            showHomePage(); // Exibe a home page
            setVisible(true);

        } else {

            JOptionPane.showMessageDialog(this, "Login ou Senha invalidos. Encerrendo o programa.");
            System.exit(0);

        }
    }

    /*
     * Metodo para verificar se ja existe o arquivo de admins, na pasta do projeto,
     * ../Novo/login.csv
     * caso não exista o metodo cria o arquivo e pede um admin inicial
     */
    private void AdminCheck() {
        File login = new File(LOGIN_FILE);

        if (!login.exists()) {
            try {
                if (login.createNewFile()) {
                    JOptionPane.showMessageDialog(this, "Criando primeiro admin...");
                    String nome = JOptionPane.showInputDialog(this, "Digite seu login:");
                    if (nome != null && !nome.isEmpty()) {
                        String senha = JOptionPane.showInputDialog(this, "Digite sua senha:");
                        if (senha != null && !senha.isEmpty()) {
                            try (FileWriter writer = new FileWriter(LOGIN_FILE, true)) {
                                writer.write(nome + ";" + senha);
                                writer.write("\n");
                                writer.flush();
                            } catch (IOException e) {
                                JOptionPane.showMessageDialog(this, "Error writing admin to file: " + e.getMessage());
                            }
                        }
                    }

                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error writing admin to file: " + e.getMessage());
            }

        }
    }

    /*
     * Metodo de inicialização para verificar se existe a pasta e o arquivo de dados
     * dos cadaveres
     * ambos sao criados no Desktop
     */
    private void Inicializar() {
        String folderPath = common.Paths.getDataFolderPath();
        File folder = new File(folderPath);
        String dataPath = folderPath + "/data.csv";
        File data = new File(dataPath);

        if (!folder.exists()) {
            try {
                if (folder.mkdir()) {
                    JOptionPane.showMessageDialog(this, "Criando pasta...");
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao criar pasta...");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }

        if (!data.exists()) {
            try {
                if (data.createNewFile()) {
                    JOptionPane.showMessageDialog(this, "Criando arquivo...");
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao criar o arquivo...");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    /* Metodo para realizar Login - 100% Chat GPT */
    private boolean realizarLogin() {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Bem vindo ao Gerenciamento do Necroterio");
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        loginPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(2, 2));

        JLabel loginLabel = new JLabel("Login:");
        JTextField loginField = new JTextField();
        formPanel.add(loginLabel);
        formPanel.add(loginField);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        loginPanel.add(formPanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(null, loginPanel, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String login = loginField.getText();
            String password = new String(passwordField.getPassword());

            try {
                List<String> credentials = Files.readAllLines(Paths.get(LOGIN_FILE));
                for (String line : credentials) {
                    String[] fields = line.split(";");
                    if (fields.length == 2 && fields[0].equals(login) && fields[1].equals(password)) {
                        return true;
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading the login file: " + e.getMessage());
            }
        }

        return false;
    }

    /* Metodo para CRIAR Pagina inicial, exibida apos realizar login */
    private void createHomePanel() {
        homePanel = new JPanel();
        homePanel.setLayout(new BorderLayout(100, 100));

        // Criar painel de botoes
        JPanel buttonsPanel = new JPanel();

        // Criar botão Listar Registros
        JButton produtosButton = new JButton("Lista de Registros");
        produtosButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarProdutos();
            }
        });
        // Adiciona o botao ao painel de botao
        buttonsPanel.add(produtosButton);

        // Criar botão Adicionar Registro
        JButton addProdutoButton = new JButton("Adicionar Registro");
        addProdutoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                adicionarCadaver();
            }
        });
        // Adiciona o botao ao painel de botao
        buttonsPanel.add(addProdutoButton);

        // Criando botão atualizar cadaver
        JButton atualizarButton = new JButton("Atualizar Registro");
        atualizarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EmDesenvolvimento();
            }
        });
        // Adiciona o botao ao painel de botao
        buttonsPanel.add(atualizarButton);

        // Adicionar painel de botoes ao painel da pagina criado anteriormente
        homePanel.add(buttonsPanel, BorderLayout.NORTH);

        // Novo painel de botoes admin
        JPanel painelBotaoAdmin = new JPanel();
        painelBotaoAdmin.setLayout(new BorderLayout(100, 100));

        // Novo botao
        JButton adminButton = new JButton("Area do Administrador");
        adminButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EmDesenvolvimento();
            }
        });
        // Adiciona o botao ao painel de botao
        painelBotaoAdmin.add(adminButton);

        // Adiciona o painel de botao ao painel da pagina
        homePanel.add(adminButton, BorderLayout.AFTER_LAST_LINE);
    }

    /*
     * Metodo executado quando clicado no botao LISTAR REGISTROS
     * abre uma nova tela e exibe todos os registros do arquivo
     */
    private void mostrarProdutos() {
        lerProdutosDoArquivo(); // Read the products from the file

        // Create the table model
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"CPF", "Nome", "Peso", "Data da morte", "Hora da morte", "Situação" });

        // Populate the table model with data from the cadaver list
        for (Cadaver cadaver : cadaveres) {
            tableModel.addRow(new Object[]{cadaver.getCpf(), cadaver.getNome(), cadaver.getPeso(), cadaver.getDataFalecimento(), cadaver.getHoraFalecimento(), cadaver.getSituacao() });
        }

        // Create the table and scroll pane
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Create the dialog and display the table
        JDialog dialog = new JDialog(this, "Cadaveres", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(scrollPane);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /* Metodo apenas para EXIBIR a pagina inicial */
    private void showHomePage() {
        cardLayout.show(mainPanel, "home");
    }

    /*
     * Metodo executado quando clicado no botao ADICIONAR REGISTROS
     * abre varias caixas de dialogo e adiciona um novo registro no arquivo
     */
    private void adicionarCadaver() {
        String cpf = JOptionPane.showInputDialog(this, "Digite a identificação do corpo (CPF):");
        if (cpf != null && !cpf.isEmpty()) {
            String nome = JOptionPane.showInputDialog(this, "Digite o nome do corpo:");
            if (nome != null && !nome.isEmpty()) {
                String dataFalecimento = JOptionPane.showInputDialog(this,
                        "Digite a data da morte do corpo:");
                if (dataFalecimento != null && !dataFalecimento.isEmpty()) {
                    /*
                     * DateTimeFormatter formatter =
                     * DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                     * LocalDateTime data_horaFalecimento = LocalDateTime.parse(data_hora,
                     * formatter);
                     * /* formatar string data e hora para localdatetime
                     */
                    String horaFalecimento = JOptionPane.showInputDialog(this, "Digite a hora da morte do corpo:");
                    if (horaFalecimento != null && !horaFalecimento.isEmpty()) {
                        Cadaver corpo = new Cadaver(cpf, nome, dataFalecimento, horaFalecimento);
                        cadaveres.add(corpo);
                        escreverProdutosNoArquivo();
                    }
                }
            }
        }
    }

    /* Metodo auxiliar para escrever dados no arquivo .csv */
    private void escreverProdutosNoArquivo() {
        try (FileWriter writer = new FileWriter(DATA_FILE_STRING, true)) {
            Cadaver produto = cadaveres.get(cadaveres.size() - 1);
            writer.write(produto.toString());
            writer.write("\n");
            writer.flush();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to produtos file: " + e.getMessage());
        }
    }

    /* Metodo auxiliar para ler todos os registros do arquivo .csv */
    private void lerProdutosDoArquivo() {
        cadaveres.clear();
        try {
            List<String> linhas = Files.readAllLines(Paths.get(DATA_FILE_STRING));
            for (String linha : linhas) {
                String[] campos = linha.split(";");
                if (campos.length >= 4) {
                    Cadaver produto = new Cadaver(campos[0], campos[1], campos[2], campos[3]);
                    cadaveres.add(produto);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading produtos file: " + e.getMessage());
        }
    }

    private void EmDesenvolvimento() {
        JOptionPane.showMessageDialog(this, "Em Desenvolvimento...");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Controlador();
            }
        });
    }
}

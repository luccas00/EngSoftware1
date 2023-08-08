package principal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import auxiliar.Criptografia;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private static String nomeUsuarioLogado = "";
    
    public Controlador() {

        setTitle("Gerenciamento Necroterio - User: " + nomeUsuarioLogado);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Esse array list recebe todos os registros do arquivo quando o programa é iniciado
        cadaveres = new ArrayList<>();

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
            lerRegistrosDoArquivo(); // Essa funçao usa a variavel List<Cadaver> cadaveres
            showHomePage(); // Exibe a home page
            setTitle("Gerenciamento Necroterio - Usuário: " + nomeUsuarioLogado);
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
                        	String key = Criptografia.generateGUID();
                            try (FileWriter writer = new FileWriter(LOGIN_FILE, true)) {
                                writer.write(nome + ";" + Criptografia.encrypt(senha, key) + ";" + key);
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

    /* Metodo para realizar Login*/
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
                    if (autenticarCriptografia(login, password)) {
                    	nomeUsuarioLogado = login;
                        return true;
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading the login file: " + e.getMessage());
            }
        }

        return false;
    }
    
    private boolean autenticarAdmin() {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Autenticação de Administrador");
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

        int result = JOptionPane.showConfirmDialog(null, loginPanel, "Autenticação de Administrador", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String login = loginField.getText();
            String password = new String(passwordField.getPassword());

            try {
                List<String> adminCredentials = Files.readAllLines(Paths.get(LOGIN_FILE));
                for (String line : adminCredentials) {
                    String[] fields = line.split(";");
                    if (autenticarCriptografia(login, password)) {
                    	JOptionPane.showMessageDialog(this, "Sucesso...");
                        return true;
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading the admin credentials file: " + e.getMessage());
            }
        }
        JOptionPane.showMessageDialog(this, "Falha...");
        return false;
    }

    private boolean autenticarCriptografia(String login, String senha) {
        try {
            List<String> adminCredentials = Files.readAllLines(Paths.get(LOGIN_FILE));
            for (String line : adminCredentials) {
                String[] fields = line.split(";");
                if (fields.length == 3 && fields[0].equals(login)) {
                    String storedEncryptedPassword = fields[1];
                    String storedKey = fields[2];
                    String encryptedPassword = "";
                    try
                    {
                    	encryptedPassword = Criptografia.encrypt(senha, storedKey);
                    }
                    catch (Exception e) {
                    	JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
					}
                    
                    if (storedEncryptedPassword.equals(encryptedPassword)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading the admin credentials file: " + e.getMessage());
        }
        return false;
    }
    
    private void createHomePanel() {
    	
        homePanel = new JPanel();
        homePanel.setLayout(new BorderLayout());

        // Create the bottom panel for the "Área do Administrador" button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton adminButton = new JButton("Área do Administrador");
        adminButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if (autenticarAdmin())
            	{
            		abrirOpcoesAdmin();
            	}
            }
        });
        bottomPanel.add(adminButton);
        // Add the bottom panel to the page panel
        homePanel.add(bottomPanel, BorderLayout.SOUTH);

        // Create the center panel for the main buttons
        JPanel centerPanel = new JPanel(new GridLayout(0, 3, 20, 40)); // 3 columns, variable rows, 10px vertical and horizontal gaps

        // Create botão Listar Registros
        JButton produtosButton = new JButton("Lista de Registros");
        produtosButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarCadaver();
            }
        });
        centerPanel.add(produtosButton);

        // Criar botão Adicionar Registro
        JButton addProdutoButton = new JButton("Adicionar Registro");
        addProdutoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                adicionarCadaver();
            }
        });
        centerPanel.add(addProdutoButton);

        // Criando botão atualizar cadaver
        JButton alterarButton = new JButton("Alterar Registro");
        alterarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EmDesenvolvimento();
            }
        });
        centerPanel.add(alterarButton);

        // Novo botão Buscar cadaver
        JButton buscarButton = new JButton("Buscar Registro");
        buscarButton.setPreferredSize(new Dimension(50, 10));
        buscarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buscarRegistros();
            }
        });
        centerPanel.add(buscarButton);
        
        // Novo botão Apagar cadaver
        JButton apagarButton = new JButton("Apagar Registro");
        apagarButton.setPreferredSize(new Dimension(50, 10));
        apagarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                apagarRegistroPorCPF();
            }
        });
        centerPanel.add(apagarButton);
        
        // Novo botão Situação cadaver
        JButton situacaoButton = new JButton("Alterar Situação");
        situacaoButton.setPreferredSize(new Dimension(50, 10));
        situacaoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EmDesenvolvimento();
            }
        });
        centerPanel.add(situacaoButton);
        
        // Novo botão Buscar cadaver
        JButton ordenarButton = new JButton("Ordenar Registros");
        ordenarButton.setPreferredSize(new Dimension(50, 10));
        ordenarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listaOrdenada();
            }
        });
        centerPanel.add(ordenarButton);
        
        // Novo botão Procedimento no cadaver
        JButton procedimentoButton = new JButton("Adicionar Procedimento");
        procedimentoButton.setPreferredSize(new Dimension(50, 10));
        procedimentoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EmDesenvolvimento();
            }
        });
        centerPanel.add(procedimentoButton);
        
        // Novo botão Encerrar Sistema
        JButton encerrarButton = new JButton("Encerrar Sessão");
        encerrarButton.setPreferredSize(new Dimension(50, 10));
        encerrarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	sairDoSistema();
            }
        });
        centerPanel.add(encerrarButton);

        // Add the center panel to the page panel
        homePanel.add(centerPanel, BorderLayout.CENTER);
    }

    /*
     * Metodo executado quando clicado no botao LISTAR REGISTROS
     * abre uma nova tela e exibe todos os registros do arquivo
     */
    private void mostrarCadaver() {
        lerRegistrosDoArquivo(); // Read the products from the file

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
        
        // Set the size of the dialog
        dialog.setSize(900, 500);
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void mostrarCadaverOrdenado() {

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
        
        // Set the size of the dialog
        dialog.setSize(900, 500);
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void mostrarCadaverOrdenado(List<Cadaver> searchResults) {

        // Create the table model
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"CPF", "Nome", "Peso", "Data da morte", "Hora da morte", "Situação" });

        // Populate the table model with data from the cadaver list
        for (Cadaver cadaver : searchResults) {
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
        
        // Set the size of the dialog
        dialog.setSize(900, 500);
        
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
        JTextField cpfField = new JTextField(15);
        JTextField nomeField = new JTextField(15);
        JTextField pesoField = new JTextField(15);
        JTextField dataFalecimentoField = new JTextField(15);
        JTextField horaFalecimentoField = new JTextField(15);

        // Set placeholder for CPF field
        cpfField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (cpfField.getText().equals("Apenas Números")) {
                    cpfField.setText("");
                    cpfField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (cpfField.getText().isEmpty()) {
                    cpfField.setText("Apenas Números");
                    cpfField.setForeground(Color.GRAY);
                }
            }
        });
        cpfField.setText("Apenas Números");
        cpfField.setForeground(Color.GRAY);

        // Set placeholder for Data de Falacimento field
        dataFalecimentoField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (dataFalecimentoField.getText().equals("DD/MM/YYYY")) {
                    dataFalecimentoField.setText("");
                    dataFalecimentoField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (dataFalecimentoField.getText().isEmpty()) {
                    dataFalecimentoField.setText("DD/MM/YYYY");
                    dataFalecimentoField.setForeground(Color.GRAY);
                }
            }
        });
        dataFalecimentoField.setText("DD/MM/YYYY");
        dataFalecimentoField.setForeground(Color.GRAY);

        // Set placeholder for Hora de Falacimento field
        horaFalecimentoField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (horaFalecimentoField.getText().equals("HH:MM")) {
                    horaFalecimentoField.setText("");
                    horaFalecimentoField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (horaFalecimentoField.getText().isEmpty()) {
                    horaFalecimentoField.setText("HH:MM");
                    horaFalecimentoField.setForeground(Color.GRAY);
                }
            }
        });
        horaFalecimentoField.setText("HH:MM");
        horaFalecimentoField.setForeground(Color.GRAY);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("CPF:"));
        panel.add(cpfField);
        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("Peso:"));
        panel.add(pesoField);
        panel.add(new JLabel("Data da Morte:"));
        panel.add(dataFalecimentoField);
        panel.add(new JLabel("Hora da Morte:"));
        panel.add(horaFalecimentoField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Adicionar Cadáver", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String cpf = cpfField.getText().replaceAll("[^0-9]", ""); // Remove non-numeric characters from the input
            String nome = nomeField.getText();
            String peso = pesoField.getText();
            String dataFalecimento = dataFalecimentoField.getText();
            String horaFalecimento = horaFalecimentoField.getText();

            if (confirmarEntrada(cpf, nome, peso, dataFalecimento, horaFalecimento)) {
                // Show a confirmation dialog before adding the record
                String message = "Deseja adicionar o seguinte cadáver?\n\n"
                        + "CPF: " + formatCPF(cpf) + "\n"
                        + "Nome: " + nome + "\n"
                        + "Peso: " + peso + "\n"
                        + "Data da Morte: " + dataFalecimento + "\n"
                        + "Hora da Morte: " + horaFalecimento + "\n";

                int confirmation = JOptionPane.showConfirmDialog(this, message, "Confirmação", JOptionPane.YES_NO_OPTION);

                if (confirmation == JOptionPane.YES_OPTION) {
                    Cadaver corpo = new Cadaver(formatCPF(cpf), nome, peso, dataFalecimento, horaFalecimento);
                    cadaveres.add(corpo);
                    escreverRegistrosNoArquivo();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos antes de adicionar o cadáver.");
            }
        }
    }

    private String formatCPF(String cpf) {
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }
    
    private boolean confirmarEntrada(String cpf, String nome, String peso, String dataFalecimento, String horaFalecimento)
    {
    	if(cpf.length() < 10 || cpf.isEmpty())
    	{
    		return false;
    	}
    	
    	if(nome.isEmpty() && peso.isEmpty() && dataFalecimento.isEmpty() && horaFalecimento.isEmpty())
    	{
    		return false;
    	}
    	
    	return true;
    }

    /* Metodo auxiliar para escrever dados no arquivo .csv */
    private void escreverRegistrosNoArquivo() {
        try (FileWriter writer = new FileWriter(DATA_FILE_STRING, true)) {
            Cadaver corpo = cadaveres.get(cadaveres.size() - 1);
            writer.write(corpo.toString());
            writer.write("\n");
            writer.flush();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to produtos file: " + e.getMessage());
        }
    }
    /* Metodo auxiliar para ler todos os registros do arquivo .csv */
    private void lerRegistrosDoArquivo() {
        cadaveres.clear();
        try {
            List<String> linhas = Files.readAllLines(Paths.get(DATA_FILE_STRING));
            for (String linha : linhas) {
            	Cadaver corpo = Cadaver.parseCadaver(linha);
            	cadaveres.add(corpo);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading produtos file: " + e.getMessage());
        }
    }

    private void EmDesenvolvimento() {
        JOptionPane.showMessageDialog(this, "Em Desenvolvimento...");
    }
    
    private void sairDoSistema() {
        int confirmation = JOptionPane.showConfirmDialog(this, "Deseja sair do sistema?", "Confirmação", JOptionPane.YES_NO_OPTION);
        
        if (confirmation == JOptionPane.YES_OPTION) {
            // Implement here the code to exit the system
            // For example, you can use System.exit(0) to terminate the application
            System.exit(0);
        }
    }

    public void listaOrdenada() {
        // Show the option dialog with the buttons
        int option = JOptionPane.showOptionDialog(
                this,
                "Escolha o parâmetro de ordenação",
                "Ordenar Cadáveres",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new Object[]{"Nome", "CPF"},
                null
        );

        // Sort the cadaveres list based on the user's choice
        if (option == 0) {
            // Sort by name
            Collections.sort(cadaveres, Comparator.comparing(Cadaver::getNome));
        } else if (option == 1) {
            // Sort by CPF
            Collections.sort(cadaveres, Comparator.comparing(Cadaver::getCpf));
        } else {
            // If the user closes the dialog or doesn't make a selection, return
            return;
        }

        // Display the ordered list using the mostrarCadaver() method
        mostrarCadaverOrdenado();
    }
    
    public void buscarRegistros() {
        // Show the option dialog with the buttons
        int option = JOptionPane.showOptionDialog(
                this,
                "Escolha o parâmetro de busca",
                "Buscar Cadáveres",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new Object[]{"Nome", "CPF"},
                null
        );

        // Check the user's choice and perform the search
        if (option == 0) {
            buscarRegistrosPorNome();
        } else if (option == 1) {
            buscarRegistrosPorCPF();
        } else {
            // If the user closes the dialog or doesn't make a selection, return
            return;
        }
    }
    
    public void buscarRegistrosPorNome() {
        // Ask the user for the search query
        String searchQuery = JOptionPane.showInputDialog(this, "Digite o Nome para buscar:");
        
        if (searchQuery == null) {
            // User canceled the input or closed the dialog
            return;
        }
        
        // Create lists to store the search results
        List<Cadaver> searchResults = new ArrayList<>();

        // Perform the search based on the user's choice (name or CPF)
        for (Cadaver cadaver : cadaveres) {
        	if (searchQuery != null)
        	{
        		if (cadaver.getNome().toLowerCase().contains(searchQuery.toLowerCase())) {
                    searchResults.add(cadaver);
                }
        	}
            
        }

        // Check if any results were found
        if (searchResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum cadáver encontrado para a busca.");
            return;
        }
        
        // Display the search results using the mostrarCadaverOrdenado() method
        mostrarCadaverOrdenado(searchResults);
    }

    public void buscarRegistrosPorCPF() {
    	
    	JTextField cpfField = new JTextField(15);

        // Set placeholder for CPF field
        cpfField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (cpfField.getText().equals("Apenas Números")) {
                    cpfField.setText("");
                    cpfField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (cpfField.getText().isEmpty()) {
                    cpfField.setText("Apenas Números");
                    cpfField.setForeground(Color.GRAY);
                }
            }
        });
        cpfField.setText("Apenas Números");
        cpfField.setForeground(Color.GRAY);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Digite o CPF do Registro:"));
        panel.add(cpfField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Digite o CPF", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
        	// Ask the user for the search query
            String searchQuery = cpfField.getText().replaceAll("[^0-9]", "");
            
            // Create lists to store the search results
            List<Cadaver> searchResults = new ArrayList<>();

            // Perform the search based on the user's choice (name or CPF)
            for (Cadaver cadaver : cadaveres) {
                if (cadaver.getCpf().equals(formatCPF(searchQuery))) {
                    searchResults.add(cadaver);
                }
            }

            // Check if any results were found
            if (searchResults.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum cadáver encontrado para a busca.");
                return;
            }
            
            // Display the search results using the mostrarCadaverOrdenado() method
            mostrarCadaverOrdenado(searchResults);
        }

    }
    
    public void apagarRegistroPorCPF() {
    	
    	JTextField cpfField = new JTextField(15);

        // Set placeholder for CPF field
        cpfField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (cpfField.getText().equals("Apenas Números")) {
                    cpfField.setText("");
                    cpfField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (cpfField.getText().isEmpty()) {
                    cpfField.setText("Apenas Números");
                    cpfField.setForeground(Color.GRAY);
                }
            }
        });
        cpfField.setText("Apenas Números");
        cpfField.setForeground(Color.GRAY);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Digite o CPF para apagar o registro:"));
        panel.add(cpfField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Digite o CPF", JOptionPane.OK_CANCEL_OPTION);

        String searchQuery = cpfField.getText();
        
        if (searchQuery == null) {
            // User canceled the input or closed the dialog
            return;
        }
        
        if (result == JOptionPane.OK_OPTION) {
        	searchQuery = searchQuery.replaceAll("[^0-9]", ""); // Remove non-numeric characters
            searchQuery = formatCPF(searchQuery);
            
            // Read all lines from the CSV file
            try {
                List<String> lines = Files.readAllLines(Paths.get(common.Paths.getDataPath()));
                boolean found = false;

                // Create a temporary list to store lines that don't match the searched CPF
                List<String> updatedLines = new ArrayList<>();
                String linha = "";
                
                // Search for the record with the given CPF and remove it from the lines list
                for (String line : lines) {
                    if (!line.startsWith(searchQuery + ";")) {
                        updatedLines.add(line);
                    } else {
                        found = true;
                        linha = line;
                    }
                }

                // If the CPF is not found, display a message and return
                if (!found) {
                    JOptionPane.showMessageDialog(this, "CPF não encontrado no arquivo.");
                    return;
                }
                
                Cadaver corpo = Cadaver.parseCadaver(linha);

                // Confirm the deletion before proceeding
                int confirmation = JOptionPane.showConfirmDialog(this, "Deseja apagar o registro com o CPF: " + searchQuery + "?" + 
                "\nNome: " + corpo.getNome() + "\nSituação: " + corpo.getSituacao(), "Confirmação", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                	if (autenticarAdmin())
                	{
                		// Write the updated data back to the CSV file
                        Files.write(Paths.get(common.Paths.getDataPath()), updatedLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                        JOptionPane.showMessageDialog(this, "Registro apagado com sucesso!");
                	}
                }
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao acessar o arquivo: " + e.getMessage());
            }
        }
        
    }
    
    public void abrirOpcoesAdmin() {
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

        JButton adicionarAdminButton = new JButton("Adicionar Admin");
        adicionarAdminButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        adicionarAdminButton.addActionListener(e -> EmDesenvolvimento());

        JButton removerAdminButton = new JButton("Remover Admin");
        removerAdminButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        removerAdminButton.addActionListener(e -> EmDesenvolvimento());

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.addActionListener(e -> EmDesenvolvimento());

        optionsPanel.add(Box.createVerticalStrut(20));
        optionsPanel.add(adicionarAdminButton);
        optionsPanel.add(Box.createVerticalStrut(20));
        optionsPanel.add(removerAdminButton);
        optionsPanel.add(Box.createVerticalStrut(20));
        optionsPanel.add(cancelButton);

        // Set the preferred size to make the box 2 times bigger
        optionsPanel.setPreferredSize(new Dimension(optionsPanel.getPreferredSize().width, optionsPanel.getPreferredSize().height * 2));

        JOptionPane.showOptionDialog(
                this,
                optionsPanel,
                "Opções de Administrador",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new Object[]{},
                null
        );
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Controlador();
            }
        });
    }
}

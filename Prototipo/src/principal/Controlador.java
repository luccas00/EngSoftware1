package principal;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import db.DB;
import db.DbException;
import db.DbIntegrityException;

public class Controlador extends JFrame {

    private JPanel mainPanel;
    private JPanel homePanel;
    private JPanel optionsPanel;
    private CardLayout cardLayout;
    private Connection conn;

    public Controlador(Connection conn) {

        this.conn = conn;

        setTitle("Gerenciamento Necroterio");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        // Chamada da funçao para criar painel home
        createHomePanel();

        mainPanel.add(homePanel, "home");

        add(mainPanel);

        if (realizarLogin()) {
            showHomePage(); // Exibe a home page
            setTitle("Gerenciamento Necroterio - User: "
                    + SessionManager.getInstance().getLoggedFuncionario().getNome());
            setVisible(true);

        } else {

            JOptionPane.showMessageDialog(this, "Login ou Senha invalidos. Encerrendo o programa.");
            System.exit(0);

        }
    }

    /* Metodo para realizar Login */
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

        JLabel passwordLabel = new JLabel("Senha:");
        JPasswordField passwordField = new JPasswordField();
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        loginPanel.add(formPanel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(null, loginPanel, "Login", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            String login = loginField.getText();
            String password = new String(passwordField.getPassword());

            PreparedStatement st = null;
            ResultSet rs = null;

            try {

                st = conn.prepareStatement(
                        "SELECT * FROM funcionario WHERE login_acesso = ? and senha = ?");

                st.setString(1, login);

                st.setString(2, password);
                rs = st.executeQuery();
                if (rs.next()) {
                    Funcionario obj = new Funcionario();
                    obj.setCpf(rs.getString("cpf"));
                    obj.setNome(rs.getString("nome"));
                    obj.setLogin_acesso(rs.getString("login_acesso"));
                    obj.setSenha(rs.getString("senha"));
                    obj.setCargo(rs.getString("cargo"));
                    SessionManager.getInstance().setLoggedFuncionario(obj);
                    JOptionPane.showMessageDialog(this, "Login autenticado!");
                    DB.closeStatement(st);

                    return true;
                }
            } catch (SQLException e) {
                throw new DbException(e.getMessage());
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

        int result = JOptionPane.showConfirmDialog(null, loginPanel, "Autenticação de Administrador",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            String login = loginField.getText();
            String password = new String(passwordField.getPassword());

            PreparedStatement st = null;
            ResultSet rs = null;

            try {
                st = conn.prepareStatement(
                        "SELECT * FROM funcionario WHERE login_acesso = ? and senha = ? and cargo = ?");

                st.setString(1, login);
                st.setString(2, password);
                st.setString(3, "Administrador");
                rs = st.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login administrador autenticado!");
                    DB.closeStatement(st);
                    return true;
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao autenticar!");
                }
            } catch (SQLException e) {
                throw new DbException(e.getMessage());
            }
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
                if (SessionManager.getInstance().getLoggedFuncionario().getCargo().equals("Administrador")) {
                    abrirOpcoesAdmin();
                } else {
                    if (autenticarAdmin()) {
                        abrirOpcoesAdmin();
                    }
                }
            }
        });
        bottomPanel.add(adminButton);
        // Add the bottom panel to the page panel
        homePanel.add(bottomPanel, BorderLayout.SOUTH);

        // Create the center panel for the main buttons
        JPanel centerPanel = new JPanel(new GridLayout(0, 3, 20, 40)); // 3 columns, variable rows, 10px vertical and
                                                                       // horizontal gaps

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
                atualizarRegistroPorCPF();
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
                alterarSituacaoCadaver();
            }
        });
        centerPanel.add(situacaoButton);

        // Novo botão Listar cadáveres por funcionário
        JButton listarCadFuncButton = new JButton("Registros por funcionário");
        listarCadFuncButton.setPreferredSize(new Dimension(50, 10));
        listarCadFuncButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listarCadaveresPorFuncionario();
            }
        });
        centerPanel.add(listarCadFuncButton);

        // Novo botão Encerrar Sistema
        JButton encerrarButton = new JButton("Encerrar Sessão");
        encerrarButton.setPreferredSize(new Dimension(50, 10));
        encerrarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                realizarLogin();
            }
        });
        centerPanel.add(encerrarButton);

        // Add the center panel to the page panel
        homePanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void listarCadaveresPorFuncionario() {

        String searchQuery = JOptionPane.showInputDialog(this, "Digite o CPF do funcionário para busca:");

        if (searchQuery == null) {
            // User canceled the input or closed the dialog
            return;
        }

        searchQuery = searchQuery.replaceAll("[^0-9]", ""); // Remove non-numeric characters
        searchQuery = formatCPF(searchQuery);
        // Create the table model

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = conn.prepareStatement(
                    "SELECT c.identificacao, c.nome_cadaver, c.situacao, f.cpf, f.nome, f.cargo FROM cadaver c INNER JOIN funcionario f ON c.cpf_funcionario = f.cpf WHERE cpf_funcionario = ?");
            st.setString(1, searchQuery);
            rs = st.executeQuery();
            JPanel panel = new JPanel(new BorderLayout());

            // Crie a tabela e o modelo
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.setColumnIdentifiers(
                    new Object[] { "CPF", "Nome do Cádaver", "Situação" });
            // Crie a JTable

            while (rs.next()) {

                Object[] rowData = {
                        rs.getString("identificacao"),
                        rs.getString("nome_cadaver"),
                        rs.getString("situacao"),
                };
                tableModel.addRow(rowData);
                String nome_func = rs.getString("nome");
                String cargo = rs.getString("cargo");
                // Crie a JTable
                JTable table = new JTable(tableModel);
                JScrollPane scrollPane = new JScrollPane(table);
                JLabel infoLabel = new JLabel("Funcionário: " + nome_func + " | Cargo: " + cargo);
                panel.add(infoLabel, BorderLayout.NORTH);
                // Adicione o scrollPane com a tabela ao JPanel
                panel.add(scrollPane, BorderLayout.CENTER);

            }

            // Crie e exiba o diálogo com o JPanel
            JDialog dialog = new JDialog(this, "Listagem de cadáveres registrados por funcionário", true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.getContentPane().add(panel); // Adicione o JPanel ao diálogo
            dialog.pack();

            // Defina o tamanho do diálogo
            dialog.setSize(900, 500);

            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } catch (

        SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    private void listarFuncionario() {

        // Create the table model
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(
                new Object[] { "CPF", "Nome", "Login", "Cargo" });

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = conn.prepareStatement("SELECT * FROM funcionario ORDER BY nome");
            rs = st.executeQuery();

            while (rs.next()) {
                Object[] rowData = {
                        rs.getString("cpf"),
                        rs.getString("nome"),
                        rs.getString("login_acesso"),
                        rs.getString("cargo"),
                };
                tableModel.addRow(rowData);
            }

            // Create the table and scroll pane
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);

            // Create the dialog and display the table
            JDialog dialog = new JDialog(this, "Funcionários", true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.getContentPane().add(scrollPane);
            dialog.pack();

            // Set the size of the dialog
            dialog.setSize(900, 500);

            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Nenhum funcionário registrado");
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    private void mostrarCadaver() {

        // Create the table model
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(
                new Object[] { "CPF", "Nome", "Peso", "Data da morte", "Hora da morte", "Situação",
                        "ID do funcionário" });

        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = conn.prepareStatement("SELECT * FROM cadaver ORDER BY nome_cadaver");
            rs = st.executeQuery();

            while (rs.next()) {
                Object[] rowData = {
                        rs.getString("identificacao"),
                        rs.getString("nome_cadaver"),
                        rs.getDouble("peso"),
                        rs.getString("dataMorte"),
                        rs.getString("horaMorte"),
                        rs.getString("situacao"),
                        rs.getString("cpf_funcionario")
                };
                tableModel.addRow(rowData);
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
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    /* Metodo apenas para EXIBIR a pagina inicial */
    private void showHomePage() {
        cardLayout.show(mainPanel, "home");
    }

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
            double peso = Double.parseDouble(pesoField.getText());
            String dataFalecimento = dataFalecimentoField.getText();
            String horaFalecimento = horaFalecimentoField.getText();
            String idFuncionario = SessionManager.getInstance().getLoggedFuncionario().getCpf();

            if (confirmarEntrada(cpf, nome, peso, dataFalecimento, horaFalecimento, idFuncionario)) {
                // Show a confirmation dialog before adding the record
                String message = "Deseja adicionar o seguinte cadáver?\n\n"
                        + "CPF: " + formatCPF(cpf) + "\n"
                        + "Nome: " + nome + "\n"
                        + "Peso: " + peso + "\n"
                        + "Data da Morte: " + dataFalecimento + "\n"
                        + "Hora da Morte: " + horaFalecimento + "\n"
                        + "ID do funcionário: " + idFuncionario;

                int confirmation = JOptionPane.showConfirmDialog(this, message, "Confirmação",
                        JOptionPane.YES_NO_OPTION);

                if (confirmation == JOptionPane.YES_OPTION) {
                    Cadaver corpo = new Cadaver(formatCPF(cpf), nome, dataFalecimento, horaFalecimento, peso,
                            idFuncionario);

                    // ---------- CONSULTA BANCO DE DADOS ---------------------
                    PreparedStatement st = null;
                    try {
                        st = conn.prepareStatement(
                                "INSERT INTO cadaver " +
                                        "(identificacao, nome_cadaver, peso, dataMorte, horaMorte, situacao, cpf_funcionario) "
                                        +
                                        "VALUES " +
                                        "(?, ?, ?, ?, ?, ?, ?)",
                                Statement.RETURN_GENERATED_KEYS);

                        st.setString(1, corpo.getCpf());
                        st.setString(2, corpo.getNome());
                        st.setDouble(3, corpo.getPeso());
                        st.setString(4, corpo.getDataFalecimento());
                        st.setString(5, corpo.getHoraFalecimento());
                        st.setString(6, corpo.getSituacao());
                        st.setString(7, SessionManager.getInstance().getLoggedFuncionario().getCpf());

                        int rowsAffected = st.executeUpdate();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(this, "Registro inserido com sucesso!");
                        } else {
                            JOptionPane.showMessageDialog(this, "Erro ao inserir!");

                        }
                    } catch (SQLException e) {
                        throw new DbException(e.getMessage());
                    } finally {
                        DB.closeStatement(st);
                    }
                }
            }
        }

    }

    /*--------Metodo para ATUALIZAR cadáver--------*/
    public void atualizarRegistroPorCPF() {
        JTextField cpfField = new JTextField(15);

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
        panel.add(new JLabel("Digite o CPF para alterar dados do registro:"));
        panel.add(cpfField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Digite o CPF", JOptionPane.OK_CANCEL_OPTION);

        String searchQuery = cpfField.getText();

        if (searchQuery == null || searchQuery.equals("Apenas Números")) {
            return;
        }
        if (result == JOptionPane.OK_OPTION) {
            searchQuery = searchQuery.replaceAll("[^0-9]", "");
            searchQuery = formatCPF(searchQuery);

            PreparedStatement st = null;
            ResultSet rs = null;
            // Aqui retorna todos os dados do cpf que buscou
            try {
                st = conn.prepareStatement(
                        "SELECT * FROM cadaver WHERE identificacao = ?");
                st.setString(1, searchQuery);

                rs = st.executeQuery();
                if (rs.next()) {
                    String updatedName = JOptionPane.showInputDialog(this, "Digite o novo nome:",
                            rs.getString("nome_cadaver"));
                    String updatedWeight = JOptionPane.showInputDialog(this, "Digite o novo peso:",
                            rs.getDouble("peso"));
                    String updatedDeathDate = JOptionPane.showInputDialog(this, "Digite a nova data de óbito:",
                            rs.getString("dataMorte"));
                    String updatedTimeDate = JOptionPane.showInputDialog(this, "Digite a nova hora de óbito:",
                            rs.getString("horaMorte"));
                    if (updatedName != null && !updatedName.isEmpty() && updatedWeight != null
                            && !updatedWeight.isEmpty() && updatedDeathDate != null && !updatedDeathDate.isEmpty()
                            && updatedTimeDate != null && !updatedTimeDate.isEmpty()) {
                        double updatedWeightD = Double.parseDouble(updatedWeight);
                        // ---------- CONSULTA BANCO DE DADOS ---------------------
                        // Aqui atualiza todos os dados do cpf inserido
                        try {
                            st = conn.prepareStatement(

                                    "UPDATE cadaver SET nome_cadaver = ?, peso = ?, dataMorte = ?, horaMorte = ?  WHERE identificacao = ?",
                                    Statement.RETURN_GENERATED_KEYS);

                            st.setString(1, updatedName);
                            st.setDouble(2, updatedWeightD);
                            st.setString(3, updatedDeathDate);
                            st.setString(4, updatedTimeDate);
                            st.setString(5, searchQuery);

                            int rowsAffected = st.executeUpdate();

                            if (rowsAffected > 0) {
                                JOptionPane.showMessageDialog(this, "Registro atualizado!");
                            } else {
                                JOptionPane.showMessageDialog(this, "Erro ao inserir!");
                            }
                        } catch (SQLException e) {
                            throw new DbException(e.getMessage());
                        } finally {
                            DB.closeStatement(st);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Os registros não podem ser vazios.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "CPF não encontrado.");
            }
        }
    }

    /*--------FIM do metodo para ATUALIZAR cadáver--------*/

    private String formatCPF(String cpf) {
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    private boolean confirmarCampos(String cpf, String nome, String login_acesso, String senha, String cargo) {
        if (cpf.length() < 10 || cpf.isEmpty()) {
            return false;
        }

        if (nome.isEmpty() && login_acesso.isEmpty() && senha.isEmpty() && cargo.isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean confirmarEntrada(String cpf, String nome, Double peso, String dataFalecimento,
            String horaFalecimento, String idFuncionario) {
        if (cpf.length() < 10 || cpf.isEmpty()) {
            return false;
        }

        if (nome.isEmpty() && peso == 0 && dataFalecimento.isEmpty() && horaFalecimento.isEmpty()) {
            return false;
        }

        return true;
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
                new Object[] { "Nome", "CPF" },
                null);

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
        PreparedStatement st = null;
        ResultSet rs = null;
        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[] { "CPF", "Nome", "Peso", "Data da morte", "Hora da morte", "Situação",
                        "ID do funcionário" },
                0);

        try {
            st = conn.prepareStatement("SELECT * FROM cadaver WHERE nome_cadaver = ?");
            st.setString(1, searchQuery);
            rs = st.executeQuery();

            while (rs.next()) {
                Object[] rowData = {
                        rs.getString("identificacao"),
                        rs.getString("nome_cadaver"),
                        rs.getDouble("peso"),
                        rs.getString("dataMorte"),
                        rs.getString("horaMorte"),
                        rs.getString("situacao"),
                        rs.getString("cpf_funcionario"),
                };
                tableModel.addRow(rowData);
            }

            // Aqui você precisa criar e configurar a tabela para exibir o modelo
            JTable table = new JTable(tableModel);

            // Adicione a tabela a um JScrollPane para permitir rolagem, se necessário
            JScrollPane scrollPane = new JScrollPane(table);

            // Agora você pode adicionar o scrollPane à sua interface gráfica
            // por exemplo, a um JPanel ou JFrame

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

        catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    public void buscarRegistrosPorCPF() {

        String searchQuery = JOptionPane.showInputDialog(this, "Digite o CPF para buscar:");

        if (searchQuery == null) {
            // User canceled the input or closed the dialog
            return;
        }
        searchQuery = searchQuery.replaceAll("[^0-9]", ""); // Remove non-numeric characters
        searchQuery = formatCPF(searchQuery);
        PreparedStatement st = null;
        ResultSet rs = null;
        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[] { "CPF", "Nome", "Peso", "Data da morte", "Hora da morte", "Situação",
                        "ID do funcionário" },
                0);

        try {
            st = conn.prepareStatement("SELECT * FROM cadaver WHERE identificacao = ?");
            st.setString(1, searchQuery);
            rs = st.executeQuery();

            while (rs.next()) {
                Object[] rowData = {
                        rs.getString("identificacao"),
                        rs.getString("nome_cadaver"),
                        rs.getDouble("peso"),
                        rs.getString("dataMorte"),
                        rs.getString("horaMorte"),
                        rs.getString("situacao"),
                        rs.getString("cpf_funcionario"),
                };
                tableModel.addRow(rowData);
            }

            // Aqui você precisa criar e configurar a tabela para exibir o modelo
            JTable table = new JTable(tableModel);

            // Adicione a tabela a um JScrollPane para permitir rolagem, se necessário
            JScrollPane scrollPane = new JScrollPane(table);

            // Agora você pode adicionar o scrollPane à sua interface gráfica
            // por exemplo, a um JPanel ou JFrame

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

        catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
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

            PreparedStatement st = null;
            ResultSet rs = null;

            try {
                st = conn.prepareStatement(
                        "SELECT * FROM cadaver WHERE identificacao = ?");

                st.setString(1, searchQuery);
                rs = st.executeQuery();
                if (rs.next()) {
                    Cadaver obj = new Cadaver();
                    obj.setCpf(rs.getString("identificacao"));
                    obj.setNome(rs.getString("nome_cadaver"));
                    obj.setSituacao(rs.getString("situacao"));
                    obj.setIdFuncionario(rs.getString("cpf_funcionario"));
                    int confirmation = JOptionPane.showConfirmDialog(this,
                            "Deseja apagar o registro com o CPF: " + searchQuery + "?" +
                                    "\nNome: " + obj.getNome() + "\nSituação: " + obj.getSituacao()
                                    + "\nID funcionário: "
                                    + obj.getIdFuncionario(),
                            "Confirmação", JOptionPane.YES_NO_OPTION);
                    // PreparedStatement st = null;
                    st.close();
                    if (confirmation == JOptionPane.YES_OPTION) {
                        st = conn.prepareStatement(
                                "DELETE FROM cadaver WHERE identificacao = ?");
                        st.setString(1, searchQuery);
                        st.executeUpdate();
                        DB.closeStatement(st);
                        JOptionPane.showMessageDialog(this, "Registro apagado com sucesso!");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "CPF não encontrado!");
                }
            } catch (SQLException e) {
                throw new DbIntegrityException(e.getMessage());
            }

        }

    }

    public void alterarSituacaoCadaver() {
        JTextField cpfField = new JTextField(15);

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
        panel.add(new JLabel("Digite o CPF para alterar a situação:"));
        panel.add(cpfField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Digite o CPF", JOptionPane.OK_CANCEL_OPTION);

        String searchQuery = cpfField.getText();

        if (searchQuery == null || searchQuery.equals("Apenas Números")) {
            return;
        }

        if (result == JOptionPane.OK_OPTION) {
            searchQuery = searchQuery.replaceAll("[^0-9]", "");
            searchQuery = formatCPF(searchQuery);

            PreparedStatement st = null;
            ResultSet rs = null;
            try {
                st = conn.prepareStatement(
                        "SELECT situacao FROM cadaver WHERE identificacao = ?");
                st.setString(1, searchQuery);

                rs = st.executeQuery();
                if (rs.next()) {
                    String updatedSituation = JOptionPane.showInputDialog(this, "Digite a nova situação:",
                            rs.getString("situacao"));

                    if (updatedSituation != null && !updatedSituation.isEmpty()) {
                        try {
                            st = conn.prepareStatement(

                                    "UPDATE cadaver SET situacao = ? WHERE identificacao = ?",
                                    Statement.RETURN_GENERATED_KEYS);

                            st.setString(1, updatedSituation);
                            st.setString(2, searchQuery);

                            int rowsAffected = st.executeUpdate();

                            if (rowsAffected > 0) {
                                JOptionPane.showMessageDialog(null, "Situação atualizada com sucesso!");

                            } else {
                                throw new DbException("Erro ao inserir!");
                            }
                        } catch (SQLException e) {

                            throw new DbException(e.getMessage());
                        }
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "CPF não encontrado");
            }
        }

    }

    public void abrirOpcoesAdmin() {
        optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

        JButton adicionarAdminButton = new JButton("Adicionar Funcionário");
        adicionarAdminButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        adicionarAdminButton.addActionListener(e -> adicionarFuncionario());

        JButton listarAdminButton = new JButton("Listar Funcionários");
        listarAdminButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        listarAdminButton.addActionListener(e -> listarFuncionario());

        JButton editarAdminButton = new JButton("Editar funcionário");
        editarAdminButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        editarAdminButton.addActionListener(e -> editarFuncionarioPorCPF());

        JButton removerAdminButton = new JButton("Remover funcionário");
        removerAdminButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        removerAdminButton.addActionListener(e -> apagarFuncionarioPorCPF());

        optionsPanel.add(Box.createVerticalStrut(20));
        optionsPanel.add(adicionarAdminButton);
        optionsPanel.add(Box.createVerticalStrut(20));
        optionsPanel.add(listarAdminButton);
        optionsPanel.add(Box.createVerticalStrut(20));
        optionsPanel.add(editarAdminButton);
        optionsPanel.add(Box.createVerticalStrut(20));
        optionsPanel.add(removerAdminButton);

        // Set the preferred size to make the box 2 times bigger
        optionsPanel.setPreferredSize(
                new Dimension(optionsPanel.getPreferredSize().width, optionsPanel.getPreferredSize().height * 2));

        JOptionPane.showOptionDialog(
                this,
                optionsPanel,
                "Opções de Administrador",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new Object[] {},
                null);
    }

    private void editarFuncionarioPorCPF() {
        JTextField cpfField = new JTextField(15);

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
        panel.add(new JLabel("Digite o CPF para alterar dados do funcionário:"));
        panel.add(cpfField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Digite o CPF",
                JOptionPane.OK_CANCEL_OPTION);

        String searchQuery = cpfField.getText();

        if (searchQuery == null || searchQuery.equals("Apenas Números")) {
            return;
        }
        if (result == JOptionPane.OK_OPTION) {
            searchQuery = searchQuery.replaceAll("[^0-9]", "");
            searchQuery = formatCPF(searchQuery);

            PreparedStatement st = null;
            ResultSet rs = null;
            // Aqui retorna todos os dados do cpf que buscou
            try {
                st = conn.prepareStatement(
                        "SELECT * FROM funcionario WHERE cpf = ?");
                st.setString(1, searchQuery);

                rs = st.executeQuery();
                if (rs.next()) {
                    String updatedName = JOptionPane.showInputDialog(this, "Digite o novo nome:",
                            rs.getString("nome"));
                    String updatedLogin = JOptionPane.showInputDialog(this,
                            "Digite o novo login:",
                            rs.getString("login_acesso"));
                    String updatedPassword = JOptionPane.showInputDialog(this,
                            "Digite a nova senha:",
                            rs.getString("senha"));
                    String updatedCargo = JOptionPane.showInputDialog(this,
                            "Digite o novo cargo:",
                            rs.getString("cargo"));
                    if (updatedName != null && !updatedName.isEmpty() && updatedLogin != null
                            && !updatedLogin.isEmpty() && updatedPassword != null &&
                            !updatedPassword.isEmpty()
                            && updatedCargo != null && !updatedCargo.isEmpty()) {
                        // ---------- CONSULTA BANCO DE DADOS ---------------------
                        // Aqui atualiza todos os dados do cpf inserido
                        try {
                            st = conn.prepareStatement(

                                    "UPDATE funcionario SET nome= ?, login_acesso = ?, senha = ?, cargo = ?  WHERE cpf = ?",
                                    Statement.RETURN_GENERATED_KEYS);

                            st.setString(1, updatedName);
                            st.setString(2, updatedLogin);
                            st.setString(3, updatedPassword);
                            st.setString(4, updatedCargo);
                            st.setString(5, searchQuery);

                            int rowsAffected = st.executeUpdate();

                            if (rowsAffected > 0) {
                                JOptionPane.showMessageDialog(this, "Funcionário atualizado!");
                            } else {
                                JOptionPane.showMessageDialog(this, "Erro ao inserir!");
                            }
                        } catch (SQLException e) {
                            throw new DbException(e.getMessage());
                        } finally {
                            DB.closeStatement(st);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Os registros não podem ser vazios.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "CPF não encontrado.");
            }
        }
    }

    // Adicionando um funcionario na aba administração
    private void adicionarFuncionario() {
        JTextField cpfField = new JTextField(15);
        JTextField nomeField = new JTextField(15);
        JTextField login_acessoField = new JTextField(15);
        JPasswordField senhaField = new JPasswordField(15);
        JTextField cargoField = new JTextField(15);

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

        // Set placeholder for Hora de Falacimento field
        senhaField.setEchoChar('*'); // Define o caractere de eco como '*'

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("CPF:"));
        panel.add(cpfField);
        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("Login:"));
        panel.add(login_acessoField);
        panel.add(new JLabel("Criar senha:"));
        panel.add(senhaField);
        panel.add(new JLabel("Cargo:"));
        panel.add(cargoField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Adicionar Funcionário", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String cpf = cpfField.getText().replaceAll("[^0-9]", ""); // Remove non-numeric characters from the input
            String nome = nomeField.getText();
            String login_acesso = login_acessoField.getText();
            // String senha = senhaField.getText();
            char[] senhaChars = senhaField.getPassword();
            String senha = new String(senhaChars);
            String cargo = cargoField.getText();

            if (confirmarCampos(cpf, nome, login_acesso, senha, cargo)) {
                // Show a confirmation dialog before adding the record
                String message = "Deseja adicionar o seguinte Funcionário?\n\n"
                        + "CPF: " + formatCPF(cpf) + "\n"
                        + "Nome: " + nome + "\n"
                        + "Login: " + login_acesso + "\n"
                        + "Senha: " + senha + "\n"
                        + "Cargo: " + cargo + "\n";

                int confirmation = JOptionPane.showConfirmDialog(this, message, "Confirmação",
                        JOptionPane.YES_NO_OPTION);

                if (confirmation == JOptionPane.YES_OPTION) {
                    Funcionario funcionario = new Funcionario(formatCPF(cpf), nome, login_acesso, senha, cargo);
                    PreparedStatement st = null;
                    try {
                        st = conn.prepareStatement(
                                "INSERT INTO funcionario " +
                                        "(cpf, nome, login_acesso, senha, cargo)" +
                                        "VALUES " +
                                        "(?, ?, ?, ?, ?)",
                                Statement.RETURN_GENERATED_KEYS);

                        st.setString(1, funcionario.getCpf());
                        st.setString(2, funcionario.getNome());
                        st.setString(3, funcionario.getLogin_acesso());
                        st.setString(4, funcionario.getSenha());
                        st.setString(5, funcionario.getCargo());

                        int rowsAffected = st.executeUpdate();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(this, "Funcionário adicionado com sucesso!");
                        } else {
                            throw new DbException("Erro ao inserir!");
                        }
                    } catch (SQLException e) {
                        throw new DbException(e.getMessage());
                    } finally {
                        DB.closeStatement(st);
                    }
                }

            }
        } else {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos antes de adicionar o funcionário.");
        }
    }

    // esse está encontrando atraves do cpf

    public void apagarFuncionarioPorCPF() {
        JTextField cpfField = new JTextField(15);

        // Adicionar o FocusListener ao campo de CPF
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

        // Configurar o placeholder e cor do texto
        cpfField.setText("Apenas Números");
        cpfField.setForeground(Color.GRAY);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Digite o CPF para apagar o funcionário:"));
        panel.add(cpfField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Digite o CPF", JOptionPane.OK_CANCEL_OPTION);

        String searchQuery = cpfField.getText();

        if (searchQuery == null) {
            // Usuário cancelou a entrada ou fechou a caixa de diálogo
            return;
        }

        if (result == JOptionPane.OK_OPTION) {
            searchQuery = searchQuery.replaceAll("[^0-9]", ""); // Remove caracteres não numéricos
            searchQuery = formatCPF(searchQuery);

            // Verificar se o CPF está vazio
            if (searchQuery.isEmpty()) {
                JOptionPane.showMessageDialog(this, "CPF inválido.");
                return;
            }

            PreparedStatement st = null;
            ResultSet rs = null;

            try {
                st = conn.prepareStatement(
                        "SELECT * FROM funcionario WHERE cpf = ?");

                st.setString(1, searchQuery);
                rs = st.executeQuery();
                if (rs.next()) {
                    Funcionario obj = new Funcionario();
                    obj.setCpf(rs.getString("cpf"));
                    obj.setNome(rs.getString("nome"));
                    int confirmation = JOptionPane.showConfirmDialog(this,
                            "Deseja apagar o funcionário com o CPF: " + searchQuery + "?" +
                                    "\nNome: " + obj.getNome(),
                            "Confirmação", JOptionPane.YES_NO_OPTION);
                    // PreparedStatement st = null;
                    st.close();
                    if (confirmation == JOptionPane.YES_OPTION) {
                        st = conn.prepareStatement(
                                "DELETE FROM funcionario WHERE cpf = ?");
                        st.setString(1, searchQuery);
                        st.executeUpdate();
                        DB.closeStatement(st);
                        JOptionPane.showMessageDialog(this, "Funcionário apagado com sucesso!");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "CPF não encontrado!");
                }
            } catch (SQLException e) {
                throw new DbIntegrityException(e.getMessage());

            }
        }

    }

    public static void main(String[] args) {
        Connection conn = DB.getConnection();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Controlador(conn);
            }
        });
    }
}

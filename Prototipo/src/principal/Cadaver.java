package principal;

public class Cadaver {

    private String cpf;
    private String nome;
    private String dataFalecimento;
    private String horaFalecimento;
    private String situacao;
    private double peso;
    private String idFuncionario;

    public Cadaver(String cpf, String nome, String dataFalecimento, String horaFalecimento, String idFuncionario) {
        this.cpf = cpf;
        this.nome = nome;
        this.dataFalecimento = dataFalecimento;
        this.horaFalecimento = horaFalecimento;
        this.situacao = "Recebido";
        this.peso = 0;
        this.idFuncionario = idFuncionario;
    }

    public Cadaver(String cpf, String nome, String dataFalecimento, String horaFalecimento, double peso,
            String idFuncionario) {
        this.cpf = cpf;
        this.nome = nome;
        this.dataFalecimento = dataFalecimento;
        this.horaFalecimento = horaFalecimento;
        this.situacao = "Recebido";
        this.peso = peso;
        this.idFuncionario = idFuncionario;
    }

    public Cadaver(String cpf, String nome, double peso, String idFuncionario) {
        this.cpf = cpf;
        this.nome = nome;
        this.peso = peso;
        this.idFuncionario = idFuncionario;
    }

    public Cadaver(String cpf, String nome, String idFuncionario) {
        this.cpf = cpf;
        this.nome = nome;
        this.peso = 0;
        this.idFuncionario = idFuncionario;
    }

    public Cadaver() {
        this.cpf = "";
        this.dataFalecimento = "";
        this.horaFalecimento = "";
        this.nome = "";
        this.peso = 0;
        this.situacao = "";
    }

    public String getCpf() {
        return this.cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPeso() {
        return this.peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public String getSituacao() {
        return this.situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getDataFalecimento() {
        return this.dataFalecimento;
    }

    public void setDataFalecimento(String dataFalecimento) {
        this.dataFalecimento = dataFalecimento;
    }

    public String getHoraFalecimento() {
        return this.horaFalecimento;
    }

    public void setHoraFalecimento(String horaFalecimento) {
        this.horaFalecimento = horaFalecimento;
    }

    @Override
    public String toString() {
        return this.cpf + ";" + this.nome + ";" + this.peso + ";"
                + this.dataFalecimento + ";" + this.horaFalecimento + ";" + this.situacao + ";" + this.idFuncionario;
    }

    /**
     * @return String return the idFuncionario
     */
    public String getIdFuncionario() {
        return idFuncionario;
    }

    /**
     * @param idFuncionario the idFuncionario to set
     */
    public void setIdFuncionario(String idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

}

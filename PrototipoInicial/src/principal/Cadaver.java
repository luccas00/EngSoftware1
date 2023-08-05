package principal;

public class Cadaver {

    private String cpf;
    private String nome;
    private String dataFalecimento;
    private String horaFalecimento;
    private String situacao;
    private double peso;

    public Cadaver(String cpf, String nome, String dataFalecimento, String horaFalecimento) {
        this.cpf = cpf;
        this.nome = nome;
        this.dataFalecimento = dataFalecimento;
        this.horaFalecimento = horaFalecimento;
        this.situacao = "Recebido";
        this.peso = 0;
    }
    
    public Cadaver(String cpf, String nome, String dataFalecimento, String horaFalecimento, double peso) {
        this.cpf = cpf;
        this.nome = nome;
        this.dataFalecimento = dataFalecimento;
        this.horaFalecimento = horaFalecimento;
        this.situacao = "Recebido";
        this.peso = peso;
    }
    
    public Cadaver(String cpf, String nome, double peso) {
        this.cpf = cpf;
        this.nome = nome;
        this.peso = peso;
    }
    
    public Cadaver(String cpf, String nome, String peso) {
        this.cpf = cpf;
        this.nome = nome;
        double aux = Double.parseDouble(peso);
        this.peso = aux;
    }
    
    public Cadaver(String cpf, String nome) {
        this.cpf = cpf;
        this.nome = nome;
        this.peso = 0;
    }
    
    public Cadaver()
    {
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
    
    public double getPeso()
    {
    	return this.peso;
    }

    public void setPeso(double peso)
    {
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
        return this.cpf + ";" + this.nome + ";" + this.dataFalecimento + ";" + this.horaFalecimento + ";"
                + this.peso + ";" + this.situacao;
    }

}

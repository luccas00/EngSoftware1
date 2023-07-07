package principal;

public class Cadaver {
	
    private String id;
    private String nome;
    private double peso;

    public Cadaver(String id, String nome, double peso) {
        this.id = id;
        this.nome = nome;
        this.peso = peso;
    }
    
    public Cadaver(String id, String nome, String peso) {
        this.id = id;
        this.nome = nome;
        double aux = Double.parseDouble(peso);
        this.peso = aux;
    }
    
    public Cadaver(String id, String nome) {
        this.id = id;
        this.nome = nome;
        this.peso = 0;
    }

    public String getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public double getPeso() {
        return this.peso;
    }

	@Override
	public String toString() {
		return this.id + ";" + this.nome + ";" + this.peso;
	}
    
    
}

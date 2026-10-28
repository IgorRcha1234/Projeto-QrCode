package com.example.myapplication;

public class RegistroAcesso {
    private String id;
    private String nome;
    private String cargoEmpresa;
    private String cpfMasked;
    private String tipo; // "ENTRADA" ou "SAÍDA"
    private String hora;
    private long timestamp;

    // Construtor vazio (Obrigatório para o Firebase Firestore)
    public RegistroAcesso() {}

    public RegistroAcesso(String id, String nome, String cargoEmpresa, String cpfMasked, String tipo, String hora, long timestamp) {
        this.id = id;
        this.nome = nome;
        this.cargoEmpresa = cargoEmpresa;
        this.cpfMasked = cpfMasked;
        this.tipo = tipo;
        this.hora = hora;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCargoEmpresa() { return cargoEmpresa; }
    public void setCargoEmpresa(String cargoEmpresa) { this.cargoEmpresa = cargoEmpresa; }

    public String getCpfMasked() { return cpfMasked; }
    public void setCpfMasked(String cpfMasked) { this.cpfMasked = cpfMasked; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
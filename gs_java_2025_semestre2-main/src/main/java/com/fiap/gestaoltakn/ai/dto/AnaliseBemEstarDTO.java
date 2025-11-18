package com.fiap.gestaoltakn.ai.dto;

public class AnaliseBemEstarDTO {
    private Long funcionarioId;
    private String funcionarioNome;
    private String analise;
    private String recomendacoes;

    public AnaliseBemEstarDTO() {}

    public AnaliseBemEstarDTO(Long funcionarioId, String funcionarioNome, String analise, String recomendacoes) {
        this.funcionarioId = funcionarioId;
        this.funcionarioNome = funcionarioNome;
        this.analise = analise;
        this.recomendacoes = recomendacoes;
    }

    public Long getFuncionarioId() { return funcionarioId; }
    public void setFuncionarioId(Long funcionarioId) { this.funcionarioId = funcionarioId; }

    public String getFuncionarioNome() { return funcionarioNome; }
    public void setFuncionarioNome(String funcionarioNome) { this.funcionarioNome = funcionarioNome; }

    public String getAnalise() { return analise; }
    public void setAnalise(String analise) { this.analise = analise; }

    public String getRecomendacoes() { return recomendacoes; }
    public void setRecomendacoes(String recomendacoes) { this.recomendacoes = recomendacoes; }

}

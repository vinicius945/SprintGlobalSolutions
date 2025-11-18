package com.fiap.gestaoltakn.dto.message;

import java.io.Serializable;
import java.time.LocalDateTime;

public class RelatorioMessage implements Serializable {
    private String tipoRelatorio;
    private LocalDateTime dataSolicitacao;
    private String usuarioSolicitante;
    private String parametros;

    public RelatorioMessage() {}

    public RelatorioMessage(String tipoRelatorio, LocalDateTime dataSolicitacao, String usuarioSolicitante, String parametros) {
        this.tipoRelatorio = tipoRelatorio;
        this.dataSolicitacao = dataSolicitacao;
        this.usuarioSolicitante = usuarioSolicitante;
        this.parametros = parametros;
    }

    public String getTipoRelatorio() { return tipoRelatorio; }
    public void setTipoRelatorio(String tipoRelatorio) { this.tipoRelatorio = tipoRelatorio; }

    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDateTime dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }

    public String getUsuarioSolicitante() { return usuarioSolicitante; }
    public void setUsuarioSolicitante(String usuarioSolicitante) { this.usuarioSolicitante = usuarioSolicitante; }

    public String getParametros() { return parametros; }
    public void setParametros(String parametros) { this.parametros = parametros; }

}

package br.com.aep.tarefas.interacao;

/**
 * O que se espera que o usuário responda. A entrada por voz usa isso para
 * escolher a gramática certa (lista fechada de frases ou ditado livre).
 */
public enum TipoResposta {
    COMANDO,
    TEXTO_LIVRE,
    NUMERO,
    SIM_NAO,
    DATA
}

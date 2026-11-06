package com.ursia.tavalendo.core.base;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.Serializable;
import java.util.List;

/**
 * Controller REST genérico abstrato que fornece endpoints básicos de listagem,
 * busca por ID e exclusão lógica, retornando respostas padronizadas em {@link RespostaApi}.
 *
 * @param <T>  Tipo da entidade de domínio.
 * @param <ID> Tipo do identificador da entidade.
 * @param <R>  Tipo do DTO de resposta (ResponseDTO) retornado ao cliente.
 */
public abstract class
ControllerGenerico<T extends EntidadeBase<ID>, ID extends Serializable, R> {

    protected final ServiceGenerico<T, ID> service;

    protected ControllerGenerico(ServiceGenerico<T, ID> service) {
        this.service = service;
    }

    protected abstract R paraResponseDTO(T entidade);

    @GetMapping
    public ResponseEntity<RespostaApi<List<R>>> listarTodos() {
        List<R> lista = service.listarTodos().stream()
                .map(this::paraResponseDTO)
                .toList();
        return ResponseEntity.ok(RespostaApi.sucesso(lista, "Registros listados com sucesso."));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespostaApi<R>> buscarPorId(@PathVariable ID id) {
        R resposta = paraResponseDTO(service.buscarPorId(id));
        return ResponseEntity.ok(RespostaApi.sucesso(resposta, "Registro encontrado com sucesso."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RespostaApi<Void>> deletar(@PathVariable ID id) {
        service.deletar(id);
        return ResponseEntity.ok(RespostaApi.sucesso(null, "Registro removido com sucesso."));
    }
}

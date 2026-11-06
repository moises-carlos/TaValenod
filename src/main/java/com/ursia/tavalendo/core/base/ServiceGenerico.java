package com.ursia.tavalendo.core.base;

import java.io.Serializable;
import java.util.List;

/**
 * Interface genérica que define o contrato padrão de operações CRUD para os serviços do sistema.
 *
 * @param <T>  Tipo da entidade que estende {@link EntidadeBase}.
 * @param <ID> Tipo do identificador único da entidade.
 */
public interface ServiceGenerico<T extends EntidadeBase<ID>, ID extends Serializable> {

    T salvar(T entidade);

    T buscarPorId(ID id);

    List<T> listarTodos();

    void deletar(ID id);
}

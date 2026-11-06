package com.ursia.tavalendo.core.base;

import com.ursia.tavalendo.exception.custom.RecursoNaoEncontradoException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * Implementação abstrata padrão de {@link ServiceGenerico}.
 * Encapsula a lógica comum de persistência, consulta filtrando apenas registros ativos
 * e exclusão lógica (soft delete) utilizando um {@link JpaRepository}.
 *
 * @param <T>  Tipo da entidade gerenciada.
 * @param <ID> Tipo do identificador único da entidade.
 */
public abstract class ServiceGenericoAbstrato<T extends EntidadeBase<ID>, ID extends Serializable>
        implements ServiceGenerico<T, ID> {

    protected final JpaRepository<T, ID> repository;

    protected ServiceGenericoAbstrato(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public T salvar(T entidade) {
        return repository.save(entidade);
    }

    @Override
    @Transactional(readOnly = true)
    public T buscarPorId(ID id) {
        return repository.findById(id)
                .filter(EntidadeBase::getAtivo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Recurso não encontrado para o ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> listarTodos() {
        return repository.findAll().stream()
                .filter(EntidadeBase::getAtivo)
                .toList();
    }

    @Override
    @Transactional
    public void deletar(ID id) {
        T entidade = buscarPorId(id);
        entidade.setAtivo(false);
        repository.save(entidade);
    }
}

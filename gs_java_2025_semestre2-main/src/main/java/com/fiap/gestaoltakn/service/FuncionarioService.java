package com.fiap.gestaoltakn.service;

import com.fiap.gestaoltakn.entity.FuncionarioEntity;
import com.fiap.gestaoltakn.enums.FuncionarioStatus;
import com.fiap.gestaoltakn.repository.FuncionarioRepository;
import com.fiap.gestaoltakn.service.message.CacheSyncProducer;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@CacheConfig(cacheNames = "funcionarios")
public class FuncionarioService {

    private final FuncionarioRepository repository;
    private final CacheSyncProducer cacheSyncProducer;

    public FuncionarioService(FuncionarioRepository repository, CacheSyncProducer cacheSyncProducer) {
        this.repository = repository;
        this.cacheSyncProducer = cacheSyncProducer;
    }

    @Cacheable(key = "'page:' + #pageable.pageNumber + ':' + #pageable.pageSize")
    public Page<FuncionarioEntity> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<FuncionarioEntity> listarTodos() {
        return repository.findAll();
    }

    @Cacheable(key = "'departamento:' + #departamentoId + ':page:' + #pageable.pageNumber + ':' + #pageable.pageSize")
    public Page<FuncionarioEntity> listarPorDepartamento(Long departamentoId, Pageable pageable) {
        return repository.findByDepartamento_Id(departamentoId, pageable);
    }

    @Cacheable(key = "#id")
    public FuncionarioEntity buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Funcionário com id " + id + " não encontrado"));
    }

    @Caching(evict = {
            @CacheEvict(allEntries = true, cacheNames = "funcionarios")
    })
    public FuncionarioEntity criar(FuncionarioEntity funcionario) {
        funcionario.setStatus(computeStatus(funcionario));
        FuncionarioEntity salvo = repository.save(funcionario);
        cacheSyncProducer.enviarInvalidacaoFuncionario(salvo.getId());
        return salvo;
    }

    @Caching(evict = {
            @CacheEvict(allEntries = true, cacheNames = "funcionarios")
    })
    public FuncionarioEntity atualizar(Long id, FuncionarioEntity atualizado) {
        FuncionarioEntity existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Funcionário com id " + id + " não encontrado"));

        existente.setNome(atualizado.getNome());
        existente.setDepartamento(atualizado.getDepartamento());
        existente.setHorasTrabalhadasUltimoMes(atualizado.getHorasTrabalhadasUltimoMes());
        existente.setStatus(computeStatus(existente));

        FuncionarioEntity salvo = repository.save(existente);
        cacheSyncProducer.enviarInvalidacaoFuncionario(salvo.getId());
        return salvo;
    }

    @Caching(evict = {
            @CacheEvict(allEntries = true, cacheNames = "funcionarios")
    })
    public void deletar(Long id) {
        repository.deleteById(id);
        cacheSyncProducer.enviarInvalidacaoFuncionario(id);
    }

    @Cacheable(key = "'search:' + #nome")
    public List<FuncionarioEntity> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    @Cacheable(key = "'departamentoNome:' + #departamentoNome")
    public List<FuncionarioEntity> buscarPorDepartamentoNome(String departamentoNome) {
        return repository.findByDepartamentoNomeContainingIgnoreCase(departamentoNome);
    }

    @Cacheable(key = "'horas:' + #horas")
    public List<FuncionarioEntity> buscarPorHoras(Integer horas) {
        return repository.findByHorasTrabalhadasUltimoMes(horas);
    }

    @Cacheable(key = "'status:' + #status.name()")
    public List<FuncionarioEntity> buscarPorStatus(FuncionarioStatus status) {
        return repository.findByStatus(status);
    }

    FuncionarioStatus computeStatus(FuncionarioEntity funcionario) {
        if (funcionario == null) return FuncionarioStatus.SAUDAVEL;
        if (funcionario.getDepartamento() == null) return FuncionarioStatus.SAUDAVEL;

        Integer maxHoras = funcionario.getDepartamento().getNumeroHorasMaximas();
        Integer trabalhadas = funcionario.getHorasTrabalhadasUltimoMes();

        if (maxHoras != null && trabalhadas != null && trabalhadas > maxHoras) {
            return FuncionarioStatus.EM_RISCO;
        }

        return FuncionarioStatus.SAUDAVEL;
    }

}

package com.sales.management.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${cache.default-ttl:300}")
    private long defaultTtl;

    /**
     * Busca valor do cache
     * @param key Chave do cache
     * @param clazz Classe do objeto
     * @return Objeto ou null se não existir
     */
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            
            if (cached == null) {
                logger.debug("Cache MISS: {}", key);
                return null;
            }

            logger.debug("Cache HIT: {}", key);
            return objectMapper.convertValue(cached, clazz);
        } catch (Exception e) {
            logger.error("Erro ao buscar cache [{}]: {}", key, e.getMessage());
            return null;
        }
    }

    /**
     * Busca valor do cache (para tipos genéricos como List, Map)
     * @param key Chave do cache
     * @param typeReference TypeReference para tipos genéricos
     * @return Objeto ou null se não existir
     */
    public <T> T get(String key, TypeReference<T> typeReference) {
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            
            if (cached == null) {
                logger.debug("Cache MISS: {}", key);
                return null;
            }

            logger.debug("Cache HIT: {}", key);
            return objectMapper.convertValue(cached, typeReference);
        } catch (Exception e) {
            logger.error("Erro ao buscar cache [{}]: {}", key, e.getMessage());
            return null;
        }
    }

    /**
     * Salva valor no cache com TTL padrão
     * @param key Chave
     * @param value Valor
     */
    public <T> void set(String key, T value) {
        set(key, value, defaultTtl);
    }

    /**
     * Salva valor no cache com TTL customizado
     * @param key Chave
     * @param value Valor
     * @param ttlSeconds TTL em segundos
     */
    public <T> void set(String key, T value, long ttlSeconds) {
        try {
            redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
            logger.info("Cache salvo [{}] - TTL: {}s", key, ttlSeconds);
        } catch (Exception e) {
            logger.error("Erro ao salvar cache [{}]: {}", key, e.getMessage());
        }
    }

    /**
     * Deleta uma chave específica
     * @param key Chave
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            logger.info("Cache deletado: {}", key);
        } catch (Exception e) {
            logger.error("Erro ao deletar cache [{}]: {}", key, e.getMessage());
        }
    }

    /**
     * Deleta múltiplas chaves por padrão (wildcards)
     * @param pattern Padrão (ex: "products:*", "sales:user:123:*")
     */
    public void deletePattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            
            if (keys == null || keys.isEmpty()) {
                logger.info("Nenhuma chave encontrada para padrão: {}", pattern);
                return;
            }

            redisTemplate.delete(keys);
            logger.info("{} chaves deletadas [{}]", keys.size(), pattern);
        } catch (Exception e) {
            logger.error("Erro ao deletar padrão [{}]: {}", pattern, e.getMessage());
        }
    }

    /**
     * Verifica se uma chave existe
     * @param key Chave
     * @return true se existir
     */
    public boolean exists(String key) {
        try {
            Boolean exists = redisTemplate.hasKey(key);
            return exists != null && exists;
        } catch (Exception e) {
            logger.error("Erro ao verificar existência [{}]: {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * Obtém o tempo restante (TTL) de uma chave
     * @param key Chave
     * @return Segundos restantes ou -1 se não existir
     */
    public long getTTL(String key) {
        try {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return ttl != null ? ttl : -1;
        } catch (Exception e) {
            logger.error("Erro ao buscar TTL [{}]: {}", key, e.getMessage());
            return -1;
        }
    }

    /**
     * Limpa TODOS os caches (use com cuidado!)
     */
    public void flushAll() {
        try {
            redisTemplate.getConnectionFactory().getConnection().flushAll();
            logger.warn("TODOS os caches foram limpos!");
        } catch (Exception e) {
            logger.error("Erro ao limpar todos os caches: {}", e.getMessage());
        }
    }

    /**
     * Helper: busca do cache ou executa função e salva
     * @param key Chave
     * @param clazz Classe do retorno
     * @param supplier Função que busca os dados (se não estiver em cache)
     * @return Dados (do cache ou da função)
     */
    public <T> T getOrCompute(String key, Class<T> clazz, java.util.function.Supplier<T> supplier) {
        T cached = get(key, clazz);
        
        if (cached != null) {
            return cached;
        }

        // Não está em cache, busca os dados
        T data = supplier.get();
        
        if (data != null) {
            set(key, data);
        }

        return data;
    }

    /**
     * Helper: busca do cache ou executa função e salva (com TTL customizado)
     */
    public <T> T getOrCompute(String key, Class<T> clazz, java.util.function.Supplier<T> supplier, long ttlSeconds) {
        T cached = get(key, clazz);
        
        if (cached != null) {
            return cached;
        }

        T data = supplier.get();
        
        if (data != null) {
            set(key, data, ttlSeconds);
        }

        return data;
    }
}
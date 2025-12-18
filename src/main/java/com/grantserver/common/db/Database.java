package com.grantserver.common.db;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.grantserver.model.Expert;
import com.grantserver.model.Participant;

/**
 * Имитация физической базы данных.
 * Содержит "таблицы" и генераторы ID.
 */
public class Database {

    private static final Database INSTANCE = new Database();

    // --- Таблицы (Tables) ---
    private final Map<Long, Participant> participantsTable = new ConcurrentHashMap<>();
    private final Map<Long, Expert> expertsTable = new ConcurrentHashMap<>();

    // --- Последовательности (Sequences / Auto Increment) ---
    private final AtomicLong userIdSeq = new AtomicLong(0);
    private final AtomicLong applicationIdSeq = new AtomicLong(0);
    private final AtomicLong evaluationIdSeq = new AtomicLong(0);

    private Database() {}

    public static Database getInstance() {
        return INSTANCE;
    }

    // --- Доступ к таблицам ---
    
    public Map<Long, Participant> getParticipantsTable() {
        return participantsTable;
    }

    public Map<Long, Expert> getExpertsTable() {
        return expertsTable;
    }

    // --- Генераторы ID ---

    public Long nextParticipantId() { return userIdSeq.incrementAndGet(); }
    public Long nextExpertId() { return userIdSeq.incrementAndGet(); }
    public Long nextApplicationId() { return applicationIdSeq.incrementAndGet(); }
    public Long nextEvaluationId() { return evaluationIdSeq.incrementAndGet(); }
}
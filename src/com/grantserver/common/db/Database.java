package com.grantserver.common.db;

import com.grantserver.model.Evaluation;
import com.grantserver.model.Expert;
import com.grantserver.model.GrantApplication;
import com.grantserver.model.Participant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Имитация физической базы данных.
 * Содержит "таблицы" и генераторы ID.
 */
public class Database {

    private static final Database INSTANCE = new Database();

    // --- Таблицы (Tables) ---
    private final Map<Long, Participant> participantsTable = new ConcurrentHashMap<>();
    private final Map<Long, Expert> expertsTable = new ConcurrentHashMap<>();
    private final Map<Long, GrantApplication> applicationsTable = new ConcurrentHashMap<>();
    private final Map<Long, Evaluation> evaluationsTable = new ConcurrentHashMap<>();

    // --- Последовательности (Sequences / Auto Increment) ---
    private final AtomicLong participantIdSeq = new AtomicLong(0);
    private final AtomicLong expertIdSeq = new AtomicLong(0);
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

    public Map<Long, GrantApplication> getApplicationsTable() {
        return applicationsTable;
    }

    public Map<Long, Evaluation> getEvaluationsTable() {
        return evaluationsTable;
    }

    // --- Генераторы ID ---

    public Long nextParticipantId() { return participantIdSeq.incrementAndGet(); }
    public Long nextExpertId() { return expertIdSeq.incrementAndGet(); }
    public Long nextApplicationId() { return applicationIdSeq.incrementAndGet(); }
    public Long nextEvaluationId() { return evaluationIdSeq.incrementAndGet(); }
}
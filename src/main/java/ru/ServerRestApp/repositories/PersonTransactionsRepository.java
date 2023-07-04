package ru.ServerRestApp.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ServerRestApp.models.CategoryTransaction;
import ru.ServerRestApp.models.Invitation;
import ru.ServerRestApp.models.PersonTransaction;

import java.util.List;

public interface PersonTransactionsRepository extends JpaRepository<PersonTransaction, Integer> {
    List<PersonTransaction> findByPersonFromId(int personFromId);
    List<PersonTransaction> findByPersonToId(int personFromId);

    @Query("SELECT pt FROM PersonTransaction pt " +
            "WHERE pt.personFrom.id = :personId OR pt.personTo.id = :personId")
    List<PersonTransaction> findByPersonId(int personId);

    @Query("SELECT pt FROM PersonTransaction pt " +
            "WHERE pt.personFrom.team.id = :teamId OR pt.personTo.team.id = :teamId")
    List<PersonTransaction> findByTeamId(int teamId);

    @Query("SELECT pt FROM PersonTransaction pt " +
            "WHERE pt.personFrom.team.id = :fromTeamId")
    List<PersonTransaction> findByFromTeamId(int fromTeamId);

    @Query("SELECT pt FROM PersonTransaction pt " +
            "WHERE pt.personTo.team.id = :toTeamId")
    List<PersonTransaction> findByToTeamId(int toTeamId);



    @Query("SELECT pt FROM PersonTransaction pt " +
            "WHERE pt.personFrom.id = :personId OR pt.personTo.id = :personId " +
            "AND YEAR(pt.createdAt) = :year " +
            "AND MONTH(pt.createdAt) = :month")
    List<PersonTransaction> findByPersonIdForMonth(int personId, int month, int year);

    @Query("SELECT pt FROM PersonTransaction pt " +
            "WHERE pt.personFrom.id = :personId " +
            "AND YEAR(pt.createdAt) = :year " +
            "AND MONTH(pt.createdAt) = :month")
    List<PersonTransaction> findByFromPersonIdForMonth(int personId, int month, int year);

    @Query("SELECT pt FROM PersonTransaction pt " +
            "WHERE pt.personTo.id = :personId " +
            "AND YEAR(pt.createdAt) = :year " +
            "AND MONTH(pt.createdAt) = :month")
    List<PersonTransaction> findByToPersonIdForMonth(int personId, int month, int year);



    @Query("SELECT pt FROM PersonTransaction pt " +
            "WHERE pt.personFrom.team.id = :teamId OR pt.personTo.team.id = :teamId " +
            "AND YEAR(pt.createdAt) = :year " +
            "AND MONTH(pt.createdAt) = :month")
    List<PersonTransaction> findByTeamIdForMonth(int teamId, int month, int year);

    @Query("SELECT pt FROM PersonTransaction pt " +
            "WHERE pt.personFrom.team.id = :teamId " +
            "AND YEAR(pt.createdAt) = :year " +
            "AND MONTH(pt.createdAt) = :month")
    List<PersonTransaction> findByFromTeamIdForMonth(int teamId, int month, int year);

    @Query("SELECT pt FROM PersonTransaction pt " +
            "WHERE pt.personTo.team.id = :teamId " +
            "AND YEAR(pt.createdAt) = :year " +
            "AND MONTH(pt.createdAt) = :month")
    List<PersonTransaction> findByToTeamIdForMonth(int teamId, int month, int year);




    @Query("SELECT pt " +
            "FROM PersonTransaction pt " +
            "WHERE pt.personFrom.id = :personId OR pt.personTo.id = :personId " +
            "ORDER BY pt.createdAt DESC")
    List<PersonTransaction> findNLastTransactionsPerson(@Param("personId") int personId, Pageable pageable);

    @Query("SELECT pt " +
            "FROM PersonTransaction pt " +
            "WHERE pt.personFrom.id = :personId " +
            "ORDER BY pt.createdAt DESC")
    List<PersonTransaction> findNLastTransactionsFromPerson(@Param("personId") int personId, Pageable pageable);

    @Query("SELECT pt " +
            "FROM PersonTransaction pt " +
            "WHERE pt.personFrom.id = :personId " +
            "ORDER BY pt.createdAt DESC")
    List<PersonTransaction> findNLastTransactionsToPerson(@Param("personId") int personId, Pageable pageable);

}

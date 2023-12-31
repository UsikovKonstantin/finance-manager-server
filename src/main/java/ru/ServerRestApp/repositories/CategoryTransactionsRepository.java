package ru.ServerRestApp.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ServerRestApp.models.CategoryTransaction;

import java.util.List;

public interface CategoryTransactionsRepository extends JpaRepository<CategoryTransaction, Integer> {
    List<CategoryTransaction> findByPersonId(int personId);
    List<CategoryTransaction> findByCategoryId(int categoryId);
    List<CategoryTransaction> findByPersonTeamId(int personTeamId);

    @Query("SELECT ctg.name, SUM(trns.amount) " +
            "FROM CategoryTransaction trns " +
            "JOIN trns.category ctg " +
            "WHERE trns.person.id = :personId AND trns.amount > 0 " +
            "GROUP BY ctg.name")
    List<Object[]> getPositiveTransactionsByCategoryForPerson(@Param("personId") int personId);

    @Query("SELECT ctg.name, SUM(trns.amount) " +
            "FROM CategoryTransaction trns " +
            "JOIN trns.category ctg " +
            "WHERE trns.person.id = :personId AND trns.amount < 0 " +
            "GROUP BY ctg.name")
    List<Object[]> getNegativeTransactionsByCategoryForPerson(@Param("personId") int personId);

    @Query("SELECT ctg.name, SUM(trns.amount) " +
            "FROM CategoryTransaction trns " +
            "JOIN trns.category ctg " +
            "WHERE trns.person.team.id = :groupId AND trns.amount > 0 " +
            "GROUP BY ctg.name")
    List<Object[]> getPositiveTransactionsByCategoryForGroup(@Param("groupId") int groupId);

    @Query("SELECT ctg.name, SUM(trns.amount) " +
            "FROM CategoryTransaction trns " +
            "JOIN trns.category ctg " +
            "WHERE trns.person.team.id = :groupId AND trns.amount < 0 " +
            "GROUP BY ctg.name")
    List<Object[]> getNegativeTransactionsByCategoryForGroup(@Param("groupId") int groupId);




    @Query("SELECT ct FROM CategoryTransaction ct " +
            "WHERE ct.person.id = :personId " +
            "AND YEAR(ct.createdAt) = :year " +
            "AND MONTH(ct.createdAt) = :month")
    List<CategoryTransaction> findByPersonIdForMonth(int personId, int month, int year);

    @Query("SELECT ct FROM CategoryTransaction ct " +
            "WHERE ct.person.team.id = :personTeamId " +
            "AND YEAR(ct.createdAt) = :year " +
            "AND MONTH(ct.createdAt) = :month")
    List<CategoryTransaction> findByPersonTeamIdForMonth(int personTeamId, int month, int year);

    @Query("SELECT ctg.name, SUM(trns.amount) " +
            "FROM CategoryTransaction trns " +
            "JOIN trns.category ctg " +
            "WHERE trns.person.id = :personId AND trns.amount > 0 AND YEAR(trns.createdAt) = :year AND MONTH(trns.createdAt) = :month " +
            "GROUP BY ctg.name")
    List<Object[]> getPositiveTransactionsByCategoryForPersonForMonth(@Param("personId") int personId, @Param("month") int month, @Param("year") int year);

    @Query("SELECT ctg.name, SUM(trns.amount) " +
            "FROM CategoryTransaction trns " +
            "JOIN trns.category ctg " +
            "WHERE trns.person.id = :personId AND trns.amount < 0 AND YEAR(trns.createdAt) = :year AND MONTH(trns.createdAt) = :month " +
            "GROUP BY ctg.name")
    List<Object[]> getNegativeTransactionsByCategoryForPersonForMonth(@Param("personId") int personId, @Param("month") int month, @Param("year") int year);

    @Query("SELECT ctg.name, SUM(trns.amount) " +
            "FROM CategoryTransaction trns " +
            "JOIN trns.category ctg " +
            "WHERE trns.person.team.id = :groupId AND trns.amount > 0 AND YEAR(trns.createdAt) = :year AND MONTH(trns.createdAt) = :month " +
            "GROUP BY ctg.name")
    List<Object[]> getPositiveTransactionsByCategoryForGroupForMonth(@Param("groupId") int groupId, @Param("month") int month, @Param("year") int year);

    @Query("SELECT ctg.name, SUM(trns.amount) " +
            "FROM CategoryTransaction trns " +
            "JOIN trns.category ctg " +
            "WHERE trns.person.team.id = :groupId AND trns.amount < 0 AND YEAR(trns.createdAt) = :year AND MONTH(trns.createdAt) = :month " +
            "GROUP BY ctg.name")
    List<Object[]> getNegativeTransactionsByCategoryForGroupForMonth(@Param("groupId") int groupId, @Param("month") int month, @Param("year") int year);



    @Query("SELECT ct " +
            "FROM CategoryTransaction ct " +
            "WHERE ct.person.id = :personId AND ct.amount > 0 " +
            "ORDER BY ct.createdAt DESC")
    List<CategoryTransaction> findNLastPositiveTransactionsForPerson(@Param("personId") int personId, Pageable pageable);

    @Query("SELECT ct " +
            "FROM CategoryTransaction ct " +
            "WHERE ct.person.id = :personId AND ct.amount < 0 " +
            "ORDER BY ct.createdAt DESC")
    List<CategoryTransaction> findNLastNegativeTransactionsForPerson(@Param("personId") int personId, Pageable pageable);
}

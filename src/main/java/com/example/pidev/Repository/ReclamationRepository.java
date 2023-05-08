package com.example.pidev.Repository;

import com.example.pidev.entity.Reclamation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {

    @Query("SELECT c FROM Reclamation c WHERE c.sendingDate = :d1 ")
    public List<Reclamation> getReclamationBYdate(@Param("d1") Date d1);
}

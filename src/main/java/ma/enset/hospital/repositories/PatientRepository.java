package ma.enset.hospital.repositories;

import ma.enset.hospital.entities.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PatientRepository extends JpaRepository<Patient,Long> {
   //si on a une méthode qui retourne des pages , il faut utiliser un 2eme para pageable
    Page<Patient> findByNomContains(String keyword, Pageable pageable);

    @Query("select p from Patient p  where p.nom like :x")
    Page<Patient> chercher(@Param("x") String keyword, Pageable pageable);
}

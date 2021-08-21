package pl.seahawks.vacationes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.seahawks.vacationes.domain.model.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
}

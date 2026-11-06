package senai499.com.br.donodont.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import senai499.com.br.donodont.model.Agendamento;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findByEmailIgnoreCaseOrderByDataConsultaAsc(String email);
}
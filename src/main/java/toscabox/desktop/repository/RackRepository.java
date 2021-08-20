package toscabox.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import toscabox.desktop.domain.Rack;

public interface RackRepository extends CommonRepository<Rack> {
    
    List<Rack> filter(String keyword, int limit);

}

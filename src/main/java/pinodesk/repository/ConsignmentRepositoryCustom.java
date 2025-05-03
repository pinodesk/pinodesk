package pinodesk.repository;

import java.util.List;

import pinodesk.viewmodel.ConsignmentFilterVM;
import pinodesk.viewmodel.ConsignmentVM;

public interface ConsignmentRepositoryCustom {
    List<ConsignmentVM> findByFilter(ConsignmentFilterVM filter);
}
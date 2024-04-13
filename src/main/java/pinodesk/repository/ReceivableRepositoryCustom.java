package pinodesk.repository;

import java.util.List;

import pinodesk.viewmodel.ReceivableFilterVM;
import pinodesk.viewmodel.ReceivableVM;

public interface ReceivableRepositoryCustom {

    List<ReceivableVM> findByFilter(ReceivableFilterVM filter);

}

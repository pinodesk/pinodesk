package pinodesk.repository;

import java.util.List;

import pinodesk.viewmodel.PayableFilterVM;
import pinodesk.viewmodel.PayableVM;

public interface PayableRepositoryCustom {

    List<PayableVM> findByFilter(PayableFilterVM filter);

}

package stoready.desktop.repository;

import java.util.List;

import stoready.desktop.viewmodel.PayableFilterVM;
import stoready.desktop.viewmodel.PayableVM;

public interface PayableRepositoryCustom {

    List<PayableVM> findByFilter(PayableFilterVM filter);

}

package stoready.desktop.repository;

import java.util.List;

import stoready.desktop.viewmodel.ReceivableFilterVM;
import stoready.desktop.viewmodel.ReceivableVM;

public interface ReceivableRepositoryCustom {

    List<ReceivableVM> findByFilter(ReceivableFilterVM filter);

}

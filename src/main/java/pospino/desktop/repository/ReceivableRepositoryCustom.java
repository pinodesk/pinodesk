package pospino.desktop.repository;

import java.util.List;

import pospino.desktop.viewmodel.ReceivableFilterVM;
import pospino.desktop.viewmodel.ReceivableVM;

public interface ReceivableRepositoryCustom {

    List<ReceivableVM> findByFilter(ReceivableFilterVM filter);

}

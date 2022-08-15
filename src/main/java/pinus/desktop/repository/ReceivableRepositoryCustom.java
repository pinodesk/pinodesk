package pinus.desktop.repository;

import java.util.List;

import pinus.desktop.viewmodel.ReceivableFilterVM;
import pinus.desktop.viewmodel.ReceivableVM;

public interface ReceivableRepositoryCustom {

    List<ReceivableVM> findByFilter(ReceivableFilterVM filter);

}

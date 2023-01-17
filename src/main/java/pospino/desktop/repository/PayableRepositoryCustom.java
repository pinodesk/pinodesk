package pospino.desktop.repository;

import java.util.List;

import pospino.desktop.viewmodel.PayableFilterVM;
import pospino.desktop.viewmodel.PayableVM;

public interface PayableRepositoryCustom {

    List<PayableVM> findByFilter(PayableFilterVM filter);

}

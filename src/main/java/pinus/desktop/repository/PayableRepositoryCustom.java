package pinus.desktop.repository;

import java.util.List;

import pinus.desktop.viewmodel.PayableFilterVM;
import pinus.desktop.viewmodel.PayableVM;

public interface PayableRepositoryCustom {

    List<PayableVM> findByFilter(PayableFilterVM filter);

}

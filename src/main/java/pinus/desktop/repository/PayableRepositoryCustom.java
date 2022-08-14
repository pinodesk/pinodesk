package pinus.desktop.repository;

import java.util.List;

import pinus.desktop.domain.Payable;
import pinus.desktop.viewmodel.PayableFilterVM;

public interface PayableRepositoryCustom {

    List<Payable> findByFilter(PayableFilterVM filter);

}

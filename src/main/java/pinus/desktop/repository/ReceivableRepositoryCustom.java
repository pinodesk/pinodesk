package pinus.desktop.repository;

import java.util.List;

import pinus.desktop.domain.Receivable;
import pinus.desktop.viewmodel.ReceivableFilterVM;

public interface ReceivableRepositoryCustom {

    List<Receivable> findByFilter(ReceivableFilterVM filter);

}

package pinus.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;

import pinus.desktop.domain.PurchaseDetail;
import pinus.desktop.viewmodel.PurchaseProductVM;

public class PurchaseDetailRepositoryImpl extends AbstractRepository<PurchaseDetail>
        implements PurchaseDetailRepositoryCustom {

    @Override
    public List<PurchaseProductVM> findByPurchaseIdJoinProducts(Long purchaseId, String languageCode) {
        String sql = """
                select
                    a.*,
                    b.name as product_name ,
                    b.unit_label as product_unit_label,
                    c.code as product_category_code,
                    c.name as product_category_name
                from purchase_detail a
                inner join product b on b.id = a.product_id
                inner join product_category c on c.code = b.category_code and c.language_code = ?
                where a.purchase_id = ?
                """;
        return performSelect(sql, List.of(languageCode, purchaseId), PurchaseProductVM.class);
    }

}

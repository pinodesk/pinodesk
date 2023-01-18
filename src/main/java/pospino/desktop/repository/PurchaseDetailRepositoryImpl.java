package pospino.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;

import pospino.desktop.domain.PurchaseDetail;
import pospino.desktop.viewmodel.PurchaseProductVM;

public class PurchaseDetailRepositoryImpl extends AbstractRepository<PurchaseDetail>
        implements PurchaseDetailRepositoryCustom {

    @Override
    public List<PurchaseProductVM> findByPurchaseIdJoinProducts(Long purchaseId, String language) {
        String sql = """
                    select
                    a.*,
                    b.name as product_name ,
                    b.unit_label as product_unit_label,
                    c.code as product_category_code,
                    c.name as product_category_name,
                    d.general_selling_price,
                    d.prescription_selling_price,
                    e.expired_date,
                    e.batch_number
                from purchase_detail a
                inner join product b on b.id = a.product_id
                inner join product_category c on c.code = b.category_code and c.language = ?
                inner join product_price d on d.id = (
                    select f.id from product_price f where f.purchase_id = a.purchase_id and f.product_id = a.product_id order by f.id desc limit 1)
                left join product_expiry e on e.id = (
                    select g.id from product_expiry g where g.purchase_id = a.purchase_id and g.product_id = a.product_id order by g.id desc limit 1)
                where a.purchase_id = ?
                """;
        return performSelect(sql, List.of(language, purchaseId), PurchaseProductVM.class);
    }

}

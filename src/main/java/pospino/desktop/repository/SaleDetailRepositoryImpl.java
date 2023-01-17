package pospino.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;

import pospino.desktop.domain.SaleDetail;
import pospino.desktop.viewmodel.SaleProductVM;

public class SaleDetailRepositoryImpl extends AbstractRepository<SaleDetail> implements SaleDetailRepositoryCustom {

    @Override
    public List<SaleProductVM> findBySaleIdJoinProducts(Long saleId, String language) {
        String sql = """
                    select
                    a.*,
                    a.quantity as sale_quantity,
                    b.quantity as current_quantity,
                    b.name as product_name ,
                    b.unit_label as product_unit_label,
                    b.general_selling_price,
                    b.prescription_selling_price,
                    c.code as product_category_code,
                    c.name as product_category_name,
                    e.expired_date,
                    e.batch_number
                from sale_detail a
                inner join product b on b.id = a.product_id
                inner join product_category c on c.code = b.category_code and c.language = ?
                left join product_expiry e on e.sale_detail_id = a.id
                where a.sale_id = ?
                """;
        return performSelect(sql, List.of(language, saleId), SaleProductVM.class);
    }

}

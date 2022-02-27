package pinus.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.model.DataModel;
import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Order;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.springframework.stereotype.Repository;

import pinus.desktop.domain.ProductPrice;

@Repository
public class ProductPriceRepositoryImpl extends AbstractRepository<ProductPrice> implements ProductPriceRepository {

    @Override
    public List<ProductPrice> findByProductId(Long productId) {
        return read(
                new Where().equals(ProductPrice.C_PRODUCT_ID, productId),
                new Order().by(DataModel.C_ID, Order.DESCENDING));
    }

}

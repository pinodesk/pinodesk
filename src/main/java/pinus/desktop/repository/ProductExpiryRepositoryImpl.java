package pinus.desktop.repository;

import java.util.List;
import java.util.Optional;

import com.gitlab.muhammadkholidb.sequel.model.DataModel;
import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Order;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.springframework.stereotype.Repository;

import pinus.desktop.domain.ProductExpiry;

@Repository
public class ProductExpiryRepositoryImpl extends AbstractRepository<ProductExpiry> implements ProductExpiryRepository {

    @Override
    public List<ProductExpiry> findByProductId(Long productId) {
        return read(
                new Where().equals(ProductExpiry.C_PRODUCT_ID, productId),
                new Order().by(DataModel.C_ID, Order.DESCENDING));
    }

    @Override
    public Optional<ProductExpiry> findTopByProductId(Long productId) {
        return readOne(
                new Where().equals(ProductExpiry.C_PRODUCT_ID, productId),
                new Order().by(DataModel.C_ID, Order.DESCENDING));
    }

    @Override
    public Optional<ProductExpiry> findTopByProductIdOrderByExpiredDate(Long productId) {
        return readOne(
                new Where().equals(ProductExpiry.C_PRODUCT_ID, productId),
                new Order().by(ProductExpiry.C_EXPIRED_DATE));
    }
}

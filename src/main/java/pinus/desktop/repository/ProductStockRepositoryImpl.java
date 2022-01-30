package pinus.desktop.repository;

import java.util.List;
import java.util.Optional;

import com.gitlab.muhammadkholidb.sequel.model.DataModel;
import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Order;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.springframework.stereotype.Repository;

import pinus.desktop.domain.ProductStock;

@Repository
public class ProductStockRepositoryImpl extends AbstractRepository<ProductStock> implements ProductStockRepository {

    @Override
    public List<ProductStock> findByProductId(Long productId) {
        return read(
                new Where().equals(ProductStock.C_PRODUCT_ID, productId),
                new Order().by(DataModel.C_ID, Order.DESCENDING));
    }

    @Override
    public Optional<ProductStock> findTopByProductId(Long productId) {
        return readOne(
                new Where().equals(ProductStock.C_PRODUCT_ID, productId),
                new Order().by(DataModel.C_ID, Order.DESCENDING));
    }
}

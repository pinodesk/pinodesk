package pospino.desktop.viewmodel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import lombok.Data;
import pospino.desktop.constant.SellingMode;

@Data
public class SaleDataVM {
    private List<SaleProductVM> saleProducts;
    private Optional<CustomerVM> customer;
    private SellingMode sellingMode;
    private Integer totalProduct;
    private BigDecimal totalSale;
}

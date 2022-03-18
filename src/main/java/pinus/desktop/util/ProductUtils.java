package pinus.desktop.util;

import pinus.desktop.constant.CommonConstants;

public final class ProductUtils {

    private ProductUtils() {
    }

    public static boolean isProductCategoryDrugs(String categoryCode) {
        return CommonConstants.PRODUCT_CATEGORY_CODE_DRUGS.equals(categoryCode);
    }

}

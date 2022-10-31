package stoready.desktop.constant;

public interface CommonConstants {
    String APP_TITLE = "Stoready";
    String LANGUAGE_CODE_ENGLISH = "en";
    String LANGUAGE_CODE_INDONESIA = "id";
    String DATETIME_DISPLAY_PATTERN = "yyyy-MM-dd HH:mm:ss";
    String DATE_DISPLAY_PATTERN = "yyyy-MM-dd";
    String PRODUCT_CATEGORY_CODE_DRUGS = "000000518";
    String PRODUCT_CATEGORY_CODE_CUSTOM_PACKAGE = "000505834";
    String UNIT_LABEL_BUNDLE = "BUNDLE";
    String CONTACT_MASK_SUPPLIER = "S-UUUU-UUUU";
    String CONTACT_MASK_CUSTOMER = "C-UUUU-UUUU";
    String CODE_PREFIX_DATE_PATTERN = "yyyyMMdd";
    String PAGE_TEMPLATE_DIR = "/assets/templates/";
    String RESOURCE_BUNDLE_PACKAGE = "stoready.desktop.lang";
    String EMPTY_DATE_MASK = "____-__-__";
    String IMPORT_TEMPLATE_FILE_NAME = "import-products-template.xlsx";
    Long USER_GROUP_ID_ADMINISTRATOR = 1l;
    String[] APP_ICON_PATHS = new String[] {
            "/assets/images/stoready-icon-circle-32.png",
            "/assets/images/stoready-icon-circle-64.png",
            "/assets/images/stoready-icon-circle-128.png",
            "/assets/images/stoready-icon-circle-256.png" };

}

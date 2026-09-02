package pinodesk.javafx.converter;

import java.util.Locale;

import com.pinodesk.pandora.converter.DefaultStringConverterAdapter;

import javafx.scene.control.ComboBox;

public class LanguageComboBoxConverter extends DefaultStringConverterAdapter<Locale> {

    private String currentLanguage;

    public LanguageComboBoxConverter(ComboBox<Locale> cb, String currentLanguage) {
        super(cb);
        this.currentLanguage = currentLanguage;
    }

    @Override
    protected String getDisplayText(Locale locale) {
        Locale currentLocale = Locale.forLanguageTag(currentLanguage);
        return locale.getDisplayLanguage(currentLocale);
    }

}

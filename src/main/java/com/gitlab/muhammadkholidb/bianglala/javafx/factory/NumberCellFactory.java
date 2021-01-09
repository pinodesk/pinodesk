package com.gitlab.muhammadkholidb.bianglala.javafx.factory;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import com.gitlab.muhammadkholidb.bianglala.constant.CommonConstants;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class NumberCellFactory<E, T extends Number> implements Callback<TableColumn<E, T>, TableCell<E, T>> {

    public static final int DEFAULT_DECIMAL_DIGIT = 0;

    private int decimalDigit;
    private char decimalSeparator;
    private char thousandSeparator;

    public NumberCellFactory(int decimalDigit, Locale locale) {
        this.decimalDigit = decimalDigit;
        this.decimalSeparator = locale.equals(CommonConstants.BAHASA) ? ',' : '.';
        this.thousandSeparator = locale.equals(CommonConstants.BAHASA) ? '.' : ',';
    }

    public NumberCellFactory(Locale locale) {
        this(DEFAULT_DECIMAL_DIGIT, locale);
    }

    @Override
    public TableCell<E, T> call(TableColumn<E, T> param) {

        return new TableCell<E, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                if (item == getItem()) {
                    return;
                }
                super.updateItem(item, empty);
                if (item == null) {
                    super.setText(null);
                    super.setGraphic(null);
                } else {
                    DecimalFormat df = new DecimalFormat();
                    DecimalFormatSymbols customSymbol = new DecimalFormatSymbols();
                    customSymbol.setDecimalSeparator(decimalSeparator);
                    customSymbol.setGroupingSeparator(thousandSeparator);
                    df.setDecimalFormatSymbols(customSymbol);
                    df.setMinimumFractionDigits(decimalDigit);
                    df.setGroupingUsed(true);
                    df.setRoundingMode(RoundingMode.HALF_UP);
                    String formatted = df.format(item);
                    super.setText(formatted);
                }
            }
        };
    }

}

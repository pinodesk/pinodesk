package com.gitlab.muhammadkholidb.bianglala;

public class Main {

    // https://stackoverflow.com/questions/52653836/maven-shade-javafx-runtime-components-are-missing
    public static void main(String[] args) {
        Bianglala.main(args);
    }
}

/**
 * Useful links:
 * https://iik.ac.id/blog/kenali-kategori-obat-sebelum-dikonsumsi/
 * http://m-rifqi-rokhman.staff.ugm.ac.id/2016/11/28/obat-wajib-apotek-lengkap/
 * https://www.fda.gov/drugs/investigational-new-drug-ind-application/general-drug-categories
 * https://www.google.com/basepages/producttype/taxonomy.en-US.txt
 * https://farmarindoapotek.wordpress.com/daftar-obat/
 * http://pusatict.com/software-apotek-master-apotek-medstore/
 * https://blog.assist.id/cara-membuat-data-obat-baru-di-apotek/
 * http://indoaplikasi.com/software-apotek-program-apotek-aplikasi-apotek-(apt01).php
 * 
 * Tables
 * t_supplier: code, name, address, phone, email, contact_person_name, remarks
 * t_customer: code, name, address, phone, email, remarks
 * t_language: code, name
 * eng, English
 * ind, Bahasa Indonesia
 * 
 * t_product_category: parent_category_id, code, language_id, name, description
 * - Baby and Toddler
 * -    Diapers
 * -    Baby Wipes
 * -    Baby Drinks
 * -    Baby Food
 * -    Baby Formula
 * -    Baby Snacks
 * -    Baby Bottles
 * -    Baby Bottle Nipples & Dots
 * - Food and Beverages
 * -    Food 
 * -        Candies
 * -        Chocolates
 * -        Breads
 * -        Snacks
 * -            Cookies
 * -            Chips & Crackers (Keripik & Kerupuk)
 * -            Pudding
 * -    Beverages 
 * -        Coffee
 * -        Tea
 * -        Milk
 * -        Non-Dairy Milk
 * -        Water
 * -        Flavored Water
 * -        Juice
 * -        Sports & Energy Drinks
 * -        Fruit Flavored Drinks
 * -    Desserts
 * -        Ice Creams
 * -        Yogurts
 * - Health Care
 * -    Medicine and Drugs
 * -    Gels & Lotions
 * -    Contraceptive Cases
 * -    Antiseptics
 * -    Medical Tape & Bandages
 * -    Vitamins & Supplements
 * -    Pregnancy Tests
 * t_drug_detail: product_id, drug_category_id, drug_rack_id, expired_date, indication, contraindication, prescription_price
 * t_drug_rack: code, name
 * t_drug_category_base: code, name, description
 * - PERMENKESRI, Kementrian Kesehatan Indonesia, Permenkes No.917 Tahun 1993
 * - USFDA, US Food and Drug Administration, General Drug Categories by US FDA
 * t_drug_category: category_base_id, code, name, description
 * - 1, PERMENKESRI01, Obat Bebas Terbatas
 * - 1, PERMENKESRI02, Obat Bebas
 * - 1, PERMENKESRI03, Obat Keras
 * - 1, PERMENKESRI04, Obat Wajib Apotek (OWA)
 * - 1, PERMENKESRI05, Obat Golongan Narkotika
 * - 1, PERMENKESRI06, Obat Psikotropika
 * - 1, PERMENKESRI07, Obat Herbal
 * 
 */

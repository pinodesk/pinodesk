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
 * 
 * Tables
 * t_supplier: code, name, address, phone, email, contact_person_name, remarks
 * t_customer: code, name, address, phone, email, remarks
 * t_language: alpha2_code, alpha3_code, name
 * en, eng, English
 * id, ind, Bahasa Indonesia
 * 
 * t_product_category: parent_category_id, code, language_id, name, description
 * - Baby and Toddler
 * - Health Care
 * - Medicine and Drugs
 * 
 * t_drug_category_base: code, name, description
 * - KEMENKESRI, Kementrian Kesehatan Indonesia, Permenkes No.917 Tahun 1993
 * 
 * t_drug_category: category_base_id, code, name, description
 * - 1, OBT, Obat Bebas Terbatas
 * - 1, OB, Obat Bebas
 * - 1, OK, Obat Keras
 * - 1, OWA, Obat Wajib Apotek
 * - 1, OGN, Obat Golongan Narkotika
 * - 1, OP, Obat Psikotropika
 * - 1, OH, Obat Herbal
 * 
 */
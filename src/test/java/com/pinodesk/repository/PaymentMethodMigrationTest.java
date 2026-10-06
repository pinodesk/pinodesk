package com.pinodesk.repository;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;

class PaymentMethodMigrationTest {
    @Test
    void upgradesExistingSalesAndProtectsReferences() throws Exception {
        String url = "jdbc:h2:mem:payment_migration;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1;NON_KEYWORDS=USER,READ,WRITE";
        Flyway.configure().dataSource(url, "sa", "").target("0047").load().migrate();
        try (Connection connection = DriverManager.getConnection(url, "sa", "");
                Statement sql = connection.createStatement()) {
            sql.execute(
                    "insert into `user` (id,user_group_id,username,password_hash,status) values (1,1,'test','hash','active')");
            sql.execute(
                    "insert into sale (selling_mode,invoice_number,payment_status,total_product,total_sale,total_payment,invoice_date,user_id) values ('GENERAL','OLD-1','PAID',1,10000,10000,current_date,1)");
            Flyway.configure().dataSource(url, "sa", "").load().migrate();
            try (ResultSet rs = sql.executeQuery(
                    "select p.name,p.category,p.default_method from sale s join payment_method p on p.id=s.payment_method_id where s.invoice_number='OLD-1'")) {
                assertTrue(rs.next());
                assertEquals("Cash", rs.getString(1));
                assertEquals("CASH", rs.getString(2));
                assertTrue(rs.getBoolean(3));
            }
            try (var context = new org.springframework.context.annotation.AnnotationConfigApplicationContext()) {
                context.registerBean(
                        javax.sql.DataSource.class,
                        () -> new org.springframework.jdbc.datasource.DriverManagerDataSource(url, "sa", ""));
                context.register(JdbcConfig.class);
                context.refresh();
                PaymentMethodRepository methods = context.getBean(PaymentMethodRepository.class);
                SaleRepository sales = context.getBean(SaleRepository.class);
                var cash = methods.findByDefaultMethodTrue().orElseThrow();
                assertTrue(sales.existsByPaymentMethodId(cash.getId()));
                cash.setName("Cash renamed");
                methods.save(cash);
                assertEquals("Cash renamed", methods.findById(cash.getId()).orElseThrow().getName());
                var card = new com.pinodesk.entity.PaymentMethod();
                card.setName("Debit BCA");
                card.setCategory("BANK_CARD");
                card = methods.save(card);
                assertEquals("BANK_CARD", methods.findByNameIgnoreCase("debit bca").orElseThrow().getCategory());
                assertTrue(methods.findAllByOrderByDefaultMethodDescNameAsc().getFirst().isDefaultMethod());
                var sale = sales.findById(1L).orElseThrow();
                sale.setPaymentMethodId(card.getId());
                sales.save(sale);
                assertEquals(card.getId(), sales.findById(1L).orElseThrow().getPaymentMethodId());
                var filter = new com.pinodesk.viewmodel.SaleFilterVM();
                filter.setPaymentMethodId(card.getId());
                var matching = sales.findByFilter(filter);
                assertEquals(1, matching.size());
                assertEquals("Debit BCA", matching.getFirst().getPaymentMethodName());
                filter.setPaymentMethodId(cash.getId());
                assertTrue(sales.findByFilter(filter).isEmpty());
                filter.setPaymentMethodId(null);
                assertEquals(1, sales.findByFilter(filter).size());
                sale.setPaymentMethodId(cash.getId());
                sales.save(sale);
                methods.delete(card);
                cash.setName("Cash");
                methods.save(cash);
            }
            assertThrows(SQLException.class, () -> sql.execute("delete from payment_method where default_method=true"));
            assertThrows(
                    SQLException.class,
                    () -> sql.execute("insert into payment_method (name,category) values ('cash','CASH')"));
            assertThrows(
                    SQLException.class,
                    () -> sql.execute("insert into payment_method (name,category) values ('Bad','INVALID')"));
            sql.execute("update payment_method set name='Tunai' where default_method=true");
            sql.execute("insert into payment_method (name,category) values ('Transfer BCA','TRANSFER')");
            sql.execute(
                    "update sale set payment_method_id=(select id from payment_method where name='Transfer BCA') where invoice_number='OLD-1'");
            try (ResultSet rs = sql.executeQuery("select count(*) from menu where code='0020'")) {
                rs.next();
                assertEquals(2, rs.getInt(1));
            }
        }
    }

    @org.springframework.context.annotation.Import(com.pinodesk.sequel.config.SequelConfig.class)
    @org.springframework.data.jdbc.repository.config.EnableJdbcRepositories(
        basePackageClasses = PaymentMethodRepository.class)
    @org.springframework.data.jdbc.repository.config.EnableJdbcAuditing
    static class JdbcConfig extends org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration {
        @org.springframework.context.annotation.Bean
        org.springframework.jdbc.datasource.DataSourceTransactionManager transactionManager(javax.sql.DataSource ds) {
            return new org.springframework.jdbc.datasource.DataSourceTransactionManager(ds);
        }

        @Override
        public org.springframework.data.relational.core.dialect.Dialect jdbcDialect(
                org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations operations) {
            return org.springframework.data.relational.core.dialect.MySqlDialect.INSTANCE;
        }
    }
}

package com.sogou.pay.notify.activeMq;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by qibaichao on 2015/5/21.
 */
public class TableCreate {

    public static void main(String[] args) throws Exception {

//        createTableSql();
        dropTableSql();
    }

    public static void createTableSql() {

        StringBuilder sb = new StringBuilder();
        String dropTable = "DROP TABLE IF EXISTS ";
        String createTable = "CREATE TABLE  IF NOT EXISTS ";
        String tablePrefix = "test_";
        String tableName = "";

        String sql = "( `pk` bigint(64) ,\n" +
                "  `id` bigint(64),\n" +
                "  `gmt_create` timestamp ,\n" +
                "  `name` varchar(64),\n" +
                "  `floatCol` float,\n" +
                "  PRIMARY KEY (`pk`))ENGINE=InnoDB DEFAULT CHARSET=utf8;";

        int tableIndex = 0;
        for (int i = 0; i < 64; i++) {
            tableName = tablePrefix + StringUtils.leftPad("" + i, 2, "0");
            sb.append(dropTable + tableName + ";\n" + createTable + tableName).append(sql).append("\n");
        }

        System.out.println(sb);
    }

    public static void dropTableSql() {
        StringBuilder sb = new StringBuilder();
        String dropTable = "DROP TABLE IF EXISTS ";
        String tablePrefix = "test_";
        String tableName = "";

        int tableIndex = 0;
        for (int i = 0; i < 64; i++) {
            tableName = tablePrefix + StringUtils.leftPad("" + i, 4, "0");
            sb.append(dropTable + tableName + ";\n");
        }

        System.out.println(sb);

    }
}

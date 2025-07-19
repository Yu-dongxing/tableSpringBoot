package com.wzz.table.service;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
@Service
public class DatabaseInitService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void initDatabase() throws SQLException {
        List<Class<?>> entityClasses = scanEntityClasses("com.wzz.table.pojo");

        for (Class<?> entityClass : entityClasses) {
            TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);
            if (tableNameAnnotation != null) {
                String tableName = tableNameAnnotation.value();
                createOrUpdateTable(entityClass, tableName);
            }
        }
    }

    private List<Class<?>> scanEntityClasses(String basePackage) {
        List<Class<?>> entityClasses = new ArrayList<>();
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(TableName.class));

        for (var beanDef : scanner.findCandidateComponents(basePackage)) {
            try {
                Class<?> entityClass = Class.forName(beanDef.getBeanClassName());
                entityClasses.add(entityClass);
            } catch (ClassNotFoundException e) {
                System.err.println("Class not found: " + beanDef.getBeanClassName());
                e.printStackTrace();
            }
        }
        return entityClasses;
    }

    private boolean tableExists(String tableName) throws SQLException {
        try {
            DatabaseMetaData metaData = jdbcTemplate.getDataSource().getConnection().getMetaData();
            try (ResultSet rs = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
                return rs.next();
            }
        } catch (Exception e) {
            System.err.println("Error checking if table " + tableName + " exists: " + e.getMessage());
            throw e;
        }
    }

    private void createOrUpdateTable(Class<?> entityClass, String tableName) throws SQLException {
        if (!tableExists(tableName)) {
            createTable(entityClass, tableName);
        } else {
            updateTable(entityClass, tableName);
        }
    }

    private void createTable(Class<?> entityClass, String tableName) throws SQLException {
        try {
            StringBuilder createTableSQL = new StringBuilder("CREATE TABLE ");
            createTableSQL.append(tableName).append(" (");

            Field[] fields = entityClass.getDeclaredFields();
            List<String> fieldDefinitions = new ArrayList<>();
            boolean hasPrimaryKey = false;

            for (Field field : fields) {
                TableId tableId = field.getAnnotation(TableId.class);
                TableField tableField = field.getAnnotation(TableField.class);

                String columnName;
                String dataType;

                if (tableId != null) {
                    columnName = tableId.value().isEmpty() ? field.getName() : tableId.value();
                    dataType = getDataType(field.getType());
                    fieldDefinitions.add(columnName + " " + dataType + " PRIMARY KEY AUTO_INCREMENT");
                    hasPrimaryKey = true;
                } else if (tableField != null) {
                    columnName = tableField.value().isEmpty() ? field.getName() : tableField.value();
                    dataType = getDataType(field.getType());
                    fieldDefinitions.add(columnName + " " + dataType);
                }
            }

            // 如果没有主键，添加一个默认主键
            if (!hasPrimaryKey) {
                fieldDefinitions.add("id BIGINT PRIMARY KEY AUTO_INCREMENT");
            }

            createTableSQL.append(String.join(", ", fieldDefinitions)).append(");");
            jdbcTemplate.execute(createTableSQL.toString());
            System.out.println("Created table " + tableName);
        } catch (Exception e) {
            System.err.println("Error creating table " + tableName + ": " + e.getMessage());
            throw e;
        }
    }

    private void updateTable(Class<?> entityClass, String tableName) throws SQLException {
        try {
            Field[] fields = entityClass.getDeclaredFields();
            for (Field field : fields) {
                TableField tableField = field.getAnnotation(TableField.class);
                if (tableField != null) {
                    String columnName = tableField.value().isEmpty() ? field.getName() : tableField.value();
                    if (!columnExists(tableName, columnName)) {
                        String dataType = getDataType(field.getType());
                        String addColumnSQL = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + dataType + ";";
                        try {
                            jdbcTemplate.execute(addColumnSQL);
                            System.out.println("Added column " + columnName + " to table " + tableName);
                        } catch (Exception e) {
                            System.err.println("Failed to add column " + columnName + " to table " + tableName + ": " + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating table " + tableName + ": " + e.getMessage());
            throw e;
        }
    }

    private boolean columnExists(String tableName, String columnName) throws SQLException {
        try {
            DatabaseMetaData metaData = jdbcTemplate.getDataSource().getConnection().getMetaData();
            try (ResultSet rs = metaData.getColumns(null, null, tableName, columnName)) {
                return rs.next();
            }
        } catch (Exception e) {
            System.err.println("Error checking if column " + columnName + " exists in table " + tableName + ": " + e.getMessage());
            throw e;
        }
    }

    private String getDataType(Class<?> fieldType) {
        // 保持原有的数据类型映射逻辑
        if (fieldType.equals(String.class)) {
            return "VARCHAR(255)";
        } else if (fieldType.equals(LocalDateTime.class)) {
            return "DATETIME";
        } else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
            return "BIGINT";
        } else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
            return "INT";
        } else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
            return "DOUBLE";
        } else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
            return "FLOAT";
        } else if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
            return "TINYINT";
        } else if (fieldType.equals(Date.class)) {
            return "DATETIME";
        } else if (fieldType.equals(java.sql.Timestamp.class)) {
            return "TIMESTAMP";
        } else if (fieldType.equals(BigDecimal.class)) {
            return "DECIMAL(19,2)";
        } else if (fieldType.equals(Byte.class) || fieldType.equals(byte.class)) {
            return "TINYINT";
        } else if (fieldType.equals(Short.class) || fieldType.equals(short.class)) {
            return "SMALLINT";
        } else if (Collection.class.isAssignableFrom(fieldType)) {
            return "JSON";
        } else if (Map.class.isAssignableFrom(fieldType)) {
            return "JSON";
        } else {
            return "VARCHAR(255)";
        }
    }
}
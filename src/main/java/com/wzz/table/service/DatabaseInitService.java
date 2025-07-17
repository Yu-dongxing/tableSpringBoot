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
        // 自动扫描指定包路径下的所有带有@TableName注解的实体类
        List<Class<?>> entityClasses = scanEntityClasses("com.wzz.table.pojo");

        for (Class<?> entityClass : entityClasses) {
            TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);
            if (tableNameAnnotation != null) {
                String tableName = tableNameAnnotation.value();
                if (!tableExists(tableName)) {
                    createTable(entityClass, tableName);
                } else {
                    updateTable(entityClass, tableName);
                }
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
                e.printStackTrace();
            }
        }
        return entityClasses;
    }

    private boolean tableExists(String tableName) throws SQLException {
        DatabaseMetaData metaData = jdbcTemplate.getDataSource().getConnection().getMetaData();
        try (ResultSet rs = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    private void createTable(Class<?> entityClass, String tableName) throws SQLException {
        StringBuilder createTableSQL = new StringBuilder("CREATE TABLE ");
        createTableSQL.append(tableName).append(" (");

        Field[] fields = entityClass.getDeclaredFields();
        List<String> fieldDefinitions = new ArrayList<>();

        for (Field field : fields) {
            TableId tableId = field.getAnnotation(TableId.class);
            TableField tableField = field.getAnnotation(TableField.class);

            String columnName;
            String dataType;

            if (tableId != null) {
                columnName = tableId.value().isEmpty() ? field.getName() : tableId.value();
                dataType = getDataType(field.getType());
                fieldDefinitions.add(columnName + " " + dataType + " PRIMARY KEY AUTO_INCREMENT");
            } else if (tableField != null) {
                columnName = tableField.value().isEmpty() ? field.getName() : tableField.value();
                dataType = getDataType(field.getType());
                fieldDefinitions.add(columnName + " " + dataType);
            }
        }

        createTableSQL.append(String.join(", ", fieldDefinitions)).append(");");
        jdbcTemplate.execute(createTableSQL.toString());
    }

    private void updateTable(Class<?> entityClass, String tableName) throws SQLException {
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            TableField tableField = field.getAnnotation(TableField.class);
            if (tableField != null) {
                String columnName = tableField.value().isEmpty() ? field.getName() : tableField.value();
                if (!columnExists(tableName, columnName)) {
                    String dataType = getDataType(field.getType());
                    String addColumnSQL = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + dataType + ";";
                    jdbcTemplate.execute(addColumnSQL);
                }
            }
        }
    }

    private boolean columnExists(String tableName, String columnName) throws SQLException {
        DatabaseMetaData metaData = jdbcTemplate.getDataSource().getConnection().getMetaData();
        try (ResultSet rs = metaData.getColumns(null, null, tableName, columnName)) {
            return rs.next();
        }
    }

    private String getDataType(Class<?> fieldType) {
        if (fieldType.equals(String.class)) {
            return "VARCHAR(255)";

        } else if (fieldType.equals(LocalDateTime.class)) {
            return "DATETIME";
        }
        else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
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
            return "DECIMAL(19,2)"; // 默认精度为19，小数点后2位
        } else if (fieldType.equals(Byte.class) || fieldType.equals(byte.class)) {
            return "TINYINT";
        } else if (fieldType.equals(Short.class) || fieldType.equals(short.class)) {
            return "SMALLINT";
        } else if (Collection.class.isAssignableFrom(fieldType)) {
            // 假设集合类型存储为JSON字符串
            return "JSON";
        } else if (Map.class.isAssignableFrom(fieldType)) {
            // 假设Map类型存储为JSON字符串
            return "JSON";
        } else {
            // 对于其他未明确处理的类型，使用VARCHAR(255)作为默认值
            return "VARCHAR(255)";
        }
    }
}
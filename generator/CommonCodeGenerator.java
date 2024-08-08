package com.xuecheng.generator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Arrays;

/**
 * MyBatis-Plus Code generation class
 */
public class CommonCodeGenerator {

	// TODO Modify the service name and table name
	private static final String SERVICE_NAME = "comments";

	private static final String DATA_SOURCE_USER_NAME  = "root";
	private static final String DATA_SOURCE_PASSWORD  = "root";
	private static final String[] TABLE_NAMES = new String[]{
			"comment",
			"comment_reply",
			"comment_target",
	};

	// TODO By default, `entity` is generated. To generate `DTO`, modify this variable.
	// Generally, you should first generate the `DTO` class, then modify this parameter before generating the `PO` class.
	private static final Boolean IS_DTO = true;

	public static void main(String[] args) {
		// Code generator
		AutoGenerator mpg = new AutoGenerator();
		// Select the freemarker engine; the default is Velocity.
		mpg.setTemplateEngine(new FreemarkerTemplateEngine());
		// Global configuration
		GlobalConfig gc = new GlobalConfig();
		gc.setFileOverride(true);
		gc.setOutputDir(System.getProperty("user.dir") + "/xuecheng-plus-generator/src/main/java");
		gc.setAuthor("itcast");
		gc.setOpen(false);
		gc.setSwagger2(false);
		gc.setServiceName("%sService");
        gc.setBaseResultMap(true);
        gc.setBaseColumnList(true);

		if (IS_DTO) {
			gc.setSwagger2(true);
			gc.setEntityName("%sDTO");
		}
		mpg.setGlobalConfig(gc);

		// Database configuration
		DataSourceConfig dsc = new DataSourceConfig();
		dsc.setDbType(DbType.MYSQL);
		dsc.setUrl("jdbc:mysql://localhost:3306/" + SERVICE_NAME
				+ "?useUnicode=true&useSSL=false&characterEncoding=utf8");
		dsc.setDriverName("com.mysql.jdbc.Driver");
		dsc.setUsername(DATA_SOURCE_USER_NAME);
		dsc.setPassword(DATA_SOURCE_PASSWORD);
		mpg.setDataSource(dsc);

		// Package configuration
		PackageConfig pc = new PackageConfig();
		pc.setModuleName(SERVICE_NAME);
		pc.setParent("com.xuecheng");

		pc.setServiceImpl("service.impl");
		pc.setXml("mapper");
		pc.setEntity("model.po");
		mpg.setPackageInfo(pc);


		// Set template
		TemplateConfig tc = new TemplateConfig();
		mpg.setTemplate(tc);

		// Strategy configuration
		StrategyConfig strategy = new StrategyConfig();
		strategy.setNaming(NamingStrategy.underline_to_camel);
		strategy.setColumnNaming(NamingStrategy.underline_to_camel);
		strategy.setEntityLombokModel(true);
		strategy.setRestControllerStyle(true);
		strategy.setInclude(TABLE_NAMES);
		strategy.setControllerMappingHyphenStyle(true);
		strategy.setTablePrefix(pc.getModuleName() + "_");
		// Whether to remove the `is` prefix for Boolean type fields
		strategy.setEntityBooleanColumnRemoveIsPrefix(true);
		strategy.setRestControllerStyle(true);

		// Automatic field filling configuration
		strategy.setTableFillList(Arrays.asList(
				new TableFill("create_date", FieldFill.INSERT),
				new TableFill("change_date", FieldFill.INSERT_UPDATE),
				new TableFill("modify_date", FieldFill.UPDATE)
		));
		mpg.setStrategy(strategy);

		mpg.execute();
	}

}

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
public class MessageCodeGenerator {

	// TODO Modify service name and table name
	private static final String SERVICE_NAME = "messagesdk";

	//Database account
	private static final String DATA_SOURCE_USER_NAME  = "root";
	//Database password
	private static final String DATA_SOURCE_PASSWORD  = "mysql";
	//Generated tables
	private static final String[] TABLE_NAMES = new String[]{
			"mq_message",
			"mq_message_history"
//			 "course_base",
//			 "course_market",
//			 "teachplan",
//			 "teachplan_media",
//			 "course_teacher",
//			 "course_pub",
//			 "course_pub_pre"
//			"course_category"
	};

	// TODO By default, generate entity. To generate DTO, modify this variable.
	// Generally, you should first generate the DTO class, then modify this parameter before generating the PO class.
	private static final Boolean IS_DTO = false;

	public static void main(String[] args) {
		// Code Generator
		AutoGenerator mpg = new AutoGenerator();
		// Choose the Freemarker engine; the default is Velocity.
		mpg.setTemplateEngine(new FreemarkerTemplateEngine());
		// Global configuration
		GlobalConfig gc = new GlobalConfig();
		gc.setFileOverride(true);
		//Generation path
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
		dsc.setUrl("jdbc:mysql://192.168.101.65:3306/xcplus_content"
				+ "?serverTimezone=UTC&useUnicode=true&useSSL=false&characterEncoding=utf8");
//		dsc.setDriverName("com.mysql.jdbc.Driver");
		dsc.setDriverName("com.mysql.cj.jdbc.Driver");
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


		// Set templates
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
		// Whether to remove the "is" prefix for Boolean type fields
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

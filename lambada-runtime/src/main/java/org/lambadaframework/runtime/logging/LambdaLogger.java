package org.lambadaframework.runtime.logging;


import com.amazonaws.services.lambda.runtime.log4j.LambdaAppender;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class LambdaLogger {

    private static Appender appender;

    private static Appender getAppender() {
        if (appender == null) {
            PatternLayout patternLayout = new PatternLayout();
            patternLayout.setConversionPattern("%d{yyyy-MM-dd HH:mm:ss} <%X{AWSRequestId}> %-5p %c{1}:%L - %m%n");
            appender = new LambdaAppender();
            appender.setLayout(patternLayout);
        }
        return appender;
    }

    public static Logger getLogger(Class clazz) {
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(clazz);
        logger.addAppender(getAppender());
        return logger;
    }
}

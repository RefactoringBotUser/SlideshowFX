package com.twasyl.slideshowfx.global.configuration;

import com.twasyl.slideshowfx.logs.SlideshowFXHandler;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * This class provides methods for accessing configuration properties.
 *
 * @author Thierry Wasylczenko
 * @version 1.1
 * @since SlideshowFX 1.0
 */
public class GlobalConfiguration {
    private static final Logger LOGGER = Logger.getLogger(GlobalConfiguration.class.getName());

    protected static final String APPLICATION_DIRECTORY_PROPERTY = "application.dir";
    protected static final String PLUGINS_DIRECTOR_PROPERTY = "plugins.dir";

    private static File APPLICATION_DIRECTORY = null;
    private static File PLUGINS_DIRECTORY = null;
    private static File CONFIG_FILE = null;
    private static File LOGGING_CONFIG_FILE = null;

    /**
     * Name of the parameter used to specify if auto saving files is enabled. The value of the parameter is a boolean.
     */
    protected static final String AUTO_SAVING_ENABLED_PARAMETER = "application.autoSaving.enabled";

    /**
     * Name of the parameter used to specify the interval for auto saving files. The value of this parameter must be
     * given in seconds.
     */
    protected static final String AUTO_SAVING_INTERVAL_PARAMETER = "application.autoSaving.interval";

    /**
     * Name of the parameter used to specify whether the temporary files are deleted when the application is exiting.
     * The value of the parameter is a boolean.
     */
    protected static final String TEMPORARY_FILES_DELETION_ON_EXIT_PARAMETER = "application.temporaryFiles.deleteOnExit";

    /**
     * Name of the parameter used to specify how old can temporary files be before being deleted when exiting the
     * application. The value of this parameter must be given in seconds.
     */
    protected static final String TEMPORARY_FILES_MAX_AGE_PARAMETER = "application.temporaryFiles.maxAge";

    /**
     * The default {@link Charset} used by the application when writing files, readings files and converting strings.
     */
    protected static final Charset DEFAULT_CHARSET = UTF_8;

    /**
     * Name of the parameter for defining the log level.
     */
    protected static final String LOG_LEVEL_PARAMETER = ".level";

    /**
     * Name of the parameter for specifying the log handler.
     */
    protected static final String LOG_HANDLERS_PARAMETER = "handlers";

    /**
     * Name of the parameter suffix for the specifying the encoding of the log file.
     */
    protected static final String LOG_ENCODING_SUFFIX = ".encoding";

    /**
     * Name of the parameter for specifying the file log limit.
     */
    protected static final String LOG_FILE_LIMIT_PARAMETER = "java.util.logging.FileHandler.limit";

    /**
     * Name of the parameter for specifying the pattern of the log file name.
     */
    protected static final String LOG_FILE_PATTERN_PARAMETER = "java.util.logging.FileHandler.pattern";

    /**
     * Name of the parameter suffix for the log file formatter.
     */
    protected static final String LOG_FORMATTER_SUFFIX = ".formatter";

    /**
     * Name of the parameter for specifying if logs must be appended to the log file.
     */
    protected static final String LOG_FILE_APPEND_PARAMETER = "java.util.logging.FileHandler.append";

    /**
     * Get the application directory used to store the plugins and the configuration. The method will determine the
     * directory by checking if there is a system property named {@value #APPLICATION_DIRECTORY_PROPERTY} that defines
     * the directory to use and if not, the directory will be the {@code .SlideshowFX} directory stored in the user's
     * home.
     *
     * @return The application directory.
     */
    public synchronized static File getApplicationDirectory() {
        if (APPLICATION_DIRECTORY == null) {
            final Properties properties = System.getProperties();

            if (properties.containsKey(APPLICATION_DIRECTORY_PROPERTY)) {
                APPLICATION_DIRECTORY = new File(properties.getProperty(APPLICATION_DIRECTORY_PROPERTY));
            } else {
                APPLICATION_DIRECTORY = new File(System.getProperty("user.home"), ".SlideshowFX");
            }
        }

        return APPLICATION_DIRECTORY;
    }

    /**
     * Get the plugins directory used to store the installed plugins. The method will determine the
     * directory by checking if there is a system property named {@value #PLUGINS_DIRECTOR_PROPERTY} that defines
     * the directory to use and if not, the directory will be the {@code plugins} directory stored in the application
     * directory returned by {@link #getApplicationDirectory()}.
     *
     * @return The plugins directory.
     */
    public synchronized static File getPluginsDirectory() {
        if (PLUGINS_DIRECTORY == null) {
            final Properties properties = System.getProperties();

            if (properties.containsKey(PLUGINS_DIRECTOR_PROPERTY)) {
                PLUGINS_DIRECTORY = new File(properties.getProperty(PLUGINS_DIRECTOR_PROPERTY));
            } else {
                PLUGINS_DIRECTORY = new File(getApplicationDirectory(), "plugins");
            }
        }
        return PLUGINS_DIRECTORY;
    }

    /**
     * Get the configuration file of the application. The file is named {@code .slideshowfx.configuration.properties}
     * and is stored in the directory returned by {@link #getApplicationDirectory()}.
     *
     * @return The configuration file.
     */
    public synchronized static File getConfigurationFile() {
        if (CONFIG_FILE == null) {
            CONFIG_FILE = new File(getApplicationDirectory(), ".slideshowfx.configuration.properties");
        }

        return CONFIG_FILE;
    }

    /**
     * Get the logging configuration file. The method will determine the file by checking if there is a system property
     * named {@code java.util.logging.config.file} that defines the file to use and if not, the file will be the
     * {@code logging.config} file stored in the application directory returned by {@link #getApplicationDirectory()}.
     *
     * @return The logging configuration file.
     */
    public synchronized static File getLoggingConfigFile() {
        if (LOGGING_CONFIG_FILE == null) {
            final Properties properties = System.getProperties();

            if (properties.containsKey("java.util.logging.config.file")) {
                LOGGING_CONFIG_FILE = new File(System.getProperty("java.util.logging.config.file"));
            } else {
                LOGGING_CONFIG_FILE = new File(getApplicationDirectory(), "logging.config");
            }
        }

        return LOGGING_CONFIG_FILE;
    }

    /**
     * Creates the configuration directory represented by the {@link #APPLICATION_DIRECTORY} variable if it doesn't
     * already exist.
     *
     * @return {@code true} if the application directory has been created by this method, {@code false} otherwise.
     */
    public synchronized static boolean createApplicationDirectory() {
        boolean created = false;

        if (!getApplicationDirectory().exists()) {
            created = getApplicationDirectory().mkdirs();
        }

        return created;
    }

    /**
     * Creates the plugins directory represented by the {@link #PLUGINS_DIRECTORY} variable if it doesn't
     * already exist.
     * If parents directories don't exist, this method will not create them and the directory will not be created.
     *
     * @return {@code true} if the plugins directory has been created by this method, {@code false} otherwise.
     */
    public synchronized static boolean createPluginsDirectory() {
        boolean created = false;

        if (!getPluginsDirectory().exists()) {
            created = getPluginsDirectory().mkdir();
        }

        return created;
    }

    /**
     * Creates the configuration file of the application, represented by the {@link #getConfigurationFile()}
     * variable if it doesn't already exist.
     *
     * @return {@code true} if the configuration file has been created by this method, {@code false} otherwise.
     */
    public synchronized static boolean createConfigurationFile() {
        boolean created = false;

        if (!getConfigurationFile().exists()) {
            try {
                created = getConfigurationFile().createNewFile();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Can not create the configuration file", e);
            }
        }

        return created;
    }

    /**
     * Creates the logging configuration file of the application, represented by the {@link #getLoggingConfigFile()}
     * variable if it doesn't already exist.
     *
     * @return {@code true} if the logging configuration file has been created by this method, {@code false} otherwise.
     */
    public synchronized static boolean createLoggingConfigurationFile() {
        boolean created = false;

        if (!getLoggingConfigFile().exists()) {
            try {
                created = getLoggingConfigFile().createNewFile();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Can not create the logging configuration file", e);
            }
        }

        return created;
    }

    /**
     * Fill the configuration file with default values if it exists.
     */
    public synchronized static void fillConfigurationWithDefaultValue() {
        if (getConfigurationFile().exists()) {
            final Properties properties = readAllPropertiesFromConfigurationFile(getConfigurationFile());

            if (!properties.containsKey(TEMPORARY_FILES_DELETION_ON_EXIT_PARAMETER))
                enableTemporaryFilesDeletionOnExit(true);
            if (!properties.containsKey(TEMPORARY_FILES_MAX_AGE_PARAMETER)) setTemporaryFilesMaxAge(7);
            if (!properties.containsKey(AUTO_SAVING_ENABLED_PARAMETER)) enableAutoSaving(false);
            if (!properties.containsKey(AUTO_SAVING_INTERVAL_PARAMETER)) setAutoSavingInterval(5);
        }
    }

    public synchronized static void fillLoggingConfigurationFileWithDefaultValue() {
        if (getLoggingConfigFile().exists()) {
            final Properties properties = readAllPropertiesFromConfigurationFile(getLoggingConfigFile());

            if (!properties.containsKey(LOG_LEVEL_PARAMETER)) setLogLevel(Level.INFO);
            if (!properties.containsKey(LOG_HANDLERS_PARAMETER))
                setLogHandler(FileHandler.class, SlideshowFXHandler.class);
            if (!properties.containsKey(LOG_FILE_APPEND_PARAMETER)) setLogFileAppend(true);
            if (!properties.containsKey(FileHandler.class.getName().concat(LOG_ENCODING_SUFFIX)))
                setLogEncoding(FileHandler.class, UTF_8);
            if (!properties.containsKey(FileHandler.class.getName().concat(LOG_FORMATTER_SUFFIX)))
                setLogFormatter(FileHandler.class, SimpleFormatter.class);
            if (!properties.containsKey(LOG_FILE_LIMIT_PARAMETER)) setLogFileLimit(50000);
            if (!properties.containsKey(LOG_FILE_PATTERN_PARAMETER)) setLogFilePattern("%h/.SlideshowFX/sfx%g.log");
            if (!properties.containsKey(SlideshowFXHandler.class.getName().concat(LOG_ENCODING_SUFFIX)))
                setLogEncoding(SlideshowFXHandler.class, UTF_8);
            if (!properties.containsKey(SlideshowFXHandler.class.getName().concat(LOG_FORMATTER_SUFFIX)))
                setLogFormatter(SlideshowFXHandler.class, SimpleFormatter.class);
        }
    }

    /**
     * Check if the temporary files can be deleted or not. Temporary files can be deleted if the value of the parameter
     * {@link #TEMPORARY_FILES_DELETION_ON_EXIT_PARAMETER} is not {@code null] and {@code true} and the value of the
     * parameter {@link #TEMPORARY_FILES_MAX_AGE_PARAMETER} is not {@code null}.
     *
     * @return {@code true} if the temporary files can be deleted, {@code false} otherwise.
     */
    public static boolean canDeleteTemporaryFiles() {
        final Boolean deleteTemporaryFilesOnExist = getBooleanProperty(TEMPORARY_FILES_DELETION_ON_EXIT_PARAMETER);
        final Long maxAge = getLongProperty(TEMPORARY_FILES_MAX_AGE_PARAMETER);

        return deleteTemporaryFilesOnExist != null && deleteTemporaryFilesOnExist && maxAge != null;
    }

    /**
     * Read all properties stored in the configuration file. If no properties are found or if the configuration file
     * doesn't exist, an empty object is returned.
     *
     * @return The properties stored in the configuration file.
     */
    protected synchronized static Properties readAllPropertiesFromConfigurationFile(final File file) {
        final Properties properties = new Properties();

        if (file.exists()) {

            try (final Reader reader = new FileReader(file)) {
                properties.load(reader);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Can not load configuration file: " + file.getAbsolutePath(), e);
            }
        }

        return properties;
    }

    /**
     * Writes all properties to the configuration file. If the given properties are null, nothing is performed.
     *
     * @param file       The file in which the properties will be written.
     * @param properties The properties to write to the configuration file.
     */
    private synchronized static void writeAllPropertiesToConfigurationFile(final File file, final Properties properties) {
        if (properties != null) {
            try (final Writer writer = new FileWriter(file)) {
                properties.store(writer, "");
                writer.flush();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Can not save configuration", e);
            }
        }
    }

    /**
     * Get a property from the configuration. This methods return {@code null} is the property
     * is not found or if the configuration file does not exist.
     *
     * @param propertyName The name of the property to retrieve.
     * @return The value of the property or {@code null} if it is not found or the configuration does not exist.
     * @throws NullPointerException     If the property name is null.
     * @throws IllegalArgumentException If the property name is empty.
     */
    public synchronized static String getProperty(final String propertyName) {
        return getProperty(getConfigurationFile(), propertyName);
    }

    /**
     * Get a property from the given {@code file}. This methods return {@code null} is the property
     * is not found or if the configuration file does not exist.
     *
     * @param file         The file from which the property will be read.
     * @param propertyName The name of the property to retrieve.
     * @return The value of the property or {@code null} if it is not found or the configuration does not exist.
     * @throws NullPointerException     If the property name is null.
     * @throws IllegalArgumentException If the property name is empty.
     */
    public synchronized static String getProperty(final File file, final String propertyName) {
        checkPropertyName(propertyName);

        String value = null;

        if (file.exists()) {
            final Properties properties = readAllPropertiesFromConfigurationFile(file);
            value = properties.getProperty(propertyName.trim());
        }

        return value;
    }

    /**
     * Save the given {@code propertyName} and {@code propertyValue} to the configuration.
     *
     * @param propertyName  The name of the property to save.
     * @param propertyValue The value of the property to save.
     * @throws NullPointerException     If the name or value of the property is null.
     * @throws IllegalArgumentException If the name or value of the property is empty.
     */
    public synchronized static void setProperty(final String propertyName, final String propertyValue) {
        setProperty(getConfigurationFile(), propertyName, propertyValue);
    }

    /**
     * Save the given {@code propertyName} and {@code propertyValue} to the given {@code file}.
     *
     * @param file          The file in which the property will be set.
     * @param propertyName  The name of the property to save.
     * @param propertyValue The value of the property to save.
     * @throws NullPointerException     If the name or value of the property is null.
     * @throws IllegalArgumentException If the name or value of the property is empty.
     */
    private synchronized static void setProperty(final File file, final String propertyName, final String propertyValue) {
        checkPropertyName(propertyName);
        checkPropertyValue(propertyValue);

        final Properties properties = readAllPropertiesFromConfigurationFile(file);
        properties.put(propertyName.trim(), propertyValue);
        writeAllPropertiesToConfigurationFile(file, properties);
    }

    /**
     * Remove a property from the configuration file. If the property doesn't exist, nothing is performed.
     *
     * @param propertyName The name of the property to remove.
     */
    public synchronized static void removeProperty(final String propertyName) {
        removeProperty(getConfigurationFile(), propertyName);
    }

    /**
     * Remove a property from the configuration file. If the property doesn't exist, nothing is performed.
     *
     * @param file         The file from which the property will be removed.
     * @param propertyName The name of the property to remove.
     */
    public synchronized static void removeProperty(final File file, final String propertyName) {
        checkPropertyName(propertyName);

        final Properties properties = readAllPropertiesFromConfigurationFile(file);

        if (properties.containsKey(propertyName.trim())) {
            properties.remove(propertyName.trim());
            writeAllPropertiesToConfigurationFile(file, properties);
        }
    }

    /**
     * Check if the given property name is valid or not. The property name is considered valid if if is not {@code null}
     * and its value is not empty.
     *
     * @param propertyName The name of the property to check.
     * @throws NullPointerException     If the property name is {@code null}.
     * @throws IllegalArgumentException If the property name is empty.
     */
    private static void checkPropertyName(final String propertyName) {
        if (propertyName == null) throw new NullPointerException("The property name can not be null");
        if (propertyName.trim().isEmpty()) throw new IllegalArgumentException("The property name can not be empty");
    }

    /**
     * Check if the given property name is valid or not. The property name is considered valid if if is not {@code null}
     * and its value is not empty.
     *
     * @param propertyValue The value to check.
     * @throws NullPointerException     If the property value is {@code null}.
     * @throws IllegalArgumentException If the property value is empty.
     */
    private static void checkPropertyValue(final String propertyValue) {
        if (propertyValue == null) throw new NullPointerException("The property value can not be null");
        if (propertyValue.trim().isEmpty()) throw new IllegalArgumentException("The property value can not be empty");
    }

    /**
     * Get the value of a property as a {@link Long}.
     *
     * @param propertyName The name of the property to get.
     * @return The value of the property or {@code null} if it is not present or can not be parsed.
     */
    public static Long getLongProperty(final String propertyName) {
        return getLongProperty(getConfigurationFile(), propertyName);
    }

    /**
     * Get the value of a property as a {@link Long}.
     *
     * @param file         The file from which retrieve the property.
     * @param propertyName The name of the property to get.
     * @return The value of the property or {@code null} if it is not present or can not be parsed.
     */
    public static Long getLongProperty(final File file, final String propertyName) {
        Long value = null;

        final String retrievedProperty = getProperty(file, propertyName);
        if (retrievedProperty != null) {
            try {
                value = Long.parseLong(retrievedProperty);
            } catch (NumberFormatException ex) {
                LOGGER.log(Level.WARNING, "The value of the property '" + propertyName + "' can not be parsed", ex);
            }
        }

        return value;
    }

    /**
     * Get the value of a property as a {@link Boolean}.
     *
     * @param propertyName The name of the property to get.
     * @return The value of the property or {@code null} if it is not present or can not be parsed.
     */
    public static Boolean getBooleanProperty(final String propertyName) {
        return getBooleanProperty(getConfigurationFile(), propertyName);
    }

    /**
     * Get the value of a property as a {@link Boolean}.
     *
     * @param file         The file from which retrieve the property.
     * @param propertyName The name of the property to get.
     * @return The value of the property or {@code null} if it is not present or can not be parsed.
     */
    public static Boolean getBooleanProperty(final File file, final String propertyName) {
        Boolean value = null;

        final String retrievedProperty = getProperty(file, propertyName);
        if (retrievedProperty != null) {
            try {
                value = Boolean.parseBoolean(retrievedProperty);
            } catch (NumberFormatException ex) {
                LOGGER.log(Level.WARNING, "The value of the property '" + propertyName + "' can not be parsed", ex);
            }
        }

        return value;
    }

    /**
     * Check if the auto saving is enabled on exit.
     *
     * @return {@code true} if the auto saving is enabled, {@code false} otherwise.
     */
    public static boolean isAutoSavingEnabled() {
        final Boolean autoSave = getBooleanProperty(AUTO_SAVING_ENABLED_PARAMETER);
        return autoSave == null ? Boolean.FALSE : autoSave;
    }

    /**
     * Enable or disable the auto saving configuration..
     *
     * @param enabled The value of the parameter.
     */
    public static void enableAutoSaving(final boolean enabled) {
        setProperty(AUTO_SAVING_ENABLED_PARAMETER, String.valueOf(enabled));
    }

    /**
     * Get the interval for auto saving files.
     *
     * @return The interval in minutes.
     */
    public static Long getAutoSavingInterval() {
        final Long intervalInSeconds = getLongProperty(AUTO_SAVING_INTERVAL_PARAMETER);
        return intervalInSeconds == null ? null : TimeUnit.SECONDS.toMinutes(intervalInSeconds);
    }

    /**
     * Set the auto saving interval configuration parameter.
     *
     * @param intervalInMinutes The interval in minutes for the auto saving parameter.
     */
    public static void setAutoSavingInterval(final long intervalInMinutes) {
        setProperty(AUTO_SAVING_INTERVAL_PARAMETER, String.valueOf(TimeUnit.MINUTES.toSeconds(intervalInMinutes)));
    }

    /**
     * Removes the auto saving interval from the configuration.
     */
    public static void removeAutoSavingInterval() {
        removeProperty(AUTO_SAVING_INTERVAL_PARAMETER);
    }

    /**
     * Check if the temporary files deletion is enabled on exit.
     *
     * @return {@code true} if the deletion is enabled, {@code false} otherwise.
     */
    public static boolean isTemporaryFilesDeletionOnExitEnabled() {
        final Boolean deleteTemporaryFiles = getBooleanProperty(TEMPORARY_FILES_DELETION_ON_EXIT_PARAMETER);
        return deleteTemporaryFiles == null ? false : deleteTemporaryFiles;
    }

    /**
     * Sets the default log level of the application.
     *
     * @param level The desired log level.
     */
    public static void setLogLevel(final Level level) {
        setProperty(getLoggingConfigFile(), LOG_LEVEL_PARAMETER, level.getName());
    }

    /**
     * Sets the default log handlers.
     *
     * @param handlers The handlers of logs.
     */
    public static void setLogHandler(final Class<? extends Handler>... handlers) {
        final StringJoiner joiner = new StringJoiner(" ");
        Arrays.stream(handlers).forEach(handler -> joiner.add(handler.getName()));

        setProperty(getLoggingConfigFile(), LOG_HANDLERS_PARAMETER, joiner.toString());
    }

    /**
     * Sets the encoding of log files.
     *
     * @param handler The class handler to set the encoding for.
     * @param charset The encoding of log files.
     */
    public static void setLogEncoding(final Class<? extends Handler> handler, final Charset charset) {
        setProperty(getLoggingConfigFile(), handler.getName().concat(LOG_ENCODING_SUFFIX), charset.displayName());
    }

    /**
     * Sets the size in bytes of the log files.
     *
     * @param size The size, in bytes, of log files.
     */
    public static void setLogFileLimit(final long size) {
        setProperty(getLoggingConfigFile(), LOG_FILE_LIMIT_PARAMETER, String.valueOf(size));
    }

    /**
     * Sets the log files pattern.
     *
     * @param pattern The pattern of log files.
     */
    public static void setLogFilePattern(final String pattern) {
        setProperty(getLoggingConfigFile(), LOG_FILE_PATTERN_PARAMETER, pattern);
    }

    /**
     * Sets the class responsible of formatting log files.
     *
     * @param handler   The class handler to set the formatter for.
     * @param formatter The formatter to use for log files.
     */
    public static void setLogFormatter(final Class<? extends Handler> handler, final Class<? extends Formatter> formatter) {
        setProperty(getLoggingConfigFile(), handler.getName().concat(LOG_FORMATTER_SUFFIX), formatter.getName());
    }

    /**
     * Defines if the logs should be append or not to the log files.
     *
     * @param append {@code true} to allow appending, {@code false} otherwise.
     */
    public static void setLogFileAppend(final boolean append) {
        setProperty(getLoggingConfigFile(), LOG_FILE_APPEND_PARAMETER, String.valueOf(append));
    }

    /**
     * Enable or disable the temporary files deletion.
     *
     * @param enable {@code true} to enable the deletion, {@code false} otherwise.
     */
    public static void enableTemporaryFilesDeletionOnExit(final boolean enable) {
        setProperty(TEMPORARY_FILES_DELETION_ON_EXIT_PARAMETER, String.valueOf(enable));
    }

    /**
     * Get the temporary files max age parameter's value.
     *
     * @return The max age of temporary files in days.
     */
    public static Long getTemporaryFilesMaxAge() {
        final Long ageInSeconds = getLongProperty(TEMPORARY_FILES_MAX_AGE_PARAMETER);
        return ageInSeconds == null ? null : TimeUnit.SECONDS.toDays(ageInSeconds);
    }

    /**
     * Set the max age of temporary files before they are deleted.
     *
     * @param maxAgeInDays The max age of the temporary files.
     */
    public static void setTemporaryFilesMaxAge(final long maxAgeInDays) {
        setProperty(TEMPORARY_FILES_MAX_AGE_PARAMETER, String.valueOf(TimeUnit.DAYS.toSeconds(maxAgeInDays)));
    }

    /**
     * Remove the temporary files max age from the configuration.
     */
    public static void removeTemporaryFilesMaxAge() {
        removeProperty(TEMPORARY_FILES_MAX_AGE_PARAMETER);
    }

    /**
     * Get the default {@link Charset} used by the application.
     *
     * @return The default charset used by the application.
     */
    public static Charset getDefaultCharset() {
        return DEFAULT_CHARSET;
    }
}

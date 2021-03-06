package thrift.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import thrift.commons.core.LogsCenter;
import thrift.commons.exceptions.DataConversionException;
import thrift.model.ReadOnlyThrift;
import thrift.model.ReadOnlyUserPrefs;
import thrift.model.UserPrefs;

/**
 * Manages storage of Thrift data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private ThriftStorage thriftStorage;
    private UserPrefsStorage userPrefsStorage;
    private CurrencyMappingsStorage currencyMappingsStorage;


    public StorageManager(ThriftStorage thriftStorage, UserPrefsStorage userPrefsStorage,
            CurrencyMappingsStorage currencyMappingsStorage) {
        super();
        this.thriftStorage = thriftStorage;
        this.userPrefsStorage = userPrefsStorage;
        this.currencyMappingsStorage = currencyMappingsStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataConversionException, IOException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ Thrift methods ==============================

    @Override
    public Path getThriftFilePath() {
        return thriftStorage.getThriftFilePath();
    }

    @Override
    public Optional<ReadOnlyThrift> readThrift() throws DataConversionException, IOException {
        return readThrift(thriftStorage.getThriftFilePath());
    }

    @Override
    public Optional<ReadOnlyThrift> readThrift(Path filePath) throws DataConversionException, IOException {
        logger.fine("Attempting to read data from file: " + filePath);
        return thriftStorage.readThrift(filePath);
    }

    @Override
    public void saveThrift(ReadOnlyThrift thrift) throws IOException {
        saveThrift(thrift, thriftStorage.getThriftFilePath());
    }

    @Override
    public void saveThrift(ReadOnlyThrift thrift, Path filePath) throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        thriftStorage.saveThrift(thrift, filePath);
    }

    // ================ Currency Mappings methods ==============================

    @Override
    public Path getCurrencyMappingsFilePath() {
        return currencyMappingsStorage.getCurrencyMappingsFilePath();
    }

    @Override
    public Optional<HashMap<String, Double>> readCurrencyMappings() throws DataConversionException, IOException {
        return currencyMappingsStorage.readCurrencyMappings();
    }

    @Override
    public void saveCurrencyMappings(Map<String, Double> currencyMappings) throws IOException {
        currencyMappingsStorage.saveCurrencyMappings(currencyMappings);
    }
}

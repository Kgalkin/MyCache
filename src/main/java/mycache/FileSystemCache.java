package mycache;

import mycache.cachepolicies.BasePolicy;
import mycache.cachepolicies.Policy;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileSystemCache<K, V extends Serializable> extends SimpleCache<K, V> {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(FileSystemCache.class);
    private Path cacheDir;
    private Map<K, String> fileNames;


    public FileSystemCache() {
        this(10);
    }

    public FileSystemCache(int maxSize) {
        this(maxSize, new BasePolicy<>());
    }

    public FileSystemCache(int maxSize, Policy<K, ?> policy){
        super(maxSize, policy);
        this.fileNames = new ConcurrentHashMap<>(maxSize);
        initCacheTempDirectory();
    }

    @Override
    public boolean contains(K key) {
        return fileNames.containsKey(key);
    }

    @Override
    public int size() {
        return fileNames.size();
    }

    @Override
    public V simpleGet(K key) {
        return readFileToObject(getFile(key));
    }

    @Override
    public void simplePut(K key, V value) {
        try {
            File file = contains(key) ? getFile(key) : Files.createTempFile(cacheDir, "", "").toFile();
            try (ObjectOutputStream objectOut = new ObjectOutputStream(new FileOutputStream(file, false))) {
                objectOut.writeObject(value);
                objectOut.flush();
                fileNames.put(key, file.getName());
            }
        } catch (IOException e) {
            log.error("Error on writing object to file " + e.getMessage());
        }
    }

    @Override
    public V simpleRemove(K key) {
        if (contains(key)) {
            File file = getFile(key);
            V value = readFileToObject(file);
            fileNames.remove(key);
            deleteFileWithWarn(file);
            return value;
        }
        return null;
    }

    @Override
    public void simpleClean() {
        fileNames.clear();
        File[] files = cacheDir.toFile().listFiles();
        if (files != null) {
            Arrays.stream(files).forEach(this::deleteFileWithWarn);
        }
    }

    private void deleteFileWithWarn(File file) {
        if (!file.delete()) {
            log.debug("Failed to delete file : " + file.getName());
        }
    }

    private File getFile(K key) {
        return new File(cacheDir + File.separator + fileNames.get(key));
    }

    private V readFileToObject(File file) {
        try (ObjectInputStream inStream = new ObjectInputStream(new FileInputStream(file))) {
            return (V) inStream.readObject();
        } catch (IOException e) {
            log.error("Failed read file [%s] from cache directory %s", file, e);
            return null;
        } catch (ClassNotFoundException e) {
            log.error("Failed read object [%s] from cached file %s", file, e);
            return null;
        }
    }

    private void initCacheTempDirectory() {
        try {
            cacheDir = Files.createTempDirectory("cacheTempDir");
            cacheDir.toFile().deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

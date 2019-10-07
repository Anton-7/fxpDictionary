package dictionary;

import file.NameTranslate;
import file.PriceFile;

import java.nio.file.Path;

public class FruitDictionary {

    public void start(Path path) {
        new PriceFile().processFile(path);
    }

    public void fillDictionary(Path path) {
        new NameTranslate().fillDictionary(path);
    }

}

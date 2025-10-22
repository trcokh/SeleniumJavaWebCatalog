package utils;

import java.util.List;

public class MultipleSubmitUtils {
    private List<String> names;
    private List<String> urls;

    public MultipleSubmitUtils(List<String> names, List<String> urls) {
        this.names = names;
        this.urls = urls;
    }

    public List<String> getNames() { return names; }
    public List<String> getUrls() { return urls; }
}
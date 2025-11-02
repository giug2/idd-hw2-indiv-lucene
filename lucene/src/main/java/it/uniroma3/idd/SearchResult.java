package it.uniroma3.idd;

public class SearchResult {
    private final String fileName;
    private final float score;

    public SearchResult(String fileName, float score) {
        this.fileName = fileName;
        this.score = score;
    }

    public String getFileName() {
        return fileName;
    }

    public float getScore() {
        return score;
    }
}

public class App {
    public static void main(String[] args) {
        String slug = "alice";
        String path = "./donnees/" + slug + "_comp.bin";
        String frequencyPath = "./donnees/" + slug + "_freq.txt";
        Decompressor decompressor = new Decompressor(path, frequencyPath);
        decompressor.decompress();
    }
}

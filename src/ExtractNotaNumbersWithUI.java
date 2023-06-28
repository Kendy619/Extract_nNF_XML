import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ExtractNotaNumbersWithUI {
    public static void main(String[] args) {
        
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecione a pasta contendo os XMLs");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            
            String xmlFolderPath = chooser.getSelectedFile().getAbsolutePath();

            String csvFilePath = xmlFolderPath + File.separator + "nf_numbers.csv";

            List<String> nfNumbers = extractNfNumbers(xmlFolderPath);

            saveNfNumbersToCsv(nfNumbers, csvFilePath);

            JOptionPane.showMessageDialog(null,
                    "Extração concluída. Os números das notas foram salvos em: " + csvFilePath);
        }
    }

    private static List<String> extractNfNumbers(String xmlFolderPath) {
        List<String> nfNumbers = new ArrayList<>();

        try {
            // Obter a lista de arquivos XML na pasta
            List<File> xmlFiles = getXmlFiles(xmlFolderPath);

            // Iterar sobre cada arquivo XML
            for (File xmlFile : xmlFiles) {
                String nfNumber = extractNfNumberFromXml(xmlFile);
                nfNumbers.add(nfNumber);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nfNumbers;
    }

    private static List<File> getXmlFiles(String xmlFolderPath) throws IOException {
        List<File> xmlFiles = new ArrayList<>();

        Files.walk(Paths.get(xmlFolderPath)).filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".xml"))
                .forEach(path -> xmlFiles.add(path.toFile()));

        return xmlFiles;
    }

    private static String extractNfNumberFromXml(File xmlFile) throws IOException {
        Document doc = Jsoup.parse(xmlFile, "UTF-8");

        Element infNFeElement = doc.selectFirst("infNFe");
        String nfNumber;

        if (infNFeElement != null && !infNFeElement.text().isEmpty()) {
            nfNumber = infNFeElement.selectFirst("nNF").text();
        } else {
            Element chNFeElement = doc.selectFirst("chNFe");
            String chNFe = chNFeElement.text();
            nfNumber = chNFe.substring(29, 35);
        }

        return nfNumber;
    }

    private static void saveNfNumbersToCsv(List<String> nfNumbers, String csvFilePath) {
        try (FileWriter writer = new FileWriter(csvFilePath)) {
            for (String nfNumber : nfNumbers) {
                writer.write(nfNumber + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

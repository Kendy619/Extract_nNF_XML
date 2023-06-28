import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ExtractNotaNumbersWithUI {
    // Função para extrair o número da nota de um arquivo XML
    private static String extrairNumeroNota(File file) {
        String numeroNota = "";

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

            Element infNFeElement = (Element) document.getElementsByTagName("infNFe").item(0);
            Element ideElement = (Element) infNFeElement.getElementsByTagName("ide").item(0);
            Element nNFElement = (Element) ideElement.getElementsByTagName("nNF").item(0);
            numeroNota = nNFElement.getTextContent();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return numeroNota;
    }

    // Função para percorrer os XMLs na pasta e extrair os números das notas
    private static String[] extrairNumerosNotas(String pastaXmls) {
        File folder = new File(pastaXmls);
        File[] files = folder.listFiles();
        String[] numerosNotas = new String[files.length];

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                String xmlPath = files[i].getAbsolutePath();
                numerosNotas[i] = extrairNumeroNota(new File(xmlPath));
            }
        }

        return numerosNotas;
    }

    // Função para salvar os números das notas em um arquivo CSV
    private static void salvarNumerosNotasCSV(String[] numerosNotas, String csvPath) {
        try {
            FileWriter writer = new FileWriter(csvPath);

            writer.append("Número da Nota");
            writer.append("\n");

            for (String numero : numerosNotas) {
                writer.append(numero);
                writer.append("\n");
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Função para abrir uma janela de seleção de diretório
    private static String escolherDiretorio() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecione o diretório contendo os XMLs");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            return selectedDirectory.getAbsolutePath();
        } else {
            return null;
        }
    }

    // Função para executar o processo completo
    private static void executarProcesso() {
        String pastaXmls = escolherDiretorio();
        if (pastaXmls != null) {
            String csvPath = pastaXmls + File.separator + "notas.csv";
            String[] numerosNotas = extrairNumerosNotas(pastaXmls);
            salvarNumerosNotasCSV(numerosNotas, csvPath);
            System.out.println("Extração concluída com sucesso!");
        } else {
            System.out.println("Nenhum diretório selecionado. Processo abortado.");
        }
    }

    // Exemplo de uso
    public static void main(String[] args) {
        executarProcesso();
    }
}
